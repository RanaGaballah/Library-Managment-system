package com.library_management_system.LibraryCRUD.repository;

import com.library_management_system.LibraryCRUD.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Book b WHERE b.id = :id")
    void deleteBookById(@Param("id") Long id);
}
