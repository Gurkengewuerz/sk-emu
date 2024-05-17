package de.hsbochum.client.business.emu;

import de.hsbochum.client.business.Constants;
import de.hsbochum.client.business.Messreihe;
import de.hsbochum.client.business.Messung;
import de.hsbochum.client.business.db.DbModel;
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

public final class EmuModel {

    private static final Logger logger = Logger.getLogger(EmuModel.class.getName());

    private static EmuModel basisModel;

    private final Client client = ClientBuilder.newClient();

    private EmuModel() {
    }

    public static EmuModel getInstance() {
        if (basisModel == null) {
            basisModel = new EmuModel();
        }
        return basisModel;
    }

    public Messung leseMessungVonEmu(int laufendeNummer) {
        logger.log(Level.INFO, "leseMessungVonEmu({0})", laufendeNummer);
        return client.target(Constants.REST_URI + Constants.REST_URI_EMU + "/messung").path(String.valueOf(laufendeNummer)).request(MediaType.APPLICATION_JSON).get(Messung.class);
    }

}
