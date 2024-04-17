package de.hsbochum.business.db;

import de.hsbochum.business.Messreihe;
import de.hsbochum.business.Messung;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public final class DbModel {

    private static DbModel basisModel;
    private DbAktionen dbAktionen = new DbAktionen();
    // wird zukuenftig noch instanziiert
    private ObservableList<Messreihe> messreihen = null;

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

    public void leseMessreihenInklusiveMessungenAusDb()
            throws ClassNotFoundException, SQLException {
        this.dbAktionen.connectDb();
        Messreihe[] messreihenAusDb
                = this.dbAktionen.leseMessreihenInklusiveMessungen();
        this.dbAktionen.closeDb();
        int anzahl = this.messreihen.size();
        for (int i = 0; i < anzahl; i++) {
            this.messreihen.remove(0);
        }
        for (int i = 0; i < messreihenAusDb.length; i++) {
            this.messreihen.add(messreihenAusDb[i]);
        }
    }

    public void speichereMessreiheInDb(Messreihe messreihe)
            throws ClassNotFoundException, SQLException {
        this.dbAktionen.connectDb();
        this.dbAktionen.fuegeMessreiheEin(messreihe);
        this.dbAktionen.closeDb();
        this.messreihen.add(messreihe);
    }

}
