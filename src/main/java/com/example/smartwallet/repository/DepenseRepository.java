package com.example.smartwallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DepenseRepository<Depense> extends JpaRepository<Depense, Long> {
}
