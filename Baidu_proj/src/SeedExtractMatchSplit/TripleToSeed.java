package SeedExtractMatchSplit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TripleToSeed {
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			String relation = tokens[1].replaceAll(" ", "_").toLowerCase();
			bw.write("seed\t" + relation + "\t" + parse(tokens[0]) + "@" + parse(tokens[2]) + "\n");
		}
		br.close();
		bw.close();
	}

	public static String parse(String input) {
		return input.trim().replaceAll("\\s+", "_").toLowerCase();
	}
}
