	package webmd.Preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Clean the passage: Upset stomach, nausea, vomiting, gas, or [diarrhea](http://www.webmd.com
 /digestive-disorders/diarrhea-10/slideshow-foods-to-avoid) may occur.
 * to: Upset stomach, nausea, vomiting, gas, or diarrhea may occur.
 * 
 * Save the Anchor and URL pair.
 */
public class ParaCleaner {

	public static String REG_EXP = "+query(.*?)";

	public static Pattern PTN = Pattern.compile(REG_EXP);

	public static HashSet<String> anchorURLMap = new HashSet<String>();

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args.length != 3) {
			System.out
					.println("ERROR: please give the inputfile, outputfile, and outFileForAnchorURL.");
			System.exit(0);
		}
		processFile(args[0], args[1], args[2]);
		// String test = "r [diarrhea](httap:/) s [diarrhea](http:/)";
		// System.out.println(cleanURL(test));
		// System.out.println(anchorURLMap);

	}

	public static void processFile(String infile, String outfile,
			String outfilePair) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(infile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
		BufferedWriter bwP = new BufferedWriter(new FileWriter(outfilePair));

		String line = br.readLine();

		while (line != null) {
			bw.write(cleanURL(line));
			bw.newLine();
			line = br.readLine();
		}
		ArrayList<String> tmpList = new ArrayList<String>();
		tmpList.addAll(anchorURLMap);
		Collections.sort(tmpList);
		for (String item : tmpList) {
			bwP.write(item);
			bwP.newLine();
		}

		bw.close();
		br.close();
		bwP.close();
	}

	public static String cleanURL(String para) {
		Matcher m = PTN.matcher(para);
		StringBuffer sb = new StringBuffer();
		if (!m.find())
			return para;
		else {
			m.reset();
			while (m.find()) {
				m.appendReplacement(sb, m.group(1));
				anchorURLMap.add(m.group(1) + "\t" + m.group(2));
			}
			return sb.toString();
		}
	}
}
