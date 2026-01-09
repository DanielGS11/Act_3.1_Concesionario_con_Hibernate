package org.example;

import jakarta.persistence.*;
import org.example.modelos.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
                    Query q = em.createNamedQuery("Concesionario.buscarPorID");
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

    public void taller(int opc) {
        abrirConexion();
        Scanner sc = new Scanner(System.in);

        switch (opc) {
            case 1:
                try {
                    System.out.println("Vamos a hacer una reparacion a un coche");
                    System.out.print("Introduzca la matricula del coche: ");
                    String matricula = sc.nextLine().toUpperCase();

                    Query q = em.createNamedQuery("Coche.buscarPorMatricula");
                    q.setParameter("matricula", matricula);

                    Coche coche = (Coche) q.getSingleResult();

                    try {
                        System.out.print("Introduzca la ID del equipamiento a añadir: ");
                        String id = sc.nextLine();

                        q = em.createNamedQuery("Equipamiento.buscarPorID");
                        q.setParameter("id", id);

                        Equipamiento equipamiento = (Equipamiento) q.getSingleResult();

                        if (coche.getEquipamientos().contains(equipamiento)) {
                            System.out.println("El coche ya tenia el equipamiento " + equipamiento.getNombre());

                        } else {
                            coche.equipamientos.add(equipamiento);

                            coche.setPrecio_base(coche.getPrecio_base() + equipamiento.getCoste());

                            System.out.println("Precio Actual del coche: " + coche.getPrecio_base());

                            em.merge(coche);

                            em.getTransaction().commit();
                        }
                    } catch (NoResultException e) {
                        System.out.println("No existe un equipamiento con ese ID");
                    }
                } catch (NoResultException e) {
                    System.out.println("No existe un coche con esa matricula");
                }
                break;

            case 2:
                try {
                    System.out.println("Vamos a hacer una reparacion a un coche");
                    System.out.print("Introduzca la matricula del coche: ");
                    String matricula = sc.nextLine().toUpperCase();

                    Query q = em.createNamedQuery("Coche.buscarPorMatricula");
                    q.setParameter("matricula", matricula);

                    Coche coche = (Coche) q.getSingleResult();

                    try {
                        System.out.print("Introduzca la ID del mecanico que hizo la reparacion: ");
                        String id = sc.nextLine();

                        q = em.createNamedQuery("Mecanico.buscarPorID");
                        q.setParameter("id", id);

                        Mecanico mecanico = (Mecanico) q.getSingleResult();

                        System.out.print("Introduzca la fecha en la que se realizo la reparacion (DD-MM-YYYY): ");
                        String fechaReparacion = sc.nextLine();

                        System.out.print("Introduzca el coste de la reparacion: ");
                        double coste = Double.parseDouble(sc.nextLine());

                        System.out.print("Aporta una breve descripcion de la reparacion: ");
                        String descripcion = sc.nextLine();

                        Reparacion reparacion = new Reparacion(LocalDate.parse(fechaReparacion, DateTimeFormatter.ofPattern("dd-MM-yyyy")), coste, descripcion, coche, mecanico);

                        coche.reparaciones.add(reparacion);

                        mecanico.reparaciones.add(reparacion);

                        em.merge(coche);
                        em.merge(mecanico);
                        em.persist(reparacion);

                        em.getTransaction().commit();
                    } catch (NoResultException e) {
                        System.out.println("No existe un mecanico con ese ID");
                    }
                } catch (NoResultException e) {
                    System.out.println("No existe un coche con esa matricula");
                }
                break;

            default:
                System.out.println("Por favor, introduzca la opcion correcta");
                break;
        }
    }

    public void venta() {
        abrirConexion();

        try {
            Scanner sc = new Scanner(System.in);

            System.out.println("Introduzca los siguientes datos que se piden para la venta");
            System.out.print("DNI del nuevo Propietario: ");
            String dni = sc.nextLine().toUpperCase();

            System.out.print("Nombre del nuevo Propietario: ");
            String name = sc.nextLine();
            name = name.toUpperCase().charAt(0) + name.toLowerCase().substring(1, name.length());

            System.out.print("Matricula del Coche a vender: ");
            String matricula = sc.nextLine().toUpperCase();

            System.out.print("ID del Concesionario: ");
            long idConcesionario = Integer.parseInt(sc.nextLine());

            Query q = em.createNamedQuery("Coche.buscarPorMatricula");
            q.setParameter("matricula", matricula);

            Coche coche = (Coche) q.getSingleResult();

            try {
                if (coche.getPropietario() != null) {
                    if (coche.getPropietario().getDni().equals(dni)) {
                        System.out.println("Este propietario ya tiene ese coche");

                    } else {
                        System.out.println("El coche ya esta vendido");

                    }
                } else if (!coche.getConcesionario().getId().equals(idConcesionario)) {
                    System.out.println("El coche no pertenece a ese concesionario");

                } else {
                    q = em.createNamedQuery("Propietario.buscarPropietario");
                    q.setParameter("dni", dni);
                    q.setParameter("nombre", name);

                    Propietario propietario = (Propietario) q.getSingleResult();

                    q = em.createNamedQuery("Concesionario.buscarPorID");
                    q.setParameter("id", idConcesionario);

                    Concesionario concesionario = (Concesionario) q.getSingleResult();

                    Venta venta = new Venta(LocalDate.now(), coche.getPrecio_base(), concesionario, propietario, coche);

                    concesionario.coches.remove(coche);
                    concesionario.ventas.add(venta);
                    propietario.coches.add(coche);
                    propietario.ventas.add(venta);
                    coche.setPropietario(propietario);

                    em.merge(concesionario);
                    em.merge(propietario);
                    em.merge(coche);

                    em.persist(venta);

                    em.getTransaction().commit();
                }
            } catch (RuntimeException e) {
                em.getTransaction().rollback();

                System.out.println("La operacion fue cancelada. Error: " + e.getMessage());
            }
        } catch (NoResultException e) {
            System.out.println("No se encontro el coche");
        }
    }

    public void stockConcesionario(int id) {
abrirConexion();

        try {
            Query q = em.createNamedQuery("Concesionario.buscarPorID");
            q.setParameter("id", id);

            Concesionario concesionario = (Concesionario) q.getSingleResult();

            System.out.println("Coches sin vender del consesionario con ID: " + id);
            concesionario.coches.stream().filter(c -> c.getPropietario() == null).forEach(System.out::println);

        } catch (NoResultException e) {
            System.out.println("No existe un concesionario con esa ID");
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
        Concesionario concesionario2 = new Concesionario("Concesionario Central 2", "Av. Principal 2");

        Equipamiento aire = new Equipamiento("Aire Acondicionado", 500);
        Equipamiento gps = new Equipamiento("GPS", 300);

        Mecanico mecanico1 = new Mecanico("Carlos", "Motor");
        Mecanico mecanico2 = new Mecanico("Luis", "Electricidad");

        Propietario p1 = new Propietario("Juan Pérez", "12345678A");
        Propietario p2 = new Propietario("Ana López", "87654321B");

        Coche coche1 = new Coche("1234ABC", "Toyota", "Corolla", 20000);
        Coche coche2 = new Coche("5678DEF", "Seat", "Ibiza", 15000);
        Coche coche3 = new Coche("4321DEF", "Seat", "Leon", 17500);

        coche1.setConcesionario(concesionario);
        coche2.setConcesionario(concesionario2);
        coche3.setConcesionario(concesionario);

        em.persist(concesionario);
        em.persist(concesionario2);
        em.persist(p1);
        em.persist(p2);
        em.persist(coche1);
        em.persist(coche2);
        em.persist(coche3);
        em.persist(aire);
        em.persist(gps);
        em.persist(mecanico1);
        em.persist(mecanico2);
    }
}
