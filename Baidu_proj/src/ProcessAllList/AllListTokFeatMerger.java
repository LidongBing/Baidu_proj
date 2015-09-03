package ProcessAllList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/*
 * Combine a few feature files of all list. They should have the same number or examples 
 * and the examples should be in the same order in different files
 * 
 * Format: listID <TAB> feat1 feat2
 */
public class AllListTokFeatMerger {
	public static void printHelp() {

		System.out
				.println("Please give the args like this: inputTokFeatureFile1  inputTokFeatureFile2 ... outputfile.");

		System.exit(0);

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args.length < 3) {
			System.out.println("ERROR happened !!");
			printHelp();
		}
		if (args.length > 0 && args[0].equals("-help"))
			printHelp();

		ArrayList<String> inputFiles = new ArrayList<String>();
		for (int i = 0; i < args.length - 1; i++) {
			inputFiles.add(args[i]);
		}

		BufferedWriter bw = new BufferedWriter(new FileWriter(
				args[args.length - 1]));

		ArrayList<BufferedReader> brs = new ArrayList<BufferedReader>();
		for (int i = 0; i < inputFiles.size(); i++) {
			brs.add(new BufferedReader(new FileReader(inputFiles.get(i))));
		}

		String[] lines = new String[brs.size()];
		for (int i = 0; i < brs.size(); i++) {
			lines[i] = brs.get(i).readLine();
		}

		StringBuffer sb;
		while (lines[0] != null) {
			String id = lines[0].split("\\t+")[0];
			sb = new StringBuffer(lines[0]);
			for (int i = 1; i < brs.size(); i++) {
				String[] toks = lines[i].split("\\t+");
				if (!id.equals(toks[0])) {
					System.out
							.println("ERROR: fatal error, the ids in the corresponding lines are not the same");
					System.exit(0);
				}
				if (toks.length == 2)
					sb.append(" " + toks[1]);
			}
			bw.write(sb.toString());
			bw.newLine();

			for (int i = 0; i < brs.size(); i++) {
				lines[i] = brs.get(i).readLine();
			}
		}

		for (int i = 0; i < brs.size(); i++) {
			brs.get(i).close();
		}
		bw.close();

	}

}
