package org.example.modelos;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Equipamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private double coste;

    @ManyToMany(mappedBy = "equipamientos")
    public List<Coche> coches;

    public Equipamiento(String nombre, double coste) {
        this.nombre = nombre;
        this.coste = coste;
    }

    public Equipamiento() {}

    public String getNombre() {
        return nombre;
    }

    public double getCoste() {
        return coste;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
