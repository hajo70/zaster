package de.spricom.zaster.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FileSourceRepository extends JpaRepository<FileSource, String>, JpaSpecificationExecutor<FileSource> {

    @Query("SELECT f FROM FileSource f " +
            "LEFT JOIN FETCH f.imported i " +
            "WHERE f.md5 = :md5")
    Optional<FileSource> findByMd5(@Param("md5") String md5);

    Page<FileSource> findAll(Pageable pageable);
}
