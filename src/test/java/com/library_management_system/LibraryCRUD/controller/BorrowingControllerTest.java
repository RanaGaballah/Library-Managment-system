package com.library_management_system.LibraryCRUD.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library_management_system.LibraryCRUD.model.Book;
import com.library_management_system.LibraryCRUD.model.BorrowingRecord;
import com.library_management_system.LibraryCRUD.model.Patron;
import com.library_management_system.LibraryCRUD.repository.BookRepository;
import com.library_management_system.LibraryCRUD.repository.BorrowingRecordRepository;
import com.library_management_system.LibraryCRUD.repository.PatronRepository;
import com.library_management_system.LibraryCRUD.dto.ApiResponse;
import com.library_management_system.LibraryCRUD.dto.BorrowingResponseDTO;
import com.library_management_system.LibraryCRUD.exception.BookAlreadyBorrowedException;
import com.library_management_system.LibraryCRUD.exception.BookAlreadyReturnedException;
import com.library_management_system.LibraryCRUD.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BorrowingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PatronRepository patronRepository;

    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book;
    private Patron patron;

    @BeforeEach
    void setUp() {
        // Initialize test data
        book = new Book(null, "Test Title", "Test Author", 2020, "1234567890");
        book = bookRepository.save(book);

        patron = new Patron(null, "Test Patron", "test@patron.com" , "+2011185660002");
        patron = patronRepository.save(patron);
    }

    @Test
    void borrowBook_success() throws Exception {
        mockMvc.perform(post("/api/borrowings/borrow/{bookId}/patron/{patronId}", book.getId(), patron.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Book borrowed successfully"))
                .andExpect(jsonPath("$.data.book.id").value(book.getId()))
                .andExpect(jsonPath("$.data.patron.id").value(patron.getId()));
    }

    @Test
    void returnBook_success() throws Exception {
        // First borrow the book
        BorrowingRecord borrowingRecord = new BorrowingRecord(null, book, patron, LocalDate.now(), null);
        borrowingRecord = borrowingRecordRepository.save(borrowingRecord);

        // Now return the book
        mockMvc.perform(put("/api/borrowings/return/{bookId}/patron/{patronId}", book.getId(), patron.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Book returned successfully"))
                .andExpect(jsonPath("$.data.book.id").value(book.getId()))
                .andExpect(jsonPath("$.data.patron.id").value(patron.getId()));
    }

    @Test
    void borrowBook_bookNotFound() throws Exception {
        int nonExistentBookId = 999;
        int patronId = patron.getId();

        mockMvc.perform(post("/api/borrowings/borrow/{bookId}/patron/{patronId}", nonExistentBookId, patronId))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Book not found with id " + nonExistentBookId));
    }

    @Test
    void borrowBook_patronNotFound() throws Exception {
        int bookId = book.getId();
        int nonExistentPatronId = 999;

        mockMvc.perform(post("/api/borrowings/borrow/{bookId}/patron/{patronId}", bookId, nonExistentPatronId))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Patron not found with id " + nonExistentPatronId));
    }

    @Test
    void borrowBook_bookAlreadyBorrowed() throws Exception {
        // Create a record that simulates a book already borrowed
        BorrowingRecord existingRecord = new BorrowingRecord(null, book, patron, LocalDate.now(), null);
        borrowingRecordRepository.save(existingRecord);

        // Attempt to borrow the book again
        mockMvc.perform(post("/api/borrowings/borrow/{bookId}/patron/{patronId}", book.getId(), patron.getId()))
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("The book is already borrowed by another patron."));
    }

    @Test
    void returnBook_bookNotFound() throws Exception {
        int nonExistentBookId = 999;
        int patronId = patron.getId();

        mockMvc.perform(put("/api/borrowings/return/{bookId}/patron/{patronId}", nonExistentBookId, patronId))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Book not found with id " + nonExistentBookId));
    }

    @Test
    void returnBook_patronNotFound() throws Exception {
        int bookId = book.getId();
        int nonExistentPatronId = 999;

        mockMvc.perform(put("/api/borrowings/return/{bookId}/patron/{patronId}", bookId, nonExistentPatronId))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Patron not found with id " + nonExistentPatronId));
    }

    @Test
    void returnBook_noActiveBorrowRecord() throws Exception {
        // Attempt to return a book that is not borrowed
        mockMvc.perform(put("/api/borrowings/return/{bookId}/patron/{patronId}", book.getId(), patron.getId()))
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("The book is already returned or was never borrowed by this patron."));
    }

    @AfterEach
    void tearDown() {
        borrowingRecordRepository.deleteAll();
        bookRepository.deleteAll();
        patronRepository.deleteAll();
    }
}