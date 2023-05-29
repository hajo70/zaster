package de.spricom.zaster.entities.common;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Embeddable
public class Location {

    protected Double longitude;

    protected Double latitude;
}
