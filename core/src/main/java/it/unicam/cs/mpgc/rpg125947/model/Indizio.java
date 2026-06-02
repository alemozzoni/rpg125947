package it.unicam.cs.mpgc.rpg125947.model;

import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

import java.util.Objects;

/**
 * Un indizio raccoglibile durante l'indagine.
 *
 * <p>E un value object immutabile ({@code final}, campi {@code private final}).
 * L'<strong>identita di dominio</strong> e l'{@code id}: due indizi sono lo
 * stesso indizio se hanno lo stesso id, a prescindere dagli altri campi. Per
 * questo {@link #equals(Object)} e {@link #hashCode()} sono ridefiniti in modo
 * coerente sull'id: cosi un {@link Indizio} puo essere inserito senza duplicati
 * nel {@link java.util.Set} del {@link Taccuino}.</p>
 */
public final class Indizio {

    private final String id;
    private final String nome;
    private final String descrizione;
    private final TipoIndizio tipo;

    public Indizio(String id, String nome, String descrizione, TipoIndizio tipo) {
        this.id = Validazioni.nonVuota(id, "id indizio");
        this.nome = Validazioni.nonVuota(nome, "nome indizio");
        this.descrizione = Validazioni.nonVuota(descrizione, "descrizione indizio");
        this.tipo = Validazioni.nonNullo(tipo, "tipo indizio");
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public TipoIndizio getTipo() {
        return tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Indizio other)) {
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Indizio[" + id + ": " + nome + "]";
    }
}
