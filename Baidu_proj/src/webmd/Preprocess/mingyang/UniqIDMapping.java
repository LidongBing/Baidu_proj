package webmd.Preprocess.mingyang;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class UniqIDMapping {
	/*
	 * This class is used for transfer between sentence code and unique code.
	 * 
	 * 1.input file: sentCodeFile format:1 4077396065221053988
	 * 4077396065221053988 B-NP CD O 0 ROOT
	 * 
	 * uniqIDFile format: S_${line_num}_${relative position}
	 * 
	 * 2.outputFile: mappingFile format:S_3_1 4077396065221053988
	 */
	public static HashMap<Integer, String> sentCodeMap = new HashMap<>();

	public static void main(String[] args) throws IOException {

		if (args.length != 3) {
			System.out
					.println("ERROR: please give the inputfile1, uniqIDFile, and outputfile.");
			System.out.println("\t inputfile1: the NLP parsed file");
			System.out
					.println("\t inputfile2: S_${line_num}_${relative position");
			System.out.println("\t outputFfile: S_3_1 4077396065221053988");
			System.exit(0);
		}

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
		String line_1 = br.readLine();
		String line_2 = "";

		while ((line = br.readLine()) != null) {
			if (line.trim().length() == 0 && line_2.trim().length() == 0) {
				sentCodeMap.put(lineNumber, line_1.split("\t")[1]);
			}
			line_2 = line_1;
			line_1 = line;
			lineNumber++;
		}

		// while ((line = br.readLine()) != null) {
		// if (line.startsWith("1\t")
		// && line.endsWith("\tB-NP\tCD\tO\t0\tROOT")) {
		// sentCodeMap.put(lineNumber, line.split("\t")[1]);
		// }
		// lineNumber++;
		// }
		br.close();
	}

	public static void query(String uniqIDFile, String mappingFile)
			throws NumberFormatException, IOException {
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
