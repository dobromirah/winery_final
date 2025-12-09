package bg.tu.varna.si.dto;

public class BottleStockMovementCreateDTO {
    public Long bottleTypeId;
    public int quantity;
    public String movementType; // IN, OUT, ADJUSTMENT
    public Long createdById;
}
