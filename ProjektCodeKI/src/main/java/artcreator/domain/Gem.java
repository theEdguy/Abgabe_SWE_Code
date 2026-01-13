package artcreator.domain;

public class Gem {
    private String farbCode;
    private char symbol;

    public Gem(String farbCode, char symbol) {
        this.farbCode = farbCode;
        this.symbol = symbol;
    }

    public String getFarbCode() { return farbCode; }
    public char getSymbol() { return symbol; }
}
