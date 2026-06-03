package it.unicam.cs.mpgc.rpg125947.model;

/**
 * Le caratteristiche (statistiche) dell'investigatore controllato dal giocatore.
 *
 * <p>Sono il cuore della componente <em>di ruolo</em>: determinano la riuscita
 * delle prove di abilita ({@link it.unicam.cs.mpgc.rpg125947.model.interfaces.RisolutoreProva})
 * durante ispezioni e interrogatori e crescono con la progressione del
 * personaggio (esperienza e livelli).</p>
 */
public enum Attributo {

    /** Acume visivo: notare dettagli, anacronismi, tracce fisiche. */
    OSSERVAZIONE("Osservazione"),
    /** Fiuto investigativo: cogliere moventi e collegamenti non evidenti. */
    INTUITO("Intuito"),
    /** Capacita dialettica: convincere i testimoni reticenti a parlare. */
    ELOQUENZA("Eloquenza"),
    /** Rigore deduttivo: ricostruire cronologie e smontare alibi. */
    LOGICA("Logica");

    private final String etichetta;

    Attributo(String etichetta) {
        this.etichetta = etichetta;
    }

    /** @return il nome leggibile dell'attributo (per HUD e taccuino) */
    public String etichetta() {
        return etichetta;
    }
}
