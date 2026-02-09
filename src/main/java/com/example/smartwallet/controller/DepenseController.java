package com.example.smartwallet.controller;

import com.example.smartwallet.model.Depense;
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
    public <Depense> List<Depense> all() {
        return (List<Depense>) service.all();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
