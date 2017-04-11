package shipbot.hardware;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DriveMotor extends Motor {
	
	// Data Fields
	public static String X = "x";
	public static String Y = "y";
	
	private String id;
	private Map<String, Integer> data;

	public DriveMotor(String id) {
		this.id = id;
		data = new HashMap<String,Integer>();
		data.put(DriveMotor.X, 0);
		data.put(DriveMotor.Y, 0);
	}

	@Override
	protected void recieveUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void sendUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int get(String field) {
		this.recieveUpdate();
		return data.get(field);
	}

	@Override
	public void set(String field, int value) {
		data.put(field, value);
		this.sendUpdate();
	}

	@Override
	public Set<String> getFields() {
		return data.keySet();
	}

}