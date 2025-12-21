package bg.tu.varna.si.mapper;

import bg.tu.varna.si.dto.*;
import bg.tu.varna.si.model.AppUser;

public class AppUserMapper {

    public static AppUserResponseDTO toDTO(AppUser entity) {
        AppUserResponseDTO dto = new AppUserResponseDTO();
        dto.id = entity.id;
        dto.keycloakId = entity.keycloakId;
        dto.fullName = entity.fullName;
        dto.role = entity.role;
        return dto;
    }

    public static AppUser fromCreateDTO(AppUserCreateDTO dto) {
        AppUser entity = new AppUser();
        entity.keycloakId = dto.keycloakId;
        entity.fullName = dto.fullName;
        entity.role = dto.role;
        return entity;
    }
}
