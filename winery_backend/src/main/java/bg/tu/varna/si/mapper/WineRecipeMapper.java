package bg.tu.varna.si.mapper;

import bg.tu.varna.si.dto.WineRecipeCreateDTO;
import bg.tu.varna.si.dto.WineRecipeResponseDTO;
import bg.tu.varna.si.model.GrapeVariety;
import bg.tu.varna.si.model.WineRecipe;
import bg.tu.varna.si.model.WineType;

public class WineRecipeMapper {

    public static WineRecipeResponseDTO toDTO(WineRecipe entity) {
        WineRecipeResponseDTO dto = new WineRecipeResponseDTO();
//        dto.id = entity.id;

        dto.wineTypeId = entity.wineType.id;
        dto.wineTypeName = entity.wineType.name;

        dto.grapeVarietyId = entity.grapeVariety.id;
        dto.grapeVarietyName = entity.grapeVariety.name;

        dto.kgPerLiter = entity.kgPerLiter;

        return dto;
    }

    public static WineRecipe fromCreateDTO(WineRecipeCreateDTO dto,
                                           WineType wineType,
                                           GrapeVariety grapeVariety) {
        WineRecipe entity = new WineRecipe();
        entity.wineType = wineType;
        entity.grapeVariety = grapeVariety;
        entity.kgPerLiter = dto.kgPerLiter;
        return entity;
    }
}
