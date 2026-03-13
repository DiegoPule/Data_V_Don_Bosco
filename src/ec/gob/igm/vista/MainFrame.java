package ec.gob.igm.vista;

import ec.gob.igm.dao.DataAccessDBosco;
import ec.gob.igm.model.Junta;
import java.util.List;

/**
 * @author PULE_DIEGO
 */
public class MainFrame {

    public static void main(String[] args) {
        DataAccessDBosco dao = new DataAccessDBosco();
        List<Junta> juntas = dao.getJuntas();
        System.out.println("Total juntas encontradas: " + juntas.size());
        for (Junta j : juntas) {
            System.out.println(j.toString());
        }
    }
}