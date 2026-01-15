package bg.tu.varna.si.dto;

import java.util.List;

public class BottleApplyResponseDTO {
    public Long batchId;
    public double bottledNowLiters;
    public double totalBottledLiters;
    public double leftoverLiters; // (produced - totalBottled)
    public List<BottlePlanItemDTO> items;
    public List<NotificationResponseDTO> notifications;
}
