package com.library_management_system.LibraryCRUD.repository;

import com.library_management_system.LibraryCRUD.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
