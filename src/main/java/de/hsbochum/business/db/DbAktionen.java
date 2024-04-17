package de.hsbochum.business.db;

import de.hsbochum.business.Messreihe;
import de.hsbochum.business.Messung;

import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;

public class DbAktionen {

    Statement statement;
    Connection con;

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

    public void fuegeMessungEin(int messreihenId, Messung messung) throws SQLException {
        String insertMessungStatement = "INSERT INTO messung "
                + "(laufendeNummer, wert, timeMillis, messreihenId) "
                + "VALUES(" + messung.getLaufendeNummer() + ", " + messung.getWert() + ", " + messung.getTimeMillis() +", " + messreihenId + ")";
        System.out.println(insertMessungStatement);
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
        System.out.println(insertMessreiheStatement);
        this.statement.executeUpdate(insertMessreiheStatement);
    }


    public void connectDb() throws ClassNotFoundException, SQLException {
        this.con = DriverManager.getConnection("jdbc:mariadb://localhost/db?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC", "user", "user_pw");
        this.statement = this.con.createStatement();
    }

    public void closeDb() throws SQLException {
        this.con.close();
    }
}    

