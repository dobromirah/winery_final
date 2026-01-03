package bg.tu.varna.si.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class WineBatch extends PanacheEntity {

    @ManyToOne
    public WineType wineType;

    public double plannedLiters;
    public double producedLiters;

    public LocalDateTime createdAt;

    @ManyToOne
    public AppUser createdBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public WineBatchStatus status = WineBatchStatus.PLANNED;

    public double bottledLiters;
}
