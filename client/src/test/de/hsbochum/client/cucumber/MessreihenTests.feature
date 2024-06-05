Feature: Messungen zu einer Messreihe durchführen

  Background:
    Given die Datenbank enthält folgende Messreihen
      | messreihenId | zeitintervall | verbraucher | messgroesse |
      | 1            | 1000          | Verbraucher1 | Größe1      |
      | 2            | 2000          | Verbraucher2 | Größe2      |

  Scenario Outline: Erfolgreiche Messung für eine Messreihe mit MessreihenId <MessreihenId> und <AnzahlMessungen> Messungen
    Given Messreihen ansehen
    And Messreihe mit MessreihenId <MessreihenId> aussuchen
    When zur aktuellen Messreihe weitere Messung durchführen
    Then Es wurde eine Messung zur aktuellen Messreihe an Position <AnzahlMessungen> gespeichert

    Examples:
      | MessreihenId | AnzahlMessungen |
      | 1            | 1               |
      | 2            | 1               |

  Scenario: Exception bei Messung für eine Messreihe mit MessreihenId 1
    Given Messreihen ansehen
    And Messreihe mit MessreihenId 1 aussuchen
    When zur aktuellen Messreihe zu schnell weitere Messung durchführen
    Then Exception zum Zeitintervall bei der Anlage einer Messung zur aktuellen Messreihe erhalten
