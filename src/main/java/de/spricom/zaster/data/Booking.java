package de.spricom.zaster.data;

import de.spricom.zaster.entities.common.TrackingDateTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "BOOKING")
public class Booking extends AbstractEntity {

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
    private Import imported;

    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Transfer> transfers;
}
