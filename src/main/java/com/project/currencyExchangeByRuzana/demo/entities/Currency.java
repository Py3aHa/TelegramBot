package com.project.currencyExchangeByRuzana.demo.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "currency_table")
public class Currency {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USD")
    private double usd;

    @Column(name = "EUR")
    private double eur;

    @Column(name = "RUB")
    private double rub;

    @Column(name = "updated_at")
    private Date updatedAt;
}
