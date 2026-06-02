package it.unicam.cs.mpgc.rpg125947.model.personaggio;

import it.unicam.cs.mpgc.rpg125947.model.Coordinata;
import it.unicam.cs.mpgc.rpg125947.model.dialogo.Dialogo;
import it.unicam.cs.mpgc.rpg125947.model.dialogo.OpzioneDialogo;
import it.unicam.cs.mpgc.rpg125947.model.dialogo.Testimonianza;
import it.unicam.cs.mpgc.rpg125947.model.interfaces.Interrogabile;
import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

import java.util.List;
import java.util.Objects;

/**
 * Personaggio non giocante (NPC) presente in una stanza e interrogabile.
 *
 * <p>E una <strong>classe astratta</strong> perche nel dominio non esiste un
 * "personaggio generico": ogni NPC e o un {@link Sospettato} o un
 * {@link Testimone}. Il metodo astratto {@link #isSospettato()} obbliga ogni
 * sottotipo a dichiarare la propria natura (polimorfismo).</p>
 *
 * <p>L'identita di dominio e il nome: due personaggi con lo stesso nome sono
 * considerati lo stesso personaggio ({@code equals}/{@code hashCode} sul nome).</p>
 */
public abstract class Personaggio implements Interrogabile {

    private final String nome;
    private final String ruolo;
    private final String spritePath;
    private final Coordinata posizione;
    private final Dialogo dialogo;
    private final double scala;

    /** Crea un personaggio con sprite a dimensione standard (scala 1.0). */
    protected Personaggio(String nome, String ruolo, String spritePath,
                          Coordinata posizione, Dialogo dialogo) {
        this(nome, ruolo, spritePath, posizione, dialogo, 1.0);
    }

    /**
     * @param scala fattore di ingrandimento dello sprite rispetto alla
     *              dimensione standard (1.0 = standard); deve essere positivo
     */
    protected Personaggio(String nome, String ruolo, String spritePath,
                          Coordinata posizione, Dialogo dialogo, double scala) {
        this.nome = Validazioni.nonVuota(nome, "nome personaggio");
        this.ruolo = Validazioni.nonVuota(ruolo, "ruolo personaggio");
        this.spritePath = Validazioni.nonVuota(spritePath, "sprite personaggio");
        this.posizione = Validazioni.nonNullo(posizione, "posizione personaggio");
        this.dialogo = Validazioni.nonNullo(dialogo, "dialogo personaggio");
        if (scala <= 0) {
            throw new IllegalArgumentException("La scala dello sprite deve essere positiva: " + scala);
        }
        this.scala = scala;
    }

    /** @return {@code true} se il personaggio e un sospettato accusabile */
    public abstract boolean isSospettato();

    @Override
    public Dialogo avviaDialogo() {
        return dialogo;
    }

    @Override
    public List<Testimonianza> testimonianze() {
        // Unica fonte di verita: le testimonianze sono le risposte del dialogo.
        return dialogo.getOpzioni().stream()
                .map(OpzioneDialogo::risposta)
                .toList();
    }

    public String getNome() {
        return nome;
    }

    public String getRuolo() {
        return ruolo;
    }

    public String getSpritePath() {
        return spritePath;
    }

    public Coordinata getPosizione() {
        return posizione;
    }

    /** @return il fattore di scala dello sprite (1.0 = dimensione standard) */
    public double getScala() {
        return scala;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Personaggio other)) {
            return false;
        }
        return nome.equals(other.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }

    @Override
    public String toString() {
        return nome + " (" + ruolo + ")";
    }
}
