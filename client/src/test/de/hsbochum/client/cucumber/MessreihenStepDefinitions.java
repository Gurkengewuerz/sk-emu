package de.hsbochum.client.cucumber;

import de.hsbochum.client.business.Messreihe;
import de.hsbochum.client.business.Messung;
import de.hsbochum.client.business.db.DbModel;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.sql.SQLException;

public class MessreihenStepDefinitions {

    private DbModel dbModel;
    private Messreihe aktuelleMessreihe;

    private long startedAt;
    private Exception exception;

    @Before
    public void setUp() throws Exception {
        startedAt = System.currentTimeMillis();

        dbModel = DbModel.getInstance();
        // Lege die Test-Messreihen in der Datenbank an
        dbModel.speichereMessreiheInDb(new Messreihe(1, 1000, "Verbraucher1", "Größe1"));
        dbModel.speichereMessreiheInDb(new Messreihe(2, 2000, "Verbraucher2", "Größe2"));
    }

    @After
    public void tearDown() throws Exception {
        exception = null;

        dbModel.loescheMessreiheInklusiveMessungenAusDb(1);
        dbModel.loescheMessreiheInklusiveMessungenAusDb(2);
    }

    @Given("die Datenbank enthält folgende Messreihen")
    public void dieDatenbankEnthaeltFolgendeMessreihen(DataTable dataTable) {
    }

    @Given("Messreihen ansehen")
    public void messreihenAnsehen() throws Exception {
        dbModel.leseMessreihenInklusiveMessungenAusDb();
    }

    @Given("Messreihe mit MessreihenId {int} aussuchen")
    public void messreiheMitMessreihenIdAussuchen(int messreihenId) {
        for (Messreihe mr : dbModel.getMessreihen()) {
            if (mr.getMessreihenId() == messreihenId) {
                aktuelleMessreihe = mr;
                break;
            }
        }
        Assert.assertNotNull("Messreihe nicht gefunden", aktuelleMessreihe);
    }

    @When("zur aktuellen Messreihe weitere Messung durchführen")
    public void zurAktuellenMessreiheWeitereMessungDurchfuehren() throws Exception {
        Messung neueMessung = new Messung(aktuelleMessreihe.getMessungen().length + 1, 10.0, startedAt + aktuelleMessreihe.getZeitintervall());
        dbModel.speichereMessungInDb(aktuelleMessreihe.getMessreihenId(), neueMessung);
    }

    @When("zur aktuellen Messreihe zu schnell weitere Messung durchführen")
    public void zurAktuellenMessreiheZuSchnellWeitereMessungDurchfuehren() {
        try {
            Messung neueMessung = new Messung(aktuelleMessreihe.getMessungen().length + 1, 10.0, startedAt);
            dbModel.speichereMessungInDb(aktuelleMessreihe.getMessreihenId(), neueMessung);

            neueMessung = new Messung(aktuelleMessreihe.getMessungen().length + 2, 10.0, startedAt + (aktuelleMessreihe.getZeitintervall() / 2));
            dbModel.speichereMessungInDb(aktuelleMessreihe.getMessreihenId(), neueMessung);
        } catch (Exception e) {
            // Exception soll geworfen werden
            exception = e;
        }
    }

    @Then("Es wurde eine Messung zur aktuellen Messreihe an Position {int} gespeichert")
    public void esWurdeEineMessungZurAktuellenMessreiheAnPositionGespeichert(int anzahlMessungen) throws SQLException {
        Assert.assertEquals(anzahlMessungen, dbModel.anzahlMessungenZuMessreihe(aktuelleMessreihe.getMessreihenId()));
    }

    @Then("Exception zum Zeitintervall bei der Anlage einer Messung zur aktuellen Messreihe erhalten")
    public void exceptionZumZeitintervallBeiDerAnlageEinerMessungZurAktuellenMessreiheErhalten() {
        // Überprüfen, ob eine Exception geworfen wurde
        Assert.assertNotNull("Es wurde keine Exception geworfen", exception);
    }
}
