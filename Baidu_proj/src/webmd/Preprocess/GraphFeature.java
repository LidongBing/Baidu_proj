package webmd.Preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

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
 * headFeature.cfacts: hasFeature <TAB> genericName@item <TAB> feature@item
 * featureOf.cfacts: featureOf <TAB> feature@item <TAB> genericName@item
 */

public class GraphFeature {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length != 10) {
			System.out
					.println("ERROR: please give eight parameters: bow_context.tok_feat, listID_SentID_Map.txt, sentId_info.txt, "
							+ "hasItem.cfacts.aug, and sentID_list_cleanSet.txt for input,  "
							+ "hasFeature.cfacts, featureOf.cfacts, inSection.cfacts, sectionHas.cfacts, for output, and stopWord file");

			System.out
					.println("       Note that sentID_list_cleanSet.txt is only effective for merged graph. It should be empty file for non-merged graph");
			System.exit(0);
		}

		HashMap<String, String> listID_bowContext_Map = loadTwoColumnMap(args[0]);
		HashMap<String, String> listID_SentID_Map = loadTwoColumnMap(args[1]);
		HashMap<String, StringBuffer> sentID_sectTitle_Map = loadSecTitleMap(args[2]);

		BufferedReader brHasItem = new BufferedReader(new FileReader(args[3]));
		HashSet<String> sentID_from_CleanData = loadSet(args[4]);
		BufferedWriter bwHasFeature = new BufferedWriter(
				new FileWriter(args[5]));
		BufferedWriter bwFeatureOf = new BufferedWriter(new FileWriter(args[6]));

		BufferedWriter bwInSection = new BufferedWriter(new FileWriter(args[7]));
		BufferedWriter bwSectionHas = new BufferedWriter(
				new FileWriter(args[8]));

		HashSet<String> stopword = loadStopWord(args[9]);

		String line = null;

		String listID = null;
		String listDrugItem = null;
		String listItem = null;
		String bowContext = null;
		String secTitle = null;
		String sentID = null;
		HashSet<String> tmpSet = null;
		while ((line = brHasItem.readLine()) != null) {
			listID = line.split("\t")[1];
			listDrugItem = line.split("\t")[2];
			listItem = listDrugItem.substring(listDrugItem.indexOf('@') + 1);
			sentID = listID_SentID_Map.get(listID);
			bowContext = listID_bowContext_Map.get(listID);

			try {
				secTitle = sentID_sectTitle_Map.get(sentID).toString();
			} catch (Exception e) {
				System.out.println(line);
				continue;
			}

			String tmp = null;
			if (!sentID_from_CleanData.contains(sentID)) {
				if (secTitle == null)
					secTitle = "";
				tmp = bowContext + " " + secTitle;
			} else
				tmp = bowContext;
			String[] toks = tmp.split("\\s+");
			tmpSet = new HashSet<String>();
			for (String tok : toks) {
				if (tmpSet.contains(tok))
					continue;
				else
					tmpSet.add(tok);

				if (stopword.contains(tok))
					continue;
				bwHasFeature.write("hasFeature\t" + listDrugItem + "\t" + tok
						+ "@" + listItem);
				bwHasFeature.newLine();
				bwFeatureOf.write("featureOf\t" + tok + "@" + listItem + "\t"
						+ listDrugItem);
				bwFeatureOf.newLine();
			}

			if (sentID_from_CleanData.contains(sentID) && secTitle != null) {
				secTitle = secTitle.toLowerCase().replaceAll("\\s+", "_");
				bwInSection.write("inSection\t" + listDrugItem + "\t"
						+ secTitle + "@" + listItem);
				bwInSection.newLine();
				bwSectionHas.write("sectionHas\t" + secTitle + "@" + listItem
						+ "\t" + listDrugItem);
				bwInSection.newLine();
			}

		}
		bwHasFeature.close();
		bwFeatureOf.close();
		brHasItem.close();
		bwInSection.close();
		bwSectionHas.close();

	}

	public static HashSet<String> loadStopWord(String file) throws IOException {
		HashSet<String> ret = new HashSet<String>();

		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		while ((line = br.readLine()) != null) {
			ret.add(line.trim().toLowerCase());
			ret.add("bowContext=" + line.trim().toLowerCase());
		}
		br.close();
		return ret;
	}

	private static HashSet<String> loadSet(String file) throws IOException {
		HashSet<String> ret = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			ret.add(line.trim());
		}
		br.close();
		return ret;
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

	public static HashMap<String, StringBuffer> loadSecTitleMap(String file)
			throws IOException {
		HashMap<String, StringBuffer> retMap = new HashMap<String, StringBuffer>();
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		String[] toks = null;
		String title = null;
		while ((line = br.readLine()) != null) {
			toks = line.split("\t");

			if (!retMap.containsKey(toks[0]))
				retMap.put(toks[0], new StringBuffer());

			if (!line.split("\t")[3].equals("null"))
				title = line.split("\t")[3];
			else
				title = "";

			retMap.get(toks[0]).append(
					" " + title.trim().replaceAll("\\s+", "_").toLowerCase());

		}
		br.close();
		return retMap;
	}

}
