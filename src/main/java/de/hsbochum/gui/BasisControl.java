package de.hsbochum.gui;

import de.hsbochum.Main;
import de.hsbochum.business.Messreihe;
import de.hsbochum.business.Messung;
import de.hsbochum.business.db.DbModel;
import de.hsbochum.business.emu.EmuCheckConnection;
import de.hsbochum.business.emu.MESSWERT;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import net.sf.yad2xx.FTDIException;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasisControl {
    private static Logger logger = Logger.getLogger(BasisControl.class.getName());

    private final DbModel dbModel;
    @FXML
    private TextField txtMessreihenId;
    @FXML
    private TextField txtZeitintervall;
    @FXML
    private TextField txtVerbraucher;
    @FXML
    private TextField txtMessgroesse;
    @FXML
    private Button btnMessungErstellen;
    @FXML
    private TableView<Messreihe> tableView;
    @FXML
    private TableColumn<Messreihe, Integer> clmIdentnummer;
    @FXML
    private TableColumn<Messreihe, Integer> clmZeitIntervall;
    @FXML
    private TableColumn<Messreihe, String> clmVerbraucher;
    @FXML
    private TableColumn<Messreihe, String> clmMessgroesse;
    @FXML
    private TableColumn<Messreihe, String> clmMessungen;
    private ObservableList<Messreihe> messreihen;


    public BasisControl() {
        dbModel = DbModel.getInstance();
    }


    @FXML
    public void speichereMessreiheInDB() {
        int identNummerMessreihe = Integer.parseInt(this.txtMessreihenId.getText());
        int zeitIntervallSekunden = Integer.parseInt(this.txtZeitintervall.getText());
        String verbraucher = this.txtVerbraucher.getText();
        String messgroesse = this.txtMessgroesse.getText();
        Messreihe messreihe = new Messreihe(identNummerMessreihe, zeitIntervallSekunden, verbraucher, messgroesse);
        try {
            this.dbModel.speichereMessreiheInDb(messreihe);
        } catch (ClassNotFoundException cnfExc) {
            logger.log(Level.SEVERE, cnfExc.getMessage(), cnfExc);
            Main.zeigeFehlermeldungAn("Fehler bei der Verbindungerstellung zur Datenbank.");
            return;
        } catch (SQLException sqlExc) {
            logger.log(Level.SEVERE, sqlExc.getMessage(), sqlExc);
            Main.zeigeFehlermeldungAn("Fehler beim Zugriff auf die Datenbank.");
            return;
        }
        this.leseMessreihenInklusiveMessungenAusDb();

        // Zurücksetzen der Textfelder
        this.txtMessreihenId.setText("");
        this.txtZeitintervall.setText("");
        this.txtVerbraucher.setText("");
        this.txtMessgroesse.setText("");
    }

    @FXML
    public void leseMessreihenInklusiveMessungenAusDb() {
        logger.log(Level.INFO, "leseMessreihenInklusiveMessungenAusDb");
        try {
            this.dbModel.leseMessreihenInklusiveMessungenAusDb();
            this.messreihen = this.dbModel.getMessreihen();
            this.tableView.getItems().setAll(this.messreihen);
        } catch (ClassNotFoundException cnfExc) {
            logger.log(Level.SEVERE, cnfExc.getMessage(), cnfExc);
            Main.zeigeFehlermeldungAn("Fehler bei der Verbindungerstellung zur Datenbank.");
        } catch (SQLException sqlExc) {
            logger.log(Level.SEVERE, sqlExc.getMessage(), sqlExc);
            Main.zeigeFehlermeldungAn("Fehler beim Zugriff auf die Datenbank.");
        }
    }

    @FXML
    public void holeMessungVonEMU() {
        logger.log(Level.INFO, "holeMessungVonEMU");

        Messreihe messreihe = this.tableView.getSelectionModel().getSelectedItem();
        if (messreihe == null) {
            Main.zeigeFehlermeldungAn("Keine Messreihe ausgewählt!");
            return;
        }
        try {
            int anzahl = this.dbModel.anzahlMessungenZuMessreihe(messreihe.getMessreihenId());
            this.holeMessungVonEMU(messreihe.getMessreihenId(), anzahl);
        } catch (ClassNotFoundException cnfExc) {
            logger.log(Level.SEVERE, cnfExc.getMessage(), cnfExc);
            Main.zeigeFehlermeldungAn("Fehler bei der Verbindungerstellung zur Datenbank.");
        } catch (SQLException sqlExc) {
            logger.log(Level.SEVERE, sqlExc.getMessage(), sqlExc);
            Main.zeigeFehlermeldungAn("Fehler beim Zugriff auf die Datenbank.");
        }
        this.leseMessreihenInklusiveMessungenAusDb();
    }

    public Messung holeMessungVonEMU(int messreihenId, int laufendeNummer) throws SQLException, ClassNotFoundException {
        Messung ergebnis = null;

        EmuCheckConnection ecc = null;
        try {
            ecc = new EmuCheckConnection();
            if (!ecc.isConnected()) {
                Main.zeigeFehlermeldungAn("Fehler beim Verbinden auf EMU.");
            } else {
                logger.log(Level.INFO, "ECC Connect...");
                ecc.connect();
                Thread.sleep(1000);
                logger.log(Level.INFO, "Programming Mode...");
                ecc.sendProgrammingMode();
                Thread.sleep(1000);
                logger.log(Level.INFO, "Request...");
                ecc.sendRequest(MESSWERT.Leistung);
                Thread.sleep(1000);
                logger.log(Level.INFO, "Ausgabe...");
                ergebnis = new Messung(laufendeNummer, ecc.gibErgebnisAus(), System.currentTimeMillis());
            }
        } catch (FTDIException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Main.zeigeFehlermeldungAn("Fehler beim Zugriff auf EMU.");
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Main.zeigeFehlermeldungAn("Thread Fehler.");
        } finally {
            if (ecc != null) {
                try {
                    ecc.disconnect();
                } catch (FTDIException _) {
                }
            }
        }

        if (ergebnis != null) this.dbModel.speichereMessungInDb(messreihenId, ergebnis);
        return ergebnis;
    }

}
