package com.doremi.booking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
@Entity
@Table(name = "INSTRUMENTO")
public class Instrumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instrumento_id")
    private Long instrumento_id;
    private String nombre;
    @Column(length = 1000)
    private String descripcion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", referencedColumnName = "categoria_id")
    private Categoria categoria;

    private double precioDia;
    @OneToMany(mappedBy = "instrumento", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Imagen> imagenes = new ArrayList<>();


    public Instrumento(String nombre, String descripcion, Categoria categoria, double precioDia, List<Imagen> imagenes) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precioDia = precioDia;
        this.imagenes = imagenes;
    }

    public Instrumento(Long instrumento_id) {
        this.instrumento_id = instrumento_id;
    }
}