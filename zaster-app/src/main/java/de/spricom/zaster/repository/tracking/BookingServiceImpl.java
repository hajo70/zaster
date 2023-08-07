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
    public BookingEntity createBooking(BookingEntity booking) {
        var bookingSaved = bookingRepository.save(booking);
        HashSet<TransferEntity> transfersSaved = new HashSet<>(booking.getTransfers().size());
        for (TransferEntity transfer : booking.getTransfers()) {
            transfer.setBooking(bookingSaved);
            transfersSaved.add(transferRepository.save(transfer));
        }
        bookingSaved.setTransfers(transfersSaved);
        return bookingSaved;
    }

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
        snapshot.setAccount(accountCurrency);
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
}
