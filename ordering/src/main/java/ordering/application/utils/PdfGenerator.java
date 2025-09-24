package ordering.application.utils;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;
import ordering.domain.model.Order;
import ordering.application.exception.PdfGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.lowagie.text.Element.*;

@Service
public class PdfGenerator {

    private static final Logger logger = LoggerFactory.getLogger(PdfGenerator.class);
    private static final String CURRENCY_FORMAT = "%.2f CFA";

    /**
     * Generates a PDF report for a single restaurant, displaying orders and financial details.
     *
     * @param restaurantName      The name of the restaurant.
     * @param orders              The list of orders to be included in the report.
     * @param commissionPercentage The commission percentage applied by the restaurant.
     * @return A ByteArrayInputStream containing the PDF report.
     */
    public ByteArrayInputStream generateSingleRestaurantReport(
            String restaurantName,
            List<Order> orders,
            double commissionPercentage) {

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new PdfPageFooter());

            document.open();

            // Add logo (aligned to top left)
            addLogo(document);

            // Add date of PDF generation
            addCurrentDate(document);

            document.add(new Paragraph(" ")); // Empty space

            // Add restaurant title
            addTitle(document, restaurantName);

            document.add(new Paragraph(" ")); // Empty space

            // Add commission info
            addCommissionInfo(document, commissionPercentage);

            document.add(new Paragraph(" ")); // Empty space

            // Add orders table
            addOrdersTable(document, orders);

            // Add total orders and revenue
            addSummaryInfo(document, orders);

            document.close();
        } catch (Exception e) {
            throw new PdfGenerationException("Error generating PDF report for " + restaurantName, e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addLogo(Document document) {
        try {
            InputStream logoStream = new ClassPathResource("jasper/logo.png").getInputStream();
            Image logo = Image.getInstance(logoStream.readAllBytes());
            logo.scaleToFit(80, 80);
            logo.setAlignment(ALIGN_LEFT);
            logo.setAbsolutePosition(30, 770);
            document.add(logo);
        } catch (Exception e) {
            logger.warn("Logo not found or could not be loaded: {}", e.getMessage());
        }
    }

    private void addCurrentDate(Document document) throws DocumentException {
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        String currentDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        Paragraph dateParagraph = new Paragraph("Generated on: " + currentDate, dateFont);
        dateParagraph.setAlignment(ALIGN_RIGHT);
        document.add(dateParagraph);
    }

    private void addTitle(Document document, String restaurantName) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Paragraph title = new Paragraph("Orders Report for: " + restaurantName, titleFont);
        title.setAlignment(ALIGN_CENTER);
        document.add(title);
    }

    private void addCommissionInfo(Document document, double commissionPercentage) throws DocumentException {
        Font commissionFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Paragraph commissionParagraph = new Paragraph(
                "Restaurant Commission: " + commissionPercentage + "%", commissionFont);
        document.add(commissionParagraph);
    }

    private void addOrdersTable(Document document, List<Order> orders) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{3, 4, 2, 5, 2});

        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        addTableHeader(table, headFont);

        for (Order order : orders) {
            table.addCell(order.getId());
            table.addCell(order.getClient().getFirstName() + " " + order.getClient().getLastName());
            table.addCell(String.format(CURRENCY_FORMAT, order.getTotal()));
            table.addCell(order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "-");
            table.addCell(order.getOrderStatus().toString());
        }

        document.add(table);
    }

    private void addTableHeader(PdfPTable table, Font headFont) {
        String[] headers = {"Order ID", "Client Name", "Total", "Delivery Address", "Status"};
        for (String col : headers) {
            PdfPCell hcell = new PdfPCell(new Phrase(col, headFont));
            styleHeader(hcell);
            table.addCell(hcell);
        }
    }

    private void addSummaryInfo(Document document, List<Order> orders) throws DocumentException {
        Font commissionFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        int totalOrders = orders.size();
        Paragraph totalOrdersParagraph = new Paragraph("Total Orders: " + totalOrders, commissionFont);
        document.add(totalOrdersParagraph);

        double completedOrdersRevenue = orders.stream()
                .filter(order -> order.getOrderStatus().toString().equalsIgnoreCase("COMPLETED"))
                .mapToDouble(Order::getTotal)
                .sum();

        Paragraph completedRevenueParagraph = new Paragraph(
                "Revenue from Completed Orders: " + String.format(CURRENCY_FORMAT, completedOrdersRevenue), commissionFont);
        document.add(completedRevenueParagraph);
    }

    private void styleHeader(PdfPCell header) {
        header.setBackgroundColor(Color.LIGHT_GRAY);
        header.setHorizontalAlignment(ALIGN_CENTER);
        header.setVerticalAlignment(ALIGN_MIDDLE);
        header.setPadding(5);
    }

    // Inner class to add page numbers
    static class PdfPageFooter extends PdfPageEventHelper {
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContent();
            Phrase footer = new Phrase("Page " + writer.getPageNumber(), footerFont);
            ColumnText.showTextAligned(canvas, ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
        }
    }
}
