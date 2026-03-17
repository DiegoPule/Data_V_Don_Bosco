package ec.gob.igm.vista;

import ec.gob.igm.dao.DataAccessDBosco;
import ec.gob.igm.generador.GlobalResources;
import ec.gob.igm.model.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
    DataAccessDBosco dao = new DataAccessDBosco();
    List<Junta> juntas = dao.getJuntas();

    System.out.println("=== CREANDO ESTRUCTURA DE CARPETAS ===");
    int resultado = GlobalResources.crearEstructuraCompleta(juntas);
    System.out.println("\nTotal juntas procesadas: " + resultado);
}
}