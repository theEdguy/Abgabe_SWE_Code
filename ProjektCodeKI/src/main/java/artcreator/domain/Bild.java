package artcreator.domain;

public class Bild {
    private String dateipfad;
    private int breite;
    private int hoehe;
    private int[][] pixelDaten;

    public Bild(String dateipfad, int breite, int hoehe, int[][] pixelDaten) {
        this.dateipfad = dateipfad;
        this.breite = breite;
        this.hoehe = hoehe;
        this.pixelDaten = pixelDaten;
    }

    public String getDateipfad() { return dateipfad; }
    public int getBreite() { return breite; }
    public int getHoehe() { return hoehe; }
    public int[][] getPixelDaten() { return pixelDaten; }
}
