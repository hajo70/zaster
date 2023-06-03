package de.spricom.zaster.repository.management;

import de.spricom.zaster.entities.managment.ApplicationUserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManagementServiceImpl {

    private final ApplicationUserRepository repository;

    public ManagementServiceImpl(ApplicationUserRepository repository) {
        this.repository = repository;
    }

    public Optional<ApplicationUserEntity> getUser(String id) {
        return repository.findById(id);
    }

    public ApplicationUserEntity updateUser(ApplicationUserEntity entity) {
        return repository.save(entity);
    }

    public void deleteUser(String id) {
        repository.deleteById(id);
    }

    public Page<ApplicationUserEntity> listUser(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<ApplicationUserEntity> listUser(Pageable pageable, Specification<ApplicationUserEntity> filter) {
        return repository.findAll(filter, pageable);
    }
}
