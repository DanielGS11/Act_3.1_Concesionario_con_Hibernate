package org.example.modelos;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
/*
 Creo una Named Query con la que no tengo que repetir la sentencia SQL cada vez que quiero buscar un objeto de la clase,
 solo introducir el dato que se pide (que esta despues de ':')
 */
@NamedQuery(
        name = "Propietario.buscarPropietario",
        query = "SELECT p FROM Propietario p WHERE dni = :dni AND nombre = :nombre"
)
public class Propietario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String dni;

    private String nombre;

    @OneToMany(mappedBy = "propietario")
    public List<Coche> coches = new ArrayList<>();

    @OneToMany(mappedBy = "propietario")
    public List<Venta> ventas = new ArrayList<>();

    public Propietario(String nombre, String dni) {
        this.nombre = nombre;
        this.dni = dni;
    }

    public Propietario() {}

    public String getDni() {
        return dni;
    }
}
