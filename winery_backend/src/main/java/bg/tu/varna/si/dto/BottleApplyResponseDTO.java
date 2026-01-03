package bg.tu.varna.si.dto;

import java.util.List;

public class BottleApplyResponseDTO {
    public Long batchId;
    public double bottledNowLiters;
    public double totalBottledLiters;
    public double leftoverLiters; // remaining after bottling (produced - totalBottled)
    public List<BottlePlanItemDTO> items;
}
