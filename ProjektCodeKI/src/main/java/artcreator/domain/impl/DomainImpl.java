package artcreator.domain.impl;


/* Factory for creating domain objects */ 

import artcreator.domain.Bild;
import artcreator.domain.Vorlage;
import artcreator.domain.Config;

public class DomainImpl {
	private Bild originalBild;
	private Vorlage aktuelleVorlage;
	private Config config;

	public Object mkObject() {
		return null;
	}

	public void setOriginalImage(Bild bild) {
		this.originalBild = bild;
	}

	public Bild getOriginalImage() {
		return this.originalBild;
	}

	public void setCurrentVorlage(Vorlage vorlage) {
		this.aktuelleVorlage = vorlage;
	}

	public Vorlage getCurrentVorlage() {
		return this.aktuelleVorlage;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public Config getConfig() {
		return this.config;
	}
}
