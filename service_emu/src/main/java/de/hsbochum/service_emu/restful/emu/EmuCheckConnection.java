package de.hsbochum.service_emu.restful.emu;

import net.sf.yad2xx.Device;
import net.sf.yad2xx.FTDIException;
import net.sf.yad2xx.FTDIInterface;

public class EmuCheckConnection extends Thread {

    private final Device[] devices;
    // Es gibt eine Klasse Device. Das Attribut device vom Typ
    // Device soll Eigenschaften und Methoden zu dem
    // angeschlossenen Leistungsmessgeraet enthalten.
    // private FTDIInterface fi = new FTDIInterface();
    private Device device = null;
    // Attribut zur Regelung des Threads, siehe unten
    private boolean connected = false;
    private String ergebnis = "";
    private boolean ergSchreiben = false;

    public EmuCheckConnection() throws FTDIException {
        // Listen Sie alle angeschlossenen Geraete auf.
        // Es gibt eine Klasse FTDIInterface, welche eine
        // statische Methode getDevices() enthaelt, welche
        // ein Array von angeschlossenen Geraeten als
        // Rueckgabewert hat.
        devices = FTDIInterface.getDevices();
        System.out.println("Anzahl gefundener FTD2 Devices: " + devices.length);

        // Das Device, dessen Beschreibung mit NZR anfaengt, ist
        // das Leistungsmessgeraet. Das Attribut device soll
        // damit belegt werden. Die Beschreibung eines Devices
        // ermitteln Sie mit Hilfe der Methode getDescripton()
        // der Klasse Device mit Rueckgabewert vom Typ String.
        for (Device value : devices) {
            if (value.getDescription().startsWith("NZR")) {
                device = value;
            }
        }

        if (device == null) return;

        this.device.open();
        // Setzen Sie die DatenCharacteristika auf 7 DataBits,
        // 1 StopBit, gerade Paritaet (2).
        // Dann setzen Sie die Baudrate auf 300.
        // Geben Sie die Beschreibung des Devices heraus.
        byte dc = 7;
        byte sb = 1;
        byte gp = 2;
        device.setDataCharacteristics(dc, sb, gp);
        device.setBaudRate(300);

        connected = true;

        System.out.println("Verbunden mit Device: " + device.getDescription());

        this.start();
    }

    public void connect() throws FTDIException {
        byte[] start = new byte[]{0x2F, 0x3F, 0x21, 0x0D, 0x0A};
        this.device.write(start);
        System.out.println("Einleitung Kommunikation USBCheck");
    }

    public double gibErgebnisAus() {
        System.out.println("Ergebnis: " + ergebnis);
        if (ergebnis.contains(new String(new byte[]{0x02}))) {
            int a = ergebnis.indexOf("(");
            int m = ergebnis.indexOf("*");
            ergebnis = ergebnis.substring(a + 1, m);
        }
        if (ergebnis.isEmpty()) return 0D;
        return Double.parseDouble(ergebnis);
    }

    public void disconnect() throws FTDIException {
        if (this.connected && this.device != null) {
            byte[] ende = new byte[]{0x01, 0x42, 0x30, 0x03};
            this.device.write(ende);
            this.device.close();
        }
        this.connected = false;
        System.out.println("Ende Kommunikation USBCheck");
    }

    public void sendProgrammingMode() throws FTDIException {
        byte[] nachricht = new byte[]{0x06, 0x30, 0x30, 0x31, 0x0D, 0x0A};
        this.device.write(nachricht);
        System.out.println("Programming Mode");
    }

    public void sendRequest(MESSWERT m) throws FTDIException {
        device.write(m.getRequest());
        System.out.println("Request " + m.getObis() + " " + m);
        ergSchreiben = true;
    }

    public boolean isConnected() {
        return connected;
    }

    // Damit das Device staendig liest, muss die Klasse
    // EmuCheckConnection von der Klasse Thread ableiten. Das
    // Thread wird am Ende des Konstruktors gestartet. Weiterhin
    // muss die Methode run ueberschrieben werden.

    public void run() {
        // Das Device soll so lange Leseversuche durchfuehren,
        // wie die Verbindung existiert (, siehe Attribut
        // connected). Es soll nur gelesen werden, falls der
        // QueueStatus des devices ungleich 0 ist. Den
        // gelesenen Wert finden Sie in dem Byte-Array der
        // Laenge 1, welcher als Parameter mitgegeben wird.
        // Dieser wird in in die Konsole geschrieben.
        // Dazu konvertieren Sie ihn vorher in ein char-Wert.

        int intZahl;
        byte[] byteArray = new byte[1];

        while (this.connected) {
            try {
                intZahl = device.getQueueStatus();
                if (intZahl != 0) {
                    intZahl = device.read(byteArray);
                    //System.out.print((char) byteArray[0]);
                    if (ergSchreiben) {
                        ergebnis = ergebnis + (char) byteArray[0];
                    }
                }
            } catch (FTDIException e) {
                if (!e.getMessage().equalsIgnoreCase("Exception in D2XX native library call."))
                    e.printStackTrace();
            }
            try {
                sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}