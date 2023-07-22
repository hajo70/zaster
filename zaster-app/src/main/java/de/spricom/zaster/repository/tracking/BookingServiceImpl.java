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
    private final BookingRepository bookingRepository;
    private final TransactionRepository transactionRepository;
    private final SnapshotRepository snapshotRepository;
    private final ObjectMapper mapper;

    @Override
    public TransactionEntity createTransaction(TransactionEntity tx) {
        var txSaved = transactionRepository.save(tx);
        HashSet<BookingEntity> bookingsSaved = new HashSet<>(tx.getBookings().size());
        for (BookingEntity booking : tx.getBookings()) {
            booking.setTransaction(txSaved);
            bookingsSaved.add(bookingRepository.save(booking));
        }
        txSaved.setBookings(bookingsSaved);
        return txSaved;
    }

    @Override
    public boolean addTransaction(ImportEntity imported, AccountEntity account, BookingRecord bookingRecord) {
        if (transactionRepository.existsByTenantAndMd5(account.getId(), bookingRecord.md5())) {
            return false;
        }
        var tx = new TransactionEntity();
        tx.setImported(imported);
        tx.setDescription(bookingRecord.description());
        tx.setSubmittedAt(bookingRecord.submittedAt());
        tx.setMd5(bookingRecord.md5());
        if (!bookingRecord.details().isEmpty()) {
            try {
                tx.setMetadata(mapper.writeValueAsString(bookingRecord.details()));
            } catch (JsonProcessingException ex) {
                throw new IllegalArgumentException("Cannot convert details to JSON for " + bookingRecord, ex);
            }
        }
        tx = transactionRepository.save(tx);
        addBooking(tx, account, bookingRecord.bookedAt(), bookingRecord.amount());
        AccountEntity partnerAccount = accountService.getOrCreateAccount(imported.getTenant(),
                bookingRecord.partnerCode(), bookingRecord.partnerName(), account.getCurrency());
        addBooking(tx, partnerAccount, bookingRecord.bookedAt(), bookingRecord.amount().negate());
        return true;
    }

    @Override
    public boolean addSnapshot(ImportEntity imported, AccountEntity account, SnapshotRecord snapshotRecord) {
        var snapshot = new SnapshotEntity();
        snapshot.setImported(imported);
        snapshot.setAccount(account);
        snapshot.setTakenAt(snapshotRecord.takenAt());
        snapshot.setBalance(snapshotRecord.balance());
        snapshotRepository.save(snapshot);
        return true;
    }

    private void addBooking(TransactionEntity tx, AccountEntity account, TrackingDateTime bookedAt, BigDecimal amount) {
        var booking = new BookingEntity();
        booking.setTransaction(tx);
        booking.setAccount(account);
        booking.setBookedAt(bookedAt);
        booking.setAmount(amount);
        bookingRepository.save(booking);
    }
}
