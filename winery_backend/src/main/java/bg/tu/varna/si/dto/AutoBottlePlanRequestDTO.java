package bg.tu.varna.si.dto;

import java.util.List;

public class AutoBottlePlanRequestDTO {
    public Long batchId;
    public Long preferredBottleTypeId; //750мл
    public List<Long> allowedBottleTypeIds;
}
