# Estendibilità

La consegna richiede un'architettura predisposta a future estensioni (più dispositivi, nuove funzionalità) anche se non tutte presenti nella prima release. Ecco i meccanismi messi a disposizione.

## Nuovi casi senza ricompilare

Lo scenario è un file **XML validato da XSD**, caricato dallo `ScenarioLoader`. Per aggiungere un nuovo caso (nuove stanze, personaggi, indizi, soluzione) basta fornire un nuovo `caso.xml` conforme a `scenario.xsd`: **nessuna modifica al codice** (Open/Closed). Si potrebbe inoltre rendere selezionabile il file di scenario, abilitando più casi.

## Nuovi formati di persistenza

`GameStateRepository` e `ScenarioLoader` sono **interfacce**. Le implementazioni attuali sono XML (JAXP). Per passare a un altro formato basta una nuova implementazione, senza toccare dominio né controller:

- **JSON** (es. Gson) → `JsonGameStateRepository implements GameStateRepository`;
- **Database relazionale** con **JPA/Hibernate** → entità `@Entity`, mapping O/R, `JpaGameStateRepository`;
- per scenari molto grandi, parsing **SAX** (a eventi) al posto di DOM, lasciando invariata l'interfaccia `ScenarioLoader`.

È l'applicazione del **Dependency Inversion Principle**: si dipende dall'astrazione, l'implementazione è intercambiabile (basta cambiare il punto di composizione in `AppContext`).

## Nuove fasi di gioco

Le fasi implementano `FaseDiGioco` (pattern **State**). Una nuova fase (es. una "fase indizio cronometrato" o un mini-gioco) si aggiunge creando una nuova classe e definendone le transizioni, **senza modificare** le fasi esistenti.

## Nuove regole di vittoria

`ValutatoreAccusa` è una **Strategy**. Oltre a `ValutatoreAccusaStandard` (colpevole + tutti gli indizi decisivi) si possono introdurre valutatori alternativi (a punteggio, a livelli di difficoltà) iniettandoli nel `MotorePartita`.

## Nuove regole per le prove di abilità

Anche la risoluzione degli **skill check** è una **Strategy** (`RisolutoreProva`), iniettabile nel `MotorePartita`. Oltre a `RisolutoreProvaDado` (tiro d20 + attributo) si possono introdurre risolutori alternativi — ad esempio un dado diverso, una formula con vantaggio/svantaggio o un esito deterministico per i test — senza toccare la logica di gioco. Allo stesso modo, nuove caratteristiche (`Attributo`) o una progressione diversa restano confinate al dominio. Vedi **[Sistema di Ruolo](Sistema-di-Ruolo)**.

## Estensione multi-dispositivo

Il modulo **`core` è platform-independent**: non dipende da JavaFX. Espone tutto tramite interfacce e oggetti di dominio puri. Un futuro front-end **mobile** (Android/Gluon) o **web** può essere aggiunto come nuovo modulo Gradle che **riusa `core`**:

```groovy
// settings.gradle
include('core')
include('javafx-app')
// include('mobile-app')  // futuro: riusa core
// include('web-app')     // futuro: riusa core
```

Solo la presentazione cambia; dominio, logica e persistenza restano invariati.
