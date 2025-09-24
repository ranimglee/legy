package ordering.application.utils;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import lombok.AllArgsConstructor;
import ordering.application.exception.FinancialReportGenerationException;
import ordering.application.usecase.order.GetAllCompletedOrdersUseCase;
import ordering.application.usecase.order.GetCompletedOrdersByRestaurantUseCase;
import ordering.domain.model.Order;
import ordering.domain.model.value.RestaurantInfo;
import org.springframework.stereotype.Service;
import shared.domain.service.RestaurantQueryService;
import shared.dto.RestaurantInfoDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AllRestaurantsFinancialReportGenerator {

    private final FinancialPdfGenerator invoiceStylePdfGenerator;
    private final RestaurantQueryService restaurantQueryService;
    private final GetCompletedOrdersByRestaurantUseCase completedOrdersByRestaurantUseCase;
    private final GetAllCompletedOrdersUseCase allCompletedOrdersUseCase;



    public ByteArrayInputStream generateMultiRestaurantFinancialReport(List<String> restaurantIds) {
        try {
            ByteArrayOutputStream mergedOutput = new ByteArrayOutputStream();
            Document document = new Document();
            PdfCopy copy = new PdfCopy(document, mergedOutput);
            document.open();

            for (String restaurantId : restaurantIds) {
                // Step 1: Fetch data
                RestaurantInfoDTO dto = restaurantQueryService.getRestaurantInfoById(restaurantId);
                RestaurantInfo restaurantInfo = new RestaurantInfo(
                        dto.restaurantId(),
                        dto.name(),
                        dto.phone(),
                        dto.address(),
                        dto.commission(),
                        dto.latitude(),
                        dto.longitude(),
                        dto.totalRevenueCommission(),
                        dto.nbrCommandesTotal(),
                        dto.logo()
                );

                List<Order> allOrders = completedOrdersByRestaurantUseCase.getAllOrdersForRestaurant(restaurantId);
                List<Order> completedOrders = completedOrdersByRestaurantUseCase.getCompletedOrdersByRestaurant(restaurantId);

                if (allOrders.isEmpty()) continue;

                double totalRevenue = allOrders.stream().mapToDouble(Order::getTotal).sum();
                Map<String, Double> monthlyCommission = allCompletedOrdersUseCase.getMonthlyCommissionRevenue(completedOrders);

                // Step 2: Generate individual PDF
                ByteArrayInputStream singlePdf = invoiceStylePdfGenerator.generateInvoiceStyleFinancialReport(
                        restaurantInfo.getName(),
                        restaurantInfo.getCommission(),
                        allOrders.size(),
                        completedOrders.size(),
                        totalRevenue,

                        monthlyCommission
                );

                // Step 3: Append to master document
                PdfReader reader = new PdfReader(singlePdf);
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    copy.addPage(copy.getImportedPage(reader, i));
                }
                reader.close();
            }

            document.close();
            return new ByteArrayInputStream(mergedOutput.toByteArray());

        } catch (Exception e) {
            throw new FinancialReportGenerationException(
                    "Error generating financial report for multiple restaurants", e
            );
        }
    }
}
