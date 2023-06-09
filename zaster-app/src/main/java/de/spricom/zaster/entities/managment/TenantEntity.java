package de.spricom.zaster.entities.managment;

import de.spricom.zaster.entities.common.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "TENANT")
public class TenantEntity extends AbstractEntity {

    private String name;
}
