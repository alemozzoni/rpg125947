package it.unicam.cs.mpgc.rpg125947.model.interfaces;

import it.unicam.cs.mpgc.rpg125947.model.AzioneGiocatore;
import it.unicam.cs.mpgc.rpg125947.model.Partita;

/**
 * Stato del gioco nel pattern <em>State</em>.
 *
 * <p>Ogni fase investigativa (esplorazione, dialogo, ispezione, accusa, finale)
 * reagisce diversamente alle azioni del giocatore decidendo la fase successiva.
 * Aggiungere una nuova fase significa creare una nuova implementazione, senza
 * modificare quelle esistenti (Open/Closed Principle).</p>
 */
public interface FaseDiGioco {

    /** @return nome leggibile della fase (utile per HUD e test) */
    String nome();

    /**
     * Invocato quando si entra in questa fase. Permette eventuali
     * inizializzazioni; l'implementazione di default non fa nulla.
     *
     * @param partita la partita corrente
     */
    default void onIngresso(Partita partita) {
        // hook opzionale
    }

    /**
     * Gestisce un'azione del giocatore e restituisce la fase risultante.
     *
     * @param azione  l'azione richiesta dal giocatore
     * @param partita la partita corrente
     * @return la fase in cui transitare (puo essere {@code this} se l'azione
     *         non comporta cambi di stato)
     */
    FaseDiGioco gestisci(AzioneGiocatore azione, Partita partita);
}
