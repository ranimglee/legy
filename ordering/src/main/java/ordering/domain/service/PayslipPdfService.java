package ordering.domain.service;

import ordering.application.dto.restoPayout.RestaurantPayslipDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PayslipPdfService {
    private static final String CURRENCY_FORMAT = "%.2f €";

    public byte[] generatePayslipPdf(RestaurantPayslipDTO payslip) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            PDRectangle mediaBox = page.getMediaBox();
            float pageWidth = mediaBox.getWidth();
            float margin = 50;
            float usableWidth = pageWidth - 2 * margin;

            // Logo
            ClassPathResource logoFile = new ClassPathResource("jasper/logo.png");
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, logoFile.getInputStream().readAllBytes(), "legy-logo");
            float logoWidth = 50;
            float logoHeight = 25;
            content.drawImage(pdImage, margin, mediaBox.getHeight() - margin - logoHeight, logoWidth, logoHeight);

            // Title
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 20);
            content.newLineAtOffset(margin, mediaBox.getHeight() - margin - logoHeight - 30);
            content.showText("Fiche de Paie");
            content.endText();

            // Reference & Date
            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 12);
            content.newLineAtOffset(margin, mediaBox.getHeight() - margin - logoHeight - 60);
            content.showText("Référence: " + payslip.getReference());
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 12);
            content.newLineAtOffset(margin + 300, mediaBox.getHeight() - margin - logoHeight - 60);
            content.showText("Date de génération: " + payslip.getGeneratedAt());
            content.endText();

            // Restaurant
            float cursorY = mediaBox.getHeight() - margin - logoHeight - 100;
            float lineHeight = 15;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.newLineAtOffset(margin, cursorY);
            content.showText("Restaurant: " + payslip.getRestaurantName());
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 12);
            content.newLineAtOffset(margin, cursorY - lineHeight);
            content.showText("Date de paiement: " + payslip.getPayoutDate());
            content.endText();

            // Draw Table
            float tableY = cursorY - 3 * lineHeight;
            float rowHeight = 25;
            float tableWidth = usableWidth;
            float col1Width = tableWidth * 0.7f;

            // Draw rows and columns
            for (int i = 0; i <= 3; i++) {
                float y = tableY - i * rowHeight;
                content.moveTo(margin, y);
                content.lineTo(margin + tableWidth, y);
            }

            content.moveTo(margin, tableY);
            content.lineTo(margin, tableY - 3 * rowHeight);
            content.moveTo(margin + col1Width, tableY);
            content.lineTo(margin + col1Width, tableY - 3 * rowHeight);
            content.moveTo(margin + tableWidth, tableY);
            content.lineTo(margin + tableWidth, tableY - 3 * rowHeight);

            content.stroke();

            // Table values
            String[][] tableData = {
                    {"Chiffre d'affaires total", String.format(CURRENCY_FORMAT, payslip.getTotalRevenue())},
                    {"Commission totale", String.format(CURRENCY_FORMAT, payslip.getTotalCommission())},
                    {"Montant net", String.format(CURRENCY_FORMAT, payslip.getNetAmount())}
            };

            for (int i = 0; i < tableData.length; i++) {
                float textY = tableY - (i + 0.75f) * rowHeight;

                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(margin + 5, textY);
                content.showText(tableData[i][0]);
                content.endText();

                content.beginText();
                content.setFont(i == 2 ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(margin + col1Width + 5, textY);
                content.showText(tableData[i][1]);
                content.endText();
            }

            // Statut
            float statusY = tableY - 3 * rowHeight - 20;
            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 12);
            content.newLineAtOffset(margin, statusY);
            content.showText("Statut: " + (payslip.isPaid() ? "Payé" : "Non payé"));
            content.endText();

            // Signature & cachet placeholders
            float bottomY = 100;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 12);
            content.newLineAtOffset(margin, bottomY);
            content.showText("Signature:");
            content.endText();

            content.moveTo(margin + 70, bottomY - 2);
            content.lineTo(margin + 220, bottomY - 2);
            content.stroke();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 12);
            content.newLineAtOffset(margin + tableWidth - 120, bottomY);
            content.showText("Cachet:");
            content.endText();

            content.addRect(margin + tableWidth - 120, bottomY - 40, 80, 40);
            content.stroke();

            content.close();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

}
