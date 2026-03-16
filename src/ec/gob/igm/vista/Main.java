package ec.gob.igm.vista;

import ec.gob.igm.dao.DataAccessDBosco;
import ec.gob.igm.model.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataAccessDBosco dao = new DataAccessDBosco();

        System.out.println("=== PROVINCIAS ===");
        List<Provincia> provincias = dao.getProvincias();
        for (Provincia p : provincias) {
            System.out.println(p);
        }

        System.out.println("\n=== CANTONES DE MORONA SANTIAGO (14) ===");
        List<Canton> cantones = dao.getCantonesByProvincia(14);
        for (Canton c : cantones) {
            System.out.println(c);
        }

        System.out.println("\n=== CIRCUNSCRIPCIONES DE SEVILLA (927) ===");
        List<Circunscripcion> circs = dao.getCircunscripcionesByCanton(927);
        for (Circunscripcion ci : circs) {
            System.out.println(ci);
        }

        System.out.println("\n=== PARROQUIAS (canton=927, circ=0) ===");
        List<Parroquia> parroquias = dao.getParroquiasByCircunscripcion(927, 0);
        for (Parroquia p : parroquias) {
            System.out.println(p);
        }

        System.out.println("\n=== ZONAS (canton=927, circ=0, parroquia=primera) ===");
        if (!parroquias.isEmpty()) {
            int codParroquia = parroquias.get(0).getCodParroquia();
            List<Zona> zonas = dao.getZonasByParroquia(927, 0, codParroquia);
            for (Zona z : zonas) {
                System.out.println(z);
            }
        }
    }
}