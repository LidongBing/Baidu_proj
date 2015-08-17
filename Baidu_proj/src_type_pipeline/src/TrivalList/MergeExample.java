package TrivalList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * merge example files of different classes, remove  the multi-class instances 
 */
public class MergeExample {

	public static void printHelp() {

		System.out
				.println("Please give the args like: subExampleFile1, subExampleFile1, .... and outfile.");

		System.exit(0);

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length < 3) {
			printHelp();
		}

		ArrayList<String> subFiles = new ArrayList<String>();
		for (int i = 0; i < args.length - 1; i++) {
			subFiles.add(args[i]);
		}

		merge(subFiles, args[args.length - 1]);
	}

	public static void merge(ArrayList<String> subFiles, String outfile)
			throws IOException {
		HashMap<String, Integer> freCnt = new HashMap<String, Integer>();
		ArrayList<String> exmpLines = new ArrayList<String>();
		load(subFiles, freCnt, exmpLines);

		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
		for (String line : exmpLines) {

			String[] toks = line.split("\\t");
			if (freCnt.get(toks[0]) == 1) {
				bw.write(line);
				bw.newLine();
			} 
//			else
//				System.out.println(line);
		}
		bw.close();
	}

	public static void load(ArrayList<String> subFiles,
			HashMap<String, Integer> freCnt, ArrayList<String> exmpLines)
			throws IOException {
		for (String subfile : subFiles) {
			BufferedReader br = new BufferedReader(new FileReader(subfile));
			String line = br.readLine();
			while (line != null) {
				String[] toks = line.split("\\t");
				if (toks.length == 2) {
					if (!freCnt.containsKey(toks[0])) {
						freCnt.put(toks[0], 0);
					}
					freCnt.put(toks[0], freCnt.get(toks[0]) + 1);
					exmpLines.add(line);
				}
				line = br.readLine();
			}
			br.close();
		}
	}

}
