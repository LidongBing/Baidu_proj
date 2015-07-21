package webmd.Preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * Get the subgraph of different sections in a data set, say a subgraph for 
 * side effect
 */
public class SectionGraph {

	public static String[] sections = { "interactions", "side_effects", "uses",
			"overdose", "precautions" };
	public static String currentSection = null;

	public static HashSet<String> secSents = null;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		if (args.length != 4) {
			System.out
					.println("ERROR: please give four parameters: sentId_info.txt, listID_SentID_Map.txt, hasItem.cfacts, inList.cfacts");

			System.out
					.println("\t such as webmd_export-2015-06-08T19-34-11_re_clean_ss_code.xml listID_SentID_Map.txt list-graph/hasItem.cfacts list-graph/inList.cfacts");
			System.exit(0);
		}

		for (String secName : sections) {
			currentSection = secName;
			secSents = new HashSet<String>();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(args[0]));

			Node itemsNode = doc.getFirstChild();
			if (itemsNode == null
					|| !itemsNode.getNodeName().equalsIgnoreCase("items")) {
				System.err.println("ERROR: the root node should be <items>");
				System.exit(0);
			}

			NodeList itemList = itemsNode.getChildNodes();
			for (int i = 0; i < itemList.getLength(); i++) {
				if (itemList.item(i).getNodeName().equalsIgnoreCase("item"))
					processOneItem(itemList.item(i));
			}
			HashMap<String, String> listID_SentID_Map = loadTwoColumnMap(args[1]);

			BufferedReader brHasItem = new BufferedReader(new FileReader(
					args[2]));
			BufferedWriter bwHasItemAug = new BufferedWriter(
					new FileWriter(args[2].replace(".cfacts", "_"
							+ currentSection + ".cfacts")));

			String line = null;
			String listID = null;
			String sentID = null;
			while ((line = brHasItem.readLine()) != null) {
				listID = line.split("\t")[1];
				sentID = listID_SentID_Map.get(listID);
				if (secSents.contains(sentID)) {
					bwHasItemAug.write(line);
					bwHasItemAug.newLine();
				}
			}
			brHasItem.close();
			bwHasItemAug.close();

			BufferedReader brInList = new BufferedReader(
					new FileReader(args[3]));
			BufferedWriter bwInListAug = new BufferedWriter(
					new FileWriter(args[3].replace(".cfacts", "_"
							+ currentSection + ".cfacts")));
			while ((line = brInList.readLine()) != null) {
				listID = line.split("\t")[2];
				sentID = listID_SentID_Map.get(listID);
				if (secSents.contains(sentID)) {
					bwInListAug.write(line);
					bwInListAug.newLine();
				}
			}
			brInList.close();
			bwInListAug.close();

		}

	}

	public static void processOneItem(Node item) {

		if (item.hasChildNodes()) {

			NodeList children = item.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				String sectionName = child.getNodeName();

				if (sectionName.equals(currentSection)) {
					String text = child.getTextContent();
					String[] toks = text.split("\\n");
					for (int j = 0; j < toks.length; j++) {
						if (isInt(toks[j].trim())) {
							secSents.add(toks[j].trim());
						}
					}

				}
			}
		}

	}

	private static boolean isInt(String str) {
		if (str == null || str.length() == 0)
			return false;

		if (!(str.charAt(0) == '-') && !Character.isDigit(str.charAt(0))) {
			return false;
		}
		for (int i = str.length(); --i >= 1;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
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
}
