package webmd.Preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
 * Get the sentence ID to section title mapping
 * Format: 12312334413 <TAB> section title
 */
public class SentIDSecMapping {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new File(
				"testData/000853a2-c598-4bc6-9aa8-61c3afa66173.xml"));
//		System.out.println(doc.getChildNodes().item(0).getTextContent()
//				.split("\\n").length);
		System.out.println(doc.getElementsByTagName("clinical_studies").item(0)
				.getTextContent());
		System.out.println("## aaa".replace("## ", ""));

		Node itemsNode = doc.getFirstChild();
		if (itemsNode == null || !itemsNode.getNodeName().equals("items")) {
			System.err.println("ERROR: the root node should be <items>");
			System.exit(0);
		}

		BufferedWriter bw = new BufferedWriter(new FileWriter("testData/out"));
		NodeList itemList = itemsNode.getChildNodes();
		for (int i = 0; i < itemList.getLength(); i++) {
			if (itemList.item(i).getNodeName().equals("item"))
				processOneItem(bw, itemList.item(i));
		}
		bw.close();

		System.out.println(isInt("222-233"));

	}

	public static void processOneItem(BufferedWriter bw, Node item)
			throws IOException {
		if (item.hasChildNodes()) {
			String drugName = getContentByTagName(item, "name");
			String genricDrugName = getContentByTagName(item, "generic_name");

			NodeList children = item.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);

				String sectionTitle = null;
				String text = child.getTextContent();
				String[] toks = text.split("\\n");
				if (toks.length != 0 && toks[0].trim().startsWith("## "))
					sectionTitle = toks[0].trim().replace("## ", "");
				for (int j = 1; j < toks.length; j++) {
					if (isInt(toks[j].trim())) {
						bw.write(toks[j].trim() + "\t" + drugName + "\t"
								+ genricDrugName + "\t" + sectionTitle);
						bw.newLine();
					}
				}
			}
		}
	}

	private static boolean isInt(String str) {
		if(str==null || str.length()==0)
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

	public static String getContentByTagName(Node item, String tagName) {
		if (item.hasChildNodes()) {
			NodeList children = item.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child.getNodeName().equalsIgnoreCase(tagName))
					return child.getTextContent().replaceAll("\\s+", " ");
			}
		}
		return null;
	}

}
