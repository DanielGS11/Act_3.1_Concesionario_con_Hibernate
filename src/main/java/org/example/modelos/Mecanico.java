package org.example.modelos;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Mecanico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String especialidad;

    @OneToMany(mappedBy = "mecanico")
    public List<Reparacion> reparaciones;

    public Mecanico(String nombre, String especialidad) {
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    public Mecanico() {}
}
