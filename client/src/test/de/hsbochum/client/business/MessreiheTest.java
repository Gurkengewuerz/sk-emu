package de.hsbochum.client.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.stream.Stream;

public class MessreiheTest {

    @ParameterizedTest
    @MethodSource("provideMessreiheArguments")
    void testMessreiheConstructor(int messreihenID, int zeitintervall, String verbraucher, String messgroesse) {
        // Arrange & Act
        Messreihe messreihe = new Messreihe(messreihenID, zeitintervall, verbraucher, messgroesse);

        // Assert
        assertEquals(messreihenID, messreihe.getMessreihenId());
        assertEquals(zeitintervall, messreihe.getZeitintervall());
        assertEquals(verbraucher, messreihe.getVerbraucher());
        assertEquals(messgroesse, messreihe.getMessgroesse());
    }

    private static Stream<Arguments> provideMessreiheArguments() {
        return Stream.of(
                Arguments.of(1, 20, "LED", "Leistung"),
                Arguments.of(2, 20, "LED", "Arbeit")
        );
    }

    @Test
    void testGibMessungenAlsStringAus() {
        // Arrange
        Messung messung1 = new Messung(1, 50.0, 1000L);
        Messung messung2 = new Messung(2, 75.0, 2000L);

        Messreihe stubMessreihe = Mockito.mock(Messreihe.class);
        Mockito.when(stubMessreihe.getMessungen()).thenReturn(new Messung[] { messung1, messung2 });

        Messreihe messreihe = new Messreihe();
        messreihe.setMessungen(stubMessreihe.getMessungen());

        // Act
        String result = messreihe.getMessungenString();

        // Assert
        String expected = "1: 50.0 / 2: 75.0";
        assertEquals(expected, result);
    }
}