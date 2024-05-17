package de.hsbochum.client.business;

public class Messung {

    private int laufendeNummer;
    private double wert;
    private long timeMillis;

    public Messung() {
    }

    public Messung(int laufendeNummer, double wert, long timeMillis) {
        super();
        this.laufendeNummer = laufendeNummer;
        this.wert = wert;
        this.timeMillis = timeMillis;
    }

    public int getLaufendeNummer() {
        return laufendeNummer;
    }

    public void setLaufendeNummer(int laufendeNummer) {
        this.laufendeNummer = laufendeNummer;
    }

    public double getWert() {
        return wert;
    }

    public void setWert(double wert) {
        this.wert = wert;
    }

    public String gibAttributeAus() {
        return this.laufendeNummer + ": " + this.wert;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }
}
