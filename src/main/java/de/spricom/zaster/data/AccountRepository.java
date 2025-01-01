package de.spricom.zaster.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {

    @Query("SELECT g FROM Account g " + "LEFT JOIN FETCH g.parent " + "LEFT JOIN FETCH g.currencies ")
    List<Account> findAccounts();

    Account findByAccountCode(String accountCode);
}
