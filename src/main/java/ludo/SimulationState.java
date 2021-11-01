package ludo;
import sim.engine.SimState;

public class SimulationState extends SimState {
	
	public SimulationState(long seed) {
		super(seed);	
	}

	public static void main(String[] args) {
		doLoop(SimulationState.class, args);
		System.exit(0);
	}
	
	@Override
	public void start() {
		super.start();
		
		//make sure you understand the different version of the scheduleOnce() und scheduleRepeating() methods (read documentation)
		//agent order is random if agents with same ordering are called at the same time
		Agent alice = new Agent("Alice");
		schedule.scheduleRepeating(alice, 0, 1.0);
		
		Agent bob = new Agent("Bob");
		schedule.scheduleRepeating(bob, 0, 1.0);
	}
	
	//call finish() to terminate gracefully
	@Override
    public void finish() {
        super.finish();
        System.out.println("simulation finished");
    }

}
