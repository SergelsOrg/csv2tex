(!) **Falls die Tabellen in diesem Dokument nicht korrekt angezeigt werden**, nutzen Sie die GitHub-Website des Projekts, um sie anzusehen 
(das Rendern von Tabellen ist [eine GitHub-spezifische Erweiterung von Markdown](https://github.github.com/gfm/#tables-extension-) -
[die Original-Spezifikation](https://daringfireball.net/projects/markdown/syntax#html) nutzt statt dessen HTML-Tabellen).


# TEX-Templates: Platzhalter, die aus den CSV-Daten ersetzt werden

## Generische Plathalter

Diese Platzhalter sind für alle Formate gleich, für alle potentiell teilnehmenden Schulen:

| Platzhalter | Bedeutung |
| --- | --- |
| `#givenName` | Vorname des Schülers. |
| `#surName` | Nachname des Schülers. |
| `#birthDay` | Geburtsdatum des Schülers. |
| `#schoolClass` | Jahrgangsstufe und Klasse des Zeugnisses, z.B. `1c` für das erste Schuljahr in Parallelklasse `c`. |
| `#schoolYear` | Tatsächliches Kalenderjahr, das in der Schule verbracht wurde, z.B. `2021 / 2022`. |
| `#partOfYear` | Markiert das Halbjahr, z.B. `Endjahr`. |
| `#absenceDaysTotal` | Wie viele Fehltage der Schüler insgesamt im Zeugnis-Zeitraum hatte. |
| `#absenceDaysUnauthorized` | Wie viele der Fehltage unentschuldigt waren (z.B. weil es kein ärztliches Attest gab). |
| `#absenceHoursTotal` | Wie viele Fehlstunden der Schüler insgesamt im Zeugnis-Zeitraum hatte. |
| `#absenceHoursUnauthorized` | Wie viele der Fehlstunden unentschuldigt waren (z.B. weil es kein ärztliches Attest gab). |
| `#tables` | Tabellen mit den eigentlichen Schulnoten - von spezifischem Code gerendert. |

Falls diese Dokumentation in Zukunft vom Code abweicht, geben die Konstanten in 
`org.example.csv2tex.placeholders.PlaceholderReplacerImpl` ggf. ein ausführlicheres Bild.

## Erfurter Pilot-Projekt

Die Schule, für die das Pilotprojekt für dieses Software-Projekt durchgeführt wurde, hat spezifische Anforderungen, 
die spezifischer Code abdeckt.

**Diese Platzhalter könnten theoretisch in einem eigenen Template genutzt werden,** sie werden aber intern durch die 
Java-Software genutzt. Die Verwendung wird nicht empfohlen.

Der einzige diesbezügliche Platzhalter in den TEX-Template-Dateien ist `#tables`, wie im vorherigen Abschnitt erwähnt.

| Platzhalter | Bedeutung |
| --- | --- |
| `#SUBJECT` |  Name des Schulfachs (Platzhalter greift einmal für jedes Schulfach). |
| `#COMPETENCIES` | Schulfach-spezifische Kompetenzen (zwischen-Platzhalter für mehrere `#COMPETENCY`-Platzhalter). |
| `#COMPETENCY` | Schulfach-spezifische Kompetenz, die benotet wird, z.B. Lesefähigkeit als Teil des Deutschunterrichts. |
| `#LEVEL` | Niveau des Schulfachs, das benotet wird. Der Code nimmt an, dass es nur folgende Werte gibt: "1", "2", "3", "7", "8", "9"; ansonsten wird keine Ausgabe generiert. |
| `#GRADE` | Schulnote. Der Code nimmt an, dass es nur folgende Werte gibt: "1", "2", "3", "4", und spezielle Werte "hj" (wird im 2. Halbjahr belegt) and "nb" (nicht erteilt).|

Falls diese Dokumentation in Zukunft vom Code abweicht, geben die Konstanten in
`org.example.csv2tex.placeholders.schoolspecific.ErfurtSchoolTablePlaceholderReplacer` ggf. ein ausführlicheres Bild.
