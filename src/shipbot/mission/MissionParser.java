package shipbot.mission;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import shipbot.staticlib.MessageLog;
import shipbot.tasks.Station;

/**
 * Library for parsing a mission file.
 * 
 * @author kat
 *
 */
public class MissionParser {
	
	private static String name = "MISSION_PARSER";
	private String mission_path;
	private final String VALVE_REGEX = "[A-H]V[1-3]";
	private final String BBOX_REGEX = "[A-H][A-B]";
	private final String BSW_REGEX = "B[1-3]";
	
	private List<Station> devices;
	
	public MissionParser(String mission_path) {
		this.mission_path = mission_path;
		this.verifyMissionPath();
		devices = new ArrayList<Station>();
		try {
			Reader reader = new FileReader(mission_path);
			StreamTokenizer t = new StreamTokenizer(reader);
			t.parseNumbers();
			
			boolean eof = false;
			int time_requirement = 0;
			int last_token = -1;
			while (!eof) {
				int token = t.nextToken();
				
				switch (token) {
					case StreamTokenizer.TT_EOF:
						eof = true;
						break;
						
					case StreamTokenizer.TT_WORD:
						String token_str = t.sval;
						if (token_str.matches(VALVE_REGEX)) {
							Station s = getStation(token_str.charAt(0));
							devices.add(s);
						}
						if (token_str.matches(BBOX_REGEX)) {
							Station s = getStation(token_str.charAt(0));
							devices.add(s);
						}
						if (token_str.matches(BSW_REGEX)) {
							// get station of last device (a breaker)
							// create breaker switch device
						}
						break;
						
					case StreamTokenizer.TT_NUMBER:
						double val = t.nval;
						if (last_token == StreamTokenizer.TT_WORD) {
							time_requirement = (int) val;
						} 
						break;
					default:
						break;
						
				}
				last_token = token;
			}
			
		} catch (IOException e) {
			// do stuff
		}
	}
	
	public List<Station> getAllDevices() {
		return devices;
	}

	private void verifyMissionPath() {
		File mission_file = new File(mission_path);
		if (!(mission_file.exists() && mission_file.isFile())) {
			MessageLog.printError(this.toString(), "Mission file does not exist or is not a file.");
			return;
		}
		if (!mission_file.canRead()) {
			MessageLog.printError(this.toString(), "Mission file was not set to readable, attempting to correct.");
			mission_file.setReadable(true);
			return;
		}
	}
	
	private Station getStation (char position) {
		Station station;
		switch (position) {
			case 'A':
				station = Station.A;
				break;
			case 'B':
				station = Station.B;
				break;
			case 'C':
				station = Station.C;
			case 'D':
				station = Station.D;
				break;
			case 'E':
				station = Station.E;
				break;
			case 'F':
				station = Station.F;
				break;
			case 'G':
				station = Station.G;
				break;
			case 'H':
				station = Station.H;
				break;
			default:
				MessageLog.printError(this.name, "Unrecognized character in mission file!");
				station = null;
		}
		return station;
	}
}
