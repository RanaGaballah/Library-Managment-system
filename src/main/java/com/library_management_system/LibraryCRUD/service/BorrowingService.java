package com.library_management_system.LibraryCRUD.service;

import org.springframework.stereotype.Service;

import com.library_management_system.LibraryCRUD.model.Book;
import com.library_management_system.LibraryCRUD.model.BorrowingRecord;
import com.library_management_system.LibraryCRUD.model.Patron;
import com.library_management_system.LibraryCRUD.repository.BookRepository;
import com.library_management_system.LibraryCRUD.repository.BorrowingRecordRepository;
import com.library_management_system.LibraryCRUD.repository.PatronRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.library_management_system.LibraryCRUD.exception.BookAlreadyBorrowedException;
import com.library_management_system.LibraryCRUD.exception.BookAlreadyReturnedException;
import com.library_management_system.LibraryCRUD.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.Optional;
@Service
public class BorrowingService {

    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PatronRepository patronRepository;

    public BorrowingRecord createBorrowingRecord(Long bookId, Long patronId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + bookId));

        Patron patron = patronRepository.findById(patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found with id " + patronId));

        Optional<BorrowingRecord> existingBorrowRecords = borrowingRecordRepository.findByBookAndReturnDateIsNull(book);
        if (existingBorrowRecords.isPresent()) {
            throw new BookAlreadyBorrowedException("The book is already borrowed by another patron.");
        }

        BorrowingRecord borrowingRecord = new BorrowingRecord();
        borrowingRecord.setBook(book);
        borrowingRecord.setPatron(patron);
        borrowingRecord.setBorrowingDate(LocalDate.now());

        return borrowingRecordRepository.save(borrowingRecord);
    }

    public BorrowingRecord returnBorrowedBook(Long bookId, Long patronId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + bookId));

        Patron patron = patronRepository.findById(patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found with id " + patronId));

        BorrowingRecord borrowingRecord = borrowingRecordRepository.findByBookAndPatronAndReturnDateIsNull(book, patron)
                .orElseThrow(() -> new BookAlreadyReturnedException("The book is already returned or was never borrowed by this patron."));

        borrowingRecord.setReturnDate(LocalDate.now());
        return borrowingRecordRepository.save(borrowingRecord);
    }
}
