package de.spricom.zaster.repository.tracking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.spricom.zaster.entities.common.TrackingDateTime;
import de.spricom.zaster.entities.tracking.*;
import de.spricom.zaster.repository.AccountService;
import de.spricom.zaster.repository.BookingService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;

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
    public BookingEntity createTransaction(BookingEntity tx) {
        var txSaved = bookingRepository.save(tx);
        HashSet<TransferEntity> bookingsSaved = new HashSet<>(tx.getTransfers().size());
        for (TransferEntity booking : tx.getTransfers()) {
            booking.setBooking(txSaved);
            bookingsSaved.add(transferRepository.save(booking));
        }
        txSaved.setTransfers(bookingsSaved);
        return txSaved;
    }

    @Override
    public boolean addTransaction(ImportEntity imported, AccountCurrencyEntity account, BookingRecord bookingRecord) {
        if (bookingRepository.existsByTenantAndMd5(account.getId(), bookingRecord.md5())) {
            return false;
        }
        var tx = new BookingEntity();
        tx.setImported(imported);
        tx.setDescription(bookingRecord.description());
        tx.setBookedAt(bookingRecord.submittedAt());
        tx.setMd5(bookingRecord.md5());
        if (!bookingRecord.details().isEmpty()) {
            try {
                tx.setMetadata(mapper.writeValueAsString(bookingRecord.details()));
            } catch (JsonProcessingException ex) {
                throw new IllegalArgumentException("Cannot convert details to JSON for " + bookingRecord, ex);
            }
        }
        tx = bookingRepository.save(tx);
        addBooking(tx, account, bookingRecord.bookedAt(), bookingRecord.amount());
        AccountCurrencyEntity partnerAccount = accountService.getOrCreateAccount(imported.getTenant(),
                bookingRecord.partnerCode(), bookingRecord.partnerName(), account.getCurrency());
        addBooking(tx, partnerAccount, bookingRecord.bookedAt(), bookingRecord.amount().negate());
        return true;
    }

    @Override
    public boolean addSnapshot(ImportEntity imported, AccountCurrencyEntity account, SnapshotRecord snapshotRecord) {
        var snapshot = new SnapshotEntity();
        snapshot.setImported(imported);
        snapshot.setAccount(account);
        snapshot.setTakenAt(snapshotRecord.takenAt());
        snapshot.setBalance(snapshotRecord.balance());
        snapshotRepository.save(snapshot);
        return true;
    }

    private void addBooking(BookingEntity tx, AccountCurrencyEntity account, TrackingDateTime bookedAt, BigDecimal amount) {
        var booking = new TransferEntity();
        booking.setBooking(tx);
        booking.setAccountCurrency(account);
        booking.setTransferredAt(bookedAt);
        booking.setAmount(amount);
        transferRepository.save(booking);
    }
}
