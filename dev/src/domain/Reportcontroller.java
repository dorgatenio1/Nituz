package domain;

import service.Response;
import domain.reports.Report;
import java.util.List;

public class Reportcontroller {
    private int reportCount;
    private List<Report<?>> reports;

    public Reportcontroller() {}

    public int generateReportId() { return 0; }
    public Response<Boolean> saveReport(Report<?> report) { return null; }
    public Response<Report<?>> getReportById(int reportId) { return null; }
    public Response<List<Report<?>>> getAllReports() { return null; }
}
