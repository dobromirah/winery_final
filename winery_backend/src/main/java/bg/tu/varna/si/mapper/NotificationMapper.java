package bg.tu.varna.si.mapper;

import bg.tu.varna.si.dto.NotificationCreateDTO;
import bg.tu.varna.si.dto.NotificationResponseDTO;
import bg.tu.varna.si.model.Notification;

public class NotificationMapper {

    public static NotificationResponseDTO toDTO(Notification entity) {
        NotificationResponseDTO dto = new NotificationResponseDTO();

        dto.id = entity.id;
        dto.type = entity.type;
        dto.resourceType = entity.resourceType;
        dto.resourceId = entity.resourceId;
        dto.level = entity.level;
        dto.message = entity.message;
        dto.createdAt = entity.createdAt != null ? entity.createdAt.toString() : null;
        dto.isRead = entity.isRead;

        return dto;
    }

    public static Notification fromCreateDTO(NotificationCreateDTO dto) {
        Notification entity = new Notification();
        entity.type = dto.type;
        entity.resourceType = dto.resourceType;
        entity.resourceId = dto.resourceId;
        entity.level = dto.level;
        entity.message = dto.message;
        entity.createdAt = java.time.LocalDateTime.now();
        entity.isRead = false;
        return entity;
    }
}
