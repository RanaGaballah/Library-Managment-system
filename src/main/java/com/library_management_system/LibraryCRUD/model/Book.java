package com.library_management_system.LibraryCRUD.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Title is mandatory")
    @Size(max = 100, message = "Title cannot be more than 100 characters")
    private String title;

    @NotBlank(message = "Author is mandatory")
    @Size(max = 100, message = "Author cannot be more than 100 characters")
    private String author;

    @NotNull(message = "Publication year is mandatory")
    @Min(value = 1900, message = "Publication year must be after 1900")
    @Max(value = 2025, message = "Publication year must be before 2025")
    private Integer publicationYear;


    @NotBlank(message = "ISBN is mandatory")
    @Size(min = 10, max = 13, message = "ISBN must be between 10 and 13 characters")
    private String isbn;



}
