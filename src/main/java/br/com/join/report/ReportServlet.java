package br.com.join.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperRunManager;

@WebServlet("/generatePessoaReport")
@Slf4j
public class ReportServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String REPORT_NAME = "pessoasReport";
	private static final String REPORT_DIR = "reports/";
	private static final String REPORT_FILE = REPORT_DIR + REPORT_NAME + ".jrxml";
	private static final String SUBREPORT_REPORT_FILE = REPORT_DIR + REPORT_NAME + ".jrxml";
	private static final String COMPILED_FILE = REPORT_DIR + REPORT_NAME + ".jasper";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		process(request, response);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		process(request, response);
	}

	private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			log.info("Generating report...");
			
			Instant processingInstant = Instant.now();
			
			ServletContext context = request.getServletContext();

			JasperCompileManager.compileReportToFile(context.getRealPath(REPORT_FILE));
			JasperCompileManager.compileReportToFile(context.getRealPath(SUBREPORT_REPORT_FILE));

			Map<String, Object> parametros = new HashMap<String, Object>();
			Connection connection = new ConnectionFactory().getConnection();

			final ByteArrayOutputStream baos = new ByteArrayOutputStream();

			baos.write(JasperRunManager.runReportToPdf(context.getRealPath(COMPILED_FILE), parametros, connection));

			baos.flush();
			baos.close();
			connection.close();

			response.reset();
			response.setHeader("Content-Disposition", "attachment; filename=pessoasReport.pdf");
			response.setContentType("application/pdf");

			response.getOutputStream().write(baos.toByteArray());
			response.getOutputStream().flush();
			
			long durationInMs = Duration.between(processingInstant, Instant.now()).toMillis();
			
			log.info("Report generated in {} ms", durationInMs);

		} catch (Exception e) {
			log.error("Error generating report", e);
			throw new RuntimeException(e);
		}
	}

}
