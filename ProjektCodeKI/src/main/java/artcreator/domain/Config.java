package artcreator.domain;

import java.util.List;

public class Config {
    private int farbtiefe;
    private int aufloesung;
    private List<String> filter;

    public Config(int farbtiefe, int aufloesung, List<String> filter) {
        this.farbtiefe = farbtiefe;
        this.aufloesung = aufloesung;
        this.filter = filter;
    }

    public int getFarbtiefe() { return farbtiefe; }
    public void setFarbtiefe(int farbtiefe) { this.farbtiefe = farbtiefe; }
    public int getAufloesung() { return aufloesung; }
    public void setAufloesung(int aufloesung) { this.aufloesung = aufloesung; }
    public List<String> getFilter() { return filter; }
    public void setFilter(List<String> filter) { this.filter = filter; }
}
