package de.spricom.zaster.repository.management;

import de.spricom.zaster.entities.managment.ApplicationUserEntity;
import de.spricom.zaster.repository.ManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ManagementServiceImpl implements ManagementService {

    private final ApplicationUserRepository repository;

    public ManagementServiceImpl(ApplicationUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ApplicationUserEntity> getUser(String id) {
        return repository.findById(id);
    }

    @Override
    public ApplicationUserEntity updateUser(ApplicationUserEntity entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteUser(String id) {
        repository.deleteById(id);
    }

    @Override
    public Page<ApplicationUserEntity> listUser(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<ApplicationUserEntity> listUser(Pageable pageable, Specification<ApplicationUserEntity> filter) {
        return repository.findAll(filter, pageable);
    }
}
