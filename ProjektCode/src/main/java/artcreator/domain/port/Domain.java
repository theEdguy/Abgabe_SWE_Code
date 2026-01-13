package artcreator.domain.port;


/* Factory for creating domain objects */ 

import java.awt.image.BufferedImage;

public interface Domain {

	Object mkObject();

	void setOriginalBild(BufferedImage bild);
	BufferedImage getOriginalBild();

	void setVorschauBild(BufferedImage bild);
	BufferedImage getVorschauBild();
}
