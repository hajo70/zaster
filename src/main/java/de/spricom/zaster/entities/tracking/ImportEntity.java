package de.spricom.zaster.entities.tracking;

import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.entities.common.TrackingDateTime;
import de.spricom.zaster.entities.settings.TenantEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    private TrackingDateTime importedAt;

    private String importerName;
}
