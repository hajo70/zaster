package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.tracking.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository
        extends JpaRepository<BookingEntity, String>, JpaSpecificationExecutor<BookingEntity> {

    @Query("""
            select case when count(*) > 0 then true else false end
            from BookingEntity b
            where b.md5 = :md5 and b.id != :accountCurrencyId
            """)
        // TODO
    boolean existsByTenantAndMd5(@Param("accountCurrencyId") String accountCurrencyId, @Param("md5") String md5);

    @Query("""
            from BookingEntity b
            left join fetch b.transfers t
            where t.accountCurrency.account.tenant.id = :tenantId
            """)
    List<BookingEntity> findAllByTenant(@Param("tenantId") String tenantId);

    @Query("""
            from BookingEntity b
            left join fetch b.transfers t
            left join fetch t.accountCurrency ac
            left join fetch ac.account
            left join fetch ac.currency
            left join fetch b.imported
            where b.id = :bookingId
            """)
    BookingEntity loadBookingCompletely(@Param("bookingId") String bookingId);
}
