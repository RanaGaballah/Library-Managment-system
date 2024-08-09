package com.library_management_system.LibraryCRUD.repository;

import com.library_management_system.LibraryCRUD.model.Book;
import com.library_management_system.LibraryCRUD.model.BorrowingRecord;
import com.library_management_system.LibraryCRUD.model.Patron;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Integer> {


    Optional<BorrowingRecord> findByBookAndReturnDateIsNull(Book book);

    List<BorrowingRecord> findAllByBookAndReturnDateIsNotNull(Book book);

    List<BorrowingRecord> findAllByPatronAndReturnDateIsNotNull(Patron patron);

    Optional<BorrowingRecord> findByPatronAndReturnDateIsNull(Patron patron);

    Optional<BorrowingRecord> findByBookAndPatronAndReturnDateIsNull(Book book, Patron patron);
}
