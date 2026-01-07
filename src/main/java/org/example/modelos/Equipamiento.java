package org.example.modelos;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Equipamiento")
public class Equipamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private double coste;

    @ManyToMany(mappedBy = "equipamientos")
    private List<Coche> coches;

    public Equipamiento(String nombre, double coste) {
        this.nombre = nombre;
        this.coste = coste;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getCoste() {
        return coste;
    }

    public void setCoste(double coste) {
        this.coste = coste;
    }

    public List<Coche> getCoches() {
        return coches;
    }

    public void setCoches(List<Coche> coches) {
        this.coches = coches;
    }
}
