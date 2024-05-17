package de.hsbochum.service_emu.restful.emu;

import de.hsbochum.service_emu.restful.Messung;
import net.sf.yad2xx.FTDIException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EmuAktionen {

    private static final Logger logger = Logger.getLogger(EmuAktionen.class.getName());

    public Messung holeMessungVonEMU(int laufendeNummer) throws RuntimeException {
        Messung ergebnis = null;

        EmuCheckConnection ecc = null;
        try {
            ecc = new EmuCheckConnection();
            if (!ecc.isConnected()) {
                throw new RuntimeException("Fehler beim Verbinden auf EMU.");
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
            throw new RuntimeException("Fehler beim Zugriff auf EMU.");
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Thread Fehler.");
        } finally {
            if (ecc != null) {
                try {
                    ecc.disconnect();
                } catch (FTDIException _) {
                }
            }
        }

        return ergebnis;
    }

}
