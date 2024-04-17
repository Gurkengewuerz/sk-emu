package de.hsbochum.gui;

import de.hsbochum.business.Messung;
import de.hsbochum.business.db.DbModel;
import de.hsbochum.business.emu.EmuCheckConnection;
import de.hsbochum.business.emu.MESSWERT;
import javafx.stage.Stage;
import net.sf.yad2xx.FTDIException;

import java.sql.SQLException;

public class BasisControl {

    private DbModel dbModel;
    private BasisView basisView;

    public BasisControl(Stage primaryStage) {
        this.dbModel = DbModel.getInstance();
        this.basisView = new BasisView(this, primaryStage, this.dbModel);
        primaryStage.show();
    }

    public Messung[] leseMessungenAusDb(String messreihenId) {
        Messung[] ergebnis = null;
        int idMessreihe = -1;
        try {
            idMessreihe = Integer.parseInt(messreihenId);
        } catch (NumberFormatException nfExc) {
            basisView.zeigeFehlermeldungAn("Das Format der eingegebenen MessreihenId ist nicht korrekt.");
        }
        try {
            ergebnis = this.dbModel.leseMessungenAusDb(idMessreihe);
        } catch (ClassNotFoundException cnfExc) {
            basisView.zeigeFehlermeldungAn("Fehler bei der Verbindungerstellung zur Datenbank.");
            cnfExc.printStackTrace();
        } catch (SQLException sqlExc) {
            basisView.zeigeFehlermeldungAn("Fehler beim Zugriff auf die Datenbank.");
            sqlExc.printStackTrace();
        }
        return ergebnis;
    }

    private void speichereMessungInDb(int messreihenId, Messung messung) {
        try {
            this.dbModel.speichereMessungInDb(messreihenId, messung);
        } catch (ClassNotFoundException cnfExc) {
            basisView.zeigeFehlermeldungAn("Fehler bei der Verbindungerstellung zur Datenbank.");
        } catch (SQLException sqlExc) {
            basisView.zeigeFehlermeldungAn("Fehler beim Zugriff auf die Datenbank.");
        }
    }

    public Messung holeMessungVonEMU(String messreihenId, String laufendeNummer) {
        Messung ergebnis = null;
        int messId;
        messId = Integer.parseInt(messreihenId);
        int lfdNr = Integer.parseInt(laufendeNummer);

        EmuCheckConnection ecc = null;
        try {
            ecc = new EmuCheckConnection();
            if (!ecc.isConnected()) {
                basisView.zeigeFehlermeldungAn("Fehler beim Verbinden auf EMU.");
            } else {
                System.out.println("ECC Connect...");
                ecc.connect();
                Thread.sleep(1000);
                System.out.println("Programming Mode...");
                ecc.sendProgrammingMode();
                Thread.sleep(1000);
                System.out.println("Request...");
                ecc.sendRequest(MESSWERT.Leistung);
                Thread.sleep(1000);
                System.out.println("Ausgabe...");
                ergebnis = new Messung(lfdNr, ecc.gibErgebnisAus(), System.currentTimeMillis());
            }
        } catch (FTDIException ex) {
            basisView.zeigeFehlermeldungAn("Fehler beim Zugriff auf EMU.");
        } catch (InterruptedException e) {
            basisView.zeigeFehlermeldungAn("Thread Fehler.");
        } finally {
            if (ecc != null) {
                try {
                    ecc.disconnect();
                } catch (FTDIException _) {
                }
            }
        }

        if (ergebnis != null) this.speichereMessungInDb(messId, ergebnis);
        return ergebnis;
    }

}
