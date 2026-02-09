package com.example.smartwallet.service;

import com.example.smartwallet.repository.DepenseRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepenseService {

    private final DepenseRepository repo;

    public DepenseService(DepenseRepository repo) {
        this.repo = repo;
    }

    public <Depense> Depense save(Depense d) {
        return (Depense) repo.save(d);
    }

    public <Depense> List<Depense> all() {
        return repo.findAll();
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
