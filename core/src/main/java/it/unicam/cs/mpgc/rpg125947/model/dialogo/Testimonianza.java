package it.unicam.cs.mpgc.rpg125947.model.dialogo;

import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

import java.util.Optional;

/**
 * Una dichiarazione rilasciata da un personaggio durante un dialogo.
 *
 * <p>Una testimonianza puo facoltativamente <em>rivelare un indizio</em>: in tal
 * caso porta l'id dell'indizio che, raccolta la testimonianza, va registrato nel
 * taccuino. L'opzionalita e modellata esplicitamente da {@link #indizioRivelato()}.</p>
 *
 * @param fonte             nome del personaggio che parla
 * @param testo             contenuto della dichiarazione
 * @param idIndizioRivelato id dell'eventuale indizio rivelato (puo essere {@code null})
 */
public record Testimonianza(String fonte, String testo, String idIndizioRivelato) {

    public Testimonianza {
        Validazioni.nonVuota(fonte, "fonte testimonianza");
        Validazioni.nonVuota(testo, "testo testimonianza");
        // idIndizioRivelato e facoltativo: nessuna validazione obbligatoria
    }

    /** Costruttore di comodo per una testimonianza che non rivela indizi. */
    public Testimonianza(String fonte, String testo) {
        this(fonte, testo, null);
    }

    /**
     * @return l'id dell'indizio rivelato, se presente
     */
    public Optional<String> indizioRivelato() {
        return Optional.ofNullable(idIndizioRivelato).filter(s -> !s.isBlank());
    }
}
