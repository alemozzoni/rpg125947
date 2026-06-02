package it.unicam.cs.mpgc.rpg125947.logic;

import it.unicam.cs.mpgc.rpg125947.TestFixtures;
import it.unicam.cs.mpgc.rpg125947.model.Caso;
import it.unicam.cs.mpgc.rpg125947.model.Indizio;
import it.unicam.cs.mpgc.rpg125947.model.accusa.Accusa;
import it.unicam.cs.mpgc.rpg125947.model.accusa.EsitoAccusa;
import it.unicam.cs.mpgc.rpg125947.model.personaggio.Sospettato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica la regola di vittoria (Strategy): serve il colpevole giusto E tutti
 * gli indizi decisivi. Copre i rami critici: successo, accusato sbagliato,
 * prove incomplete.
 */
class ValutatoreAccusaStandardTest {

    private final ValutatoreAccusaStandard valutatore = new ValutatoreAccusaStandard();
    private Sospettato colpevole;
    private Sospettato innocente;
    private Indizio prova1;
    private Indizio prova2;
    private Caso caso;

    @BeforeEach
    void preparaCaso() {
        colpevole = TestFixtures.sospettato("Beatrice");
        innocente = TestFixtures.sospettato("Elena");
        prova1 = TestFixtures.indizio("anacronismo");
        prova2 = TestFixtures.indizio("log_badge");
        Set<Indizio> decisivi = new LinkedHashSet<>(Set.of(prova1, prova2));
        caso = new Caso("Caso di prova", "descrizione", "vittima", "atrio",
                colpevole, decisivi, "Il caso e risolto.");
    }

    @Test
    void accusaCorrettaConTutteLeProveHaSuccesso() {
        EsitoAccusa esito = valutatore.valuta(new Accusa(colpevole, Set.of(prova1, prova2)), caso);
        assertTrue(esito.successo());
    }

    @Test
    void accusaConColpevoleSbagliatoFallisce() {
        EsitoAccusa esito = valutatore.valuta(new Accusa(innocente, Set.of(prova1, prova2)), caso);
        assertFalse(esito.successo());
    }

    @Test
    void accusaConProveIncompleteFallisce() {
        EsitoAccusa esito = valutatore.valuta(new Accusa(colpevole, Set.of(prova1)), caso);
        assertFalse(esito.successo());
    }
}
