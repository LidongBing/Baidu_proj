package PostProcessGPig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/*
 * Prepare Proppr Graph from listsAsGraph.gp
 * 
 * Input line format: s_4_17_part1 <TAB> nonsteroidal anti-inflammatory drugs
 * 
 * Output feature line format: hasItem <TAB> listID <TAB> item
 * All in lower case
 * 
 */
public class PropprGraph {

	public static void printHelp() {

		System.out
				.println("Please give three args: infile, outFileHasItem, and outFileInList.");

		System.exit(0);

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// process("testData\\SvmListBowFeature_infile",
		// "testData\\SvmListBowFeature_outfile");

		if (args.length != 3) {
			printHelp();
		}
		process(args[0], args[1], args[2]);
	}

	public static void process(String infile, String outFileHasItem,
			String outFileInList) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(infile));
		BufferedWriter bwHI = new BufferedWriter(new FileWriter(outFileHasItem));
		BufferedWriter bwIL = new BufferedWriter(new FileWriter(outFileInList));

		String line = br.readLine();
		String[] tokens;

		while (line != null) {
			if (line.trim().length() == 0) {
				line = br.readLine();
				continue;
			}
			tokens = line.split("\\t+");
			if (tokens.length < 2) {
				line = br.readLine();
				continue;
			}

			bwHI.write("hasItem\t" + tokens[0] + "\t"
					+ tokens[1].replaceAll(" ", "_"));
			bwHI.newLine();

			bwIL.write("inList\t" + tokens[1].replaceAll(" ", "_") + "\t"
					+ tokens[0]);
			bwIL.newLine();

			line = br.readLine();

		}
		br.close();
		bwHI.close();
		bwIL.close();
	}

}
