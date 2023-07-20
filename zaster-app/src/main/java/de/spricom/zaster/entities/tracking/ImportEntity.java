package de.spricom.zaster.entities.tracking;

import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.entities.common.TrackingDateTime;
import de.spricom.zaster.entities.managment.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "IMPORT")
public class ImportEntity extends AbstractEntity {

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    private TenantEntity tenant;

    @Embedded
    @AttributeOverride(name = "date", column = @Column(name="IMPORTED_AT_DATE"))
    @AttributeOverride(name = "zonedDateTime", column = @Column(name="IMPORTED_AT_TS"))
    private TrackingDateTime importedAt;

    private String importerName;
}
