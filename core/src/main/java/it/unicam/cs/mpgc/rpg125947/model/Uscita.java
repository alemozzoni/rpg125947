package it.unicam.cs.mpgc.rpg125947.model;

import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

/**
 * Collegamento navigabile da una stanza verso un'altra.
 *
 * @param idStanzaDestinazione id della stanza raggiungibile
 * @param etichetta            nome leggibile mostrato sul controllo (es. "Foyer")
 * @param direzione            posizione del controllo nell'HUD
 */
public record Uscita(String idStanzaDestinazione, String etichetta, Direzione direzione) {

    public Uscita {
        Validazioni.nonVuota(idStanzaDestinazione, "destinazione uscita");
        Validazioni.nonVuota(etichetta, "etichetta uscita");
        Validazioni.nonNullo(direzione, "direzione uscita");
    }
}
