package webmd.Preprocess;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SplitBySection {
	private static Document webmdDocument = null;

	public static void main(String args[]) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException, TransformerException {
		String allXml = args[0];
		String relativePath= args[1];
		String[] sectionList= args[2].split(",");
		loadWebmdFile(allXml);
		buildXmlFile(relativePath,sectionList);
	}

	public static void loadWebmdFile(String inputWebmdFile) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		webmdDocument = db.parse(new File(inputWebmdFile));
	}

	public static void buildXmlFile(String relativePath,String[] sectionList) throws ParserConfigurationException, TransformerException, XPathExpressionException {
		HashSet<String> validSection = new HashSet<String>(Arrays.asList("name", "url", "generic_name"));
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		XPath xPath = XPathFactory.newInstance().newXPath();
		for (String section : sectionList) {
			validSection.add(section);
			Document doc = docBuilder.newDocument();
			DOMSource source = new DOMSource(doc);
			Element root = doc.createElement("items");
			doc.appendChild(root);

			NodeList itemList = (NodeList) xPath.compile("/items/item").evaluate(webmdDocument, XPathConstants.NODESET);
			for (int i = 0; null != itemList && i < itemList.getLength(); i++) {
				Element itemElement = doc.createElement("item");
				root.appendChild(itemElement);
				Node itemNode = itemList.item(i);
				NodeList sectList = (NodeList) xPath.compile(".//*[name() = 'name' or name() = 'url' or name() = 'generic_name'  or name() = '"+section+"']").evaluate(itemNode, XPathConstants.NODESET);

				if (sectList.getLength()==0 || sectList==null) System.out.println("wrong");
				for (int j = 0; null != sectList && j < sectList.getLength(); j++) {
					Node sectNode = sectList.item(j);
					if (validSection.contains(sectNode.getNodeName())) {
						Node child = doc.importNode(sectNode, true);
						itemElement.appendChild(child);
					}
				}
			}
			StreamResult result = new StreamResult(new File(relativePath + "/" + section + "/step1_xml_parse/all.xml"));
			transformer.transform(source, result);
			validSection.remove(section);
		}
	}
}