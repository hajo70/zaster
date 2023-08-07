package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.tracking.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository
        extends JpaRepository<BookingEntity, String>, JpaSpecificationExecutor<BookingEntity> {

    @Query("""
            select case when count(*) > 0 then true else false end
            from BookingEntity b"
            where b.md5 = :md5 and b.id != :accountCurrencyId
            """) // TODO
    boolean existsByTenantAndMd5(@Param("accountCurrencyId") String accountCurrencyId, @Param("md5") String md5);
}
