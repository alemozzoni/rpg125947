package it.unicam.cs.mpgc.rpg125947.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifica l'identita di dominio di {@link Indizio}: due indizi sono uguali se
 * e solo se condividono l'id, e il contratto equals/hashCode e rispettato.
 */
class IndizioTest {

    @Test
    void dueIndiziConStessoIdSonoUguali() {
        Indizio a = new Indizio("x", "Nome A", "Descr A", TipoIndizio.FISICO);
        Indizio b = new Indizio("x", "Nome diverso", "Descr diversa", TipoIndizio.DOCUMENTALE);
        assertEquals(a, b, "l'identita e l'id, non gli altri campi");
        assertEquals(a.hashCode(), b.hashCode(), "hashCode coerente con equals");
    }

    @Test
    void indiziConIdDiversiSonoDiversi() {
        Indizio a = new Indizio("x", "Nome", "Descr", TipoIndizio.FISICO);
        Indizio c = new Indizio("y", "Nome", "Descr", TipoIndizio.FISICO);
        assertNotEquals(a, c);
    }

    @Test
    void idVuotoVieneRifiutato() {
        assertThrows(IllegalArgumentException.class,
                () -> new Indizio(" ", "Nome", "Descr", TipoIndizio.FISICO));
    }
}
