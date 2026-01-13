package artcreator.statemachine.port;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface State {

	boolean isSubStateOf(State state);
	
	boolean isSuperStateOf(State state);

	public enum S implements State {
		NO_IMAGE,
		IMAGE_LOADED,
		PARAMETERS_READY,
		PREVIEW_GENERATED,
		EXPORT_READY;

		private List<State> subStates;

		public static final S INITIAL_STATE = NO_IMAGE;

		private S(State... subS) {
			this.subStates = new ArrayList<>(Arrays.asList(subS));
		}

		@Override
		public boolean isSuperStateOf(State s) {
			boolean result = (s == null) || (this == s);
			for (State state : this.subStates)
				result |= state.isSuperStateOf(s);
			return result;
		}

		@Override
		public boolean isSubStateOf(State state) {
			return (state != null) && state.isSuperStateOf(this);
		}
	}

}