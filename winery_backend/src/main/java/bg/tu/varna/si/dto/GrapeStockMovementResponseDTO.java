package bg.tu.varna.si.dto;

public class GrapeStockMovementResponseDTO {
    public Long id;

    public Long varietyId;
    public String varietyName;
    public String category;

    public double quantityKg;
    public String movementType;
    public String createdAt;

    public Long createdById;
    public String createdByFullName;
}
