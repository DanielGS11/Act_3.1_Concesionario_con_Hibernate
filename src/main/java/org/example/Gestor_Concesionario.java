package org.example;

import jakarta.persistence.*;
import org.example.modelos.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

// Esta clase contiene las opciones que se seleccionaran en el menu
public class Gestor_Concesionario {
    /*
    Creo 3 variables: los 2 entitymanager (entityManagerFactory y entityManager) con los que operare con la
    Base de Datos y un boolean llamado 'conectado', que comprobara la conexio con la Base de Datos para no abrir una
    conexion nueva si ya lo esta
     */
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("concesionarioHibernate");
    private final EntityManager em = emf.createEntityManager();
    private boolean conectado = false;

    // Metodo para cargar unos datos de ejemplo en la Base de Datos
    public void cargarDatosIniciales() {
        // Me conecto a la Base de Datos, este metodo auxiliar se llama al principio de cada opcion
        abrirConexion();

        try {
            // Llamo a los 2 metodos auxiliares que limpian e insertan unos datos iniciales en la Base de Datos
            limpiarTablas();

            plantillaDatos();

            // Confirmo la transaccion
            em.getTransaction().commit();

            System.out.println("Datos Iniciales cargados con exito");

            /*
            Si, por algun casual, ocurre un error, hara un rollback para borrar los cambios
             */
        } catch (RuntimeException e) {
            em.getTransaction().rollback();

            System.out.println("Carga de datos cancelada con exito");
            System.out.println(e.getMessage());

            /*
             Este bloque finally estara en practicamente todos los metodos que usen un try/catch para que se ejecute al
             terminar, se encargara de limpiar la cache del entityManager para asi operar con los datos sin necesidad de
             cerrar la conexion, es decir, para que, despues de terminar este metodo, si seguido se selecciona otro sin
             cerrar la conexion, se pueda operar con los datos que este metodo plasmo
             */
        } finally {
            // Este metodo auixiliar es simple relleno (Capricho propio), no afecta en nada
            esperar1Segundo();

            em.clear();
        }
    }

    // Metodo para dar de alta un coche o concesionario
    public void gestionStock(int opc) {
        abrirConexion();
        Scanner sc = new Scanner(System.in);

        switch (opc) {
            // Dar de alta un concesionario
            case 1:
                // Pedimos nombre y direccion
                System.out.print("Introduzca el nombre del concesionario: ");
                String concesionarioName = sc.nextLine();

                System.out.print("Introduzca la direccion del concesionario: ");
                String concesionarioDirection = sc.nextLine();

                // Creamos el nuevo concesionario, lo plasmamos en la Base de Datos y confirmamos transaccion
                Concesionario concesionario = new Concesionario(concesionarioName, concesionarioDirection);

                em.persist(concesionario);

                em.getTransaction().commit();

                System.out.println("Concesionario registrado con exito");

                // Aqui no es necesario el bloque finally, por lo que, al terminar la operacion, hago un clear
                em.clear();
                break;

            // Dar de alta un coche
            case 2:
                // Pido los datos del coche y la id del concesionario al que pertenece
                System.out.print("Introduzca la matricula del coche: ");
                String matricula = sc.nextLine().toUpperCase();

                /*
                 Cada vez que opero con un coche, como debo confirmar que su matricula tenga el formato correcto,
                 he hecho un metodo auxiliar que se encarga de ello y, si no tiene buen formato, informa al usuario y
                 vuelve al menu. Este metodo se llamara cada vez que se pida una matricula de u coche
                 */
                if (!comprobarMatricula(matricula)) {
                    return;
                }

                System.out.print("Introduzca el modelo del coche: ");
                String model = sc.nextLine();

                System.out.print("Introduzca la marca del coche: ");
                String marca = sc.nextLine();

                System.out.print("Introduzca el precio base del coche: ");
                double precioBase = Double.parseDouble(sc.nextLine());

                System.out.print("Introduzca la ID del concesionario al que pertenece el coche: ");
                int idConcesionario = Integer.parseInt(sc.nextLine());

                /*
                Con el metodo find, busco en la Base de Datos y creo un objeto dandole la clase del objeto y clave
                primaria a buscar. Este metodo funciona como un Select, en este caso, busco y creo un objeto
                concesionario con la id recogida anteriormente
                 */
                Concesionario c = em.find(Concesionario.class, idConcesionario);

                /*
                 Compruebo que dicho concesionario exista en la Base de Datos y si no, informo al usuario y vuelvo al
                 menu
                 */
                if (c == null) {
                    System.out.println("No existe el concesionario con ID: " + idConcesionario);
                    return;
                }

                // Comrpobados los datos, creo el objeto coche
                Coche coche = new Coche(matricula, marca, model, precioBase, c);

                try {
                    // Plasmo el coche en la Base de Datos y confirmo cambios
                    em.persist(coche);

                    em.getTransaction().commit();

                    System.out.println("Coche registrado con exito");

                    // cancelo cambios si algo sale mal e informo al usuario
                } catch (RuntimeException e) {
                    em.getTransaction().rollback();

                    System.out.printf("El coche con matricula %s ya existe\n", matricula);
                } finally {
                    em.clear();
                }
                break;

            // Informo cuando el usuario introduzca una opcion no valida en este metodo
            default:
                System.out.println("Por favor, introduzca la opcion correcta (1 o 2)");
                break;
        }
        esperar1Segundo();
    }

    // Metodo para instalar un extra (Equipamiento) o hacer una reparacion a un coche
    public void taller(int opc) {
        abrirConexion();
        Scanner sc = new Scanner(System.in);
        /*
         Estas variables y objeto, al estar fuera de los trys de las opciones, los creo fuera del switch sin inicializar
         para que no haya errores, se inicializaran en la opcion correspondiente
         */
        String matricula;
        int id;
        Coche coche;

        switch (opc) {
            // Instalar un extra
            case 1:
                // Pido la mtricula del coche y la compruebo
                System.out.println("Vamos a instalar un extra a un coche");
                System.out.print("Introduzca la matricula del coche: ");
                matricula = sc.nextLine().toUpperCase();

                if (!comprobarMatricula(matricula)) {
                    return;
                }

                // Busco en la Base de Datos dicho coche, creo el objeto (si no hay, da null) y compruebo que exista
                coche = em.find(Coche.class, matricula);

                if (coche == null) {
                    System.out.println("No existe el coche con la matricula: " + matricula);
                    return;
                }
                // Ahora pido el id del equipamiento y creo el objeto respectivo, ademas de comprobarlo, como el coche
                System.out.print("Introduzca la ID del equipamiento a añadir: ");
                id = Integer.parseInt(sc.nextLine());

                Equipamiento equipamiento = em.find(Equipamiento.class, id);

                if (equipamiento == null) {
                    System.out.println("No existe el equipamiento con lel ID: " + id);
                    return;
                }

                // Compruebo que el coche no tenga dicho equipamiento
                if (coche.getEquipamientos().contains(equipamiento)) {
                    System.out.println("El coche ya tenia el equipamiento " + equipamiento.getNombre());

                } else {
                    try {
                        /*
                         Hechas las comprobaciones, añado el equipamiento a la lista del coche y creo una variable
                         atomica para que se incremente en el flujo que hare, su valor inicial es el precio del coche.
                         NOTA: Al añadir el equipamiento en el coche y plasmarlo en la Base de Datos, se creara un
                         registro en la tabla 'coche_equipamiento' automaticamente
                         */
                        coche.equipamientos.add(equipamiento);

                        AtomicReference<Double> costeActual = new AtomicReference<>(coche.getPrecio_base());

                        /*
                        Hago un flujo que recoga el cste de cada equipamiento del coche y lo sume a la variable atomica
                         */
                        coche.equipamientos.forEach(e ->
                                costeActual.getAndSet(costeActual.get() + e.getCoste())
                        );

                        /*
                         Por ultimo, sobreescribo el coche con merge, confirmo transaccion y muestro el coste actual del
                         coche, recogido en la variable atomica
                         */
                        em.merge(coche);

                        em.getTransaction().commit();

                        System.out.println("Instalacion de extra realizada con exito");

                        System.out.println("Precio Actual del coche: " + costeActual.get());

                        // Rolback si algo sale mal
                    } catch (RuntimeException e) {
                        em.getTransaction().rollback();

                        System.out.println("La operacion fue cancelada. Error: " + e.getMessage());
                    } finally {
                        em.clear();
                    }
                }
            break;

            // Hacer una reparacion a un coche
            case 2:
                // Pido la matricula del coche, la compruebo y compruebo la existencia del coche en la Base de Datos
                System.out.println("Vamos a hacer una reparacion a un coche");
                System.out.print("Introduzca la matricula del coche: ");
                matricula = sc.nextLine().toUpperCase();

                if (!comprobarMatricula(matricula)) {
                    return;
                }

                coche = em.find(Coche.class, matricula);

                if (coche == null) {
                    System.out.println("No existe el coche con la matricula: " + matricula);
                    return;
                }

                // Lo mismo con el mecanico que hara la reparacion
                System.out.print("Introduzca la ID del mecanico que hizo la reparacion: ");
                id = Integer.parseInt(sc.nextLine());

                Mecanico mecanico = em.find(Mecanico.class, id);

                if (mecanico == null) {
                    System.out.println("No existe el mecanico con el ID: " + id);
                    return;
                }

                /*
                 Comprobados los 2, pido la fecha, descripcion y coste de la reparacion que se hara y creo el objeto
                 Reparacion que se plasmara en la Base de Datos
                 */
                System.out.print("Introduzca la fecha en la que se realizo la reparacion (DD-MM-YYYY): ");
                String fechaReparacion = sc.nextLine();

                System.out.print("Introduzca el coste de la reparacion: ");
                double coste = Double.parseDouble(sc.nextLine());

                System.out.print("Aporta una breve descripcion de la reparacion: ");
                String descripcion = sc.nextLine();

                Reparacion reparacion = new Reparacion(LocalDate.parse(fechaReparacion, DateTimeFormatter.ofPattern("dd-MM-yyyy")), coste, descripcion, coche, mecanico);

                try {
                    /*
                    añado dicha reparacion a las listas del coche y el mecanico, los sobreescribo en la Base de Datos
                    y plasmo la reparacion y confirmo cambios
                     */
                    coche.reparaciones.add(reparacion);

                    mecanico.reparaciones.add(reparacion);

                    em.merge(coche);
                    em.merge(mecanico);
                    em.persist(reparacion);

                    em.getTransaction().commit();

                    System.out.println("Reparacion realizada con exito");
                } catch (RuntimeException e) {
                    em.getTransaction().rollback();

                    System.out.println("La operacion fue cancelada. Error: " + e.getMessage());
                } finally {
                    em.clear();
                }
                break;

            default:
                System.out.println("Por favor, introduzca la opcion correcta (1 o 2)");
                break;
        }
        esperar1Segundo();
    }

    // Metodo para hacer una venta de un coche
    public void venta() {
        abrirConexion();
        Scanner sc = new Scanner(System.in);

        /*
         Pido los datos del propietario, concesionario y coche, asi como el precio de la venta, compruebo y hago sus
         respectivos objetos
         */
        System.out.println("Introduzca los siguientes datos que se piden para la venta");
        System.out.print("DNI del nuevo Propietario: ");
        String dni = sc.nextLine().toUpperCase();

        System.out.print("Nombre del nuevo Propietario: ");
        String name = sc.nextLine();
        name = name.toUpperCase().charAt(0) + name.toLowerCase().substring(1, name.length());

        System.out.print("Matricula del Coche a vender: ");
        String matricula = sc.nextLine().toUpperCase();

        System.out.print("Precio pactado de la venta: ");
        double precio = Double.parseDouble(sc.nextLine());

        if (!comprobarMatricula(matricula)) {
            return;
        }

        System.out.print("ID del Concesionario: ");
        long idConcesionario = Integer.parseInt(sc.nextLine());

        Coche coche = em.find(Coche.class, matricula);

        if (coche == null) {
            System.out.println("No existe el coche con la matricula: " + matricula);
            return;
        }

        try {
            TypedQuery<Propietario> q = em.createNamedQuery("Propietario.buscarPropietario", Propietario.class);
            q.setParameter("dni", dni);
            q.setParameter("nombre", name);

            Propietario propietario = q.getSingleResult();

            /*
             Compruebo que el coche nbo tenga propietario (tambien que si lo tiene no sea el que introducimos) y que
             pertenezca al concesionario con la ID introducida
             */
            if (coche.getPropietario() != null) {
                if (coche.getPropietario().getDni().equals(dni)) {
                    System.out.println("Este propietario ya tiene ese coche");

                } else {
                    System.out.println("El coche ya esta vendido");

                }
            } else if (!coche.getConcesionario().getId().equals(idConcesionario)) {
                System.out.println("El coche no pertenece a ese concesionario");

            } else {
                /*
                 Comprobado lo anterior, creo el objeto conceisonario y compruebo que exista en la Base de Datos, y
                 tambien creo el objeto venta con los datos aportados y comprobados
                 */
                Concesionario concesionario = em.find(Concesionario.class, idConcesionario);

                if (concesionario == null) {
                    System.out.println("No existe el concesionario con la ID: " + idConcesionario);
                    return;
                }

                Venta venta = new Venta(LocalDate.now(), precio, concesionario, propietario, coche);

                /*
                Por ultimo, añado el propietario al coche, la venta a la lista del concesionario y el propietario, y
                el coche a la lista del propietario. Luego, sobreescribo con merge y plasmo la venta con persist y
                confirmo los cambios
                 */
                try {
                    coche.setPropietario(propietario);
                    concesionario.ventas.add(venta);
                    propietario.coches.add(coche);
                    propietario.ventas.add(venta);

                    em.merge(concesionario);
                    em.merge(propietario);
                    em.merge(coche);

                    em.persist(venta);

                    em.getTransaction().commit();

                    System.out.println("Venta realizada con exito");
                /*
                Hago 2 catches, 1 para comprobar el propietario, ya que si no existe en la Base de Datos, la TypedQuery
                tirara una excepcionL; y otra para comprobar la propia operacion
                 */
                } catch (RuntimeException e) {
                    em.getTransaction().rollback();

                    System.out.println("La operacion fue cancelada. Error: " + e.getMessage());
                }
            }
        } catch (NoResultException e) {
            System.out.println("No existe un Propietario con esos datos");
        } finally {
            esperar1Segundo();

            em.clear();
        }
    }

    /**
     Ahora vamos con las consultas.
     Consulta para ver el stock de coches que le queda al concesionario

     @param id id (PK) del concesionario a buscar
     */
    public void stockConcesionario(int id) {
        abrirConexion();

        try {
            /*
             Busco el concesionario con la id pasada en el menu y creo una variable atomica que recogera cuanto stock le
             queda
             */
            Concesionario concesionario = em.find(Concesionario.class, id);

            System.out.println("Coches sin vender del consesionario con ID: " + id);

            AtomicInteger stock = new AtomicInteger(0);
            /*
             Filtro los coches sin dueño en la lista del concesionario y por cada 1, imprimo los datos e incremento la
             variable atomica
             */
            concesionario.coches.stream().filter(c -> c.getPropietario() == null).forEach(coche -> {
                stock.incrementAndGet();
                System.out.println(coche);
            });

            // En caso de no quedar stock, informa al usuario
            if (stock.get() == 0) {
                System.out.println("Este concesionario no tiene stock de coches");
            }

        // Catch por si hay un error al no encontrar resultados en el find y llamar al objeto mas adelante
        } catch (NullPointerException e) {
            System.out.println("No existe un concesionario con esa ID");
        } finally {
            esperar1Segundo();

            em.clear();
        }
    }

    /**
     Consulta del historial de reparaciones de un mecanico

     @param id id (PK) del mecanico a buscar
      */
    public void historialMecanico(int id) {
        abrirConexion();

        try {
            /*
            Creo el objeto del mecanico que busco en la Base de Datos segun la id recogida en el menu y compruebo que
            su lista de reparaciones tenga algun registro
             */
            Mecanico mecanico = em.find(Mecanico.class, id);

            System.out.println("Reparaciones realizadas por el Mecanico con ID: " + id);

            if (mecanico.reparaciones.isEmpty()) {
                System.out.println("Este mecanico no ha hecho reparaciones");

            } else {
                // SI lo hay, imprimo cada reparacion que haya hecho
                mecanico.reparaciones.forEach(System.out::println);
            }

        // Catch por si da un error al no encontrar el objeto en la Base de Datos y llamarlo mas adelante
        } catch (NullPointerException e) {
            System.out.println("No existe un Mecanico con esa ID");
        } finally {
            esperar1Segundo();

            em.clear();
        }
    }

    /**
     Consulta de las ventas y total vendido de un concesionario

     @param id id (PK) del Concesionario a buscar
      */
    public void ventasConcesionario(int id) {
        abrirConexion();

        try {
            // Busco en la Base de Datos, creo el objeto concesionario y compruebo que tenga ventas
            Concesionario concesionario = em.find(Concesionario.class, id);

            System.out.println("Ventas del consesionario con ID: " + id);

            if (concesionario.ventas.isEmpty()) {
                System.out.println("Este Concesionario no tiene ventas");

            } else {
                /*
                Si las hay, creo una variable atomica que recogera el precio de cada venta para hacer un total.
                Recorro la lista de ventas del concesionario y recogo el precio de cada venta en dicha variable y,
                cuando termine, muestro el total
                 */
                AtomicReference<Double> totalRecaudado = new AtomicReference<Double>(0.0);

                concesionario.ventas.forEach(venta -> {
                    totalRecaudado.getAndSet(totalRecaudado.get() + venta.getPrecio_final());

                    System.out.println(venta);
                });

                System.out.println("Total Recaudado: " + totalRecaudado.get());
            }

        // Catch por si no existe el concesionario en la Base de Datos y se llama al objeto mas adelante
        } catch (NullPointerException e) {
            System.out.println("No existe un concesionario con esa ID");
        } finally {
            esperar1Segundo();

            em.clear();
        }
    }

    /**
     Consulta sobre el coste actual (coste de la venta, equipamientos y reparaciones) de un coche

     @param matricula matricula (PK) del coche a buscar
     */
    public void costeCoche(String matricula) {
        if (!comprobarMatricula(matricula)) {
            return;
        }

        abrirConexion();

        try {
            /*
             Comprobado el formato de la matricula, busco en la Base de Datos y creo el objeto del coche recogido y
             compruebo si tiene un propietario
             */
            Coche coche = em.find(Coche.class, matricula);

            if (coche.getPropietario() == null) {
                System.out.println("Este coche no tiene un propietario, no se puede calcular su precio actual");
                return;
            }

            /*
             Creo la sentencia de la consulta que recogera la suma del precio de venta, coste de equipamientos y
             reparaciones de dicho coche

             NOTA: COALESCE es un comprobante de nulls (parecido a '?') por el que, si el coche no tiene extras o
             reparaciones, simplemente devuelva un 0, ya que si no saldria una excepcion
             */
            TypedQuery<Double> qCoste = em.createQuery("SELECT SUM(v.precio_final " +
                    "+ COALESCE((SELECT SUM(e.coste) FROM Coche c JOIN c.equipamientos e WHERE c.matricula = :matricula), 0) " +
                    "+ COALESCE((SELECT SUM(r.coste) FROM Reparacion r WHERE r.coche.matricula = :matricula), 0)) " +
                    "FROM Venta v WHERE v.coche.matricula = :matricula", Double.class);

            /*
             Defino la variable de la sentencia, que sera la matricula por la que se busca y recogo el resultado en una
             variable para luego mostrarla por pantalla
             */
            qCoste.setParameter("matricula", matricula);

            double costeActual = qCoste.getSingleResult();

            System.out.printf("Coste actual del Coche con matricula %s (precio del coche, reparaciones y extras): %.2f\n", matricula, costeActual);

        // Catch para si e coche no se encuentra en la Base de Datos y se llama a dicho objeto creado mas adelante
        } catch (NullPointerException e) {
            System.out.println("No existe un coche con esa Matricula");
            System.out.println(e.getMessage());
        } finally {
            esperar1Segundo();

            em.clear();
        }
    }

    // Por ultimo, este metodo simplemente cierra la conexion, asi termina el programa
    public void salir() {
        System.out.println("Adios");

        em.close();
    }

    //----------------------------------------------- METODOS AUXILIARES -----------------------------------------------
    /*
     Este metodo es simple capricho, no influye en nada, simplemente añade una espera de 1 segundo para cuando se
     termina una opcion
     */
    private void esperar1Segundo() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     Metodo auxiliar que comprueba el formato de la matricula

     @param matricula matricula que recoge para confirmar que el formato sea correcto
     */
    private boolean comprobarMatricula(String matricula) {
        /*
         El formato (puesto en expresion regular) es: 4 numeros ('[1-9]{4}') y 3 letras ('{a-zA-Z]{3}')

         NOTA: Las '\\b' en la expresion regular es uina especie de limitador, significa 'unicamente si el dato es asi',
         ya que el usuario podri introducir algo como '12352635abssduiDG' y le daria correcto ya que entre esa secuencia
         en el medio hay un trozo con 4 numeros y 3 letras
         */
        if (matricula.matches("\\b[1-9]{4}[a-zA-Z]{3}\\b")) {
            return true;
        } else {
            System.out.println("Formato de matricula no valido, debe constar de 4 numeros seguidos de 3 letras");

            esperar1Segundo();
            return false;
        }
    }

    // Metodo para comprobar la conexion y abrirla
    private void abrirConexion() {
        // Comprueba si la transacion no esta activa (tras un clear tambien se vuelve inactiva)
        if (!em.getTransaction().isActive()) {
            /*
             Si esta activa y es su primera vez conectandose, muestra un mensaje de conexion a la Base de Datos y pasa
             la variable que confirma la conexion a true, asi evitamos la repeticion del mensaje en cada llamada al
             metodo
             */
            if (!conectado) {
                System.out.println("Conectando a la base de datos...");

                conectado = true;

                System.out.println("Conexion Establecida");
            }

            // Por ultimo, empezamos la transaccion con la Base de Datos
            em.getTransaction().begin();
        }
    }

    // Metodo para limpiar las tablas de la Base de Datos
    private void limpiarTablas() {
        em.createQuery("DELETE FROM Reparacion").executeUpdate();
        em.createQuery("DELETE FROM Venta").executeUpdate();
        em.createQuery("DELETE FROM Coche").executeUpdate();
        em.createQuery("DELETE FROM Equipamiento").executeUpdate();
        em.createQuery("DELETE FROM Mecanico").executeUpdate();
        em.createQuery("DELETE FROM Propietario").executeUpdate();
        em.createQuery("DELETE FROM Concesionario").executeUpdate();
    }

    // Plantilla con los datos iniciales de ejemplo
    private void plantillaDatos() {
        // Cremaos los objetos de cada entidad de cada tabla con sus datos
        Concesionario concesionario = new Concesionario("Concesionario Central", "Av. Principal 1");
        Concesionario concesionario2 = new Concesionario("Concesionario Central 2", "Av. Principal 2");

        Equipamiento aire = new Equipamiento("Aire Acondicionado", 500);
        Equipamiento gps = new Equipamiento("GPS", 300);

        Mecanico mecanico1 = new Mecanico("Carlos", "Motor");
        Mecanico mecanico2 = new Mecanico("Luis", "Electricidad");

        Propietario p1 = new Propietario("Juan Pérez", "12345678A");
        Propietario p2 = new Propietario("Ana López", "87654321B");

        Coche coche1 = new Coche("1234ABC", "Toyota", "Corolla", 20000, concesionario);
        Coche coche2 = new Coche("5678DEF", "Seat", "Ibiza", 15000, concesionario2);
        Coche coche3 = new Coche("4321DEF", "Seat", "Leon", 17500, concesionario);

        // Los plasmo en un 'borrador' a espera de confirmar la introduccion de datos con commit
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
