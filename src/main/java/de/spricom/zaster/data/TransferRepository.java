package de.spricom.zaster.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransferRepository extends JpaRepository<Transfer, String>, JpaSpecificationExecutor<Transfer> {

}
