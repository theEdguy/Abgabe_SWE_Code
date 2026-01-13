package artcreator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import artcreator.creator.CreatorFactory;
import artcreator.creator.port.Creator;
import artcreator.domain.DomainFactory;
import artcreator.domain.port.Domain;
import artcreator.statemachine.StateMachineFactory;
import artcreator.statemachine.port.StateMachine;
import artcreator.statemachine.port.Subject;
import artcreator.statemachine.port.Observer;
import artcreator.statemachine.port.State;
import artcreator.statemachine.port.State.S;

class InitTest implements Observer {

	State s;

	@Test
	void test() {

		StateMachine stateMachine = StateMachineFactory.FACTORY.stateMachine();
		Assertions.assertNotNull(stateMachine);
		Subject subject = StateMachineFactory.FACTORY.subject();
		Assertions.assertEquals(stateMachine, subject);
		subject.attach(this);

		Assertions.assertTrue(stateMachine.getState().isSubStateOf(S.EXPORT_READY));
		Assertions.assertEquals(S.EXPORT_READY, this.s);
		subject.detach(this);

		Domain domain = DomainFactory.FACTORY.domain();
		Assertions.assertNotNull(domain);

		Creator creator = CreatorFactory.FACTORY.creator();
		Assertions.assertNotNull(creator);

		creator.sysop("test");
		Assertions.assertTrue(true);

	}

	@Override
	public void update(State currentState) {
		this.s = currentState;
	}

	@Test
	void useCaseVorlageErstellen() {
		Creator creator = CreatorFactory.FACTORY.creator();
		Domain domain = DomainFactory.FACTORY.domain();

		// Dummy-Bild erzeugen und laden
		int[][] pixel = new int[10][10];
		for (int y = 0; y < 10; y++) for (int x = 0; x < 10; x++) pixel[y][x] = 0xFF0000;
		artcreator.domain.Bild bild = new artcreator.domain.Bild("dummy.png", 10, 10, pixel);
		domain.setOriginalImage(bild);

		// Default-Parameter setzen
		creator.applyDefaultParameters();
		Assertions.assertNotNull(domain.getConfig());

		// Vorschau generieren
		Object preview = creator.generatePreview(null);
		Assertions.assertNotNull(preview);

		// Parameter ändern
		artcreator.domain.Config neueConfig = new artcreator.domain.Config(4, 8, java.util.Collections.emptyList());
		creator.setParameters(neueConfig);
		Assertions.assertEquals(4, domain.getConfig().getFarbtiefe());

		// Verarbeitung bestätigen
		artcreator.domain.Vorlage vorlage = new artcreator.domain.Vorlage(new int[8][8], java.util.Collections.emptyList(), new artcreator.domain.Palette(4));
		domain.setCurrentVorlage(vorlage);
		creator.confirmProcessing();
		Assertions.assertTrue(domain.getCurrentVorlage().isFinalized());
	}
}
