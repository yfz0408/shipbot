package shipbot.staticlib;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface for interacting with device data files. 
 * 
 * @author kat
 *
 */
public class DeviceData {

	private static String sensor_path_format = "devices/sensors/%s.txt";
	private static String motor_path_format = "devices/actuators/%s.txt";
	
	/**
	 * Test sensor and motor data functions. 
	 * Does nothing if Config.DEBUG is false.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (!Config.DEBUG) {
			return;
		}
		// SENSOR TEST
		String id = "IR_0";
		Map<String,Double> data = DeviceData.getSensorData(id);
		for (String key : data.keySet()) {
			if (key != "owner") {
				System.out.print(key);
				System.out.print(": ");
				System.out.println(data.get(key).toString());
			} else {
				System.out.print("Owner is ");
				System.out.println(data.get(key).toString());
			}
		}
		
		// MOTOR READ TEST
		id = "DRIVE_0";
		Map<String, Integer> motor_data = DeviceData.getMotorData(id);
		for (String key : motor_data.keySet()) {
			if (key != "owner") {
				System.out.print(key);
				System.out.print(": ");
				System.out.println(motor_data.get(key).toString());
			} else {
				System.out.print("Owner is ");
				System.out.println(motor_data.get(key).toString());
			}
		}
		
		// MOTOR WRITE TEST
		motor_data.put("modified", 490);
		motor_data.remove("owner");
		DeviceData.writeMotorData(id, motor_data);
	}
	
	/**
	 * Tokenizes the sensor data file, reads fields and stores data as a map
	 * of field -> (double) value. 
	 * 
	 * @param sensor_id
	 * @return map of data file
	 */
	public static Map<String, Double> getSensorData(String sensor_id) {
		String sensor_path = String.format(sensor_path_format, sensor_id);
		Map<String, Double> data = new HashMap<String, Double>();
		try {
			// TODO: lock sensor data file before reading!
			Reader reader = new FileReader(sensor_path);
			StreamTokenizer tok = new StreamTokenizer(reader);
			tok.parseNumbers();
			
			boolean eof = false;
			String field = "";
			while (!eof) {
				int token = tok.nextToken();
				switch (token) {
					case StreamTokenizer.TT_EOF:
						eof = true;
						break;
					case StreamTokenizer.TT_WORD:
						field = tok.sval;
						break;
					case StreamTokenizer.TT_NUMBER:
						data.put(field, tok.nval);
						break;
					default:
						if ((char) token == '@') {
							token = tok.nextToken();
							data.put("owner", tok.nval);
						}
						break;
				}
			}
			reader.close();
		} catch (IOException e) {
			MessageLog.printError("SENSOR_UPDATE", "IOException while updating sensor data.");
		}
		return data;
	}
	
	/**
	 * Tokenizes the motor data file, reads fields and stores data as a map
	 * of field -> (int) value. Be sure to check that the owner is Arduino
	 * (which is to say, the last writer was the Arduino) before storing values
	 * in system state. 
	 * 
	 * @param motor_id
	 * @return
	 */
	public static Map<String, Integer> getMotorData(String motor_id) {
		String motor_path = String.format(motor_path_format, motor_id);
		Map<String, Integer> data = new HashMap<String, Integer>();
		try {
			// TODO: lock motor file before reading!
			Reader reader = new FileReader(motor_path);
			StreamTokenizer tok = new StreamTokenizer(reader);
			tok.parseNumbers();
			
			boolean eof = false;
			String field = "";
			int owner = 0;
			while (!eof) {
				int token = tok.nextToken();
				switch (token) {
					case StreamTokenizer.TT_EOF:
						eof = true;
						break;
					case StreamTokenizer.TT_WORD:
						field = tok.sval;
						break;
					case StreamTokenizer.TT_NUMBER:
						data.put(field, (int) tok.nval);
						break;
					default:
						if ((char) token == '@') {
							token = tok.nextToken();
							owner = (int) tok.nval;
						}
						break;
				}
			}
			reader.close();
			if (owner != Config.OWNER_ARDUINO) {
				MessageLog.printError("MOTOR_UPDATE", "Read old data! Dumping.");
				return null;
			}
		} catch (IOException e) {
			MessageLog.printError("MOTOR_UPDATE", "IOException while updating motor data.");
		}
		return data;
	}
	
	/**
	 * Writes mapped data to specified motor's data file, setting the file owner to be the Pi. 
	 * @param motor_id - the motor whose data should be modified
	 * @param data - the field,value map data to write out
	 */
	public static void writeMotorData(String motor_id, Map<String, Integer> data) {
		String motor_path = String.format(motor_path_format, motor_id);
		StringBuilder sb = new StringBuilder(String.format("@ %d\n", Config.OWNER_PI));
		for (String key : data.keySet()) {
			sb.append(key).append(" ");
			sb.append(data.get(key).toString());
			sb.append('\n');
		}
		try {
			Writer writer = new FileWriter(motor_path);
			writer.write(sb.toString());
			writer.close();
		} catch (IOException e) {
			MessageLog.printError("MOTOR_UPDATE", "IOException while writing motor data.");
		}
		return;
	}
}
