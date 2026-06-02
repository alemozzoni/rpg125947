package it.unicam.cs.mpgc.rpg125947.model.accusa;

import it.unicam.cs.mpgc.rpg125947.model.Indizio;
import it.unicam.cs.mpgc.rpg125947.model.personaggio.Sospettato;
import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * L'ipotesi investigativa finale: chi e l'accusato e quali prove il giocatore
 * porta a supporto.
 *
 * @param accusato     il sospettato indicato come colpevole
 * @param proveFornite l'insieme degli indizi presentati a sostegno dell'accusa
 */
public record Accusa(Sospettato accusato, Set<Indizio> proveFornite) {

    public Accusa {
        Validazioni.nonNullo(accusato, "accusato");
        // copia difensiva + sola lettura: l'accusa e immutabile dopo la creazione
        proveFornite = Collections.unmodifiableSet(new HashSet<>(Validazioni.nonNullo(proveFornite, "prove")));
    }
}
