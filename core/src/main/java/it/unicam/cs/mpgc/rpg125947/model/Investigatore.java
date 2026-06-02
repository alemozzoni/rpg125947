package it.unicam.cs.mpgc.rpg125947.model;

import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

/**
 * Il protagonista controllato dal giocatore. Entita semplice: incapsula il
 * nome scelto in fase di creazione del personaggio, con validazione vicino
 * al dato (error handling nel costruttore).
 */
public final class Investigatore {

    private final String nome;

    public Investigatore(String nome) {
        this.nome = Validazioni.nonVuota(nome, "nome investigatore");
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return "Investigatore " + nome;
    }
}
