package webmd.Preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UniqueSentence {

	public static Pattern PTN = Pattern.compile("<[^\\s]+>");
	public static HashMap<Long, String> sentMap = new HashMap<Long, String>();

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length != 3) {
			System.out
					.println("ERROR: please give the inputfile, outputfile, and outFileForSentCode.");
			System.exit(0);
		}
		getUniqueSentence(args[0], args[1], args[2]);
		// Matcher m = PTN.matcher("<aa>");
		// System.out.println(m.find());
	}

	public static void getUniqueSentence(String inputfile, String outputfile,
			String outputSentCode) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(inputfile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputfile));
		BufferedWriter bwC = new BufferedWriter(new FileWriter(outputSentCode));

		String line = br.readLine();

		while (line != null) {
			if (line.trim().length() == 0) {
				line = br.readLine();
				continue;
			}

			Matcher m = PTN.matcher(line);
			if (m.find()) {
				bw.write(line);
				bw.newLine();
			} else {
				long sentCode = getCode(line);
				if (!sentMap.containsKey(sentCode)) {
					sentMap.put(sentCode, line);
				}
				bw.write(sentCode + "");
				bw.newLine();
			}
			line = br.readLine();
		}

		for (Long key : sentMap.keySet()) {
			bwC.write(key + "");
			bwC.newLine();
			bwC.write(sentMap.get(key));
			bwC.newLine();
		}

		bw.close();
		br.close();
		bwC.close();
	}

	public static long getCode(String sent) {
		String sentFirstHalf = sent.substring(0, sent.length() / 2);
		String sentSecondHalf = sent
				.substring(sent.length() / 2, sent.length());

		long sentCode = sentFirstHalf.hashCode();
		sentCode = (sentCode << Integer.SIZE);
		sentCode += sentSecondHalf.hashCode();
		return sentCode;
	}
}
