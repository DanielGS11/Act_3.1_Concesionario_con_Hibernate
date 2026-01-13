package org.example.modelos;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

// Establezco la clase como una entidad (tabla) de la base de datos
@Entity
/*
 Creo una Named Query con la que no tengo que repetir la sentencia SQL cada vez que quiero buscar un objeto de la clase,
 solo introducir el dato que se pide (que esta despues de ':')
 */
@NamedQuery(
        name = "Coche.buscarPorMatricula",
        query = "SELECT c FROM Coche c WHERE matricula = :matricula"
)
public class Coche {
    // @id es para establecer una primary key (PK)
    @Id
    // Aqui establezco que la PK es unica y, en este caso, no sobrepase los 20 caracteres
    @Column(length = 20, unique = true)
    //atributo de la PK
    private String matricula;

    // otros atributos
    private String marca;
    private String modelo;
    private double precio_base;

    /*
     Relacion N:1, FetchType.LAZY es para que cargue solo el dato necesario en el momento, asi nos evitamos cargas
     innecesarias
     */
    @ManyToOne(fetch = FetchType.LAZY)
    // Atributo de la tabla de la relacion 'N' que traemos a esta
    @JoinColumn(name = "concesionario_id")
    // Objeto del atributo
    private Concesionario concesionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id")
    private Propietario propietario;

    /*
    Relacion 1:N, en este caso, este objeto es el de la 'N' de la relacion, por lo que mapeamos
     */
    @OneToMany(mappedBy = "coche")
    public List<Reparacion> reparaciones;

    // Relacion N:M
    @ManyToMany
    // Asigno el nombre de la tabla de la relacion, el id de la clase coche y el id de la otra clase de la relacion
    @JoinTable(
            name = "coche_equipamiento",
            joinColumns = @JoinColumn(name = "coche_matricula"),
            inverseJoinColumns = @JoinColumn(name = "equipamiento_id")
    )
    // Lo asocio al objeto de la clase, que sera equipamiento
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

    public double getPrecio_base() {
        return precio_base;
    }

    public Concesionario getConcesionario() {
        return concesionario;
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

    @Override
    public String toString() {
        return String.format("Matricula: %s\nMarca: %s\nModelo: %s\nPrecio Base: %.2f\nEquipamientos: %s\n",
                matricula, marca, modelo, precio_base, equipamientos);
    }
}
