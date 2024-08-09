package com.library_management_system.LibraryCRUD.controller;


import com.library_management_system.LibraryCRUD.dto.ApiResponse;
import com.library_management_system.LibraryCRUD.exception.ResourceNotFoundException;
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
    public ResponseEntity<ApiResponse<Patron>> getPatronById(@PathVariable @Min(1) Integer id) {
        try {
            Patron patron = patronService.getPatronById(id);
            ApiResponse<Patron> response = new ApiResponse<>("success", "Patron found", patron);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            ApiResponse<Patron> response = new ApiResponse<>("error", e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Patron>> addPatron(@RequestBody @Valid Patron patron) {
        Patron savedPatron = patronService.addPatron(patron);
        ApiResponse<Patron> response = new ApiResponse<>("success", "Patron has been successfully added.", savedPatron);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Patron>> updatePatron(@PathVariable @Min(1) Integer id, @RequestBody @Valid Patron patron) {
        try {
            Patron updatedPatron = patronService.updatePatron(id, patron);
            ApiResponse<Patron> response = new ApiResponse<>("success", "Patron updated successfully.", updatedPatron);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            ApiResponse<Patron> response = new ApiResponse<>("error", e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePatron(@PathVariable @Min(1) Integer id) {
        try {
            patronService.deletePatron(id);
            ApiResponse<Void> response = new ApiResponse<>("success", "Patron with ID " + id + " has been successfully deleted.", null);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            ApiResponse<Void> response = new ApiResponse<>("error", e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
