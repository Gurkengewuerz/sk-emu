package de.hsbochum.service_db.restful.db;

import de.hsbochum.service_db.restful.Messreihe;
import de.hsbochum.service_db.restful.Messung;

import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbAktionen {
    private static final Logger logger = Logger.getLogger(DbAktionen.class.getName());

    private Statement statement;
    private Connection con;

    public Messung[] leseMessungen(int messreihenId) throws SQLException {
        ResultSet ergebnis;
        ergebnis = this.statement.executeQuery("SELECT * FROM messung WHERE messreihenId = " + messreihenId);
        Vector<Messung> messungen = new Vector<Messung>();
        while (ergebnis.next()) {
            messungen.add(new Messung(
                    Integer.parseInt(ergebnis.getString(1)),
                    Double.parseDouble(ergebnis.getString(2)),
                    Long.parseLong(ergebnis.getString(3))));
        }
        ergebnis.close();
        return messungen.toArray(new Messung[0]);
    }

    public Messreihe leseMessreihe(int messreihenId) throws SQLException {
        ResultSet ergebnis;
        ergebnis = this.statement.executeQuery("SELECT * FROM messreihe WHERE messreihenId = " + messreihenId);
        Messreihe res = new Messreihe();
        while (ergebnis.next()) {
            res = new Messreihe(ergebnis.getInt(1), ergebnis.getInt(2), ergebnis.getString(3), ergebnis.getString(4));
        }
        ergebnis.close();
        return res;
    }

    public void fuegeMessungEin(int messreihenId, Messung messung) throws SQLException {
        String insertMessungStatement = "INSERT INTO messung "
                + "(laufendeNummer, wert, timeMillis, messreihenId) "
                + "VALUES(" + messung.getLaufendeNummer() + ", " + messung.getWert() + ", " + messung.getTimeMillis() + ", " + messreihenId + ")";
        logger.log(Level.INFO, insertMessungStatement);
        this.statement.executeUpdate(insertMessungStatement);
    }

    public Messreihe[] leseMessreihenInklusiveMessungen() throws SQLException {
        ResultSet ergebnis;
        ergebnis = this.statement.executeQuery("SELECT * FROM messreihe");
        ArrayList<Messreihe> messreihen = new ArrayList<>();
        while (ergebnis.next()) {
            messreihen.add(new Messreihe(Integer.parseInt(ergebnis.getString(1)),
                    Integer.parseInt(ergebnis.getString(2)),
                    ergebnis.getString(3), ergebnis.getString(4)));
        }
        for (Messreihe messreihe : messreihen) messreihe.setMessungen(this.leseMessungen(messreihe.getMessreihenId()));
        ergebnis.close();
        return messreihen.toArray(new Messreihe[0]);
    }

    public void fuegeMessreiheEin(Messreihe messreihe)
            throws SQLException {
        String insertMessreiheStatement = "INSERT INTO messreihe "
                + "(messreihenId, zeitintervall, verbraucher, messgroesse) "
                + "VALUES(" + messreihe.getMessreihenId() + ", "
                + messreihe.getZeitintervall() + ", '"
                + messreihe.getVerbraucher() + "', '"
                + messreihe.getMessgroesse() + "')";
        logger.log(Level.INFO, insertMessreiheStatement);
        this.statement.executeUpdate(insertMessreiheStatement);
    }

    public void loescheMessreiheInklusiveMessungen(int messreihenId) throws SQLException {
        String deleteMessungenStatement = "DELETE FROM messung WHERE messreihenId = " + messreihenId;
        this.statement.executeUpdate(deleteMessungenStatement);
        String deleteMessreiheStatement = "DELETE FROM messreihe WHERE messreihenId = " + messreihenId;
        this.statement.executeUpdate(deleteMessreiheStatement);
    }

    public void connectDb() throws SQLException {
        DriverManager.registerDriver(new org.mariadb.jdbc.Driver());
        this.con = DriverManager.getConnection("jdbc:mariadb://localhost/db?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC", "user", "user_pw");
        this.statement = this.con.createStatement();
    }

    public void closeDb() throws SQLException {
        this.con.close();
    }
}    

