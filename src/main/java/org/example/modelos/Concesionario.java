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

    @OneToMany(mappedBy = "concesionario", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Coche> coches = new ArrayList<>();

    @OneToMany(mappedBy = "concesionario", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Venta> ventas = new ArrayList<>();

    public Concesionario(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public Concesionario() {}

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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Coche> getCoches() {
        return coches;
    }

    public void setCoches(List<Coche> coches) {
        this.coches = coches;
    }

    public List<Venta> getVentas() {
        return ventas;
    }

    public void setVentas(List<Venta> ventas) {
        this.ventas = ventas;
    }
}
