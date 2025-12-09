package bg.tu.varna.si.mapper;

import bg.tu.varna.si.dto.WineBatchGrapeUsageCreateDTO;
import bg.tu.varna.si.dto.WineBatchGrapeUsageResponseDTO;
import bg.tu.varna.si.model.GrapeVariety;
import bg.tu.varna.si.model.WineBatch;
import bg.tu.varna.si.model.WineBatchGrapeUsage;

public class WineBatchGrapeUsageMapper {

    public static WineBatchGrapeUsageResponseDTO toDTO(WineBatchGrapeUsage entity) {
        WineBatchGrapeUsageResponseDTO dto = new WineBatchGrapeUsageResponseDTO();
        dto.id = entity.id;

        dto.batchId = entity.batch.id;

        dto.varietyId = entity.variety.id;
        dto.varietyName = entity.variety.name;

        dto.quantityKg = entity.quantityKg;

        return dto;
    }

    public static WineBatchGrapeUsage fromCreateDTO(WineBatchGrapeUsageCreateDTO dto,
                                                    WineBatch batch,
                                                    GrapeVariety variety) {
        WineBatchGrapeUsage entity = new WineBatchGrapeUsage();
        entity.batch = batch;
        entity.variety = variety;
        entity.quantityKg = dto.quantityKg;
        return entity;
    }
}
