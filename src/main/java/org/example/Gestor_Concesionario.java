package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.modelos.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Gestor_Concesionario {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("concesionarioHibernate");
    private final EntityManager em = emf.createEntityManager();

    public void IniciarEntityManager() {
        try {
            if (!em.getTransaction().isActive()) {
                abrirConexion();
            }

            em.createQuery("DELETE FROM Reparacion").executeUpdate();
            em.createQuery("DELETE FROM Venta").executeUpdate();
            em.createQuery("DELETE FROM Coche").executeUpdate();
            em.createQuery("DELETE FROM Equipamiento").executeUpdate();
            em.createQuery("DELETE FROM Mecanico").executeUpdate();
            em.createQuery("DELETE FROM Propietario").executeUpdate();
            em.createQuery("DELETE FROM Concesionario").executeUpdate();

            plantillaDatos();

            em.getTransaction().commit();

            System.out.println("Datos Iniciales cargados con exito");
        } catch (Exception e) {
            em.getTransaction().rollback();

            System.out.println("Carga de datos cancelada con exito");
        } finally {
            em.close();
        }
    }

    //----------------------------------------------- METODOS AUXILIARES -----------------------------------------------
    public void abrirConexion() {
        em.getTransaction().begin();

        System.out.println("Conexion Establecida");
    }

    //------------------------------------------------ DATOS INICIALES -------------------------------------------------
    private void plantillaDatos() {

        Concesionario concesionario = new Concesionario("Concesionario Central", "Av. Principal 1");

        Equipamiento aire = new Equipamiento("Aire Acondicionado", 500);
        Equipamiento gps = new Equipamiento("GPS", 300);

        Mecanico mecanico1 = new Mecanico("Carlos", "Motor");
        Mecanico mecanico2 = new Mecanico("Luis", "Electricidad");

        Propietario p1 = new Propietario("12345678A", "Juan Pérez");
        Propietario p2 = new Propietario("87654321B", "Ana López");

        Coche coche1 = new Coche("1234ABC", "Toyota", "Corolla", 20000);
        Coche coche2 = new Coche("5678DEF", "Seat", "Ibiza", 15000);

        coche1.setEquipamientos(List.of(aire, gps));
        coche2.setConcesionario(concesionario);
        coche2.setPropietario(p2);

        Reparacion r1 = new Reparacion(LocalDate.now(), 250, "Cambio de aceite", coche1, mecanico1);

        Venta v1 = new Venta(LocalDate.now(), 21000.0, concesionario, p1, coche1);

        em.persist(concesionario);
        em.persist(p1);
        em.persist(p2);
        em.persist(coche1);
        em.persist(coche2);
        em.persist(aire);
        em.persist(gps);
        em.persist(mecanico1);
        em.persist(mecanico2);
        em.persist(v1);
        em.persist(r1);
    }
}
