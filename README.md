ToDo App - Dashboard

Dies ist eine ToDo-App, die es Benutzern ermöglicht, Aufgaben zu erstellen, zu bearbeiten, zu löschen und ihren Status zwischen "aktiv" und "erledigt" zu ändern. Die App verwendet eine einfache Benutzeroberfläche mit einer Priorisierung von Aufgaben, um die Verwaltung von Aufgaben zu erleichtern.

Installationsanleitung

Voraussetzungen
Stelle sicher, dass du folgende Tools und Software installiert hast:

Android Studio (Empfohlen) – Android Studio ist die bevorzugte IDE zur Entwicklung von Android-Apps.
JDK 11 oder höher (Java Development Kit) – Wird benötigt, um die App zu kompilieren und auszuführen.
Ein Android-Gerät oder ein Emulator – Zum Testen der App.
Schritte
Repository klonen
Klone das Repository auf deinen lokalen Rechner, indem du den folgenden Befehl im Terminal eingibst:

git clone https://github.com/dein-benutzername/todo-app-dashboard.git
Projekt öffnen
Öffne das Projekt in Android Studio:

Starte Android Studio und klicke auf „Open an existing project“.
Wähle den Ordner aus, in dem du das Repository geklont hast.
Abhängigkeiten installieren
Android Studio sollte automatisch alle erforderlichen Abhängigkeiten installieren. Wenn dies nicht automatisch passiert, öffne das Terminal in Android Studio und führe den folgenden Befehl aus:

./gradlew build
App starten
Wähle ein Android-Gerät oder einen Emulator aus und klicke auf den grünen "Run"-Button in Android Studio.
Die App wird auf dem Gerät oder Emulator installiert und gestartet.
Funktionsbeschreibung

Die ToDo-App bietet folgende Funktionen:

ToDo erstellen
Über die Schaltfläche „+“ kannst du neue ToDos erstellen. Du gibst Name, Priorität (niedrig, mittel, hoch), Beschreibung und Enddatum ein.
ToDo bearbeiten
Du kannst bestehende ToDos bearbeiten, indem du sie auswählst und die Details änderst.
ToDo löschen
Du kannst ToDos löschen, sowohl aktive als auch erledigte Aufgaben.
ToDo als erledigt markieren
Aktiv markierte ToDos können als erledigt gekennzeichnet werden. Ebenso können erledigte ToDos wieder in die Liste der aktiven Aufgaben verschoben werden.
Liste anzeigen
ToDos werden in zwei Kategorien angezeigt: aktive ToDos und erledigte ToDos. Du kannst zwischen diesen Listen umschalten, um deine Aufgaben nach Status zu filtern.
Prioritäten
ToDos können in drei Prioritäten kategorisiert werden (Niedrig, Mittel, Hoch). Die Priorität wird durch Farben visualisiert (grün, gelb, rot), um die Wichtigkeit der Aufgaben hervorzuheben.
Verwendete Technologien

Kotlin: Die Hauptprogrammiersprache, die für die Android-Entwicklung verwendet wird.
Jetpack Compose: Ein modernes UI-Toolkit für die Gestaltung der Benutzeroberfläche, das deklarative UI-Programmierung ermöglicht.
Android SDK: Für die Entwicklung von nativen Android-Apps.
SQLite: Wird verwendet, um die ToDos lokal auf dem Gerät zu speichern.
Material3: Ein Design-System, das es ermöglicht, moderne und ansprechende Benutzeroberflächen zu erstellen.
Coroutines: Für asynchrone Programmierung, insbesondere beim Laden von ToDos aus der Datenbank und anderen Hintergrundprozessen.
DatePicker Dialog: Für das Auswählen von Enddaten für ToDos, mit einer benutzerfreundlichen DatePicker-Komponente.
Bekannte Probleme

Datumsauswahl
Der DatePicker Dialog funktioniert möglicherweise nicht korrekt auf älteren Android-Versionen. Eine zusätzliche Anpassung könnte erforderlich sein, um eine konsistente Funktionalität auf allen Geräten zu gewährleisten.
Leere ToDo-Namen
Beim Erstellen eines neuen ToDos wird der Name überprüft, aber es gibt derzeit keine umfassende Validierung, die sicherstellt, dass der Name nicht leer bleibt (obwohl dies visuell angezeigt wird).
Löschen von ToDos
Beim Löschen von ToDos aus der erledigten Liste wird das ToDo korrekt aus der Ansicht entfernt. In einigen Fällen kann es jedoch zu einer Verzögerung bei der Synchronisation mit der Datenbank kommen.
Datenbankgeschwindigkeit
Bei einer großen Anzahl von ToDos kann es zu einer langsamen Datenbankabfrage kommen. Dies könnte in zukünftigen Versionen durch Optimierungen verbessert werden.
Keine Benachrichtigungen
Die App bietet derzeit keine Benachrichtigungen oder Erinnerungen für fällige Aufgaben. Eine Erweiterung dieser Funktionalität könnte in zukünftigen Versionen erfolgen.
