package com.library_management_system.LibraryCRUD.service;

import com.library_management_system.LibraryCRUD.exception.ResourceNotFoundException;
import com.library_management_system.LibraryCRUD.model.Book;
import com.library_management_system.LibraryCRUD.model.BorrowingRecord;
import com.library_management_system.LibraryCRUD.repository.BookRepository;
import com.library_management_system.LibraryCRUD.repository.BorrowingRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;


    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + id + " not found."));
    }

    @Transactional
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long id, Book book) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book with ID " + id + " not found.");
        }
        book.setId(id);
        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();

            // Check if the book has any active borrowing records
            Optional<BorrowingRecord> activeBorrowingRecord = borrowingRecordRepository.findByBookAndReturnDateIsNull(book);
            if (activeBorrowingRecord.isPresent()) {
                throw new IllegalArgumentException("Cannot delete the book with ID " + id + " because it is currently borrowed.");
            }

            // Delete any borrowing records associated with this book that have been returned
            List<BorrowingRecord> records = borrowingRecordRepository.findAllByBookAndReturnDateIsNotNull(book);
            borrowingRecordRepository.deleteAll(records);

            // Now delete the book
            bookRepository.deleteById(id);
        }
        throw new ResourceNotFoundException("Book with ID " + id + " not found.");



    }
}
