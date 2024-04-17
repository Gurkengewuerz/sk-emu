package de.hsbochum.gui;

import de.hsbochum.business.Messung;
import de.hsbochum.business.db.DbModel;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class BasisView {

    private DbModel dbModel;
    private BasisControl basisControl;

    private Pane pane = new Pane();
    private Label lblMessreihenId = new Label("MessreihenId");
    private Label lblLaufendeNummer = new Label("lfd. Nr. der Messung");
    private TextField txtMessreihenId = new TextField();
    private TextField txtLaufendeNummer = new TextField();
    private TextField txtAnzeige = new TextField();
    private Button btnLeseMessungenAusDB = new Button("Messungen aus DB lesen");
    private Button btnHoleMessungVonEMU = new Button("Messung aus EMU aufnehmen");

    public BasisView(BasisControl basisControl, Stage stage, DbModel dbModel) {
        Scene scene = new Scene(this.pane, 510, 170);
        stage.setScene(scene);
        stage.setTitle("EMU-Anwendung");
        this.basisControl = basisControl;
        this.dbModel = dbModel;
        this.initKomponenten();
        this.initListener();
    }

    private void initKomponenten() {
        // Labels
        lblMessreihenId.setLayoutX(10);
        lblMessreihenId.setLayoutY(30);
        lblLaufendeNummer.setLayoutX(10);
        lblLaufendeNummer.setLayoutY(70);
        pane.getChildren().addAll(lblMessreihenId, lblLaufendeNummer);
        // Textfelder
        txtMessreihenId.setLayoutX(140);
        txtMessreihenId.setLayoutY(30);
        txtLaufendeNummer.setLayoutX(140);
        txtLaufendeNummer.setLayoutY(70);
        txtAnzeige.setLayoutX(10);
        txtAnzeige.setLayoutY(110);
        txtAnzeige.setPrefWidth(480);
        pane.getChildren().addAll(txtMessreihenId, txtLaufendeNummer, txtAnzeige);
        // Buttons
        btnLeseMessungenAusDB.setLayoutX(310);
        btnLeseMessungenAusDB.setLayoutY(30);
        btnLeseMessungenAusDB.setPrefWidth(180);
        btnHoleMessungVonEMU.setLayoutX(310);
        btnHoleMessungVonEMU.setLayoutY(70);
        btnHoleMessungVonEMU.setPrefWidth(180);
        pane.getChildren().addAll(
                btnLeseMessungenAusDB, btnHoleMessungVonEMU);
    }

    private void initListener() {
        btnLeseMessungenAusDB.setOnAction(_ -> {
            Messung[] ergMessungen = basisControl.leseMessungenAusDb(txtMessreihenId.getText());
            StringBuilder erg = new StringBuilder();
            for (Messung messung : ergMessungen) erg.append(messung.gibAttributeAus()).append(" / ");
            txtAnzeige.setText(erg.toString());
        });
        btnHoleMessungVonEMU.setOnAction(_ -> {
            txtAnzeige.setText(basisControl.holeMessungVonEMU(txtMessreihenId.getText(), txtLaufendeNummer.getText()).gibAttributeAus());
        });
    }

    void zeigeFehlermeldungAn(String meldung) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Fehlermeldung");
        alert.setContentText(meldung);
        alert.showAndWait();
    }

}

