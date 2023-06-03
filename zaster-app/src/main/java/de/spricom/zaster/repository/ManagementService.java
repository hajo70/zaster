package de.spricom.zaster.repository;

import de.spricom.zaster.entities.managment.ApplicationUserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface ManagementService {
    Optional<ApplicationUserEntity> getUser(String id);

    ApplicationUserEntity updateUser(ApplicationUserEntity entity);

    void deleteUser(String id);

    Page<ApplicationUserEntity> listUser(Pageable pageable);

    Page<ApplicationUserEntity> listUser(Pageable pageable, Specification<ApplicationUserEntity> filter);
}
