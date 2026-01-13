package artcreator.creator.port;

import java.awt.image.BufferedImage;

public interface Creator {
	
	void sysop(String str);

	//funktionen fürs Interface
	void lade_bild(String pfad);
	void erstelle_vorschau(Object parameter);
	BufferedImage get_mein_bild_roh();
	BufferedImage get_mein_bild_vorschau();


}
