package bg.tu.varna.si.mapper;

import bg.tu.varna.si.dto.BottleStockMovementCreateDTO;
import bg.tu.varna.si.dto.BottleStockMovementResponseDTO;
import bg.tu.varna.si.model.*;

public class BottleStockMovementMapper {

    public static BottleStockMovementResponseDTO toDTO(BottleStockMovement entity) {
        BottleStockMovementResponseDTO dto = new BottleStockMovementResponseDTO();

        dto.id = entity.id;
        dto.bottleTypeId = entity.bottleType.id;
        dto.bottleDescription = entity.bottleType.description;

        dto.quantity = entity.quantity;
        dto.movementType = entity.movementType;
        dto.createdAt = entity.createdAt != null ? entity.createdAt.toString() : null;

        dto.createdById = entity.createdBy != null ? entity.createdBy.id : null;
        dto.createdByFullName = entity.createdBy != null ? entity.createdBy.fullName : null;

        return dto;
    }

    public static BottleStockMovement fromCreateDTO(
            BottleStockMovementCreateDTO dto,
            BottleType bottleType,
            AppUser user
    ) {
        BottleStockMovement entity = new BottleStockMovement();
        entity.bottleType = bottleType;
        entity.quantity = dto.quantity;
        entity.movementType = dto.movementType;
        entity.createdBy = user;
        return entity;
    }
}
