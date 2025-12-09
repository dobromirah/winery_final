package bg.tu.varna.si.mapper;

import bg.tu.varna.si.dto.BottledWineCreateDTO;
import bg.tu.varna.si.dto.BottledWineResponseDTO;
import bg.tu.varna.si.model.BottledWine;
import bg.tu.varna.si.model.BottleType;
import bg.tu.varna.si.model.WineBatch;

public class BottledWineMapper {

    public static BottledWineResponseDTO toDTO(BottledWine entity) {
        BottledWineResponseDTO dto = new BottledWineResponseDTO();
        dto.id = entity.id;

        dto.batchId = entity.batch.id;

        dto.bottleTypeId = entity.bottleType.id;
        dto.bottleDescription = entity.bottleType.description;

        dto.quantityBottles = entity.quantityBottles;

        return dto;
    }

    public static BottledWine fromCreateDTO(BottledWineCreateDTO dto,
                                            WineBatch batch,
                                            BottleType bottleType) {
        BottledWine entity = new BottledWine();
        entity.batch = batch;
        entity.bottleType = bottleType;
        entity.quantityBottles = dto.quantityBottles;
        return entity;
    }
}
