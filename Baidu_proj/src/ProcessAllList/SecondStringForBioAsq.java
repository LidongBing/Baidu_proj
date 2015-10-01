package ProcessAllList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import com.wcohen.ss.DistanceLearnerFactory;
import com.wcohen.ss.api.StringDistanceLearner;
import com.wcohen.ss.expt.Blocker;
import com.wcohen.ss.expt.MatchData;
import com.wcohen.ss.expt.MatchExpt;

public class SecondStringForBioAsq {
	public static final String BLOCKER_PACKAGE = "com.wcohen.ss.expt.";
	public static HashMap<String, String> map = new HashMap<>();

	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		String dataFile = args[0];
		String tripleFile = args[1];
		String mappingFile = args[2];
		String newTripleFile = args[3];
		mappingGenericName(mappingFile, dataFile);
		loadMappingResult(mappingFile, 0.8);
		loadTripleFile(tripleFile, newTripleFile);
	}

	public static void mappingGenericName(String mappingFile, String dataFile) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		Blocker blocker = (Blocker) Class.forName(BLOCKER_PACKAGE + "TokenBlocker").newInstance();
		StringDistanceLearner learner = DistanceLearnerFactory.build("SoftTFIDF");
		MatchData data = new MatchData(dataFile);
		MatchExpt expt = new MatchExpt(data, learner, blocker);
		expt.dumpResults(new PrintStream(new FileOutputStream(mappingFile)));
	}

	public static void loadMappingResult(String mappingFile, double boundary) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(mappingFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			double score = Double.parseDouble(tokens[0].trim());
			String key = tokens[1];
			String value = tokens[2];
			if (score < boundary)
				break;
			if (!map.containsKey(key)) {
				map.put(key, value);
			}
		}
		br.close();
	}

	public static void loadTripleFile(String tripleFile, String outputFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(tripleFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] token = line.split("\t");
			String key = token[1] + "@" + token[2];
			if (map.containsKey(key)) {
				String[] pair = map.get(key).split("@");
				bw.write(token[0] + "\t" + pair[0] + "\t" + pair[1]+"\n");
			}
			else {
				bw.write(line+"\n");
			}
		}
		bw.close();
		br.close();
	}
}
