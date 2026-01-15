package bg.tu.varna.si.dto;

import java.util.List;

public class WineTypeMaxLitersDTO {
    public Long wineTypeId;
    public Double maxLiters;
    public String limitingVarietyName;
    public List<VarietyLimitDTO> limits;
    public static class VarietyLimitDTO {
        public Long varietyId;
        public String varietyName;
        public Double availableKg;
        public Double kgPerLiter;
        public Double maxLitersForVariety;
    }
}
