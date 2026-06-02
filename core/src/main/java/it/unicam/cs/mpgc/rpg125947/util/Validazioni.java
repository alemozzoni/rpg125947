package it.unicam.cs.mpgc.rpg125947.util;

/**
 * Piccola utility di validazione degli argomenti, centralizzata per evitare
 * duplicazione (DRY) dei controlli nei costruttori del dominio.
 */
public final class Validazioni {

    private Validazioni() {
        // classe di sole utility statiche
    }

    /**
     * @param valore   stringa da controllare
     * @param campo    nome del campo, per il messaggio d'errore
     * @return la stringa stessa se valida
     * @throws IllegalArgumentException se {@code valore} e {@code null} o vuoto
     */
    public static String nonVuota(String valore, String campo) {
        if (valore == null || valore.isBlank()) {
            throw new IllegalArgumentException(campo + " non puo essere nullo o vuoto");
        }
        return valore;
    }

    /**
     * @param valore oggetto da controllare
     * @param campo  nome del campo, per il messaggio d'errore
     * @return l'oggetto stesso se non nullo
     * @throws IllegalArgumentException se {@code valore} e {@code null}
     */
    public static <T> T nonNullo(T valore, String campo) {
        if (valore == null) {
            throw new IllegalArgumentException(campo + " non puo essere nullo");
        }
        return valore;
    }
}
