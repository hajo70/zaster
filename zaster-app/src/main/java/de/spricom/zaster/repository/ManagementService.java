package de.spricom.zaster.repository;

import de.spricom.zaster.entities.managment.ApplicationUserEntity;
import de.spricom.zaster.entities.managment.TenantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface ManagementService {
    ApplicationUserEntity createTenant(ApplicationUserEntity user);

    TenantEntity updateTenant(TenantEntity tenant);

    void deleteTenant(String tenantId);

    Optional<ApplicationUserEntity> getUser(String id);

    Optional<ApplicationUserEntity> findByUsername(String username);

    boolean existsUsername(String username);

    ApplicationUserEntity saveUser(ApplicationUserEntity entity);

    void deleteUser(String id);

    Page<ApplicationUserEntity> listUser(Pageable pageable);

    Page<ApplicationUserEntity> listUser(Pageable pageable, Specification<ApplicationUserEntity> filter);
}
