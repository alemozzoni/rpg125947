package it.unicam.cs.mpgc.rpg125947.persistence;

import it.unicam.cs.mpgc.rpg125947.model.Caso;
import it.unicam.cs.mpgc.rpg125947.model.Partita;

import java.util.List;
import java.util.Optional;

/**
 * Repository (DAO) per la persistenza dello <strong>stato della partita</strong>
 * su memoria non volatile.
 *
 * <p>Astrae il <em>come</em> si salva e si carica: oggi un'implementazione XML,
 * domani potrebbe essere JSON o un database relazionale (JPA), senza impatti sul
 * dominio o sui controller (Dependency Inversion). Il caricamento richiede il
 * {@link Caso} di riferimento per ricollegare gli indizi salvati (per id) alle
 * entita dello scenario.</p>
 */
public interface GameStateRepository {

    /**
     * Salva la partita nello slot indicato (sovrascrivendo se gia esistente).
     *
     * @throws PersistenzaException in caso di errore di scrittura
     */
    void salva(Partita partita, String slot);

    /**
     * Carica la partita dallo slot indicato.
     *
     * @param slot nome dello slot
     * @param caso il caso a cui la partita si riferisce
     * @return la partita ricostruita, o {@link Optional#empty()} se lo slot non esiste
     * @throws PersistenzaException in caso di errore di lettura/parsing
     */
    Optional<Partita> carica(String slot, Caso caso);

    /** @return gli slot di salvataggio attualmente disponibili */
    List<String> slotDisponibili();
}
