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
        * Weit verbunden Zellen üben große Kraft aus
    * Ablauf
        1. Einlesen der Netzliste / fixierten Pads
            * ArrayList anstatt LinkedList verwenden
        2. Erstellen des Netzgraphens
        3. Zufällig Platzierung der Logikblöcke
        4. Ermitteln der ZFT Position eines Blocks
        5. Abbruchbedingung

