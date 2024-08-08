package com.library_management_system.LibraryCRUD.dto;

import com.library_management_system.LibraryCRUD.model.Book;
import com.library_management_system.LibraryCRUD.model.Patron;

public class BorrowingResponseDTO {

    private BookDTO book;
    private PatronDTO patron;

    public BorrowingResponseDTO(Book book, Patron patron) {
        this.book = new BookDTO(book.getId(), book.getTitle(), book.getAuthor(), book.getPublicationYear(), book.getIsbn());
        this.patron = new PatronDTO(patron.getId(), patron.getName(), patron.getEmail(), patron.getContactNumber());
    }

    // Getters and setters
    public BookDTO getBook() {
        return book;
    }

    public void setBook(BookDTO book) {
        this.book = book;
    }

    public PatronDTO getPatron() {
        return patron;
    }

    public void setPatron(PatronDTO patron) {
        this.patron = patron;
    }
}
