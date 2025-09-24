package ordering.application.utils;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import ordering.application.dto.order.ModeratorStatsDTO;
import ordering.application.exception.PdfGenerationException;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import java.io.ByteArrayOutputStream;
@Service

public class PdfExportOrderIssueService {

    public byte[] exportModeratorStatsToPdf(List<ModeratorStatsDTO> stats) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font bodyFont = new Font(Font.HELVETICA, 12);

            Paragraph title = new Paragraph("Moderator Issue Resolution Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 2, 3, 2});

            // Header
            Stream.of("Moderator ID", "Issues Resolved", "Avg Resolution Time (hrs)", "Resolution Rate (%)")
                    .forEach(colTitle -> {
                        PdfPCell header = new PdfPCell(new Phrase(colTitle, headerFont));
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setBackgroundColor(Color.LIGHT_GRAY);
                        table.addCell(header);
                    });

            // Rows
            for (ModeratorStatsDTO stat : stats) {
                table.addCell(new Phrase(stat.moderatorId(), bodyFont));
                table.addCell(new Phrase(String.valueOf(stat.issuesResolved()), bodyFont));
                table.addCell(new Phrase(String.format("%.2f", stat.avgResolutionTimeHours()), bodyFont));
                table.addCell(new Phrase(String.format("%.2f", stat.resolutionRate()), bodyFont));
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new PdfGenerationException("Failed to generate Moderator stats PDF", e);
        }
    }
}
