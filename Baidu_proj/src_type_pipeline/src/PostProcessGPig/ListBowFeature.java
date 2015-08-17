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
 * Prepare listBow features from listsAsGraph.gp
 * 
 * Input line format: s_4_17_part1 <TAB> nonsteroidal anti-inflammatory drugs
 * 
 * Output feature line format: s_4_17_part1 <TAB> listBow=nonsteroidal listBow=x
 * All in lower case
 * 
 */
public class ListBowFeature {

	public static void printHelp() {

		System.out.println("Please give two args: infile and outfile.");

		System.exit(0);

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// process("testData\\SvmListBowFeature_infile",
		// "testData\\SvmListBowFeature_outfile");

		if (args.length != 2) {
			printHelp();
		}
		process(args[0], args[1]);
	}

	public static void process(String infile, String outFile)
			throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(infile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));

		String line = br.readLine();
		String[] tokens = line.split("\\t+");

		String curID = tokens[0];
		String preID = curID;
		String listItems = "";

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
			curID = tokens[0];
			if (!curID.equals(preID)) {
				saveFeats(preID, listItems, bw);
				preID = curID;
				listItems = (tokens[1] + " ");
			} else {
				listItems += (tokens[1] + " ");
			}
			line = br.readLine();
		}
		saveFeats(curID, listItems, bw);
		br.close();
		bw.close();
	}

	public static void saveFeats(String listID, String listItems,
			BufferedWriter bw) throws IOException {
		String[] itemToks = listItems.toLowerCase().split("\\s+");
		StringBuffer sb = new StringBuffer(listID + "\t");
		for (String iTok : itemToks) {
			sb.append(iTok + " ");
		}
		bw.write(sb.toString());
		bw.newLine();

	}

}
