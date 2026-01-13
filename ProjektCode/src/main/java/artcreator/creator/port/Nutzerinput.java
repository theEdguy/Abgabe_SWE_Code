package artcreator.creator.port;

public class Nutzerinput {
    public int breite;
    public int hoehe;
    public int anzahlFarben; 
    public String hintergrundHex; 

    public Nutzerinput(int breite, int hoehe, int anzahlFarben, String hintergrundHex) {
        this.breite = breite;
        this.hoehe = hoehe;
        this.anzahlFarben = anzahlFarben;
        this.hintergrundHex = hintergrundHex;
    }
}
