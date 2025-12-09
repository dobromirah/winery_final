package bg.tu.varna.si.mapper;

import bg.tu.varna.si.dto.WineBatchCreateDTO;
import bg.tu.varna.si.dto.WineBatchGrapeUsageDTO;
import bg.tu.varna.si.dto.WineBatchResponseDTO;
import bg.tu.varna.si.model.AppUser;
import bg.tu.varna.si.model.WineBatch;
import bg.tu.varna.si.model.WineBatchGrapeUsage;
import bg.tu.varna.si.model.WineType;

import java.util.List;
import java.util.stream.Collectors;

public class WineBatchMapper {

    public static WineBatchResponseDTO toDTO(
            WineBatch entity,
            List<WineBatchGrapeUsage> usages
    ) {
        WineBatchResponseDTO dto = new WineBatchResponseDTO();
        dto.id = entity.id;

        dto.wineTypeId = entity.wineType.id;
        dto.wineTypeName = entity.wineType.name;

        dto.plannedLiters = entity.plannedLiters;
        dto.producedLiters = entity.producedLiters;

        dto.createdAt = entity.createdAt != null ? entity.createdAt.toString() : null;
        dto.createdById = entity.createdBy != null ? entity.createdBy.id.intValue() : null;
        dto.createdByFullName = entity.createdBy != null ? entity.createdBy.fullName : null;

        dto.grapeUsage = usages.stream()
                .map(u -> {
                    WineBatchGrapeUsageDTO item = new WineBatchGrapeUsageDTO();
                    item.grapeVarietyId = u.variety.id;
                    item.grapeVarietyName = u.variety.name;
                    item.kgUsed = u.quantityKg;
                    return item;
                })
                .collect(Collectors.toList());

        return dto;
    }

    public static WineBatch fromCreateDTO(WineBatchCreateDTO dto,
                                          WineType wineType,
                                          AppUser user) {
        WineBatch entity = new WineBatch();
        entity.wineType = wineType;
        entity.plannedLiters = dto.plannedLiters;
        entity.producedLiters = 0.0; // to be updated later if needed
        entity.createdAt = java.time.LocalDateTime.now();
        entity.createdBy = user;
        return entity;
    }
}
