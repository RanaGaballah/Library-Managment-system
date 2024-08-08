package com.library_management_system.LibraryCRUD.controller;


import com.library_management_system.LibraryCRUD.dto.ApiResponse;
import com.library_management_system.LibraryCRUD.model.Patron;
import com.library_management_system.LibraryCRUD.service.PatronService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patrons")
@Validated
public class PatronController {
    @Autowired
    private PatronService patronService;

    @GetMapping
    public ResponseEntity<List<Patron>> getAllPatrons() {
        List<Patron> patrons = patronService.getAllPatrons();
        return ResponseEntity.ok(patrons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patron> getPatronById(@PathVariable Long id) {
        Optional<Patron> patron = patronService.getPatronById(id);
        return patron.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Patron>> addPatron(@RequestBody @Valid Patron patron) {
        Patron savedPatron = patronService.addPatron(patron);
        ApiResponse<Patron> response = new ApiResponse<>("success", "Patron has been successfully added.", savedPatron);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Patron>> updatePatron(@PathVariable Long id, @RequestBody @Valid Patron patron) {
        Patron updatedPatron = patronService.updatePatron(id, patron);
        if (updatedPatron != null) {
            ApiResponse<Patron> response = new ApiResponse<>("success", "Patron with ID " + id + " has been successfully updated.", updatedPatron);
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Patron> response = new ApiResponse<>("error", "Patron with ID " + id + " not found.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePatron(@PathVariable @Min(1) Long id) {
        boolean isDeleted = patronService.deletePatron(id);
        if (isDeleted) {
            ApiResponse<Void> response = new ApiResponse<>("success", "Patron with ID " + id + " has been successfully deleted.", null);
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Void> response = new ApiResponse<>("error", "Patron with ID " + id + " not found.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
