package annotation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
 * Give the list of names that cannot be selected.
 */
public class GetAnnoFiles {
	private static HashSet<String> nameSet = null;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		if (args.length != 3) {
			System.out
					.println("ERROR: please give three parameters: nameList, inputXMLfile, and outputDir.");
			System.out
					.println("       nameList: exclude the items of inputXMLfile having the names in this list.");
			System.out
					.println("       inputXMLfile: the big xml file containing all items.");
			System.exit(0);
		}
		nameSet = loadSet(args[0]);
		loadWebmdFile(args[1], args[2]);
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

	public static void loadWebmdFile(String inputXMLfile, String outDir)
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException, Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();

		Document dailymed = db.parse(new File(inputXMLfile));
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList gnList = (NodeList) xPath.compile("/items/item/generic_name")
				.evaluate(dailymed, XPathConstants.NODESET);
		if (gnList.getLength() == 0) {
			gnList = (NodeList) xPath.compile("/items/item/name").evaluate(
					dailymed, XPathConstants.NODESET);
		}

		for (int i = 0; i < gnList.getLength(); i++) {
			Node gn = gnList.item(i);
			String gnStr = gn.getTextContent().toLowerCase();
			if (!nameSet.contains(gnStr)) {
				Node item = gn.getParentNode();

				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(
						"{http://xml.apache.org/xslt}indent-amount", "2");

				try {
					Document newDoc = db.newDocument();

					newDoc.appendChild(newDoc.adoptNode(item.cloneNode(true)));
					DOMSource source = new DOMSource(newDoc);
					StreamResult result = new StreamResult(new File(outDir
							+ "/" + gnStr.replaceAll(" ", "_")+".txt"));
					transformer.transform(source, result);
				} catch (Exception e) {

				}
			}
		}

	}

}
