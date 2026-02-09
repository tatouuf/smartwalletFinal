package com.example.smartwallet.controller;

import com.example.smartwallet.entity.Depense;
import com.example.smartwallet.service.DepenseService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/depenses")
public class DepenseController {

    private final DepenseService service;

    public DepenseController(DepenseService service) {
        this.service = service;
    }

    @PostMapping
    public Depense add(@RequestBody Depense d) {
        return service.save(d);
    }

    @GetMapping
    public List<Depense> all() {
        return service.all();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
