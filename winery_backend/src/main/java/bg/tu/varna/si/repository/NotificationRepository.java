package bg.tu.varna.si.repository;

import bg.tu.varna.si.model.Notification;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class NotificationRepository implements PanacheRepository<Notification> {

    public List<Notification> findUnread() {
        return list("isRead", false);
    }
}
