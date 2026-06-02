package it.unicam.cs.mpgc.rpg125947.persistence;

import it.unicam.cs.mpgc.rpg125947.model.Caso;

/**
 * Factory di scenario: costruisce un {@link Caso} a partire da una fonte
 * esterna, nascondendone il formato.
 *
 * <p>E un'astrazione (Dependency Inversion): il resto dell'applicazione dipende
 * da questo contratto, non dall'implementazione XML. Cambiare formato (JSON, DB)
 * significa fornire un nuovo {@code ScenarioLoader}, senza toccare dominio ne UI.</p>
 */
public interface ScenarioLoader {

    /**
     * @return il caso caricato e validato
     * @throws PersistenzaException se la fonte e assente o non valida
     */
    Caso carica();
}
