package bg.tu.varna.si.dto;

import java.util.List;

public class AutoBottlePlanRequestDTO {
    public Long batchId;

    // ако е зададено → приоритет (пример: 750ml type id)
    public Long preferredBottleTypeId;

    // ако е зададено → позволени типове (например само 750ml)
    public List<Long> allowedBottleTypeIds;
}
