package bg.tu.varna.si.resource;

import bg.tu.varna.si.dto.NotificationResponseDTO;
import bg.tu.varna.si.mapper.NotificationMapper;
import bg.tu.varna.si.model.Notification;
import bg.tu.varna.si.repository.NotificationRepository;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.stream.Collectors;

@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {

    @Inject
    NotificationRepository repository;

    // -------------------------------------------------------
    // GET ALL NOTIFICATIONS
    // -------------------------------------------------------
    @GET
    @RolesAllowed({"ADMIN", "WAREHOUSE_MANAGER", "OPERATOR"})
    public List<NotificationResponseDTO> listAll() {
        return repository.listAll()
                .stream()
                .map(NotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // GET ONLY UNREAD NOTIFICATIONS
    // -------------------------------------------------------
    @GET
    @Path("/unread")
    @RolesAllowed({"ADMIN", "WAREHOUSE_MANAGER", "OPERATOR"})
    public List<NotificationResponseDTO> listUnread() {
        return repository.list("isRead", false)
                .stream()
                .map(NotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // FILTER BY TYPE (GRAPE_LOW, BOTTLE_LOW, SHORTAGE)
    // -------------------------------------------------------
    @GET
    @Path("/type/{type}")
    @RolesAllowed({"ADMIN", "WAREHOUSE_MANAGER", "OPERATOR"})
    public List<NotificationResponseDTO> listByType(@PathParam("type") String type) {
        return repository.list("type", type)
                .stream()
                .map(NotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // MARK SINGLE NOTIFICATION AS READ
    // -------------------------------------------------------
    @PUT
    @Path("/{id}/read")
    @RolesAllowed({"ADMIN", "WAREHOUSE_MANAGER", "OPERATOR"})
    @Transactional
    public NotificationResponseDTO markAsRead(@PathParam("id") Long id) {

        Notification n = repository.findById(id);
        if (n == null) {
            throw new NotFoundException("Notification not found");
        }

        n.isRead = true;

        return NotificationMapper.toDTO(n);
    }

    // -------------------------------------------------------
    // DELETE NOTIFICATION (ADMIN ONLY)
    // -------------------------------------------------------
    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        boolean deleted = repository.deleteById(id);

        if (!deleted) {
            throw new NotFoundException("Notification not found");
        }
    }
}
