package webmd.Preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/*
 * Input: 
 * hasItem.cfacts: hasItem <TAB> s_xx_yy <TAB> item
 * inList.cfacts: inList <TAB> item <TAB> s_xx_yy 
 * 
 * Input: listID_SentID_Map.txt, format: s_xx_yy <TAB> unique setence ID (e.g.6801022152812041277)
 * 
 * Input: webmd_export-2015-06-08T19-34-11_sentId_info.txt, 
 * format: 6801022152812041277 <TAB> SIMVASTATIN <TAB> Zocor <TAB> Interactions
 * 
 * Output: 
 * hasItem.cfacts.aug: hasItem <TAB> s_xx_yy <TAB> item@genericName
 * inList.cfacts.aug: inList <TAB> genericName@item <TAB> s_xx_yy 
 * genericName: lower case
 * 
 */
public class DrugAugmentGraph {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length != 6) {
			System.out
					.println("ERROR: please give six parameters: hasItem.cfacts, inList.cfacts, listID_SentID_Map.txt, "
							+ "and sentId_info.txt, for input, hasItem.cfacts.aug and inList.cfacts.aug, for output.");
			System.exit(0);
		}

		HashMap<String, String> listID_SentID_Map = load_sentID_genericName_Map(args[2]);
		HashMap<String, String> sentID_genericName_Map = load_sentID_genericName_Map(args[3]);

		BufferedReader brHasItem = new BufferedReader(new FileReader(args[0]));
		BufferedWriter bwHasItemAug = new BufferedWriter(
				new FileWriter(args[4]));
		String line = null;
		String listID = null;
		String listItem = null;
		String listItemAug = null;
		while ((line = brHasItem.readLine()) != null) {
			listID = line.split("\t")[1];
			listItem = line.split("\t")[2];
			listItemAug = sentID_genericName_Map
					.get(listID_SentID_Map.get(listID)).replaceAll("\\s+", "_")
					.toLowerCase().trim()
					+ "@" + listItem;
			bwHasItemAug.write("hasItem\t" + listID + "\t" + listItemAug);
			bwHasItemAug.newLine();
			bwHasItemAug.flush();
		}
		brHasItem.close();
		bwHasItemAug.close();

		BufferedReader brInList = new BufferedReader(new FileReader(args[1]));
		BufferedWriter bwInListAug = new BufferedWriter(new FileWriter(args[5]));
		while ((line = brInList.readLine()) != null) {
			listID = line.split("\t")[2];
			listItem = line.split("\t")[1];
			listItemAug = sentID_genericName_Map
					.get(listID_SentID_Map.get(listID)).replaceAll("\\s+", "_")
					.toLowerCase().trim()
					+ "@" + listItem;
			bwInListAug.write("inList\t" + listItemAug + "\t" + listID);
			bwInListAug.newLine();

		}
		brInList.close();
		bwInListAug.close();

	}

	public static HashMap<String, String> load_listID_SentID_Map(String file)
			throws IOException {
		HashMap<String, String> retMap = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		while ((line = br.readLine()) != null) {
			retMap.put(line.split("\t")[0], line.split("\t")[1]);
		}
		br.close();
		return retMap;
	}

	public static HashMap<String, String> load_sentID_genericName_Map(
			String file) throws IOException {
		HashMap<String, String> retMap = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		while ((line = br.readLine()) != null) {
			if (!line.split("\t")[1].equals("null"))
				retMap.put(line.split("\t")[0], line.split("\t")[1]);
			else
				retMap.put(line.split("\t")[0], line.split("\t")[2]);
		}
		br.close();
		return retMap;
	}

}
