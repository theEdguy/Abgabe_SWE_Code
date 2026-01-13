package artcreator.creator;

import artcreator.creator.impl.CreatorImpl;
import artcreator.creator.port.Creator;
import artcreator.domain.DomainFactory;
import artcreator.statemachine.StateMachineFactory;
import artcreator.statemachine.port.StateMachine;
import artcreator.statemachine.port.State.S;

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
		if (this.stateMachine.getState().isSubStateOf(S.NO_IMAGE))
			this.creator.sysop(str);
	}

	public synchronized void loadImage(String path) {
		if (this.stateMachine.getState().isSubStateOf(S.NO_IMAGE)) {
			this.creator.loadImage(path);
			this.stateMachine.setState(S.IMAGE_LOADED);
		}
	}

	public synchronized void applyDefaultParameters() {
		if (this.stateMachine.getState().isSubStateOf(S.IMAGE_LOADED)) {
			this.creator.applyDefaultParameters();
			this.stateMachine.setState(S.PARAMETERS_READY);
		}
	}

	public synchronized Object generatePreview(Object parameters) {
		if (this.stateMachine.getState().isSubStateOf(S.PARAMETERS_READY)) {
			Object result = this.creator.generatePreview(parameters);
			this.stateMachine.setState(S.PREVIEW_GENERATED);
			return result;
		}
		return null;
	}

	public synchronized void setParameters(Object parameters) {
		if (this.stateMachine.getState().isSubStateOf(S.PREVIEW_GENERATED)) {
			this.creator.setParameters(parameters);
			this.stateMachine.setState(S.PARAMETERS_READY);
		}
	}

	public synchronized void confirmProcessing() {
		if (this.stateMachine.getState().isSubStateOf(S.PREVIEW_GENERATED)) {
			this.creator.confirmProcessing();
			this.stateMachine.setState(S.EXPORT_READY);
		}
	}

}
