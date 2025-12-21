package bg.tu.varna.si.mapper;

import bg.tu.varna.si.dto.*;
import bg.tu.varna.si.model.BottleType;

public class BottleTypeMapper {

    public static BottleTypeResponseDTO toDTO(BottleType entity) {
        BottleTypeResponseDTO dto = new BottleTypeResponseDTO();
        dto.id = entity.id;
        dto.volumeMl = entity.volumeMl;
        dto.description = entity.description;
        dto.criticalMinQty = entity.criticalMinQty;
        return dto;
    }

    public static BottleType fromCreateDTO(BottleTypeCreateDTO dto) {
        BottleType entity = new BottleType();
        entity.volumeMl = dto.volumeMl;
        entity.description = dto.description;
        entity.criticalMinQty = dto.criticalMinQty;
        return entity;
    }
}
