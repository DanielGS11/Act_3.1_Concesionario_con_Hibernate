package org.example.modelos;

import jakarta.persistence.*;

import java.util.List;

@Entity
@NamedQuery(
        name = "Mecanico.buscarPorID",
        query = "SELECT m FROM Mecanico m WHERE id = :id"
)
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

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public List<Reparacion> getReparaciones() {
        return reparaciones;
    }

    public void setReparaciones(List<Reparacion> reparaciones) {
        this.reparaciones = reparaciones;
    }
}
