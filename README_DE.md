**<img src="https://raw.githubusercontent.com/SergelsOrg/csv2tex/main/src/main/resources/org/example/csv2tex/ui/openmoji_flag-united-kingdom_1f1ec-1f1e7.png" alt="Flag DE" width="20"/>English version / Englische Version:** [README.md](README.md)

# csv2tex

[![CircleCI](https://circleci.com/gh/SergelsOrg/csv2tex/tree/main.svg?style=shield)](https://circleci.com/gh/SergelsOrg/csv2tex/tree/main)


Ein Software-Tool, um Platzhalter in Tex-Dateien mit Inhalten aus einem CSV zu ersetzen. 

Konkreter Anwendnugsfall: Automatisierte Generierung von Schulzeugnissen als PDF.

## Starten der Anwendung

Um die Software zu starten, muss man:

* Den Inhalt dieses Repositories auschecken oder downloaden 
* Auf der Kommandozeile folgenden Code ausführen: 

```bash
$ ./runApplicationUsingGradle.sh 
```

Das Build-Werkzeug Gradle lädt dann alle nötigen Bibliotheken und startet die grafische Anwendung (GUI).

Als Laufzeitumgebung wird angenommen, dass **Java Version 11** oder höher installiert ist.  
Das mag sich in Zukunft ändern - Ziel ist, dass das Projekt auf einer aktuellen Distribution von Ubuntu Linux direkt 
ausgeführt werden kann.


## Eingabe-Daten

### CSV-Datei

Ein Teil der benötigten Eingabedaten ist eine `.csv`-Datei mit den Daten für die Zeugnisse.

Das Format der Datei wird in [docs/CSV_Format_DE.md](docs/CSV_Format_DE.md) beschrieben.

### TEX-Template-Datei

Ein weiterer Teil der benötigten Eingabedaten ist eine `.tex`-Datei mit Platzhaltern, die das Programm letztendlich mit 
den CSV-Daten ersetzt.

Die Platzhalter werden in [docs/TEX_Placeholders_Format_DE.md](docs/TEX_Placeholders_Format_DE.md) beschrieben.

## "Es läuft bei mir nicht" / "Ich habe einen Bug (Fehler) gefunden" / "Es fehlt eine Funktionalität"

Für solche Anfragen bitte ein Issue im öffentlichen GitHub-Repository des Projekts anlegen
([==> Link zu den GitHub-Issues <==](https://github.com/SergelsOrg/csv2tex/issues)).  
Man muss sich anmelden, aber die Nutzung ist kostenlos.


## Verschiedenes


### Projekt-Lizenz

Die Lizenz des Projekts liegt [hier im Repository als Datei](LICENSE).

Zur Zeit des Schreibens ist das Projekt unter der [GNU General Public License (GPL)](https://www.gnu.org/licenses/gpl-3.0.en.html) 
lizensiert - das heißt, dass alle Änderungen an der Software ebenso veröffentlicht werden müssen
(und, idealerweise, hierher als Pull Request zurückgeführt).

Eine kurze und vereinfachte Erklärung findet sich [hier](https://tldrlegal.com/license/gnu-general-public-license-v3-(gpl-3)).

### Emoji: Lizenz

Die Flaggen-Emoji, die im Projekt benutzt werden, entstammen dem [OpenMoji-Projekt](https://openmoji.org/about/) 
und sind unter [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/) lizensiert.