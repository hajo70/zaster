package de.spricom.zaster.repository.settings;

import de.spricom.zaster.entities.settings.TenantEntity;
import de.spricom.zaster.entities.settings.UserEntity;
import de.spricom.zaster.entities.settings.UserEntity_;
import de.spricom.zaster.repository.SettingsService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
@Log4j2
public class SettingsServiceImpl implements SettingsService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    @Override
    public UserEntity createTenant(UserEntity user) {
        var tenant = tenantRepository.save(user.getTenant());
        user.setTenant(tenant);
        return saveUser(user);
    }

    @Override
    public TenantEntity updateTenant(TenantEntity tenant) {
        log.info("updating tenant to {}", tenant);
        return tenantRepository.save(tenant);
    }

    @Override
    public void deleteTenant(String tenantId) {
        // TODO: delete all dependencies
        tenantRepository.deleteById(tenantId);
    }

    @Override
    public Optional<UserEntity> getUser(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    @Override
    public boolean existsUsername(String username) {
        return userRepository.exists((Specification<UserEntity>) (root, query, builder)
                -> builder.equal(root.get(UserEntity_.username), username));
    }

    @Override
    public UserEntity saveUser(UserEntity entity) {
        return userRepository.save(entity);
    }

    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public Page<UserEntity> listUser(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<UserEntity> listUser(Pageable pageable, Specification<UserEntity> filter) {
        return userRepository.findAll(filter, pageable);
    }
}
