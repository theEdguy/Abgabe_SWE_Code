package artcreator.domain;

public class Palette {
    private int anzahlFarben;

    public Palette(int anzahlFarben) {
        this.anzahlFarben = anzahlFarben;
    }

    public int getAnzahlFarben() { return anzahlFarben; }
    public void setAnzahlFarben(int anzahlFarben) { this.anzahlFarben = anzahlFarben; }
}
