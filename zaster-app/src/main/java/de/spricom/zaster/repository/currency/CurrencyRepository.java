package de.spricom.zaster.repository.currency;

import de.spricom.zaster.entities.currency.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<CurrencyEntity, String> {
}
