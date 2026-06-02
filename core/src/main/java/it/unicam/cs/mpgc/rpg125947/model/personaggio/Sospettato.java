package it.unicam.cs.mpgc.rpg125947.model.personaggio;

import it.unicam.cs.mpgc.rpg125947.model.Coordinata;
import it.unicam.cs.mpgc.rpg125947.model.dialogo.Dialogo;
import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

/**
 * Un personaggio che puo essere formalmente accusato. Aggiunge al
 * {@link Personaggio} il <em>movente</em> e l'<em>alibi dichiarato</em>,
 * informazioni centrali per l'indagine.
 */
public final class Sospettato extends Personaggio {

    private final String movente;
    private final String alibiDichiarato;

    public Sospettato(String nome, String ruolo, String spritePath, Coordinata posizione,
                      Dialogo dialogo, String movente, String alibiDichiarato) {
        this(nome, ruolo, spritePath, posizione, dialogo, movente, alibiDichiarato, 1.0);
    }

    public Sospettato(String nome, String ruolo, String spritePath, Coordinata posizione,
                      Dialogo dialogo, String movente, String alibiDichiarato, double scala) {
        super(nome, ruolo, spritePath, posizione, dialogo, scala);
        this.movente = Validazioni.nonVuota(movente, "movente");
        this.alibiDichiarato = Validazioni.nonVuota(alibiDichiarato, "alibi");
    }

    @Override
    public boolean isSospettato() {
        return true;
    }

    public String getMovente() {
        return movente;
    }

    public String getAlibiDichiarato() {
        return alibiDichiarato;
    }
}
