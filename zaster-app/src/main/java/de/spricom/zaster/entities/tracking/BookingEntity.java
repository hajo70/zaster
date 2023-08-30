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
@Table(name = "BOOKING")
public class BookingEntity extends AbstractEntity {

    @Embedded
    private TrackingDateTime bookedAt;

    @Column(length = 4095)
    private String description;

    @Column(length = 63)
    private String md5;

    @Lob
    @Column(length = 65535)
    private String metadata;

    @ManyToOne(fetch = FetchType.LAZY)
    private ImportEntity imported;

    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<TransferEntity> transfers;
}
