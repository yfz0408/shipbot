package shipbot.tasks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import shipbot.hardware.SystemState;
import shipbot.staticlib.MessageLog;

/**
 * Represents a task (motion, end effector articulation) that has been scheduled.
 * 
 * @author kat
 *
 */
public abstract class Task {
	
	public abstract void executeTask(SystemState sys);
	
	protected abstract TaskStatus getStatus();
	
}