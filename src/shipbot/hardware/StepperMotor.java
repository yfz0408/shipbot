package shipbot.hardware;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StepperMotor extends Motor {
	
	// Data Fields
	public static String POS = "position";
	
	private String id;
	private Map<String, Integer> data;
	
	public StepperMotor(String id) {
		this.id = id;
		data = new HashMap<String,Integer>();
		data.put(StepperMotor.POS, 0);
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
		// TODO field validation?
		data.put(field, value);
		this.sendUpdate();
	}

	@Override
	public Set<String> getFields() {
		return data.keySet();
	}
}