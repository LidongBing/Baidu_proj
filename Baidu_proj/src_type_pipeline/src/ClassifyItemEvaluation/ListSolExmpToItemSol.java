package ClassifyItemEvaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/*
 * This is a duplication of ListSolExmpToItemSol.java in PropprSSLItemEvaluation
 * Format the list solution example to item solution.
 * input file1: hasItem <TAB> listID <TAB> item 
 * Input file2: listID <TAB> label [<TAB> list score]
 * Output file1: item <TAB> label <TAB> listID <TAB> list score
 * Output file2: item <TAB> label <TAB> summed list score
 */
public class ListSolExmpToItemSol {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length != 4) {
			System.out
					.println("ERROR: please give four args: hasItemFile, solExmpFile, itemSolFile and mergedItemSolFile");
			System.exit(0);
		}

		HashMap<String, ArrayList<String>> listItemMap = new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<String>> itemListMap = new HashMap<String, ArrayList<String>>();
		loadListItemMap(args[0], listItemMap, itemListMap);

		BufferedWriter bwItemSol = new BufferedWriter(new FileWriter(args[2]));
		BufferedWriter bwMergeItemSol = new BufferedWriter(new FileWriter(
				args[3]));

		HashMap<String, Double> mergedItemSolMap = new HashMap<String, Double>();

		BufferedReader brSol = new BufferedReader(new FileReader(args[1]));
		String line = brSol.readLine();
		while (line != null) {
			String[] toks = line.split("\\t+");
			String listID = toks[0];
			String label = toks[1];

			double score = 0;
			if (toks.length > 2)
				score = Double.parseDouble(toks[2]);

			ArrayList<String> listItems = listItemMap.get(listID);
			if (listItems != null) {
				for (String item : listItems) {
					bwItemSol.write(item + "\t" + label + "\t" + listID + "\t"
							+ score);
					bwItemSol.newLine();

					String combItem = item + "\t" + label;
					if (!mergedItemSolMap.containsKey(combItem)) {
						mergedItemSolMap.put(combItem, 0.0);
					}
					mergedItemSolMap.put(combItem,
							mergedItemSolMap.get(combItem) + score);
				}
			}

			line = brSol.readLine();
		}

		// Set<Entry<String, Double>> entries = mergedItemSolMap.entrySet();
		ArrayList<Entry<String, Double>> entrylist = new ArrayList<Entry<String, Double>>(
				mergedItemSolMap.entrySet());

		Collections.sort(entrylist, new Comparator<Entry<String, Double>>() {
			// descending
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}

		});

		for (Entry<String, Double> entry : entrylist) {
			bwMergeItemSol.write(entry.getKey() + "\t" + entry.getValue());
			bwMergeItemSol.newLine();
		}

		brSol.close();
		bwMergeItemSol.close();
		bwItemSol.close();

	}

	public static void loadListItemMap(String hasItemFile,
			HashMap<String, ArrayList<String>> listItemMap,
			HashMap<String, ArrayList<String>> itemListMap) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(hasItemFile));

		String line = br.readLine();
		while (line != null) {
			String[] toks = line.split("\\t+");
			String list = toks[1];
			String item = toks[2];
			if (!listItemMap.containsKey(list))
				listItemMap.put(list, new ArrayList<String>());
			if (!itemListMap.containsKey(item))
				itemListMap.put(item, new ArrayList<String>());
			listItemMap.get(list).add(item);
			itemListMap.get(item).add(list);

			line = br.readLine();
		}
		br.close();
	}
}
