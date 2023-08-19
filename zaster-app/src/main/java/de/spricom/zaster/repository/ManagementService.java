package de.spricom.zaster.repository;

import de.spricom.zaster.entities.settings.TenantEntity;
import de.spricom.zaster.entities.settings.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface ManagementService {
    UserEntity createTenant(UserEntity user);

    TenantEntity updateTenant(TenantEntity tenant);

    void deleteTenant(String tenantId);

    Optional<UserEntity> getUser(String id);

    Optional<UserEntity> findByUsername(String username);

    boolean existsUsername(String username);

    UserEntity saveUser(UserEntity entity);

    void deleteUser(String id);

    Page<UserEntity> listUser(Pageable pageable);

    Page<UserEntity> listUser(Pageable pageable, Specification<UserEntity> filter);
}
