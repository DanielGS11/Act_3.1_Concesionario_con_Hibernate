package org.example;

import jakarta.persistence.*;
import org.example.modelos.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Gestor_Concesionario {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("concesionarioHibernate");
    private final EntityManager em = emf.createEntityManager();

    public void iniciarEntityManager() {
        if (!em.getTransaction().isActive()) {
            abrirConexion();
        }

        try {

            limpiarTablas();

            plantillaDatos();

            em.getTransaction().commit();

            System.out.println("Datos Iniciales cargados con exito");

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            System.out.println("Carga de datos cancelada con exito");
            System.out.println(e.getMessage());

        }
    }

    public void gestionStock(int opc) {
        abrirConexion();
        Scanner sc = new Scanner(System.in);

        switch (opc) {
            case 1:
                System.out.print("Introduzca el nombre del concesionario: ");
                String concesionarioName = sc.nextLine();

                System.out.print("Introduzca la direccion del concesionario: ");
                String concesionarioDirection = sc.nextLine();

                Concesionario concesionario = new Concesionario(concesionarioName, concesionarioDirection);

                em.persist(concesionario);

                em.getTransaction().commit();
                break;

            case 2:
                System.out.print("Introduzca la matricula del coche: ");
                String matricula = sc.nextLine().toUpperCase();

                System.out.print("Introduzca el modelo del coche: ");
                String model = sc.nextLine();

                System.out.print("Introduzca la marca del coche: ");
                String marca = sc.nextLine();

                System.out.print("Introduzca el precio base del coche: ");
                double precioBase = Double.parseDouble(sc.nextLine());

                System.out.print("Introduzca la ID del concesionario al que pertenece coche: ");
                int idConcesionario = Integer.parseInt(sc.nextLine());

                Coche coche = new Coche(matricula, marca, model, precioBase);

                try {
                    Query q = em.createQuery("Select c FROM Concesionario c WHERE c.id = :id");
                    q.setParameter("id", idConcesionario);

                    Concesionario c = (Concesionario) q.getSingleResult();

                    coche.setConcesionario(c);

                    em.persist(coche);

                    try {
                        em.getTransaction().commit();
                    } catch (RuntimeException e) {
                        System.out.printf("El coche con matricula %s ya existe\n", matricula);
                    }
                } catch (NoResultException e) {
                    System.out.println("La ID del concesionario no existe");
                }
                break;

            default:
                System.out.println("Por favor, introduzca la opcion correcta");
                break;
        }
    }

    //----------------------------------------------- METODOS AUXILIARES -----------------------------------------------
    private void abrirConexion() {
        if (!em.getTransaction().isActive()) {
            try {
                System.out.println("Conectando a la base de datos...");
                Thread.sleep(0000);

                em.getTransaction().begin();

                System.out.println("Conexion Establecida");
            } catch (InterruptedException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void limpiarTablas() {
        em.createQuery("DELETE FROM Reparacion").executeUpdate();
        em.createQuery("DELETE FROM Venta").executeUpdate();
        em.createQuery("DELETE FROM Coche").executeUpdate();
        em.createQuery("DELETE FROM Equipamiento").executeUpdate();
        em.createQuery("DELETE FROM Mecanico").executeUpdate();
        em.createQuery("DELETE FROM Propietario").executeUpdate();
        em.createQuery("DELETE FROM Concesionario").executeUpdate();
    }

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
