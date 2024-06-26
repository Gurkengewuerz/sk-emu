package de.hsbochum.client.business.db;

import de.hsbochum.client.business.Constants;
import de.hsbochum.client.business.Messreihe;
import de.hsbochum.client.business.Messung;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DbModel {

    private static final Logger logger = Logger.getLogger(DbModel.class.getName());

    private static DbModel basisModel;
    private final ObservableList<Messreihe> messreihen = FXCollections.observableArrayList();

    private final Client client = ClientBuilder.newClient();

    private DbModel() {
    }

    public static DbModel getInstance() {
        if (basisModel == null) {
            basisModel = new DbModel();
        }
        return basisModel;
    }

    public void speichereMessungInDb(int messreihenId, Messung messung) throws SQLException {
        logger.log(Level.INFO, "speichereMessungInDb({0}, ...)", messreihenId);
        try (final Response response = client.target(Constants.REST_URI + Constants.REST_URI_DB + "/messung").path(String.valueOf(messreihenId)).request(MediaType.TEXT_PLAIN).post(Entity.entity(messung, MediaType.APPLICATION_JSON))) {
            if (response.getStatus() != 201) {
                logger.log(Level.SEVERE, "Fehler in speichereMessungInDb(): response.getStatus != 201 / = {0}", response.getStatus());
                throw new SQLException("Fehler beim Eintragen in die Datenbank!"); // Ausstieg bei Fehler!
            }
        }
    }

    public void leseMessreihenInklusiveMessungenAusDb() throws SQLException {
        logger.log(Level.INFO, "leseMessreihenInklusiveMessungenAusDb()");
        this.messreihen.clear();
        final Messreihe[] messreihen = client.target(Constants.REST_URI + Constants.REST_URI_DB + "/messreihen").request(MediaType.APPLICATION_JSON).get(Messreihe[].class);
        // Neue Messreihenliste aufbauen
        this.messreihen.addAll(Arrays.asList(messreihen));
    }

    public void speichereMessreiheInDb(Messreihe messreihe) throws SQLException {
        logger.log(Level.INFO, "speichereMessreiheInDb(...)");
        try (final Response response = client.target(Constants.REST_URI + Constants.REST_URI_DB + "/messreihe").path(String.valueOf(messreihe.getMessreihenId())).request(MediaType.TEXT_PLAIN).post(Entity.entity(messreihe, MediaType.APPLICATION_JSON))) {
            if (response.getStatus() != 201) {
                logger.log(Level.SEVERE, "Fehler in speichereMessreiheInDb(): response.getStatus != 201 / = {0}", response.getStatus());
                throw new SQLException("Fehler beim Eintragen in die Datenbank!");
            }
        }
    }

    public int anzahlMessungenZuMessreihe(int messreihenId) throws SQLException {
        logger.log(Level.INFO, "anzahlMessungenZuMessreihe({0})", messreihenId);
        final Messung[] messungen = client.target(Constants.REST_URI + Constants.REST_URI_DB + "/messreihe").path(String.valueOf(messreihenId)).path("messungen").request(MediaType.APPLICATION_JSON).get(Messung[].class);
        return messungen.length;
    }

    public void loescheMessreiheInklusiveMessungenAusDb(Messreihe messreihe) throws SQLException {
        logger.log(Level.INFO, "loescheMessreiheInklusiveMessungenAusDb(...)");
        try (final Response response = client.target(Constants.REST_URI + Constants.REST_URI_DB + "/messreihe").path(String.valueOf(messreihe.getMessreihenId())).request(MediaType.TEXT_PLAIN).delete()) {
            if (response.getStatus() != 200) {
                logger.log(Level.SEVERE, "Fehler in loescheMessreiheInklusiveMessungenAusDb(): response.getStatus != 200 / = {0}", response.getStatus());
                throw new SQLException("Fehler beim Löschen aus der Datenbank!");
            }
        }
    }

    public void loescheMessreiheInklusiveMessungenAusDb(int messreiheID) throws SQLException {
        loescheMessreiheInklusiveMessungenAusDb(new Messreihe(messreiheID, 0, "Dummy", "Dummy"));
    }

    public ObservableList<Messreihe> getMessreihen() {
        return messreihen;
    }
}
