package com.library_management_system.LibraryCRUD.service;

import com.library_management_system.LibraryCRUD.model.Patron;
import com.library_management_system.LibraryCRUD.repository.PatronRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatronService {
    @Autowired
    private PatronRepository patronRepository;

    public List<Patron> getAllPatrons() {
        return patronRepository.findAll();
    }

    public Optional<Patron> getPatronById(Long id) {
        return patronRepository.findById(id);
    }

    public Patron addPatron(Patron patron) {
        return patronRepository.save(patron);
    }

    public Patron updatePatron(Long id, Patron patronDetails) {
        return patronRepository.findById(id).map(patron -> {
            patron.setName(patronDetails.getName());
            patron.setEmail(patronDetails.getEmail());
            patron.setContactNumber(patronDetails.getContactNumber());
            return patronRepository.save(patron);
        }).orElse(null);
    }

    public boolean deletePatron(Long id) {
        if (patronRepository.existsById(id)) {
            patronRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
