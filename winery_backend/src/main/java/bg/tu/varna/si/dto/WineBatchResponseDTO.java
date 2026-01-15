package bg.tu.varna.si.dto;

import java.util.List;

public class WineBatchResponseDTO {
    public Long id;
    public Long wineTypeId;
    public String wineTypeName;
    public double plannedLiters;
    public double producedLiters;
    public double bottledLiters;
    public String createdAt;
    public Integer createdById;
    public String createdByFullName;
    public String status;
    public List<WineBatchGrapeUsageDTO> grapeUsage;
    public List<NotificationResponseDTO> notifications;

}
