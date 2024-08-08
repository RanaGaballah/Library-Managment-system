package com.library_management_system.LibraryCRUD.repository;

import com.library_management_system.LibraryCRUD.model.Patron;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatronRepository extends JpaRepository<Patron, Long> {
}
