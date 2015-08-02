package webmd.Preprocess;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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

public class CleanWebmdByGenericName {
	private static HashMap<String, Node> webmdMap = new HashMap<>();
	private static Document webmdDocument = null;
	private static HashSet<Integer> top = new HashSet<>();

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException {
		String oriWebmdXml = args[0];
		String outputCleanXml = args[1];
		String[] tokens=args[2].split(",");
		for(String token : tokens){
			top.add(Integer.valueOf(token));
		}
		loadWebmdFile(oriWebmdXml);
		buildXmlFile(outputCleanXml);
	}

	public static void buildXmlFile(String outXmlFile) throws ParserConfigurationException, TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		DOMSource source = new DOMSource(doc);

		Element root = doc.createElement("items");
		doc.appendChild(root);
		int count=0;
		StreamResult result=null;
		for (Node child : webmdMap.values()) {
			Node node = doc.importNode(child, true);
			root.appendChild(node);
			count++;
			if(top.contains(count)){
				result = new StreamResult(new File(outXmlFile+"/all_clean_"+count+".xml"));
				transformer.transform(source, result);
			}
		}
		result = new StreamResult(new File(outXmlFile+"/all_clean.xml"));
		transformer.transform(source, result);
		System.out.println("output unique generic nodes: "+root.getChildNodes().getLength());
	}

	public static void loadWebmdFile(String inputWebmdFile) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		webmdDocument = db.parse(new File(inputWebmdFile));
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList) xPath.compile("/items/item/generic_name").evaluate(webmdDocument, XPathConstants.NODESET);
		int duplicate = 0;
		for (int i = 0; null != nodeList && i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String genericName = node.getTextContent();
			if (genericName.equals("")) {
				System.out.println("webmd generica name empty");
				Node nameNode = (Node) xPath.compile("../name").evaluate(node, XPathConstants.NODE);
				webmdMap.put(nameNode.getTextContent().replaceAll("\\s+", " ").toLowerCase().trim(), node.getParentNode());
			}
			else {
				String key = genericName.replaceAll("\\s+", " ").toLowerCase().trim();
				Node parentNode = node.getParentNode();
				if (webmdMap.containsKey(key)) {
					Node previous = webmdMap.get(key);
					if (previous.getTextContent().length() < parentNode.getTextContent().length()) {
						webmdMap.put(key, parentNode);
					}
					duplicate++;
				}
				else {
					webmdMap.put(key, parentNode);
				}
			}
		}
		System.out.println("webmd duplicate" + duplicate);
	}

}
