package org.example.modelos;

import jakarta.persistence.*;

import java.util.List;

@Entity
@NamedQuery(
        name = "Equipamiento.buscarPorID",
        query = "SELECT e FROM Equipamiento e WHERE id = :id"
)
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

    public Long getId() {
        return id;
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
