package bg.tu.varna.si.mapper;

import bg.tu.varna.si.dto.*;
import bg.tu.varna.si.model.GrapeVariety;

public class GrapeVarietyMapper {

    public static GrapeVarietyResponseDTO toDTO(GrapeVariety entity) {
        GrapeVarietyResponseDTO dto = new GrapeVarietyResponseDTO();
        dto.id = entity.id;
        dto.name = entity.name;
        dto.category = entity.category;
        dto.yieldLitersPerKg = entity.yieldLitersPerKg;
        dto.criticalMinKg = entity.criticalMinKg;
        return dto;
    }

    public static GrapeVariety fromCreateDTO(GrapeVarietyCreateDTO dto) {
        GrapeVariety entity = new GrapeVariety();
        entity.name = dto.name;
        entity.category = dto.category;
        entity.yieldLitersPerKg = dto.yieldLitersPerKg;
        entity.criticalMinKg = dto.criticalMinKg;
        return entity;
    }
}
