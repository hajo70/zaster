package de.spricom.zaster.repository.management;

import de.spricom.zaster.entities.managment.ApplicationUserEntity;
import de.spricom.zaster.entities.managment.ApplicationUserEntity_;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.repository.ManagementService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class ManagementServiceImpl implements ManagementService {

    private final ApplicationUserRepository userRepository;
    private final TenantRepository tenantRepository;

    @Override
    public ApplicationUserEntity createTenant(ApplicationUserEntity user) {
        var tenant = tenantRepository.save(user.getTenant());
        user.setTenant(tenant);
        return saveUser(user);
    }

    @Override
    public TenantEntity updateTenant(TenantEntity tenant) {
        return tenantRepository.save(tenant);
    }

    @Override
    public void deleteTenant(String tenantId) {
        // TODO: delete all dependencies
        tenantRepository.deleteById(tenantId);
    }

    @Override
    public Optional<ApplicationUserEntity> getUser(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<ApplicationUserEntity> findByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    @Override
    public boolean existsUsername(String username) {
        return userRepository.exists((Specification<ApplicationUserEntity>) (root, query, builder)
                -> builder.equal(root.get(ApplicationUserEntity_.username), username));
    }

    @Override
    public ApplicationUserEntity saveUser(ApplicationUserEntity entity) {
        return userRepository.save(entity);
    }

    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public Page<ApplicationUserEntity> listUser(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<ApplicationUserEntity> listUser(Pageable pageable, Specification<ApplicationUserEntity> filter) {
        return userRepository.findAll(filter, pageable);
    }
}
