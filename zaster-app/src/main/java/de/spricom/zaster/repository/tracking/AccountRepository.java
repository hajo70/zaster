package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.tracking.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, String> {
}
