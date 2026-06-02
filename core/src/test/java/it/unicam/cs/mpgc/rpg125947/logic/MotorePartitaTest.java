package it.unicam.cs.mpgc.rpg125947.logic;

import it.unicam.cs.mpgc.rpg125947.logic.fase.FaseEsplorazione;
import it.unicam.cs.mpgc.rpg125947.logic.fase.FaseFinale;
import it.unicam.cs.mpgc.rpg125947.model.Caso;
import it.unicam.cs.mpgc.rpg125947.model.Hotspot;
import it.unicam.cs.mpgc.rpg125947.model.Investigatore;
import it.unicam.cs.mpgc.rpg125947.model.Partita;
import it.unicam.cs.mpgc.rpg125947.model.Stanza;
import it.unicam.cs.mpgc.rpg125947.model.accusa.EsitoAccusa;
import it.unicam.cs.mpgc.rpg125947.model.dialogo.Testimonianza;
import it.unicam.cs.mpgc.rpg125947.persistence.xml.CaricatoreCasoXml;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test di integrazione del motore di gioco sullo scenario reale: si gioca il
 * caso dall'inizio alla soluzione, verificando le meccaniche principali
 * (stanza chiusa a chiave, raccolta indizi, accusa corretta -> vittoria).
 */
class MotorePartitaTest {

    private Caso caso;
    private MotorePartita motore;
    private Partita partita;

    @BeforeEach
    void avvia() {
        caso = new CaricatoreCasoXml().carica();
        motore = new MotorePartita();
        partita = motore.nuovaPartita(new Investigatore("Investigatrice"), caso);
    }

    @Test
    void laPartitaIniziaInEsplorazioneNellAtrio() {
        assertSame(FaseEsplorazione.INSTANCE, partita.getFase());
        assertEquals("atrio", partita.getStanzaCorrente().getId());
    }

    @Test
    void lUfficioSiApreSoloDopoAverOttenutoLaChiave() {
        Stanza ufficio = caso.getStanza("ufficio");
        assertFalse(motore.puoEntrare(ufficio), "all'inizio l'ufficio e chiuso");

        Testimonianza chiave = testimonianzaCheRivela("chiave_ufficio");
        motore.raccogli(chiave);

        assertTrue(motore.puoEntrare(ufficio), "ottenuta la chiave, l'ufficio e accessibile");
    }

    @Test
    void raccogliendoLeProveDecisiveLAccusaCorrettaVince() {
        ispeziona("sala_linguaggi", "teca");      // anacronismo
        ispeziona("sala_server", "terminale");    // log_badge
        ispeziona("ufficio", "scrivania");        // perizia

        EsitoAccusa esito = motore.accusa(caso.getColpevole(), caso.getIndiziDecisivi());

        assertTrue(esito.successo());
        assertTrue(partita.isConclusa());
        assertSame(FaseFinale.INSTANCE, partita.getFase());
    }

    @Test
    void accusaSenzaProveDecisiveFallisce() {
        EsitoAccusa esito = motore.accusa(caso.getColpevole(), java.util.Set.of());
        assertFalse(esito.successo());
    }

    // ===== helper di gioco =====

    private void ispeziona(String idStanza, String idHotspot) {
        Hotspot hotspot = caso.getStanza(idStanza).getHotspot().stream()
                .filter(h -> h.getId().equals(idHotspot))
                .findFirst().orElseThrow();
        motore.ispeziona(hotspot);
    }

    private Testimonianza testimonianzaCheRivela(String idIndizio) {
        return caso.getStanze().stream()
                .flatMap(s -> s.getPersonaggi().stream())
                .flatMap(p -> p.testimonianze().stream())
                .filter(t -> t.indizioRivelato().map(idIndizio::equals).orElse(false))
                .findFirst().orElseThrow();
    }
}
