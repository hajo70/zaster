package de.spricom.zaster.entities.settings;

import de.spricom.zaster.entities.common.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZoneId;
import java.util.Locale;

@Getter
@Setter
@ToString
@Entity
@Table(name = "TENANT")
public class TenantEntity extends AbstractEntity {

    private String name;
    private Locale locale;
    private ZoneId timezone;

}
