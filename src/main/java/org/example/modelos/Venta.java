package org.example.modelos;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@NamedQuery(
        name = "Venta.buscarPorID",
        query = "SELECT v FROM Venta v WHERE id = :id"
)
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private Double precio_final;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concesionario_id")
    private Concesionario concesionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id")
    private Propietario propietario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coche_matricula")
    private Coche coche;

    public Venta(LocalDate fecha, Double precio_final, Concesionario concesionario, Propietario propietario, Coche coche) {
        this.fecha = fecha;
        this.precio_final = precio_final;
        this.concesionario = concesionario;
        this.propietario = propietario;
        this.coche = coche;
    }

    public Venta() {}

    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Double getPrecio_final() {
        return precio_final;
    }

    public void setPrecio_final(Double precio_final) {
        this.precio_final = precio_final;
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

    public Coche getCoche() {
        return coche;
    }

    public void setCoche(Coche coche) {
        this.coche = coche;
    }

    @Override
    public String toString() {
        return String.format("ID: %d\nFecha: %s\nMatricula del coche: %s\nPrecio: %.2f\nDNI del Comprador: %s\n",
                id, fecha, coche.getMatricula(), precio_final, propietario.getDni());
    }
}
