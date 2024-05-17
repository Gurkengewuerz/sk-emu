package de.hsbochum.client.gui;

import de.hsbochum.client.Main;
import de.hsbochum.client.business.Messreihe;
import de.hsbochum.client.business.Messung;
import de.hsbochum.client.business.db.DbModel;
import de.hsbochum.client.business.emu.EmuModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasisControl {
    private static final Logger logger = Logger.getLogger(BasisControl.class.getName());

    private final DbModel dbModel;
    private final EmuModel emuModel;
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
        emuModel = EmuModel.getInstance();
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
        Messung ergebnis = this.emuModel.leseMessungVonEmu(laufendeNummer);
        this.dbModel.speichereMessungInDb(messreihenId, ergebnis);
        return ergebnis;
    }

}
