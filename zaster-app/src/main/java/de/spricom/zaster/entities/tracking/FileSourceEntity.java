package de.spricom.zaster.entities.tracking;

import de.spricom.zaster.entities.common.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "FILE_SOURCE")
public class FileSourceEntity extends AbstractEntity {

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ImportEntity imported;

    private String filename;

    private Integer totalCount;

    private Integer importedCount;

    @Column(length = 63)
    private String md5;
}
