package com.library_management_system.LibraryCRUD.controller;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library_management_system.LibraryCRUD.model.Patron;
import com.library_management_system.LibraryCRUD.repository.PatronRepository;
import com.library_management_system.LibraryCRUD.service.PatronService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PatronControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatronRepository patronRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatronService patronService;


    @Test
    void getAllPatrons() throws Exception {
        mockMvc.perform(get("/api/patrons")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    void getPatronById() throws Exception {
        // Given
        Patron patron = new Patron(null, "John Doe", "john.doe@example.com", "+1234567890");
        patronRepository.save(patron);

        // When & Then
        mockMvc.perform(get("/api/patrons/{id}", patron.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("John Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("john.doe@example.com"));
    }

    @Test
    void getPatronById_notFound() throws Exception {
        mockMvc.perform(get("/api/patrons/{id}", 999) // ID that doesn't exist
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Patron with ID 999 not found."));
    }

    @Test
    void addPatron() throws Exception {
        Patron patron = new Patron(null, "Jane Doe", "jane.doe@example.com", "+0987654321");
        mockMvc.perform(post("/api/patrons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patron)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Patron has been successfully added."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("Jane Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("jane.doe@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.contactNumber").value("+0987654321"));
    }

    @Test
    void addPatron_missingName() throws Exception {
        Patron patron = new Patron(null, "", "jane.doe@example.com", "+0987654321"); // Name is empty

        mockMvc.perform(post("/api/patrons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patron)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"));

    }

    @Test
    void addPatron_missingEmail() throws Exception {
        Patron patron = new Patron(null, "Jane Doe", "", "+0987654321"); // Email is empty

        mockMvc.perform(post("/api/patrons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patron)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("email"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Email is mandatory"));
    }

    @Test
    void addPatron_invalidEmail() throws Exception {
        Patron patron = new Patron(null, "Jane Doe", "invalid-email", "+0987654321"); // Invalid email

        mockMvc.perform(post("/api/patrons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patron)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("email"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Email should be valid"));
    }

    @Test
    void addPatron_missingContactNumber() throws Exception {
        Patron patron = new Patron(null, "Jane Doe", "jane.doe@example.com", ""); // Contact number is empty

        mockMvc.perform(post("/api/patrons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patron)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("contactNumber"));
    }

    @Test
    void addPatron_invalidContactNumber() throws Exception {
        Patron patron = new Patron(null, "Jane Doe", "jane.doe@example.com", "123"); // Invalid contact number

        mockMvc.perform(post("/api/patrons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patron)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("contactNumber"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Contact number must be between 10 and 15 digits long and may start with a '+'"));
    }

    @Test
    void updatePatron() throws Exception {
        Patron patron = new Patron(null, "Old Name", "old.email@example.com", "+1234567890");
        patron = patronRepository.save(patron);
        Patron updatedPatron = new Patron(patron.getId(), "New Name", "new.email@example.com", "+0987654321");

        mockMvc.perform(put("/api/patrons/{id}", patron.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPatron)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("New Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("new.email@example.com"));
    }

    @Test
    void updatePatron_notFound() throws Exception {
        Patron patron = new Patron(null, "Old Name", "old.email@example.com", "+1234567890");
        patron = patronRepository.save(patron);
        Patron updatedPatron = new Patron(patron.getId(), "New Name", "new.email@example.com", "+0987654321");

        mockMvc.perform(put("/api/patrons/{id}", 999) // ID that doesn't exist
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPatron)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Patron with ID 999 not found."));
    }

    @Test
    void updatePatron_missingName() throws Exception {
        Patron patron = new Patron(null, "Old Name", "old.email@example.com", "+1234567890");
        patron = patronRepository.save(patron);
        Patron updatedPatron = new Patron(patron.getId(), "", "new.email@example.com", "+0987654321"); // Name is empty

        mockMvc.perform(put("/api/patrons/{id}", patron.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPatron)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("name"));
    }

    @Test
    void updatePatron_missingEmail() throws Exception {
        Patron patron = new Patron(null, "Old Name", "old.email@example.com", "+1234567890");
        patron = patronRepository.save(patron);
        Patron updatedPatron = new Patron(patron.getId(), "New Name", "", "+0987654321"); // Email is empty

        mockMvc.perform(put("/api/patrons/{id}", patron.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPatron)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("email"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Email is mandatory"));
    }

    @Test
    void updatePatron_invalidEmail() throws Exception {
        Patron patron = new Patron(null, "Old Name", "old.email@example.com", "+1234567890");
        patron = patronRepository.save(patron);
        Patron updatedPatron = new Patron(patron.getId(), "New Name", "invalid-email", "+0987654321"); // Invalid email

        mockMvc.perform(put("/api/patrons/{id}", patron.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPatron)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("email"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Email should be valid"));
    }

    @Test
    void updatePatron_missingContactNumber() throws Exception {
        Patron patron = new Patron(null, "Old Name", "old.email@example.com", "+1234567890");
        patron = patronRepository.save(patron);
        Patron updatedPatron = new Patron(patron.getId(), "New Name", "new.email@example.com", ""); // Contact number is empty

        mockMvc.perform(put("/api/patrons/{id}", patron.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPatron)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void updatePatron_invalidContactNumber() throws Exception {
        Patron patron = new Patron(null, "Old Name", "old.email@example.com", "+1234567890");
        patron = patronRepository.save(patron);
        Patron updatedPatron = new Patron(patron.getId(), "New Name", "new.email@example.com", "123"); // Invalid contact number

        mockMvc.perform(put("/api/patrons/{id}", patron.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPatron)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].field").value("contactNumber"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Contact number must be between 10 and 15 digits long and may start with a '+'"));
    }

    @Test
    void deletePatron() throws Exception {
        Patron patron = new Patron(null, "John Doe", "john.doe@example.com", "+1234567890");
        patron = patronRepository.save(patron);

        mockMvc.perform(delete("/api/patrons/{id}", patron.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/patrons/{id}", patron.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePatron_notFound() throws Exception {
        mockMvc.perform(delete("/api/patrons/{id}", 999) // ID that doesn't exist
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Patron with ID 999 not found."));
    }

    @AfterEach
    void tearDown() {
        patronRepository.deleteAll();
    }

}