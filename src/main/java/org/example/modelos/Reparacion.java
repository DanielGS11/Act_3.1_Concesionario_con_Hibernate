package org.example.modelos;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Reparacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private double coste;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coche_matricula")
    private Coche coche;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mecanico_id")
    private Mecanico mecanico;

    public Reparacion(LocalDate fecha, double coste, String descripcion, Coche coche, Mecanico mecanico) {
        this.fecha = fecha;
        this.coste = coste;
        this.descripcion = descripcion;
        this.coche = coche;
        this.mecanico = mecanico;
    }

    public Reparacion() {}

    @Override
    public String toString() {
        return String.format("ID: %d\nFecha: %s\nMatricula del Coche Reparado: %s\nCoste: %.2f\nDescripcion de la Reparacion: %s\n",
                id, fecha, coche.getMatricula(), coste, descripcion);
    }
}
