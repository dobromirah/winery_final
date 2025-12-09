package bg.tu.varna.si.dto;

public class BottleStockReportDTO {
    public Long bottleTypeId;
    public int volumeMl;
    public String description;

    public int currentQty;
    public int criticalMinQty;

    public boolean belowMinimum;
}
