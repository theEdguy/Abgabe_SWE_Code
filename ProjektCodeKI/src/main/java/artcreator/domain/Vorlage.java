package artcreator.domain;

import java.util.List;

public class Vorlage {
    private int[][] raster;
    private List<Gem> gems;
    private Palette palette;
    private boolean finalized;

    public Vorlage(int[][] raster, List<Gem> gems, Palette palette) {
        this.raster = raster;
        this.gems = gems;
        this.palette = palette;
        this.finalized = false;
    }

    public int[][] getRaster() { return raster; }
    public List<Gem> getGems() { return gems; }
    public Palette getPalette() { return palette; }
    public boolean isFinalized() { return finalized; }
    public void setFinalized(boolean finalized) { this.finalized = finalized; }
}
