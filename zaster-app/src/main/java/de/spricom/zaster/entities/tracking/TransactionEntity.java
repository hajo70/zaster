package de.spricom.zaster.entities.tracking;

import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.entities.common.TrackingDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "TRANSACTION")
public class TransactionEntity extends AbstractEntity {

    @Embedded
    @AttributeOverride(name = "date", column = @Column(name="SUBMITTED_AT_DATE"))
    @AttributeOverride(name = "zonedDateTime", column = @Column(name="SUBMITTED_AT_TS"))
    private TrackingDateTime submittedAt;

    private String description;

    @Lob
    @Column(length = 65536)
    private String metadata;

    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookingEntity> bookings;
}
