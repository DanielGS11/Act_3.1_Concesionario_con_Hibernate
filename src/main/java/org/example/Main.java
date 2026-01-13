package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        menu();
    }

    public static void menu() {
        Gestor_Concesionario g = new Gestor_Concesionario();

        Scanner sc = new Scanner(System.in);

        int ans = 0;

        while (ans != 9) {
            System.out.print("""
                    \n--------------- CONCESIONARIO DANIEL ---------------
                    - 1) Cargar Datos Iniciales de la Base de Datos
                    - 2) Registrar un Concesionario o Coche nuevo
                    - 3) Taller: Instalar un Extra o Reparar un Coche
                    - 4) Hacer una Venta
                    - 5) Consultar el stock de un Concesionario
                    - 6) Consultar el historial de un Mecanico
                    - 7) Consultar las ventas de un Concesionario
                    - 8) Consultar el coste actual de un Coche
                    - 9) Salir
                    
                    Introduzca el NUMERO de la Opcion (1 - 9):\s""");

            ans = Integer.parseInt(sc.nextLine());

            System.lineSeparator();

            switch (ans) {
                case 1:
                    g.cargarDatosIniciales();
                    break;

                case 2:
                    System.out.print("""
                            - 1) Registrar un Concesionario
                            - 2) Registrar un Coche
                            
                            Introduzca el NUMERO de la Opcion (1 o 2):\s""");
                    g.gestionStock(Integer.parseInt(sc.nextLine()));
                    break;

                case 3:
                    System.out.print("""
                            - 1) Instalar un extra a un Coche
                            - 2) Reparar un Coche
                            
                            Introduzca el NUMERO de la Opcion (1 o 2):\s""");
                    g.taller(Integer.parseInt(sc.nextLine()));
                    break;

                case 4:
                    g.venta();
                    break;

                case 5:
                    System.out.print("Introduzca la ID del Concesionario a consultar: ");
                    g.stockConcesionario(Integer.parseInt(sc.nextLine()));
                    break;

                case 6:
                    System.out.print("Introduzca la ID del Mecanico a consultar: ");
                    g.historialMecanico(Integer.parseInt(sc.nextLine()));
                    break;

                case 7:
                    System.out.print("Introduzca la ID del Concesionario a consultar: ");
                    g.ventasConcesionario(Integer.parseInt(sc.nextLine()));
                    break;

                case 8:
                    System.out.print("Introduzca la MATRICULA del Coche a consultar: ");
                    g.costeCoche(sc.nextLine());
                    break;

                case 9:
                    g.salir();
                    break;

                default:
                    System.out.println("Por favor, seleccione una opcion valida (numero del 1 al 9)");
                    break;
            }
        }
    }
}