package payment.application.service;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import payment.domain.model.Investment;
import payment.domain.repository.InvestmentRepository;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvestmentExportService {

    private final InvestmentRepository investmentRepository;

    public byte[] exportInvestmentsToPdf(Pageable pageable) throws JRException {
        List<Investment> data = investmentRepository.findAll(pageable).getContent();
        return generateReport("investments_template.jrxml", data, ExportFormat.PDF);
    }

    public byte[] exportInvestmentsToCsv(Pageable pageable) throws JRException {
        List<Investment> data = investmentRepository.findAll(pageable).getContent();
        return generateReport("investments_template.jrxml", data, ExportFormat.CSV);
    }

    public byte[] exportInvestmentsToExcel(Pageable pageable) throws JRException {
        List<Investment> data = investmentRepository.findAll(pageable).getContent();
        return generateReport("investments_template.jrxml", data, ExportFormat.EXCEL);
    }

    private byte[] generateReport(String templatePath, List<Investment> data, ExportFormat format) throws JRException {
        InputStream templateStream = getClass().getResourceAsStream("/jasper/" + templatePath);
        JasperReport report = JasperCompileManager.compileReport(templateStream);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Legy Investments");

        JasperPrint print = JasperFillManager.fillReport(report, parameters, dataSource);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        switch (format) {
            case PDF -> JasperExportManager.exportReportToPdfStream(print, outputStream);
            case CSV -> {
                JRCsvExporter exporter = new JRCsvExporter();
                exporter.setExporterInput(new SimpleExporterInput(print));
                exporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));
                exporter.exportReport();
            }
            case EXCEL -> {
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(print));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                exporter.exportReport();
            }
        }

        return outputStream.toByteArray();
    }

    enum ExportFormat {
        PDF, CSV, EXCEL
    }
}
