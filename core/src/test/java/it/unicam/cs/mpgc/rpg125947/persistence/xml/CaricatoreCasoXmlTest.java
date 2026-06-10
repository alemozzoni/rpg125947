package it.unicam.cs.mpgc.rpg125947.persistence.xml;

import it.unicam.cs.mpgc.rpg125947.model.Attributo;
import it.unicam.cs.mpgc.rpg125947.model.Caso;
import it.unicam.cs.mpgc.rpg125947.model.Indizio;
import it.unicam.cs.mpgc.rpg125947.model.dialogo.Dialogo;
import it.unicam.cs.mpgc.rpg125947.model.dialogo.OpzioneDialogo;
import it.unicam.cs.mpgc.rpg125947.model.personaggio.Personaggio;
import it.unicam.cs.mpgc.rpg125947.persistence.PersistenzaException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica il caricamento dello scenario via DOM + XSD + XPath: i dati
 * principali del caso sono corretti e uno scenario non valido viene rifiutato.
 */
class CaricatoreCasoXmlTest {

    @Test
    void caricaLoScenarioPredefinitoConIDatiAttesi() {
        Caso caso = new CaricatoreCasoXml().carica();

        assertEquals("Delitto al Museo della Computazione", caso.getTitolo());
        assertEquals("atrio", caso.getStanzaIniziale().getId());
        assertEquals(6, caso.getStanze().size(), "il museo ha sei sale");
        assertEquals("Beatrice Lovato", caso.getColpevole().getNome());

        Set<String> idDecisivi = caso.getIndiziDecisivi().stream()
                .map(Indizio::getId)
                .collect(Collectors.toSet());
        assertEquals(Set.of("anacronismo", "log_badge", "perizia"), idDecisivi);
    }

    @Test
    void laStanzaInizialeContieneLaGuardia() {
        Caso caso = new CaricatoreCasoXml().carica();
        boolean cEGuardia = caso.getStanzaIniziale().getPersonaggi().stream()
                .anyMatch(p -> p.getNome().equals("Tommaso Greco"));
        assertTrue(cEGuardia);
    }

    @Test
    void lUfficioEChiusoAChiave() {
        Caso caso = new CaricatoreCasoXml().carica();
        assertTrue(caso.getStanza("ufficio").isChiusaAChiave());
    }

    @Test
    void ogniStileVedeLaDomandaUniversaleDellaChiaveEUnaDedicata() {
        Caso caso = new CaricatoreCasoXml().carica();
        Personaggio guardia = caso.getStanzaIniziale().getPersonaggi().stream()
                .filter(p -> p.getNome().equals("Tommaso Greco"))
                .findFirst().orElseThrow();
        Dialogo dialogo = guardia.avviaDialogo();

        for (Attributo stile : Attributo.values()) {
            List<OpzioneDialogo> opzioni = dialogo.opzioniPer(stile);
            // La chiave dell'ufficio (domanda universale) resta accessibile a ogni profilo.
            boolean offreChiave = opzioni.stream().anyMatch(o -> o.risposta()
                    .indizioRivelato().map("chiave_ufficio"::equals).orElse(false));
            assertTrue(offreChiave, "lo stile " + stile + " deve poter ottenere la chiave");
            // E ciascuno sblocca almeno una domanda riservata al proprio stile.
            assertTrue(opzioni.stream().anyMatch(o -> o.stileRichiesto().isPresent()),
                    "lo stile " + stile + " deve avere una domanda dedicata");
        }
    }

    @Test
    void unoScenarioNonValidoVieneRifiutato() {
        CaricatoreCasoXml caricatore = new CaricatoreCasoXml(
                "/it/unicam/cs/mpgc/rpg125947/scenario/caso_invalido.xml",
                "/it/unicam/cs/mpgc/rpg125947/scenario/scenario.xsd");
        assertThrows(PersistenzaException.class, caricatore::carica);
    }
}
