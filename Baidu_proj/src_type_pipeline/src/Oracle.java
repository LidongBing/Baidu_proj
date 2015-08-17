import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

/*
 * Return every NP as instance of every type, from the file list_bow.tok_feat
 * of NP (trivial-list-pipeline) classification. It will be used to calculated the best recall that can
 * be achieved. 
 */
public class Oracle {
	public static void printHelp() {

		System.out
				.println("Please give three args, listBowFile, outFile, and outFileMerge");

		System.exit(0);
	}

	public static HashMap<String, String> labelMap = new HashMap<String, String>();
	static {
		labelMap.put("disease", "1");
		labelMap.put("drug", "2");
		labelMap.put("ingredient", "3");
		labelMap.put("symptom", "4");
	};

	public static ArrayList<String> labels = new ArrayList<String>();
	static {
		labels.add("1");
		labels.add("2");
		labels.add("3");
		labels.add("4");
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args.length != 3) {
			System.out.println("ERROR happened !!");
			printHelp();
		}

		if (args.length > 0 && args[0].equals("-help"))
			printHelp();
		String listBowFile = args[0];
		String outFile = args[1];
		String outFileMerge = args[2];
		BufferedReader brBow = new BufferedReader(new FileReader(listBowFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));

		String lineBow = brBow.readLine();

		HashSet<String> mergedSet = new HashSet<String>();

		while (lineBow != null) {

			String[] toks = lineBow.split("\\t");
			if (toks.length > 1) {
				for (String label : labels) {

					bw.write(toks[1].trim().replace(" ", "_") + "\t" + label);
					bw.newLine();

					String combItem = toks[1].trim().replace(" ", "_") + "\t"
							+ label;
					mergedSet.add(combItem);
				}
			}
			lineBow = brBow.readLine();
		}
		brBow.close();

		bw.close();

		BufferedWriter bwMergeItemSol = new BufferedWriter(new FileWriter(
				outFileMerge));
		ArrayList<String> entrylist = new ArrayList<String>(mergedSet);

		Collections.sort(entrylist);

		for (String entry : entrylist) {
			bwMergeItemSol.write(entry);
			bwMergeItemSol.newLine();
		}

		bwMergeItemSol.close();
	}
}
