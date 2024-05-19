package de.spricom.zaster.repository.tracking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.spricom.zaster.entities.common.TrackingDateTime;
import de.spricom.zaster.entities.settings.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountCurrencyEntity;
import de.spricom.zaster.entities.tracking.BookingEntity;
import de.spricom.zaster.entities.tracking.ImportEntity;
import de.spricom.zaster.entities.tracking.SnapshotEntity;
import de.spricom.zaster.entities.tracking.TransferEntity;
import de.spricom.zaster.repository.AccountService;
import de.spricom.zaster.repository.BookingService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class BookingServiceImpl implements BookingService {

    private final AccountService accountService;
    private final TransferRepository transferRepository;
    private final BookingRepository bookingRepository;
    private final SnapshotRepository snapshotRepository;
    private final ObjectMapper mapper;

    @Override
    public boolean addBooking(ImportEntity imported,
                              AccountCurrencyEntity accountCurrency,
                              BookingRecord bookingRecord) {
        if (bookingRepository.existsByTenantAndMd5(accountCurrency.getId(), bookingRecord.md5())) {
            return false;
        }
        var booking = new BookingEntity();
        booking.setImported(imported);
        booking.setDescription(bookingRecord.description());
        booking.setBookedAt(bookingRecord.submittedAt());
        booking.setMd5(bookingRecord.md5());
        if (!bookingRecord.details().isEmpty()) {
            try {
                booking.setMetadata(mapper.writeValueAsString(bookingRecord.details()));
            } catch (JsonProcessingException ex) {
                throw new IllegalArgumentException("Cannot convert details to JSON for " + bookingRecord, ex);
            }
        }
        booking = bookingRepository.save(booking);
        addTransfer(booking, accountCurrency, bookingRecord.bookedAt(), bookingRecord.amount());
        AccountCurrencyEntity partnerAccount = accountService.getOrCreateAccountCurrency(imported.getTenant(),
                bookingRecord.partnerCode(), bookingRecord.partnerName(), accountCurrency.getCurrency());
        addTransfer(booking, partnerAccount, bookingRecord.bookedAt(), bookingRecord.amount().negate());
        return true;
    }

    @Override
    public boolean addSnapshot(ImportEntity imported,
                               AccountCurrencyEntity accountCurrency,
                               SnapshotRecord snapshotRecord) {
        var snapshot = new SnapshotEntity();
        snapshot.setImported(imported);
        snapshot.setAccountCurrency(accountCurrency);
        snapshot.setTakenAt(snapshotRecord.takenAt());
        snapshot.setBalance(snapshotRecord.balance());
        snapshotRepository.save(snapshot);
        return true;
    }

    private void addTransfer(BookingEntity booking,
                             AccountCurrencyEntity accountCurrency,
                             TrackingDateTime bookedAt,
                             BigDecimal amount) {
        var transfer = new TransferEntity();
        transfer.setBooking(booking);
        transfer.setAccountCurrency(accountCurrency);
        transfer.setTransferredAt(bookedAt);
        transfer.setAmount(amount);
        transferRepository.save(transfer);
    }

    @Override
    public List<SnapshotEntity> loadAllSnapshots(TenantEntity tenant) {
        return snapshotRepository.findAllByTenant(tenant.getId());
    }

    @Override
    public List<BookingEntity> loadAllBookings(TenantEntity tenant) {
        return bookingRepository.findAllByTenant(tenant.getId());
    }

    @Override
    public BookingEntity loadBooking(String bookingId) {
        return bookingRepository.loadBookingCompletely(bookingId);
    }

    @Override
    public BookingEntity saveBooking(BookingEntity booking) {
        if (booking.getTransfers() != null) {
            booking.getTransfers().forEach(tf -> tf.setBooking(booking));
        }
        return bookingRepository.save(booking);
    }

    @Override
    public void deleteBooking(BookingEntity booking) {
        bookingRepository.delete(booking);
    }

    @Override
    public TransferEntity saveTransfer(TransferEntity transfer) {
        return transferRepository.save(transfer);
    }

    @Override
    public void deleteTransfer(TransferEntity transfer) {
        transferRepository.delete(transfer);
    }
}
