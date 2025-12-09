package bg.tu.varna.si.dto;

import java.util.List;

public class WineBatchResponseDTO {
    public Long id;

    public Long wineTypeId;
    public String wineTypeName;

    public double plannedLiters;
    public double producedLiters;

    public String createdAt;

    public Integer createdById;
    public String createdByFullName;

    public List<WineBatchGrapeUsageDTO> grapeUsage;
}
