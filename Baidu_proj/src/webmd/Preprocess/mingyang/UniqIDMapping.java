package webmd.Preprocess.mingyang;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class UniqIDMapping {

	public static HashMap<Integer, String> sentCodeMap = new HashMap<>();

	public static void main(String[] args) throws IOException {
		String sentCodeFile = args[0];
		String uniqIDFile = args[1];
		String mappingFile = args[2];
		initialMap(sentCodeFile);
		query(uniqIDFile, mappingFile);

	}

	public static void initialMap(String sentCodeFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(sentCodeFile));
		String line = null;
		Integer lineNumber = 1;
		while ((line = br.readLine()) != null) {
			if (line.startsWith("1\t") && line.endsWith("\tB-NP\tCD\tO\t0\tROOT")) {
				sentCodeMap.put(lineNumber, line.split("\t")[1]);
			}
			lineNumber++;
		}
		br.close();
	}

	public static void query(String uniqIDFile, String mappingFile) throws NumberFormatException, IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(mappingFile));
		BufferedReader br = new BufferedReader(new FileReader(uniqIDFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			Integer index = Integer.valueOf(line.split("_")[1]);
			bw.write(line + "\t" + sentCodeMap.get(index - 2) + "\n");
		}
		bw.flush();
		bw.close();
		br.close();
	}

}
