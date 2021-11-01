package ludo;
import sim.engine.SimState;
import sim.engine.Steppable;

public class Agent implements Steppable {
	
	private String name;
	
	public Agent(String name) {
		this.name = name;
	}

	//step() method is basically the start point for agent behavior
	@Override
	public void step(SimState simState) {
		SimulationState simulationState = (SimulationState) simState;
		System.out.println("my name is " + name);
	}

}
