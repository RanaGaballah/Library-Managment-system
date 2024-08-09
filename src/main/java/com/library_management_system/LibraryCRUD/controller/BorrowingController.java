package com.library_management_system.LibraryCRUD.controller;


import com.library_management_system.LibraryCRUD.dto.ApiResponse;
import com.library_management_system.LibraryCRUD.dto.BorrowingResponseDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library_management_system.LibraryCRUD.model.BorrowingRecord;
import com.library_management_system.LibraryCRUD.service.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/borrowings")
public class BorrowingController {

    @Autowired
    private BorrowingService borrowingRecordService;

    @PostMapping("/borrow/{bookId}/patron/{patronId}")
    public ResponseEntity<ApiResponse<BorrowingResponseDTO>> borrowBook(@PathVariable Long bookId, @PathVariable Long patronId) {
        BorrowingRecord borrowingRecord = borrowingRecordService.createBorrowingRecord(bookId, patronId);
        BorrowingResponseDTO borrowingResponseDTO = new BorrowingResponseDTO(borrowingRecord.getBook(), borrowingRecord.getPatron());
        ApiResponse<BorrowingResponseDTO> response = new ApiResponse<>("success", "Book borrowed successfully", borrowingResponseDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/return/{bookId}/patron/{patronId}")
    public ResponseEntity<ApiResponse<BorrowingResponseDTO>> returnBook(@PathVariable Long bookId, @PathVariable Long patronId) {
        BorrowingRecord borrowingRecord = borrowingRecordService.returnBorrowedBook(bookId, patronId);
        BorrowingResponseDTO borrowingResponseDTO = new BorrowingResponseDTO(borrowingRecord.getBook(), borrowingRecord.getPatron());
        ApiResponse<BorrowingResponseDTO> response = new ApiResponse<>("success", "Book returned successfully", borrowingResponseDTO);
        return ResponseEntity.ok(response);
    }
}
