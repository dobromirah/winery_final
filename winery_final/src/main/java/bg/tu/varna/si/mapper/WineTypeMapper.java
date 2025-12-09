package bg.tu.varna.si.mapper;

import bg.tu.varna.si.dto.WineTypeCreateDTO;
import bg.tu.varna.si.dto.WineTypeResponseDTO;
import bg.tu.varna.si.model.WineType;

public class WineTypeMapper {

    public static WineTypeResponseDTO toDTO(WineType entity) {
        WineTypeResponseDTO dto = new WineTypeResponseDTO();
//        dto.id = entity.id;
        dto.name = entity.name;
        dto.color = entity.color;
        dto.description = entity.description;
        return dto;
    }

    public static WineType fromCreateDTO(WineTypeCreateDTO dto) {
        WineType entity = new WineType();
        entity.name = dto.name;
        entity.color = dto.color;
        entity.description = dto.description;
        return entity;
    }
}
