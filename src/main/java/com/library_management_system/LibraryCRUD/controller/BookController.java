package com.library_management_system.LibraryCRUD.controller;

import com.library_management_system.LibraryCRUD.exception.ResourceNotFoundException;
import com.library_management_system.LibraryCRUD.model.Book;
import com.library_management_system.LibraryCRUD.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.library_management_system.LibraryCRUD.dto.ApiResponse;
import java.util.List;



@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> getBookById(@PathVariable @Min(1) Long id) {
        try {
            Book book = bookService.getBookById(id);
            ApiResponse<Book> response = new ApiResponse<>("success", "Book found", book);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            ApiResponse<Book> response = new ApiResponse<>("error", e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Book>> addBook(@RequestBody @Valid Book book) {
        Book savedBook = bookService.addBook(book);
        ApiResponse<Book> response = new ApiResponse<>("success", "Book has been successfully added.", savedBook);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> updateBook(@PathVariable @Min(1) Long id, @RequestBody @Valid Book book) {
        try {
            Book updatedBook = bookService.updateBook(id, book);
            ApiResponse<Book> response = new ApiResponse<>("success", "Book updated successfully.", updatedBook);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            ApiResponse<Book> response = new ApiResponse<>("error", e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable @Min(1) Long id) {
        try {
            bookService.deleteBook(id);
            ApiResponse<Void> response = new ApiResponse<>("success", "Book with ID " + id + " has been successfully deleted.", null);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            ApiResponse<Void> response = new ApiResponse<>("error", e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

    }
}
