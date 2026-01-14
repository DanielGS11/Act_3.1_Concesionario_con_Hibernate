package org.example.modelos;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
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

    public Double getPrecio_final() {
        return precio_final;
    }

    @Override
    public String toString() {
        return String.format("ID: %d\nFecha: %s\nMatricula del coche: %s\nPrecio: %.2f\nDNI del Comprador: %s\n",
                id, fecha, coche.getMatricula(), precio_final, propietario.getDni());
    }
}
