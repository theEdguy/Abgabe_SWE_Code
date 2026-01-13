package artcreator.creator;

import artcreator.creator.impl.CreatorImpl;
import artcreator.creator.port.Creator;
import artcreator.domain.DomainFactory;
import artcreator.statemachine.StateMachineFactory;
import artcreator.statemachine.port.State.S;
import artcreator.statemachine.port.StateMachine;
import java.awt.image.BufferedImage;

public class CreatorFacade implements CreatorFactory, Creator {

	private CreatorImpl creator;
	private StateMachine stateMachine;
	
	@Override
	public Creator creator() {
		if (this.creator == null) {
			this.stateMachine = StateMachineFactory.FACTORY.stateMachine();
			this.creator = new CreatorImpl(stateMachine, DomainFactory.FACTORY.domain());
		}
		return this;
	}

	@Override
	public synchronized void sysop(String str) {
		if (this.stateMachine.getState().isSubStateOf( S.CREATE_TEMPLATE /* choose right state*/ ))
			this.creator.sysop(str);
	}

	@Override
    public void lade_bild(String pfad) {
        
        this.creator.lade_bild(pfad);
    }

    @Override
    public void erstelle_vorschau(Object parameter) { // Achte auf den deutschen Namen!
        this.creator.erstelle_vorschau(parameter);
    }

    @Override
    public BufferedImage get_mein_bild_roh() {
        return this.creator.get_mein_bild_roh();
    }

	@Override
		public BufferedImage get_mein_bild_vorschau() {
        return this.creator.get_mein_bild_vorschau();
    }



	

}
