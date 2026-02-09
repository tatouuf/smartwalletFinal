package com.example.smartwallet.service;

import com.example.smartwallet.entity.Depense;
import com.example.smartwallet.repository.DepenseRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepenseService {

    private final DepenseRepository repo;

    public DepenseService(DepenseRepository repo) {
        this.repo = repo;
    }

    public Depense save(Depense d) {
        return repo.save(d);
    }

    public List<Depense> all() {
        return repo.findAll();
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
