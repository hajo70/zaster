package de.spricom.zaster.entities.tracking;

import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.entities.common.TrackingDateTime;
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

    @Embedded
    @AttributeOverride(name = "date", column = @Column(name="TAKEN_AT_DATE"))
    @AttributeOverride(name = "zonedDateTime", column = @Column(name="TAKEN_AT_TS"))
    private TrackingDateTime importedAt;

    private String importerName;
}
