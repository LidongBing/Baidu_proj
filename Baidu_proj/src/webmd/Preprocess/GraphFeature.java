package webmd.Preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/*
 * Generate hasFeature and featureOf files.
 * 
 * Input: bow_context.tok_feat, the file contains the context of the sentence containing a list
 * Format: s_10000_8 <TAB> bowContext=the bowContext=rod 
 * 
 * Input: listID_SentID_Map.txt, 
 * format: s_xx_yy <TAB> unique setence ID (e.g.6801022152812041277)
 * 
 * Input: webmd_export-2015-06-08T19-34-11_sentId_info.txt, 
 * format: 6801022152812041277 <TAB> SIMVASTATIN <TAB> Zocor <TAB> Interactions
 * 
 * Input: 
 * hasItem.cfacts.aug: hasItem <TAB> s_xx_yy <TAB> genericName@item
 * 
 * 
 * output:
 * headFeature.cfacts: hasFeature <TAB> genericName@item <TAB> feature
 * featureOf.cfacts: featureOf <TAB> feature <TAB> genericName@item
 */

public class GraphFeature {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length != 6) {
			System.out
					.println("ERROR: please give six parameters: bow_context.tok_feat, listID_SentID_Map.txt, sentId_info.txt, "
							+ "and hasItem.cfacts.aug, for input,  hasFeature.cfacts and featureOf.cfacts, for output.");
			System.exit(0);
		}

		HashMap<String, String> listID_bowContext_Map = loadTwoColumnMap(args[0]);
		HashMap<String, String> listID_SentID_Map = loadTwoColumnMap(args[1]);
		HashMap<String, String> sentID_sectTitle_Map = loadSecTitleMap(args[2]);

		BufferedReader brHasItem = new BufferedReader(new FileReader(args[3]));
		BufferedWriter bwHasFeature = new BufferedWriter(
				new FileWriter(args[4]));
		BufferedWriter bwFeatureOf = new BufferedWriter(new FileWriter(args[5]));

		String line = null;

		String listID = null;
		String listDrugItem = null;
		String listItem = null;
		String bowContext = null;
		String secTitle = null;
		while ((line = brHasItem.readLine()) != null) {
			listID = line.split("\t")[1];
			listDrugItem = line.split("\t")[2];
			listItem = listDrugItem.substring(listDrugItem.indexOf('@') + 1);

			bowContext = listID_bowContext_Map.get(listID);
			try {
				secTitle = sentID_sectTitle_Map
						.get(listID_SentID_Map.get(listID))
						.replaceAll("\\s+", "_").toLowerCase().trim();
			} catch (Exception e) {
				System.out.println(line);
				continue;
			}
			String tmp = bowContext + " " + secTitle;
			String[] toks = tmp.split("\\s+");
			for (String tok : toks) {
				bwHasFeature.write("hasFeature\t" + listDrugItem + "\t" + tok
						+ "@" + listItem);
				bwHasFeature.newLine();
				bwFeatureOf.write("featureOf\t" + tok + "@" + listItem + "\t"
						+ listDrugItem);
				bwFeatureOf.newLine();
			}
		}
		bwHasFeature.close();
		bwFeatureOf.close();
		brHasItem.close();

	}

	public static HashMap<String, String> loadTwoColumnMap(String file)
			throws IOException {
		HashMap<String, String> retMap = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		while ((line = br.readLine()) != null) {
			if (line.split("\t").length == 2)
				retMap.put(line.split("\t")[0], line.split("\t")[1]);
			if (line.split("\t").length == 1)
				retMap.put(line.split("\t")[0], "");
		}
		br.close();
		return retMap;
	}

	public static HashMap<String, String> loadSecTitleMap(String file)
			throws IOException {
		HashMap<String, String> retMap = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		while ((line = br.readLine()) != null) {
			if (!line.split("\t")[3].equals("null"))
				retMap.put(line.split("\t")[0], line.split("\t")[3]);
			else
				retMap.put(line.split("\t")[0], "");
		}
		br.close();
		return retMap;
	}

}
