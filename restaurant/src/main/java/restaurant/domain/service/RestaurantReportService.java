package restaurant.domain.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import restaurant.domain.model.Restaurant;
import restaurant.domain.repository.RestaurantRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RestaurantReportService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantReportService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }


    /**
     * Generate a PDF of all restaurants (paginated).
     * @return PDF byte array
     */
    public byte[] generateRestaurantsReport() throws JRException {
        List<Restaurant> data = restaurantRepository.findAll();
        return generateReport("jasper/restaurantsReport.jrxml", data);
    }

    private byte[] generateReport(String templatePath, List<Restaurant> data) throws JRException {
        // Load the template file from the classpath
        InputStream templateStream = getClass().getClassLoader().getResourceAsStream(templatePath);
        if (templateStream == null) {
            throw new JRException("Jasper report template not found: " + templatePath);
        }

        // Compile the report from the .jrxml file
        JasperReport report = JasperCompileManager.compileReport(templateStream);

        // Prepare the data source for the report (JRBeanCollectionDataSource)
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

        // Parameters to pass to the report (can be customized as needed)
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Legy restaurants"); // Add any additional parameters here

        // Fill the report with data
        JasperPrint print = JasperFillManager.fillReport(report, parameters, dataSource);

        // Export the filled report to a PDF byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(print, outputStream);

        // Return the generated PDF as byte array
        return outputStream.toByteArray();
    }
}
