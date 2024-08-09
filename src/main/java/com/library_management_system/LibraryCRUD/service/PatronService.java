package com.library_management_system.LibraryCRUD.service;

import com.library_management_system.LibraryCRUD.model.Book;
import com.library_management_system.LibraryCRUD.model.BorrowingRecord;
import com.library_management_system.LibraryCRUD.model.Patron;
import com.library_management_system.LibraryCRUD.repository.BorrowingRecordRepository;
import com.library_management_system.LibraryCRUD.repository.PatronRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PatronService {
    @Autowired
    private PatronRepository patronRepository;

    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;

    @Transactional(readOnly = true)
    public List<Patron> getAllPatrons() {
        return patronRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Patron> getPatronById(Long id) {
        return patronRepository.findById(id);
    }

    @Transactional
    public Patron addPatron(Patron patron) {
        return patronRepository.save(patron);
    }

    @Transactional
    public Patron updatePatron(Long id, Patron patronDetails) {
        return patronRepository.findById(id).map(patron -> {
            patron.setName(patronDetails.getName());
            patron.setEmail(patronDetails.getEmail());
            patron.setContactNumber(patronDetails.getContactNumber());
            return patronRepository.save(patron);
        }).orElse(null);
    }

    @Transactional
    public boolean deletePatron(Long id) {
        Optional<Patron> patronOptional = patronRepository.findById(id);
        if (patronOptional.isPresent()) {
            Patron patron = patronOptional.get();

            // Check if the book has any active borrowing records
            Optional<BorrowingRecord> activeBorrowingRecord = borrowingRecordRepository.findByPatronAndReturnDateIsNull(patron);
            if (activeBorrowingRecord.isPresent()) {
                throw new IllegalArgumentException("Cannot delete the patron with ID " + id + "  because they have active borrowing records.");
            }

            // Delete any borrowing records associated with this patron
            List<BorrowingRecord> records = borrowingRecordRepository.findAllByPatronAndReturnDateIsNotNull(patron);
            borrowingRecordRepository.deleteAll(records);

            // Now delete the patron
            patronRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
