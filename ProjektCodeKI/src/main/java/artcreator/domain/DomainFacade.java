package artcreator.domain;

import artcreator.domain.impl.DomainImpl;
import artcreator.domain.port.Domain;

import artcreator.domain.Bild;
import artcreator.domain.Vorlage;
import artcreator.domain.Config;

public class DomainFacade implements DomainFactory, Domain{

	private DomainImpl domain = new DomainImpl();

	@Override
	public synchronized Domain domain() {
		if (this.domain == null)
			this.domain = new DomainImpl();
		return this;
	}

	@Override
	public synchronized Object mkObject() {
		return this.domain.mkObject();
	}

	public synchronized void setOriginalImage(Bild bild) {
		this.domain.setOriginalImage(bild);
	}

	public synchronized Bild getOriginalImage() {
		return this.domain.getOriginalImage();
	}

	public synchronized void setCurrentVorlage(Vorlage vorlage) {
		this.domain.setCurrentVorlage(vorlage);
	}

	public synchronized Vorlage getCurrentVorlage() {
		return this.domain.getCurrentVorlage();
	}

	public synchronized void setConfig(Config config) {
		this.domain.setConfig(config);
	}

	public synchronized Config getConfig() {
		return this.domain.getConfig();
	}
}
