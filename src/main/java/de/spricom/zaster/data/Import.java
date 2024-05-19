package de.spricom.zaster.data;

import de.spricom.zaster.entities.common.TrackingDateTime;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "IMPORT")
public class Import extends AbstractEntity {

    @Embedded
    private TrackingDateTime importedAt;

    private String importerName;
}
