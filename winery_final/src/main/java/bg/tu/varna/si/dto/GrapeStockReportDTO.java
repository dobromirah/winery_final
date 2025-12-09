package bg.tu.varna.si.dto;

public class GrapeStockReportDTO {
    public Long varietyId;
    public String varietyName;
    public String category;

    public double currentKg;
    public double criticalMinKg;

    public boolean belowMinimum;
}
