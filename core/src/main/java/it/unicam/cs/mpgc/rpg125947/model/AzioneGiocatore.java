package it.unicam.cs.mpgc.rpg125947.model;

/**
 * Le azioni di alto livello che il giocatore puo richiedere e che possono
 * far transitare la partita da una {@link it.unicam.cs.mpgc.rpg125947.model.interfaces.FaseDiGioco}
 * all'altra (pattern State).
 */
public enum AzioneGiocatore {
    /** Avvia un dialogo con un personaggio presente nella stanza. */
    ENTRA_IN_DIALOGO,
    /** Ispeziona un oggetto/hotspot della scena. */
    ISPEZIONA,
    /** Chiude l'interazione corrente (dialogo o ispezione) e torna a esplorare. */
    CHIUDI_INTERAZIONE,
    /** Apre la schermata di formulazione dell'accusa. */
    APRI_ACCUSA,
    /** Conferma l'accusa formulata: porta al finale. */
    CONFERMA_ACCUSA,
    /** Annulla l'accusa e torna all'esplorazione. */
    ANNULLA_ACCUSA
}
