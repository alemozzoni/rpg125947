# 🕵️ Delitto al Museo della Computazione

Gioco di ruolo investigativo (**murder mystery RPG**) con interfaccia grafica, scritto in **Java 25** con **JavaFX**.
Il giocatore veste i panni di un investigatore che deve risolvere un omicidio avvenuto durante l'inaugurazione notturna di un museo dedicato alla storia dell'informatica: esplora le sale, interroga i sospettati, raccoglie indizi nel taccuino e formula l'accusa finale.

Progetto d'esame per il corso di **Metodologie di Programmazione e Modellazione della Conoscenza** — Università degli Studi di Camerino, A.A. 2025/2026.

---

## 🚀 Come eseguire il progetto

### Prerequisiti

- **Java 25** (JDK). Non è necessario installare Gradle né JavaFX a mano: il **Gradle Wrapper** scarica la versione corretta di Gradle e il plugin JavaFX risolve automaticamente le librerie grafiche. La toolchain Java 25, se assente, viene scaricata automaticamente.

### Istruzioni

```bash
git clone https://github.com/alemozzoni/rpg125947.git
cd rpg125947
```

### Build del progetto

```bash
./gradlew build
```

### Esecuzione

```bash
./gradlew run
```

> Due soli comandi bastano per compilare ed eseguire, come da specifica di consegna.

---

## 🎮 Come si gioca

1. All'avvio inserisci il **nome dell'investigatore** e premi _Inizia a Giocare_.
2. **Esplora** le sale del museo usando le frecce/uscite sul bordo della scena.
3. **Clicca sui personaggi** per interrogarli: le testimonianze finiscono nel taccuino.
4. **Clicca sugli hotspot** (aree che si illuminano al passaggio del mouse) per ispezionare oggetti e trovare indizi.
5. Consulta il **Taccuino** per rivedere indizi, testimonianze e sospettati; puoi **salvare** e **caricare** la partita.
6. Quando hai le prove, premi **Formula accusa**, indica il colpevole e seleziona gli indizi a supporto. L'accusa è corretta solo con il colpevole giusto **e** tutte le prove decisive.

---

## 🏗️ Struttura del progetto

Build **Gradle multi-progetto** che separa il dominio dalla presentazione (predisposizione multi-dispositivo):

| Modulo       | Responsabilità                                                                                                                                                                                              |
| ------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `core`       | Modello di dominio, logica di gioco e persistenza astratta. **Platform-independent**: non dipende da JavaFX. Persistenza in **XML** (JAXP: DOM/SAX, validazione **XSD**, query **XPath**), inclusa nel JDK. |
| `javafx-app` | Front-end desktop **JavaFX** (FXML + CSS + binding), dipende da `core`.                                                                                                                                     |

Tutto il codice è nel package `it.unicam.cs.mpgc.rpg125947`.

La documentazione completa (funzionalità, responsabilità delle classi e interfacce, organizzazione dei dati e persistenza, meccanismi di estensione) è nella **[Wiki del repository](https://github.com/alemozzoni/RPG/wiki)**; le stesse pagine sono versionate nella cartella [`wiki/`](wiki/).

---

## 🤖 Uso di strumenti di AI

Questo progetto è stato realizzato **con un uso mirato di Intelligenza Artificiale**, in particolare **Claude (Anthropic)**, utilizzato per consigli e stesura di alcune righe di codice per semplificare il lavoro. 

**Per quali attività è stata usata l'AI:**

- generazione dello scheletro del progetto (build Gradle multi-progetto, wrapper, configurazione JavaFX/JUnit);
- aiuto nella scrittura del codice del dominio, della logica di gioco, della persistenza XML e dei controller JavaFX, **a partire dal documento di progettazione** (trama, architettura, pattern e scelte tecnologiche definiti a monte);
- controllato e revisionato i testi del caso (dialoghi, indizi, soluzione) e della documentazione (README e Wiki);
- generazione degli asset grafici procedurali tramite uno script ImageMagick.

**Livello di intervento e supervisione personale:**

- le **scelte di progettazione** (architettura, pattern, stack, trama e meccaniche) sono state definite e validate consapevolmente;
- ogni componente è stato **compreso, verificato ed eseguito**: il progetto compila con `./gradlew build`, supera la suite di **test JUnit** ed è stato avviato e provato graficamente;
- l'AI è stata usata come **supporto** alla scrittura e all'organizzazione del codice, non come sostituto della comprensione.

Una **dichiarazione dettagliata** dell'uso dell'AI è disponibile nella pagina dedicata della Wiki.
