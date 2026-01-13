package org.example.modelos;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQuery(
        name = "Concesionario.buscarPorID",
        query = "SELECT c FROM Concesionario c WHERE id = :id"
)
public class Concesionario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String direccion;

    @OneToMany(mappedBy = "concesionario")
    public List<Coche> coches = new ArrayList<>();

    @OneToMany(mappedBy = "concesionario")
    public List<Venta> ventas = new ArrayList<>();

    public Concesionario(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public Concesionario() {}

    public Long getId() {
        return id;
    }
}
