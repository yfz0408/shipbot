package shipbot.mission;

import java.util.ArrayList;
import java.util.List;

import shipbot.hardware.CVSensing;
import shipbot.tasks.AlignTask;
import shipbot.tasks.CaptureTask;
import shipbot.tasks.EngageTask;
import shipbot.tasks.MoveTask;
import shipbot.tasks.PositionTask;
import shipbot.tasks.Task;

/**
 * Rotational devices, including the small and large circular valves. 
 * They should have an angular rotational goal, and have relatively the same task breakdown.
 * 
 * @author kat
 *
 */
public class Valve extends Device {
	
	private static String VALVE_SM = "Small Valve";
	private static String VALVE_LG = "Large Valve";
	private static String SHUTTLECOCK = "Shuttlecock";
	
	private String id;
	private Station station;
	private int angle = -1;
	
	public Valve(Station s, String id) {
		this.station = s;
		switch (id) {
			case "V1":
				this.id = Valve.VALVE_SM;
				break;
			case "V2":
				this.id = Valve.VALVE_LG;
				break;
			case "V3":
				this.id = Valve.SHUTTLECOCK;
				break;
		}
	}
	
	public int getGoalState() {
		return this.angle;
	}

	@Override
	public void addGoalState(int goal_state) {
		this.angle = goal_state;
	}

	@Override
	protected Station getStation() {
		return station;
	}
	
	public List<Task> getTasks() {
		List<Task> tasks = new ArrayList<Task>();
		// Move to position (go to station & recalibrate).
		tasks.add(new MoveTask(this));
		
		// Capture & identify device image
		tasks.add(new CaptureTask(this));
		
		// Position HEBIs
		tasks.add(new PositionTask(this));
		
		// Position arm (y & z steppers)
		tasks.add(new AlignTask(this));
		
		// Rotate effector
		tasks.add(new EngageTask(this));
		return tasks;
	}

	@Override
	public String getDescription() {
		String format = "Device: %s @ Station %s -- Rotate to [%d] degrees.";
		return String.format(format, this.id, station.toString(), this.angle);
	}

	@Override
	protected String id() {
		return this.id;
	}

}
