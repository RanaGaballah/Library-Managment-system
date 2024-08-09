package com.library_management_system.LibraryCRUD.repository;

import com.library_management_system.LibraryCRUD.model.Patron;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PatronRepository extends JpaRepository<Patron, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Patron p WHERE p.id = :id")
    void deletePatronById(@Param("id") Long id);
}
