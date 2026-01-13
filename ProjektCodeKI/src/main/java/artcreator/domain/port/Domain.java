package artcreator.domain.port;


/* Factory for creating domain objects */ 

import artcreator.domain.Bild;
import artcreator.domain.Vorlage;
import artcreator.domain.Config;

public interface Domain {
	Object mkObject();

	void setOriginalImage(Bild bild);
	Bild getOriginalImage();

	void setCurrentVorlage(Vorlage vorlage);
	Vorlage getCurrentVorlage();

	void setConfig(Config config);
	Config getConfig();
}
