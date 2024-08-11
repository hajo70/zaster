package de.spricom.zaster.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "FILE_SOURCE")
public class FileSource extends AbstractEntity {

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Import imported;

    private String filename;

    private Integer totalCount;

    @Column(length = 63)
    private String md5;
}
