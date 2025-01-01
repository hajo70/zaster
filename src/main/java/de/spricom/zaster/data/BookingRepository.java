package de.spricom.zaster.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String>, JpaSpecificationExecutor<Booking> {

    @Query("""
            select case when count(*) > 0 then true else false end
            from Booking b
            where b.md5 = :md5 and b.id != :accountCurrencyId
            """)
    boolean existsByMd5(@Param("accountCurrencyId") String accountCurrencyId, @Param("md5") String md5);

    @Query("""
            from Booking b
            left join fetch b.transfers t
            left join fetch t.accountCurrency ac
            left join fetch ac.account
            left join fetch ac.currency
            left join fetch b.imported
            where b.id = :bookingId
            """)
    Booking loadBookingCompletely(@Param("bookingId") String bookingId);

    @Query("""
            from Booking b
            left join fetch b.transfers t
            """)
    List<Booking> loadBookingsWithTransfers();
}
