package de.hsbochum.client.business;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessungTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5}) // verschiedene laufende Nummern
    void testGibAttributeAus(int laufendeNummer) {
        // Arrange
        double wert = 42.0; // Beispielwert für den Test
        long timeMillis = System.currentTimeMillis(); // Beispielwert für den Test
        Messung messung = new Messung(laufendeNummer, wert, timeMillis);

        // Act
        String result = messung.gibAttributeAus();

        // Assert
        String expected = laufendeNummer + ": " + wert;
        assertEquals(expected, result);
    }
}
