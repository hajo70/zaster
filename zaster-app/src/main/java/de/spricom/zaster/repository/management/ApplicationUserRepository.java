package de.spricom.zaster.repository.management;

import de.spricom.zaster.entities.managment.ApplicationUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUserEntity, String>, JpaSpecificationExecutor<ApplicationUserEntity> {

    ApplicationUserEntity findByUsername(String username);
}
