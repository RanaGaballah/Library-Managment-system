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
import java.util.Optional;


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
    public ResponseEntity<Book> getBookById(@PathVariable @Min(1) Long id) {
        Optional<Book> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Book>> addBook(@RequestBody @Valid Book book) {
        Book savedBook = bookService.addBook(book);
        ApiResponse<Book> response = new ApiResponse<>("success", "Book has been successfully added.", savedBook);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> updateBook(@PathVariable Long id, @RequestBody @Valid Book book) {
        Book updatedBook = bookService.updateBook(id, book);
        if (updatedBook != null) {
            ApiResponse<Book> response = new ApiResponse<>("success", "Book with ID " + id + " has been successfully updated.", updatedBook);
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Book> response = new ApiResponse<>("error", "Book with ID " + id + " not found.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable @Min(1) Long id) {
        boolean isDeleted = bookService.deleteBook(id);
        if (isDeleted) {
            ApiResponse<Void> response = new ApiResponse<>("success", "Book with ID " + id + " has been successfully deleted.", null);
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Void> response = new ApiResponse<>("error", "Book with ID " + id + " not found.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
