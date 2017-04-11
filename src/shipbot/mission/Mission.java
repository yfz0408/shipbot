
package shipbot.mission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import shipbot.hardware.SystemState;
import shipbot.staticlib.Config;
import shipbot.staticlib.DeviceData;
import shipbot.staticlib.MessageLog;
import shipbot.tasks.ApproachTask;
import shipbot.tasks.MoveTask;
import shipbot.tasks.Task;

/**
 * Loads and stores the mission file, maintains the mission log, and 
 * executes tasks extracted from the mission file.
 * 
 * @author kat
 *
 */
public class Mission {

	private SystemState system;
	private List<Device> devices;
	private List<Task> tasks;
	
	/**
	 * Test mission execution.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "missions/mission_log.txt";
		Mission mission = new Mission(path);
		try {
			// Check for arduino serial sync
			System.out.println("WAITING FOR SYNC");
			
			boolean synced = false;
			while (!synced) {
				// Check all the files
				if (!DeviceData.waiting(Config.DRIVE_MOTOR_ID)) {
					System.out.println("- Drive motor synced.");
					if (!DeviceData.waiting(Config.Y_STEPPER_ID)) {
						System.out.println("- Depth stepper synced.");
						if (!DeviceData.waiting(Config.Z_STEPPER_ID)) {
							System.out.println("- Height stepper synced.");
							synced = true;
						}
					}
				}
			}
			System.out.println("SYNC ACQUIRED");
		} catch (IOException e) {
			System.out.println("EXCEPTION");
			System.out.println(e.getMessage());
			return;
		}
		mission.executeMission();
	}
	
	/**
	 * Create a new mission from a specified mission file.
	 * 
	 * @param mission_file_path - the path to desired mission file
	 */
	public Mission(String mission_file_path) {
		MessageLog.clearLogs();
		MessageLog.logMissionStatus("New mission initialized.");
		system = new SystemState();
		MissionParser parser = new MissionParser(mission_file_path);
		MessageLog.logMissionStatus("Loaded goals from mission file.");
		
		devices = parser.getAllDevices();
		tasks = new ArrayList<Task>();
		int time_limit = parser.getTimeLimit();
		String format = "Mission time limit is %d sec.";
		MessageLog.logMissionStatus(String.format(format, time_limit));
		
		// Add a task to visit each device!
		for (Device dev : devices) {
			MessageLog.logMissionStatus(dev.getDescription());
			Task move = new MoveTask(dev.getCoordinates());
			tasks.add(move);
			Task approach = new ApproachTask(dev.getPosition());
			tasks.add(approach);
		}
	}
	
	/**
	 * Execute all tasks in mission.
	 */
	public void executeMission() {
		MessageLog.logMissionStatus("Starting task execution.");
		// Execute tasks.
		for (Task task : tasks) {
			System.out.println(String.format("Executing task: %s", task.toString()));
			task.executeTask(system);
			MessageLog.logTaskStatus(task.toString());
		}
		MessageLog.logMissionStatus("Task execution complete.");
	}
 }
