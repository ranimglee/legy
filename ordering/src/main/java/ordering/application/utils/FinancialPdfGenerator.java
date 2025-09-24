package ordering.application.utils;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import ordering.application.exception.FinancialReportGenerationException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service
public class FinancialPdfGenerator {
    private static final String CURRENCY_FORMAT = "%.2f CFA";

    public ByteArrayInputStream generateInvoiceStyleFinancialReport(
            String restaurantName,
            double commissionPercentage,
            int totalOrders,
            int completedOrders,
            double commissionRevenue,
            Map<String, Double> monthlyCommissionMap
    ) {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new PdfPageFooter());
            document.open();

            // ================= HEADER =================
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new int[]{2, 4});

            headerTable.addCell(createLogoCell());



            PdfPCell titleCell = new PdfPCell();
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            titleCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            headerTable.addCell(titleCell);
            document.add(headerTable);

            document.add(Chunk.NEWLINE);

            // ================= METADATA =================
            PdfPTable metaTable = new PdfPTable(3);
            metaTable.setWidthPercentage(100);
            metaTable.setWidths(new float[]{3, 3, 3});

            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            metaTable.addCell(getMetaCell("Report For:", labelFont));
            metaTable.addCell(getMetaCell("Date Issued:", labelFont));
            metaTable.addCell(getMetaCell("Commission Rate:", labelFont));

            metaTable.addCell(getMetaCell(restaurantName, valueFont));
            metaTable.addCell(getMetaCell(new SimpleDateFormat("dd MMM yyyy").format(new Date()), valueFont));
            metaTable.addCell(getMetaCell(commissionPercentage + "%", valueFont));

            document.add(metaTable);
            document.add(Chunk.NEWLINE);

            LineSeparator line = new LineSeparator();
            line.setLineColor(new Color(200, 200, 200));
            document.add(line);
            document.add(Chunk.NEWLINE);

            // ================= SUMMARY =================
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            document.add(new Paragraph("Financial Summary:", sectionFont));
            document.add(Chunk.NEWLINE);

            PdfPTable summaryTable = new PdfPTable(4);
            summaryTable.setWidthPercentage(100);
            summaryTable.setWidths(new float[]{4, 2, 2, 2});
            addSummaryHeader(summaryTable);

            addSummaryRow(summaryTable, "Total Orders", String.valueOf(totalOrders), "—", "—");
            addSummaryRow(summaryTable, "Completed Orders", String.valueOf(completedOrders), "—", "—");
            addSummaryRow(summaryTable, "Commission Revenue", "—", commissionPercentage + "%", String.format(CURRENCY_FORMAT, commissionRevenue));

            // Highlighted TOTAL
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL COMMISSION", totalFont));
            totalLabel.setColspan(3);
            totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalLabel.setBackgroundColor(new Color(255, 204, 102));
            totalLabel.setPadding(6);

            PdfPCell totalValue = new PdfPCell(new Phrase(String.format(CURRENCY_FORMAT, commissionRevenue), totalFont));
            totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValue.setPadding(6);
            summaryTable.addCell(totalLabel);
            summaryTable.addCell(totalValue);

            document.add(summaryTable);
            document.add(Chunk.NEWLINE);

            // ================= MONTHLY BREAKDOWN =================
            document.add(new Paragraph("Monthly Breakdown:", sectionFont));
            document.add(Chunk.NEWLINE);

            if (monthlyCommissionMap.isEmpty()) {
                document.add(new Paragraph("No monthly data available.", valueFont));
            } else {
                PdfPTable monthTable = new PdfPTable(2);
                monthTable.setWidthPercentage(60);
                monthTable.setWidths(new float[]{3, 2});
                addMonthlyHeader(monthTable);

                for (Map.Entry<String, Double> entry : monthlyCommissionMap.entrySet()) {
                    monthTable.addCell(getBodyCell(entry.getKey(), Element.ALIGN_LEFT));
                    monthTable.addCell(getBodyCell(String.format(CURRENCY_FORMAT, entry.getValue()), Element.ALIGN_RIGHT));
                }
                document.add(monthTable);
            }

            document.close();
        } catch (Exception e) {
            throw new FinancialReportGenerationException(
                    "Error generating financial report PDF for restaurant: " + restaurantName, e
            );
        }


        return new ByteArrayInputStream(out.toByteArray());
    }

    // =================== HELPERS =======================

    private PdfPCell getMetaCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        return cell;
    }
    private PdfPCell createLogoCell() {
        try {
            InputStream logoStream = new ClassPathResource("jasper/logo.png").getInputStream();
            Image logo = Image.getInstance(logoStream.readAllBytes());
            logo.scaleToFit(80, 80);
            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setBorder(Rectangle.NO_BORDER);
            return logoCell;
        } catch (Exception e) {
            PdfPCell emptyLogo = new PdfPCell(new Phrase(""));
            emptyLogo.setBorder(Rectangle.NO_BORDER);
            return emptyLogo;
        }
    }

    private void addSummaryHeader(PdfPTable table) {
        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        table.addCell(getHeaderCell("Description", headFont));
        table.addCell(getHeaderCell("Qty", headFont));
        table.addCell(getHeaderCell("Rate", headFont));
        table.addCell(getHeaderCell("Amount", headFont));
    }

    private void addSummaryRow(PdfPTable table, String desc, String qty, String rate, String amount) {
        table.addCell(getBodyCell(desc, Element.ALIGN_LEFT));
        table.addCell(getBodyCell(qty, Element.ALIGN_RIGHT));
        table.addCell(getBodyCell(rate, Element.ALIGN_RIGHT));
        table.addCell(getBodyCell(amount, Element.ALIGN_RIGHT));
    }

    private void addMonthlyHeader(PdfPTable table) {
        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        table.addCell(getHeaderCell("Month", headFont));
        table.addCell(getHeaderCell("Commission", headFont));
    }

    private PdfPCell getHeaderCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(255, 153, 0));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        return cell;
    }

    private PdfPCell getBodyCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5);
        return cell;
    }

    static class PdfPageFooter extends PdfPageEventHelper {
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContent();
            Phrase footer = new Phrase("Page " + writer.getPageNumber() + "  |  © 2025 Legy – www.legy.sn", footerFont);
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
        }
    }
}
