# Platzierung

* Wohe kommen Kostenfunktionen für die Verdrahtung?

* Min-Cut-Verfahren
    * Konstruktiv
    * Revedieren von Schnitten
        * Video 10 1:12
        * Soll schlecht sein!
    * Partitionierung implementieren?

* KräftePlatzierung
    * Iterativ
    * Kostenfunktion über "Federn"
        * Weit verbundene Zellen üben große Kraft aus
    * Ablauf
        1. ~~Einlesen der Netzliste~~
            * .global clk mit einlesen
        2. Einlesen der fixierten Pads
            * Fixierungs Datei erstellen
            * Einlesen der Fixen werte 
        3. ~~Einlesen der .arch Datei? Bzw setzen der wichtigsten Werte als Konstanten~~
        4. ~~Erstellen des Netzgraphens~~
        5. Intelligente Platzierung der Logikblöcke
        6. Ermitteln der ZFT Position eines Blocks
        7. Abbruchbedingung

    * Todo: 
        * ToString implementieren

