package it.unicam.cs.mpgc.rpg125947.model.prova;

import it.unicam.cs.mpgc.rpg125947.model.Attributo;
import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

/**
 * Risultato di una <strong>prova di abilita</strong> (skill check): il giocatore
 * tenta un'azione il cui esito dipende da un {@link Attributo} dell'investigatore.
 *
 * <p>E un value object immutabile che riporta tutti gli ingredienti del calcolo
 * ({@code tiro} del dado, {@code valoreAttributo}, {@code difficolta}) cosi la UI
 * puo mostrarli in modo trasparente, come in un gioco di ruolo da tavolo.</p>
 *
 * @param attributo       caratteristica messa alla prova
 * @param tiro            risultato grezzo del dado
 * @param valoreAttributo valore dell'attributo dell'investigatore
 * @param difficolta      soglia da raggiungere o superare
 * @param superata        {@code true} se {@code tiro + valoreAttributo >= difficolta}
 */
public record EsitoProva(Attributo attributo, int tiro, int valoreAttributo,
                         int difficolta, boolean superata) {

    public EsitoProva {
        Validazioni.nonNullo(attributo, "attributo prova");
    }

    /** @return il totale realizzato: somma del tiro e del valore di attributo */
    public int totale() {
        return tiro + valoreAttributo;
    }
}
