package com.library_management_system.LibraryCRUD.repository;

import com.library_management_system.LibraryCRUD.model.Book;
import com.library_management_system.LibraryCRUD.model.BorrowingRecord;
import com.library_management_system.LibraryCRUD.model.Patron;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {

    Optional<BorrowingRecord> findByBook_IdAndPatron_Id(Long bookId, Long patronId);

    Optional<BorrowingRecord> findByBookAndReturnDateIsNull(Book book);

    Optional<BorrowingRecord> findByBookAndPatronAndReturnDateIsNull(Book book, Patron patron);
}
