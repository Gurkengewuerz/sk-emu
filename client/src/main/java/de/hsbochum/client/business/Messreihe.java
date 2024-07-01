package de.hsbochum.client.business;


public class Messreihe {

    private int messreihenId;
    private int zeitintervall;
    private String verbraucher;
    private String messgroesse;
    private Messung[] messungen;
    private String messungenString;

    public Messreihe() {
    }

    public Messreihe(int messreihenId, int zeitintervall, String verbraucher, String messgroesse) {
        this.messreihenId = messreihenId;
        this.zeitintervall = zeitintervall;
        this.verbraucher = verbraucher;
        this.messgroesse = messgroesse;
        this.messungen = new Messung[0];
    }

    public Messreihe(int messreihenId, int zeitintervall) throws IllegalArgumentException {
        this.messreihenId = messreihenId;
        if (zeitintervall >= 15 && zeitintervall <= 3600) this.zeitintervall = zeitintervall;
        else if (zeitintervall < 15)
            throw new IllegalArgumentException("Das Zeitintervall muss mindestens 15 Sekunden lang sein.");
        else
            throw new IllegalArgumentException("Das Zeitintervall darf hoechstens 3600 Sekunden lang sein.");
    }

    public int getMessreihenId() {
        return messreihenId;
    }

    public void setMessreihenId(int messreihenId) {
        this.messreihenId = messreihenId;
    }

    public int getZeitintervall() {
        return zeitintervall;
    }

    public void setZeitintervall(int zeitintervall) {
        this.zeitintervall = zeitintervall;
    }

    public String getVerbraucher() {
        return verbraucher;
    }

    public void setVerbraucher(String verbraucher) {
        this.verbraucher = verbraucher;
    }

    public String getMessgroesse() {
        return messgroesse;
    }

    public void setMessgroesse(String messgroesse) {
        this.messgroesse = messgroesse;
    }

    public Messung[] getMessungen() {
        return messungen;
    }

    public void setMessungen(Messung[] messungen) {
        this.messungen = messungen;
    }

    public String gibAttributeAus() {
        return (this.messreihenId + " " + this.zeitintervall + " " + this.verbraucher + " " + this.messgroesse);
    }

    public String getMessungenString() {
        if (this.messungen == null) return "";
        String[] stringArray = new String[this.messungen.length];
        for (int i = 0; i < this.messungen.length; i++) {
            stringArray[i] = this.messungen[i].gibAttributeAus();
        }
        return String.join(" / ", stringArray);
    }


}
