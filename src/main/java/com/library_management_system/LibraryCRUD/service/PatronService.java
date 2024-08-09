package com.library_management_system.LibraryCRUD.service;

import com.library_management_system.LibraryCRUD.exception.ResourceNotFoundException;
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
    public Patron getPatronById(Integer id) {
        return patronRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patron with ID " + id + " not found."));
    }
    @Transactional
    public Patron addPatron(Patron patron) {
        return patronRepository.save(patron);
    }

    @Transactional
    public Patron updatePatron(Integer id, Patron patron) {
        if (!patronRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patron with ID " + id + " not found.");
        }
        patron.setId(id);
        return patronRepository.save(patron);
    }

    @Transactional
    public void deletePatron(Integer id) {
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
        }else{
            throw new ResourceNotFoundException("Patron with ID " + id + " not found.");
        }

    }
}
