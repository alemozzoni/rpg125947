package it.unicam.cs.mpgc.rpg125947.model.interfaces;

import it.unicam.cs.mpgc.rpg125947.model.Indizio;

import java.util.Optional;

/**
 * Contratto di un oggetto della scena che il giocatore puo ispezionare e che
 * puo rivelare un {@link Indizio}.
 *
 * <p>Interfaccia minimale (ISP): separa la capacita di "essere ispezionato"
 * dal resto del dominio. Un hotspot puo non rivelare alcun indizio
 * (ispezione "a vuoto"), per questo il risultato e un {@link Optional}.</p>
 */
public interface Ispezionabile {

    /**
     * Ispeziona l'oggetto. La prima ispezione utile rivela l'indizio; le
     * successive non devono duplicarlo (responsabilita dell'implementazione).
     *
     * @return l'eventuale indizio rivelato, altrimenti {@link Optional#empty()}
     */
    Optional<Indizio> ispeziona();

    /**
     * @return {@code true} se l'oggetto e gia stato ispezionato almeno una volta
     */
    boolean giaIspezionato();
}
