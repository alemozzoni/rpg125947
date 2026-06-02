package it.unicam.cs.mpgc.rpg125947.persistence.xml;

import it.unicam.cs.mpgc.rpg125947.logic.MotorePartita;
import it.unicam.cs.mpgc.rpg125947.model.Caso;
import it.unicam.cs.mpgc.rpg125947.model.Indizio;
import it.unicam.cs.mpgc.rpg125947.model.Investigatore;
import it.unicam.cs.mpgc.rpg125947.model.Partita;
import it.unicam.cs.mpgc.rpg125947.persistence.GameStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica il round-trip salva -> carica della partita su XML: i dati salvati
 * vengono ricostruiti fedelmente.
 */
class XmlGameStateRepositoryTest {

    @TempDir
    Path cartellaTemporanea;

    private Caso caso;
    private GameStateRepository repository;

    @BeforeEach
    void prepara() {
        caso = new CaricatoreCasoXml().carica();
        repository = new XmlGameStateRepository(cartellaTemporanea);
    }

    @Test
    void salvaECaricaRiproduceLoStato() {
        MotorePartita motore = new MotorePartita();
        Partita partita = motore.nuovaPartita(new Investigatore("Sherlock"), caso);
        Indizio anacronismo = caso.getIndizio("anacronismo").orElseThrow();
        partita.getTaccuino().registra(anacronismo);
        partita.muoviVerso(caso.getStanza("sala_server"));

        repository.salva(partita, "slot1");
        Optional<Partita> ricaricata = repository.carica("slot1", caso);

        assertTrue(ricaricata.isPresent());
        Partita q = ricaricata.get();
        assertEquals("Sherlock", q.getInvestigatore().getNome());
        assertEquals("sala_server", q.getStanzaCorrente().getId());
        assertTrue(q.getTaccuino().contiene(anacronismo), "l'indizio salvato e stato ricostruito");
    }

    @Test
    void caricareUnoSlotInesistenteRestituisceVuoto() {
        assertTrue(repository.carica("inesistente", caso).isEmpty());
    }

    @Test
    void gliSlotSalvatiVengonoElencati() {
        Partita partita = new MotorePartita().nuovaPartita(new Investigatore("Poirot"), caso);
        repository.salva(partita, "slotA");
        repository.salva(partita, "slotB");
        assertEquals(java.util.List.of("slotA", "slotB"), repository.slotDisponibili());
    }
}
