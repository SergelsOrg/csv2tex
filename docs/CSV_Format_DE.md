# CSV input data format

(!) Bei Software liegt **die "letztendliche Wahrheit" darüber, wie ein Programm arbeitet, im Code**, der durch Software-
Tests verifiziert wird. Somit geben **jegliche Test-Daten dieses Projekts vielerlei Beispiele** dafür, wie valide 
Eingabedaten aussehen können.  
Dateien, deren Name das Wort "faulty" enthält, sollten jedoch vermieden werden, da sie genutzt werden, um die 
Ausnahmebehandlung im Fall *invalider* Eingabedaten zu testen.

## Beispieldaten, die hier benutzt werden

Wir wollen also folglich mit einem der Codebeispiele arbeiten - das hier ist der Inhalt von `src/test/resources/csv/student_data_example_one_competency.csv`.

``` 
Name,Vorname,Klasse,Schuljahr,Halbjahr,Geburtstag,Fehltage,Fehltage unentschuldigt,Fehlstunden,Fehlstunden unentschuldigt,niveau,"Deutsch
Texte rezipieren
Lese- und Hörverstehen
Ich kann Informationen aus Texten entnehmen und wiedergeben. Ich kann Inhalt und Aussage von Texten wiedergeben. Ich kenne die Gattungsmerkmale von Märchen und Sagen. "
Otto,Karl,5a,2019/2020,1,27.12.85,7,5,5,4,1,1
```

## Datenformat

### Zeugnis- und Schüler-Basisdaten (Positions-Parameter)

Die ersten 10 Spalten sind **feststehende Spalten, unabhängig von den konkreten Namen in der Kopfzeile**.
(wenn man so will, Positions-Parameter):

1. Nachname des Schülers (`Otto`)
1. Vorname des Schülers (`Karl`)
1. (Parallel-)Klasse (z.B. `5a` für "a-Parallelklasse des 5. Jahrgangs") (`5a`)
1. Schuljahre (spiegelt die Kalenderjahre wieder, die das Schuljahr abdeckt) (`2019/2020`)
1. Halbjahr (1. oder 2. Hälfte) (`1`, hier könnte aber auch ein Wert wie `Endjahr` stehen)
1. Geburtsdatum des Schülers (`27.12.85`, das konkrete Datums-Format wird aber nicht geprüft - `12/27/85` funktioniert z.B. genauso gut)
1. alle Fehltage - Wie viele Fehltage der Schüler im Zeugnis-Zeitraum hatte. (`7`)
1. davon unentschuldigte Fehltage - Wie viele der Fehltage unentschuldigt waren (z.B. weil es kein ärztliches Attest gab). (`5`)
1. alle Fehlstunden - Wie viele Fehlstunden der Schüler insgesamt im Zeugnis-Zeitraum hatte. (`5`)
1. davon unentschuldigte Fehlstunden - Wie viele der Fehlstunden unentschuldigt waren (z.B. weil es kein ärztliches Attest gab).  (`4`)

Zur Zeit des Schreibens dieses Dokuments gibt es keine "Sanity-Checks" bezüglich der Datensemantik. 
D.h., es wird nicht validiert, dass die Anzahl der Fehltage oder -stunden größer oder gleich der entsprechenden Zahl der 
unentschuldigten ist.

### Niveau und Fach-spezifische Daten 

#### Niveau-Spalte

Streng genommen reichen die obigen Daten schon, um ein Zeugnis ohne Fächer oder Noten zu generieren.   
Realistisch gesehen wollen wir diese aber.

Dafür erwartet das Programm direkt nach den Basisdaten, dass eine Spalte folgt, die das Niveau der in den nachfolgenden 
Spalten definierten Fächer definiert.  
Die Programmlogik verlangt nur eine solche Spalte. Der Wert wird auf alle folgenden Spalten angewandt bis eine weitere 
Niveau-Spalte ihn umdefiniert.

Um eine Spalte als Niveau-Spalte zu kennzeichnen, muss in der Kopfzeile "Niveau" oder "Level" stehen (ungeachtet der 
Großschreibung).  
(Das kann sich über die Zeit ändern, `org.example.csv2tex.csv.CsvParsingUtil#isLevelSettingColumn` sollte die aktuelle 
Implementierung beinhalten.)

#### Fach-spezifische Daten - mehrzeilige Kopfzeile 

Die Daten für Fächer können verschieden aussehen.

1. Es kann lediglich der Name des Fachs sein und eine Kompetenz.
2. Es kann ein Fach, eine Kompetenz, und eine detaillierte Beschreibung sein.
3. Es kann ein Fach, eine Kompetenz, eine Unter-Kompetenz und eine detaillierte Beschreibung sein.

Um solches für das Programm leicht verarbeitbar zu machen und für den Nutzer so einfach wie möglich einzugeben,
müssen diese einzelnen Teile durch einen Zeilenumbruch im CSV getrennt sein.  
Falls Sie eine Tabellenkalkulations-Anwendung verwenden (z.B. LibreOffice Calc, Microsoft Excel), können Sie 
Zeilenumbrüche innerhalb von Zellen dadurch eingegeben, dass sie `Steuerung` halten und dann `Enter` drücken
(das könnte sich je nach Betriebssystem und Tabellenkalkulations-Anwendung unterscheiden).

In unserem Beispiel haben wir die 3. Form:
* Schulfach (`Deutsch`)
* Kompetenz (`Texte rezipieren`)
* Unter-Kompetenz (`Lese- und Hörverstehen`)
* detaillierte Beschreibung (`Ich kann Informationen aus Texten ...`)
  

## "Wer soll sich das alles merken, was denkt ihr euch eigentlich?"

Die Entwickler dieses Programms haben **viel Zeit** aufgewandt, um sicherzustellen, dass 
der Nutzer in (was wir annehmen) häufigen Fehlerfällen eine sprechende Fehlermeldung erhält; diese gibt an, 
was an den Eingabedaten nicht stimmt.  
Wir hoffen, dass das Programm direkt benutzt werden kann, ohne viel Lesen von Dokumentation.  

Sagen Sie uns gerne, wie gut das funktioniert hat, indem Sie auf GitHub für Probleme ein Issue anlegen, wie in 
[README.md](../README.md) beschrieben.