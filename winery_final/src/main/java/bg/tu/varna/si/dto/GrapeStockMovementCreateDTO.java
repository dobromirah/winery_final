package bg.tu.varna.si.dto;

public class GrapeStockMovementCreateDTO {
    public Long varietyId;
    public double quantityKg;    // +IN, -OUT
    public String movementType;  // IN, OUT, ADJUSTMENT
    public Long createdById;
}
