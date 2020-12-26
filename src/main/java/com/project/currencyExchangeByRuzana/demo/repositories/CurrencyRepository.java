package com.project.currencyExchangeByRuzana.demo.repositories;

import com.project.currencyExchangeByRuzana.demo.entities.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Currency findTopByOrderByUpdatedAtDesc();
}
