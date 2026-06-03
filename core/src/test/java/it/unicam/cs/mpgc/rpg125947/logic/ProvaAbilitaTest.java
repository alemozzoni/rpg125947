package it.unicam.cs.mpgc.rpg125947.logic;

import it.unicam.cs.mpgc.rpg125947.model.Caso;
import it.unicam.cs.mpgc.rpg125947.model.Hotspot;
import it.unicam.cs.mpgc.rpg125947.model.Investigatore;
import it.unicam.cs.mpgc.rpg125947.model.Partita;
import it.unicam.cs.mpgc.rpg125947.model.interfaces.RisolutoreProva;
import it.unicam.cs.mpgc.rpg125947.persistence.xml.CaricatoreCasoXml;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica gli skill check sull'ispezione: con una prova fallita l'indizio non
 * viene scoperto (e si puo ritentare); con una prova superata l'indizio entra
 * nel taccuino e accredita esperienza.
 */
class ProvaAbilitaTest {

    private Caso caso;

    @BeforeEach
    void prepara() {
        caso = new CaricatoreCasoXml().carica();
    }

    /** La "teca" della Sala dei Linguaggi e un hotspot con prova (OSSERVAZIONE 12). */
    private Hotspot teca() {
        return caso.getStanza("sala_linguaggi").getHotspot().stream()
                .filter(h -> h.getId().equals("teca"))
                .findFirst().orElseThrow();
    }

    @Test
    void laTecaRichiedeUnaProvaDiAbilita() {
        assertTrue(teca().richiedeProva());
    }

    @Test
    void provaFallitaNonRivelaLIndizioNeDaEsperienza() {
        MotorePartita motore = new MotorePartita(new ValutatoreAccusaStandard(), RisolutoreProva.sempreFallita());
        Partita partita = motore.nuovaPartita(new Investigatore("Novizio"), caso);

        RisultatoInterazione esito = motore.ispeziona(teca());

        assertTrue(esito.provaFallita());
        assertTrue(esito.indizioScoperto().isEmpty());
        assertEquals(0, esito.xpGuadagnati());
        assertTrue(partita.getTaccuino().getIndizi().isEmpty(), "il taccuino resta vuoto");
    }

    @Test
    void provaSuperataRivelaLIndizioEAccreditaEsperienza() {
        MotorePartita motore = new MotorePartita(new ValutatoreAccusaStandard(), RisolutoreProva.sempreSuperata());
        Partita partita = motore.nuovaPartita(new Investigatore("Esperto"), caso);

        RisultatoInterazione esito = motore.ispeziona(teca());

        assertTrue(esito.provaTentata().isPresent());
        assertTrue(esito.provaTentata().get().superata());
        assertEquals("anacronismo", esito.indizioScoperto().orElseThrow().getId());
        assertEquals(MotorePartita.XP_PER_INDIZIO, esito.xpGuadagnati());
        assertEquals(MotorePartita.XP_PER_INDIZIO, partita.getInvestigatore().getEsperienza());
    }

    @Test
    void riIspezionareNonDuplicaLEsperienza() {
        MotorePartita motore = new MotorePartita(new ValutatoreAccusaStandard(), RisolutoreProva.sempreSuperata());
        motore.nuovaPartita(new Investigatore("Esperto"), caso);
        Hotspot teca = teca();

        motore.ispeziona(teca);
        RisultatoInterazione seconda = motore.ispeziona(teca);

        assertEquals(0, seconda.xpGuadagnati(), "un indizio gia raccolto non ridà esperienza");
        assertFalse(seconda.salitoDiLivello());
    }
}
