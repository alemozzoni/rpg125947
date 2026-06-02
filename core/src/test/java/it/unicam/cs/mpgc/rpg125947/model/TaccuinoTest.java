package it.unicam.cs.mpgc.rpg125947.model;

import it.unicam.cs.mpgc.rpg125947.model.dialogo.Testimonianza;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica le strutture dati del {@link Taccuino}: il {@link java.util.Set}
 * degli indizi non ammette duplicati e le testimonianze sono indicizzate per fonte.
 */
class TaccuinoTest {

    private final Indizio indizio = new Indizio("anacronismo", "Anacronismo", "descrizione", TipoIndizio.FISICO);

    @Test
    void loStessoIndizioNonVieneDuplicato() {
        Taccuino taccuino = new Taccuino();
        assertTrue(taccuino.registra(indizio), "primo inserimento riuscito");
        assertFalse(taccuino.registra(indizio), "secondo inserimento ignorato (Set)");
        assertEquals(1, taccuino.getIndizi().size());
        assertTrue(taccuino.contiene(indizio));
    }

    @Test
    void leTestimonianzeSonoRaggruppatePerFonte() {
        Taccuino taccuino = new Taccuino();
        taccuino.registra(new Testimonianza("Marco", "I log sono blindati."));
        taccuino.registra(new Testimonianza("Marco", "Hash a catena."));
        taccuino.registra(new Testimonianza("Giulia", "Beatrice e uscita."));

        assertEquals(2, taccuino.getTestimonianzePerFonte().get("Marco").size());
        assertEquals(1, taccuino.getTestimonianzePerFonte().get("Giulia").size());
    }
}
