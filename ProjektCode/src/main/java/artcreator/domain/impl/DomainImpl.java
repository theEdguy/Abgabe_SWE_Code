package artcreator.domain.impl;

import java.awt.image.BufferedImage;

public class DomainImpl {

    private BufferedImage originalBild;
    private BufferedImage vorschauBild;

    public Object mkObject() { return null; }

    public void setOriginalBild(BufferedImage bild) { this.originalBild = bild; }
    public BufferedImage getOriginalBild() { return this.originalBild; }

    public void setVorschauBild(BufferedImage bild) { this.vorschauBild = bild; }
    public BufferedImage getVorschauBild() { return this.vorschauBild; }
}
