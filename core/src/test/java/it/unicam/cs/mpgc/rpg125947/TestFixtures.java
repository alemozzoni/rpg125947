package it.unicam.cs.mpgc.rpg125947;

import it.unicam.cs.mpgc.rpg125947.model.Coordinata;
import it.unicam.cs.mpgc.rpg125947.model.Indizio;
import it.unicam.cs.mpgc.rpg125947.model.TipoIndizio;
import it.unicam.cs.mpgc.rpg125947.model.dialogo.Dialogo;
import it.unicam.cs.mpgc.rpg125947.model.personaggio.Sospettato;

import java.util.List;

/**
 * Costruttori di comodo per le entita di dominio usate nei test, per evitare
 * duplicazione (DRY) nella preparazione dei dati di prova.
 */
public final class TestFixtures {

    private TestFixtures() {
    }

    public static Dialogo dialogoVuoto() {
        return new Dialogo("Salve.", List.of());
    }

    public static Sospettato sospettato(String nome) {
        return new Sospettato(nome, "ruolo", nome + ".png", new Coordinata(0.5, 0.5),
                dialogoVuoto(), "un movente", "un alibi");
    }

    public static Indizio indizio(String id) {
        return new Indizio(id, "Indizio " + id, "Descrizione di " + id, TipoIndizio.FISICO);
    }
}
