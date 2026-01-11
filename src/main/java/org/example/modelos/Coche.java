package org.example.modelos;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@NamedQuery(
        name = "Coche.buscarPorMatricula",
        query = "SELECT c FROM Coche c WHERE matricula = :matricula"
)
public class Coche {
    @Id
    @Column(length = 20, unique = true)
    private String matricula;

    private String marca;
    private String modelo;
    private double precio_base;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concesionario_id")
    private Concesionario concesionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id")
    private Propietario propietario;

    @OneToMany(mappedBy = "coche")
    public List<Reparacion> reparaciones;

    @ManyToMany
    @JoinTable(
            name = "coche_equipamiento",
            joinColumns = @JoinColumn(name = "coche_matricula"),
            inverseJoinColumns = @JoinColumn(name = "equipamiento_id")
    )
    public Set<Equipamiento> equipamientos;

    public Coche(String matricula, String marca, String modelo, double precio_base, Concesionario concesionario) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.precio_base = precio_base;
        this.concesionario = concesionario;
    }

    public Coche() {
    }

    public String getMatricula() {
        return matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public double getPrecio_base() {
        return precio_base;
    }

    public void setPrecio_base(double precio_base) {
        this.precio_base = precio_base;
    }

    public Concesionario getConcesionario() {
        return concesionario;
    }

    public void setConcesionario(Concesionario concesionario) {
        this.concesionario = concesionario;
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }

    public Set<Equipamiento> getEquipamientos() {
        return equipamientos;
    }

    public void setEquipamientos(Set<Equipamiento> equipamientos) {
        this.equipamientos = equipamientos;
    }

    public List<Reparacion> getReparaciones() {
        return reparaciones;
    }

    public void setReparaciones(List<Reparacion> reparaciones) {
        this.reparaciones = reparaciones;
    }

    @Override
    public String toString() {
        return String.format("Matricula: %s\nMarca: %s\nModelo: %s\nPrecio Base: %.2f\nEquipamientos: %s\n",
                matricula, marca, modelo, precio_base, equipamientos);
    }
}
