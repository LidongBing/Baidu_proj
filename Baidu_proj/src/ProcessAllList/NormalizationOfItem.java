package ProcessAllList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;

import com.wcohen.ss.DistanceLearnerFactory;
import com.wcohen.ss.api.StringDistanceLearner;
import com.wcohen.ss.expt.Blocker;
import com.wcohen.ss.expt.MatchData;
import com.wcohen.ss.expt.MatchExpt;

public class NormalizationOfItem {

	public static final String BLOCKER_PACKAGE = "com.wcohen.ss.expt.";

	// arg0: runX_pred_file_trec_eval
	// arg1: disease_anno_trec_eval
	// arg2: dataFile
	// arg3: mappingFile
	// arg4: matchedFile
	// arg5: boundary
	static HashSet<String> predication = new HashSet<>();
	static HashSet<String> evalution = new HashSet<>();
	static HashSet<String> preItem = new HashSet<>();
	static HashSet<String> evalItem = new HashSet<>();
	static HashMap<String, String> preToEval = new HashMap<>();

	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		String pre_eval = args[0];
		String marked_eval = args[1];
		String dataFile = args[2];
		String mappingFile = args[3];
		String matchedFile = args[4];
		double boundary = Double.parseDouble(args[5]);
		loadFile(pre_eval, predication, preItem);
		loadFile(marked_eval, evalution, evalItem);
		generateDataFile(dataFile);
		mappingGenericName(mappingFile, dataFile);
		loadMappingResult(mappingFile, boundary, preToEval);
		matchedResult(matchedFile);
	}

	public static void matchedResult(String outputFile) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		HashMap<String, String> map = new HashMap<String, String>();
		for (String string : predication) {
			String item = string.split("\t")[2];
			if (preToEval.containsKey(item)) {
				string = string.replace("\t" + item + "\t", "\t" + preToEval.get(item) + "\t");
			}
			String[] token = string.split("\t");
			String key = token[0] + "@" + token[2];
			if (map.containsKey(key)) {
				double d1 = Double.parseDouble(map.get(key).split("\t")[4]);
				double d2 = Double.parseDouble(string.split("\t")[4]);
				if (d2 > d1)
					map.put(key, string);
			}
			else {
				map.put(key, string);
			}
		}
		for(String string : map.values()){
			bw.write(string + "\n");
		}
		bw.close();
	}

	public static void loadMappingResult(String mappingFile, double boundary, HashMap<String, String> map) throws IOException {
		BufferedReader bw = new BufferedReader(new FileReader(mappingFile));
		String line = null;
		while ((line = bw.readLine()) != null) {
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
		bw.close();
	}

	public static void loadFile(String file, HashSet<String> list, HashSet<String> set) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			list.add(line);
			set.add(line.split("\t")[2]);
		}
		br.close();
	}

	public static void mappingGenericName(String mappingFile, String dataFile) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		Blocker blocker = (Blocker) Class.forName(BLOCKER_PACKAGE + "TokenBlocker").newInstance();
		StringDistanceLearner learner = DistanceLearnerFactory.build("SoftTFIDF");
		MatchData data = new MatchData(dataFile);
		MatchExpt expt = new MatchExpt(data, learner, blocker);
		expt.dumpResults(new PrintStream(new FileOutputStream(mappingFile)));
	}

	public static void generateDataFile(String data) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(data));
		for (String str : preItem) {
			bw.write("pre\t1\t" + str + "\n");
		}
		for (String str : evalItem) {
			bw.write("eval\t1\t" + str + "\n");
		}
		bw.close();
	}

}
