package artcreator.domain;

import java.awt.image.BufferedImage;
import artcreator.domain.impl.DomainImpl;
import artcreator.domain.port.Domain;

public class DomainFacade implements DomainFactory, Domain {

	private DomainImpl domain = new DomainImpl();

	@Override
	public synchronized Domain domain() {
		if (this.domain == null) this.domain = new DomainImpl();
		return this;
	}

	@Override
	public synchronized Object mkObject() { return this.domain.mkObject(); }

	@Override
	public void setOriginalBild(BufferedImage bild) { this.domain.setOriginalBild(bild); }

	@Override
	public BufferedImage getOriginalBild() { return this.domain.getOriginalBild(); }

	@Override
	public void setVorschauBild(BufferedImage bild) { this.domain.setVorschauBild(bild); }

	@Override
	public BufferedImage getVorschauBild() { return this.domain.getVorschauBild(); }
}
