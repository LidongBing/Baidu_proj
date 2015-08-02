package SeedExtractMatchSplit;

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

public class SeedCfactsSecondStringPairComparsion {
	private static HashSet<String> graphSet = new HashSet<String>();
	private static HashSet<String> seedSet = new HashSet<String>();

	private static HashMap<String, String> genericMapping = new HashMap<>();
	public static final String BLOCKER_PACKAGE = "com.wcohen.ss.expt.";

	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		String seedFile = args[0];
		String cfactsFile = args[1];
		String dataFile=args[2];
		String mappingFile=args[3];
		double boundary= Double.parseDouble(args[4]);
		String outputFile= args[5];
		loadSet(seedFile,seedSet);
		loadSet(cfactsFile,graphSet);
		generateDataFile(dataFile);
		mappingGenericName(mappingFile,dataFile);
		loadMappingResult(mappingFile,boundary);
		output(outputFile,seedFile);
	}
	public static void output(String outputFile, String seedFile) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		BufferedReader br = new BufferedReader(new FileReader(seedFile));
		String line = null;
		int count=0;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			if(genericMapping.containsKey(tokens[2])){
				count++;
				bw.write(line.replace(tokens[2], genericMapping.get(tokens[2]))+"\n");
			}else{
				bw.write(line+"\n");
			}
		}
		System.out.println("total matched: "+count);
		bw.close();
		br.close();
	}
	public static void loadSet(String file, HashSet<String> set) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			set.add(tokens[2]);
		}
		br.close();
	}
	public static void generateDataFile(String dataFile) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(dataFile));
		for (String str : seedSet) {
			bw.write("seed\t1\t" + str + "\n");
		}
		for (String str : graphSet) {
			bw.write("graph\t1\t" + str + "\n");
		}
		bw.close();
	}
	public static void mappingGenericName(String mappingFile, String dataFile) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		Blocker blocker = (Blocker) Class.forName(BLOCKER_PACKAGE + "TokenBlocker").newInstance();
		StringDistanceLearner learner = DistanceLearnerFactory.build("SoftTFIDF");
		MatchData data = new MatchData(dataFile);
		MatchExpt expt = new MatchExpt(data, learner, blocker);
		expt.dumpResults(new PrintStream(new FileOutputStream(mappingFile)));
	}
	public static void loadMappingResult(String mappingFile, double doundary) throws IOException {
		BufferedReader bw = new BufferedReader(new FileReader(mappingFile));
		String line = null;
		while ((line = bw.readLine()) != null) {
			String[] tokens = line.split("\t");
			double score = Double.parseDouble(tokens[0].trim());
			String key = tokens[2];
			String value = tokens[1];
			if (!genericMapping.containsKey(key) && score >= doundary) {
				genericMapping.put(key, value);
			}
		}
		bw.close();
	}
}
