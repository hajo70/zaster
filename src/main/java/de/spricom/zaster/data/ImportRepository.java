package de.spricom.zaster.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ImportRepository extends JpaRepository<Import, String>, JpaSpecificationExecutor<Import> {
}
