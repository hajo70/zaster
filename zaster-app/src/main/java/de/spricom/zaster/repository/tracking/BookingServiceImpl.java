package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.tracking.BookingEntity;
import de.spricom.zaster.entities.tracking.TransactionEntity;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@AllArgsConstructor
@Log4j2
public class BookingServiceImpl implements de.spricom.zaster.repository.BookingService {

    private final BookingRepository bookingRepository;
    private final TransactionRepository transactionRepository;

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
}
