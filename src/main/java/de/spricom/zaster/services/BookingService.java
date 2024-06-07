package de.spricom.zaster.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.spricom.zaster.data.AccountCurrency;
import de.spricom.zaster.data.Booking;
import de.spricom.zaster.data.BookingRepository;
import de.spricom.zaster.data.Import;
import de.spricom.zaster.data.Snapshot;
import de.spricom.zaster.data.SnapshotRepository;
import de.spricom.zaster.data.TrackingDateTime;
import de.spricom.zaster.data.Transfer;
import de.spricom.zaster.data.TransferRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Log4j2
public class BookingService {

    public record BookingRecord(
            TrackingDateTime submittedAt,
            TrackingDateTime bookedAt,
            String partnerCode,
            String partnerName,
            String description,
            BigDecimal amount,
            Map<String, String> details,
            String md5
    ) {}

    public record SnapshotRecord(
            TrackingDateTime takenAt,
            BigDecimal balance
    ) {}

    private final AccountService accountService;
    private final TransferRepository transferRepository;
    private final BookingRepository bookingRepository;
    private final SnapshotRepository snapshotRepository;
    private final ObjectMapper mapper;

    public Page<Booking> list(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    public Page<Booking> list(Pageable pageable, Specification<Booking> filter) {
        return bookingRepository.findAll(filter, pageable);
    }

    public boolean addBooking(Import imported,
                              AccountCurrency accountCurrency,
                              BookingRecord bookingRecord) {
        if (bookingRepository.existsByMd5(accountCurrency.getId(), bookingRecord.md5())) {
            return false;
        }
        var booking = new Booking();
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
        addTransfer(booking, 1, accountCurrency, bookingRecord);
        AccountCurrency partnerAccount = accountService.getOrCreateAccountCurrency(
                bookingRecord.partnerCode(), bookingRecord.partnerName(), accountCurrency.getCurrency());
        addTransfer(booking, 2, partnerAccount, bookingRecord);
        return true;
    }

    public boolean addSnapshot(Import imported,
                               AccountCurrency accountCurrency,
                               SnapshotRecord snapshotRecord) {
        var snapshot = new Snapshot();
        snapshot.setImported(imported);
        snapshot.setAccountCurrency(accountCurrency);
        snapshot.setTakenAt(snapshotRecord.takenAt());
        snapshot.setBalance(snapshotRecord.balance());
        snapshotRepository.save(snapshot);
        return true;
    }

    private void addTransfer(Booking booking,
                             int position,
                             AccountCurrency accountCurrency,
                             BookingRecord bookingRecord) {
        var transfer = new Transfer();
        transfer.setBooking(booking);
        transfer.setPosition(position);
        transfer.setAccountCurrency(accountCurrency);
        transfer.setTransferredAt(bookingRecord.bookedAt);
        if (position == 1) {
            transfer.setAmount(bookingRecord.amount);
        } else {
            transfer.setAmount(bookingRecord.amount.negate());
        }
        transferRepository.save(transfer);
    }

    public List<Snapshot> loadAllSnapshots() {
        return snapshotRepository.findAll();
    }

    public List<Booking> loadAllBookings() {
        return bookingRepository.loadBookingsWithTransfers();
    }

    public Booking loadBooking(String bookingId) {
        return bookingRepository.loadBookingCompletely(bookingId);
    }

    public Booking saveBooking(Booking booking) {
        if (booking.getTransfers() != null) {
            booking.getTransfers().forEach(tf -> tf.setBooking(booking));
        }
        return bookingRepository.save(booking);
    }

    public void deleteBooking(Booking booking) {
        bookingRepository.delete(booking);
    }

    public Transfer saveTransfer(Transfer transfer) {
        return transferRepository.save(transfer);
    }

    public void deleteTransfer(Transfer transfer) {
        transferRepository.delete(transfer);
    }
}
