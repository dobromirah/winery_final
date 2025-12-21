package bg.tu.varna.si.mapper;

import bg.tu.varna.si.dto.GrapeStockMovementCreateDTO;
import bg.tu.varna.si.dto.GrapeStockMovementResponseDTO;
import bg.tu.varna.si.model.AppUser;
import bg.tu.varna.si.model.GrapeStockMovement;
import bg.tu.varna.si.model.GrapeVariety;

public class GrapeStockMovementMapper {

    public static GrapeStockMovementResponseDTO toDTO(GrapeStockMovement entity) {
        GrapeStockMovementResponseDTO dto = new GrapeStockMovementResponseDTO();

        dto.id = entity.id;

        dto.varietyId = entity.variety.id;
        dto.varietyName = entity.variety.name;
        dto.category = entity.variety.category;

        dto.quantityKg = entity.quantityKg;
        dto.movementType = entity.movementType;
        dto.createdAt = entity.createdAt != null ? entity.createdAt.toString() : null;

        dto.createdById = entity.createdBy != null ? entity.createdBy.id : null;
        dto.createdByFullName = entity.createdBy != null ? entity.createdBy.fullName : null;

        return dto;
    }

    public static GrapeStockMovement fromCreateDTO(
            GrapeStockMovementCreateDTO dto,
            GrapeVariety variety,
            AppUser user
    ) {
        GrapeStockMovement entity = new GrapeStockMovement();
        entity.variety = variety;
        entity.quantityKg = dto.quantityKg;
        entity.movementType = dto.movementType;
        entity.createdAt = java.time.LocalDateTime.now();
        entity.createdBy = user;
        return entity;
    }
}
