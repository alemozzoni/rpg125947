package it.unicam.cs.mpgc.rpg125947.model.personaggio;

import it.unicam.cs.mpgc.rpg125947.model.Coordinata;
import it.unicam.cs.mpgc.rpg125947.model.dialogo.Dialogo;

/**
 * Un personaggio informato sui fatti ma non accusabile: fornisce testimonianze
 * utili (ad esempio per la verifica incrociata degli alibi) ma non ha movente.
 */
public final class Testimone extends Personaggio {

    public Testimone(String nome, String ruolo, String spritePath, Coordinata posizione, Dialogo dialogo) {
        super(nome, ruolo, spritePath, posizione, dialogo);
    }

    public Testimone(String nome, String ruolo, String spritePath, Coordinata posizione,
                     Dialogo dialogo, double scala) {
        super(nome, ruolo, spritePath, posizione, dialogo, scala);
    }

    @Override
    public boolean isSospettato() {
        return false;
    }
}
