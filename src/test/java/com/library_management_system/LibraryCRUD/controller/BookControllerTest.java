package com.library_management_system.LibraryCRUD.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.library_management_system.LibraryCRUD.model.Book;
import com.library_management_system.LibraryCRUD.model.Patron;
import com.library_management_system.LibraryCRUD.repository.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

    }

    @Test
    void getAllBooks() throws Exception {
        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    void getBookById() throws Exception {
        // Given
        Book book = new Book(null, "Title", "Author", 2020, "1234567890");
        bookRepository.save(book);

        // When & Then
        mockMvc.perform(get("/api/books/{id}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("Title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.author").value("Author"));
    }

    @Test
    void getBookById_notFound() throws Exception {
        mockMvc.perform(get("/api/books/{id}", 999) // ID that doesn't exist
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Book with ID 999 not found."));
    }


    @Test
    void addBook() throws Exception {
        Book book = new Book(null, "Title", "Author", 2020, "1234567890");
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Book has been successfully added."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("Title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.author").value("Author"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.publicationYear").value(2020))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.isbn").value("1234567890"));
    }

    @Test
    void addBook_missingTitle() throws Exception {
        Book book = new Book(null, "", "Author", 2020, "1234567890"); // Title is empty

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Title is mandatory"));
    }

    @Test
    void addBook_missingAuthor() throws Exception {
        Book book = new Book(null, "Title", "", 2020, "1234567890"); // Author is empty

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("author"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Author is mandatory"));
    }

    @Test
    void addBook_missingPublicationYear() throws Exception {
        Book book = new Book(null, "Title", "Author", null, "1234567890"); // PublicationYear is empty

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("publicationYear"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Publication year is mandatory"));
    }

    @Test
    void addBook_invalidMINIPublicationYear() throws Exception {
        Book book = new Book(null, "Title", "Author", 1500, "1234567890"); // PublicationYear is empty

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("publicationYear"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Publication year must be after 1900"));
    }

    @Test
    void addBook_invalidMAXPublicationYear() throws Exception {
        Book book = new Book(null, "Title", "Author", 2100, "1234567890"); // PublicationYear is empty

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("publicationYear"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Publication year must be before 2025"));
    }

    @Test
    void addBook_invalidIsbn() throws Exception {
        Book book = new Book(null, "Title", "Author", 2020, "123"); // ISBN is too short

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("isbn"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("ISBN must be between 10 and 13 characters"));
    }




    @Test
    void updateBook() throws Exception {
        Book book = new Book(null, "Old Title", "Old Author", 2020, "1234567890");
        book = bookRepository.save(book);
        Book updatedBook = new Book(book.getId(), "New Title", "New Author", 2021, "0987654321");

        mockMvc.perform(put("/api/books/{id}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("New Title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.author").value("New Author"));
    }

    @Test
    void updateBook_notFound() throws Exception {
        Book book = new Book(null, "Old Title", "Old Author", 2020, "1234567890");
        book = bookRepository.save(book);
        Book updatedBook = new Book(book.getId(), "New Title", "New Author", 2021, "0987654321");

        mockMvc.perform(put("/api/books/{id}", 999) // ID that doesn't exist
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Book with ID 999 not found."));
    }

    @Test
    void updateBook_missingTitle() throws Exception {
        Book existingBook = new Book(null, "Title", "Author", 2020, "1234567890");
        existingBook = bookRepository.save(existingBook);

        Book updatedBook = new Book(existingBook.getId(), "", "Author", 2020, "1234567890"); // Missing title

        mockMvc.perform(put("/api/books/{id}", existingBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Title is mandatory"));
    }

    @Test
    void updateBook_missingAuthor() throws Exception {
        Book existingBook = new Book(null, "Title", "Author", 2020, "1234567890");
        existingBook = bookRepository.save(existingBook);

        Book updatedBook = new Book(existingBook.getId(), "Title", "", 2020, "1234567890"); // Missing author

        mockMvc.perform(put("/api/books/{id}", existingBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("author"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Author is mandatory"));
    }

    @Test
    void updateBook_missingPublicationYear() throws Exception {
        Book existingBook = new Book(null, "Title", "Author", 2020, "1234567890");
        existingBook = bookRepository.save(existingBook);

        Book updatedBook = new Book(existingBook.getId(), "Title", "Author", null, "1234567890"); // Missing publication year

        mockMvc.perform(put("/api/books/{id}", existingBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("publicationYear"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Publication year is mandatory"));
    }

    @Test
    void updateBook_invalidPublicationYear() throws Exception {
        Book existingBook = new Book(null, "Title", "Author", 2020, "1234567890");
        existingBook = bookRepository.save(existingBook);

        Book updatedBook = new Book(existingBook.getId(), "Title", "Author", 1800, "1234567890"); // Invalid publication year

        mockMvc.perform(put("/api/books/{id}", existingBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("publicationYear"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Publication year must be after 1900"));
    }

    @Test
    void updateBook_invalidMaxPublicationYear() throws Exception {
        Book existingBook = new Book(null, "Title", "Author", 2020, "1234567890");
        existingBook = bookRepository.save(existingBook);

        Book updatedBook = new Book(existingBook.getId(), "Title", "Author", 2100, "1234567890"); // Invalid publication year

        mockMvc.perform(put("/api/books/{id}", existingBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("publicationYear"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Publication year must be before 2025"));
    }

    @Test
    void updateBook_invalidIsbn() throws Exception {
        Book existingBook = new Book(null, "Title", "Author", 2020, "1234567890");
        existingBook = bookRepository.save(existingBook);

        Book updatedBook = new Book(existingBook.getId(), "Title", "Author", 2020, "123"); // Invalid ISBN

        mockMvc.perform(put("/api/books/{id}", existingBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("isbn"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("ISBN must be between 10 and 13 characters"));
    }


    @Test
    void deleteBook() throws Exception {
        Book book = new Book(null, "Title", "Author", 2020, "123123123123");
        book = bookRepository.save(book);

        mockMvc.perform(delete("/api/books/{id}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/books/{id}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    void deleteBook_notFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/999")
                        .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Book with ID 999 not found."));
    }


    @AfterEach
    void tearDown() {
        bookRepository.deleteAll(); // Clear the database after each test
    }

}