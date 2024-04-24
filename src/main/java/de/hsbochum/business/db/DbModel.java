package de.hsbochum.business.db;

import de.hsbochum.business.Messreihe;
import de.hsbochum.business.Messung;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.Collections;
import java.util.logging.Logger;

public final class DbModel {

    private static Logger logger = Logger.getLogger(DbModel.class.getName());

    private static DbModel basisModel;
    private final DbAktionen dbAktionen = new DbAktionen();
    private final ObservableList<Messreihe> messreihen = FXCollections.observableArrayList();

    private DbModel() {
    }

    public static DbModel getInstance() {
        if (basisModel == null) {
            basisModel = new DbModel();
        }
        return basisModel;
    }

    public Messung[] leseMessungenAusDb(int messreihenId)
            throws ClassNotFoundException, SQLException {
        Messung[] ergebnis = null;
        this.dbAktionen.connectDb();
        ergebnis = this.dbAktionen.leseMessungen(messreihenId);
        this.dbAktionen.closeDb();
        return ergebnis;
    }

    public void speichereMessungInDb(int messreihenId, Messung messung)
            throws ClassNotFoundException, SQLException {
        this.dbAktionen.connectDb();
        this.dbAktionen.fuegeMessungEin(messreihenId, messung);
        this.dbAktionen.closeDb();
    }

    public void leseMessreihenInklusiveMessungenAusDb() throws ClassNotFoundException, SQLException {
        this.dbAktionen.connectDb();
        Messreihe[] messreihenAusDb = this.dbAktionen.leseMessreihenInklusiveMessungen();
        this.dbAktionen.closeDb();
        int anzahl = this.messreihen.size();
        for (int i = 0; i < anzahl; i++) {
            this.messreihen.remove(0);
        }
        Collections.addAll(this.messreihen, messreihenAusDb);
    }

    public void speichereMessreiheInDb(Messreihe messreihe) throws ClassNotFoundException, SQLException {
        this.dbAktionen.connectDb();
        this.dbAktionen.fuegeMessreiheEin(messreihe);
        this.dbAktionen.closeDb();
        this.messreihen.add(messreihe);
    }

    public int anzahlMessungenZuMessreihe(int messreihenId) throws SQLException, ClassNotFoundException {
        this.dbAktionen.connectDb();
        Messung[] messungen = this.dbAktionen.leseMessungen(messreihenId);
        this.dbAktionen.closeDb();
        int anzahlMessungen = messungen.length;
        return anzahlMessungen;
    }

    public ObservableList<Messreihe> getMessreihen() {
        return messreihen;
    }
}
