package SeedExtractMatchSplit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExtractRelationSeed {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// String factPath =
		// "/remote/curtis/baidu/mingyanl/freebase/freebase-easy-14-04-14/facts.txt";
		String relativeOutPath = args[2];
		// System.out.println(args[0]+"..."+args[1]);
		String relation = args[0].replaceAll(" ", "_").toLowerCase().replaceAll("[^a-zA-Z0-9_]", "");
		BufferedWriter bw = new BufferedWriter(new FileWriter(relativeOutPath + relation));
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		while ((line = stdInput.readLine()) != null) {
			String[] tokens = line.split("\t");
			if (tokens.length >= 2 && tokens[1].equals(args[0])) {
				// System.out.println(line);
				if (args[1].equals("true")) {
//					System.out.println("reverse order " + args[0]);
					bw.write("seed\t" + relation + "\t" + parse(tokens[2]) + "@" + parse(tokens[0]) + "\n");
				}
				else {
//					System.out.println("in order " + args[0]);
					bw.write("seed\t" + relation + "\t" + parse(tokens[0]) + "@" + parse(tokens[2]) + "\n");
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