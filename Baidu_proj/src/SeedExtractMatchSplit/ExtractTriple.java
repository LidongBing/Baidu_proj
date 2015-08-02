package SeedExtractMatchSplit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExtractTriple {

	public static void main(String[] args) throws IOException {

		String relativeOutPath = args[2];
		String relation = args[0].replaceAll(" ", "_").toLowerCase().replaceAll("[^a-zA-Z0-9_]", "");
		BufferedWriter bw = new BufferedWriter(new FileWriter(relativeOutPath + relation));
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		while ((line = stdInput.readLine()) != null) {
			String[] tokens = line.split("\t");
			if (tokens.length >= 2 && tokens[1].equals(args[0])) {
				if (args[1].equals("true")) {
					bw.write(parse(tokens[2]) + "\t" + relation + "\t" + parse(tokens[0]) + "\n");
				}
				else {
					bw.write(parse(tokens[0]) + "\t" + relation + "\t" + parse(tokens[2]) + "\n");
				}
			}
		}
		bw.flush();
		bw.close();
	}

	public static String parse(String input) {
		return input.trim().replaceAll("\\s+", "_").toLowerCase();
	}
}