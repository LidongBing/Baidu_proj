import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

import com.wcohen.ss.DistanceLearnerFactory;
import com.wcohen.ss.api.StringDistanceLearner;
import com.wcohen.ss.expt.Blocker;
import com.wcohen.ss.expt.MatchData;
import com.wcohen.ss.expt.MatchExpt;

public class MergeNmlWebmd {

	public static final String BLOCKER_PACKAGE = "com.wcohen.ss.expt.";
	private static HashMap<String, Node> nlmMap = new HashMap<>();
	private static HashMap<String, Node> webmdMap = new HashMap<>();
	private static HashMap<String, String> genericMapping = new HashMap<>();
	// which has bigger number which is base for xml merging
	private static boolean isWebmdAsBase = true;
	private static Document nlmDocument = null;
	private static Document webmdDocument = null;

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, XPathExpressionException, SAXException, IOException, ParserConfigurationException, TransformerException {
		if (args.length != 6) {
			System.out.println("inputNlmFile=args[0];inputWebmdFile=args[1];outputMappingFile = args[2];outputDataFile = args[3]; boundary=args[4]");
			return;
		}
		String inputNlmFile = args[0];
		String inputWebmdFile = args[1];
		String outputMappingFile = args[2];
		String outputDataFile = args[3];
		double boundary = Double.parseDouble(args[4]);

		loadNLmFile(inputNlmFile);
		loadWebmdFile(inputWebmdFile);
		generateDataFile(outputDataFile);
		mappingGenericName(outputMappingFile, outputDataFile);
		loadMappingResult(outputMappingFile, boundary);
		buildXmlFile(args[5]);
	}

	public static void buildXmlFile(String outputXmlFile) throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Element root = doc.createElement("items");
		doc.appendChild(root);
		if (isWebmdAsBase) {
			for (Map.Entry<String, Node> entry : nlmMap.entrySet()) {
				String key = entry.getKey();
				Node node = entry.getValue();
				if (genericMapping.containsKey(key)) {
					combine(webmdMap.get(genericMapping.get(key)), node, webmdDocument);
				}
				else {
					webmdMap.put(key, node);
				}
			}
			outputXml(webmdDocument, outputXmlFile);
		}
		else {
			for (Map.Entry<String, Node> entry : webmdMap.entrySet()) {
				String key = entry.getKey();
				Node node = entry.getValue();
				if (genericMapping.containsKey(key)) {
					combine(nlmMap.get(genericMapping.get(key)), node, nlmDocument);
				}
				else {
					nlmMap.put(key, node);
				}
			}
			outputXml(nlmDocument, outputXmlFile);
		}
	}

	public static void outputXml(Document doc, String outXmlFile) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(outXmlFile));
		transformer.transform(source, result);
	}

	public static void combine(Node oldNode, Node newNode, Document doc) {
		NodeList nodeList = newNode.getChildNodes();
		for (int i = 0; null != nodeList && i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			if (!(child.getNodeName().equals("name") || child.getNodeName().equals("generic_name"))) {
				Node node = doc.importNode(child, true);
				oldNode.appendChild(node);
			}
		}
	}

	public static void loadMappingResult(String outputMappingFile, double doundary) throws IOException {
		BufferedReader bw = new BufferedReader(new FileReader(outputMappingFile));
		String line = null;
		while ((line = bw.readLine()) != null) {
			String[] tokens = line.split("\t");
			double score = Double.parseDouble(tokens[0].trim());
			String key = tokens[2];
			String value = tokens[1];
			if (!genericMapping.containsKey(key) && score >= doundary) {
				genericMapping.put(key, value);
			}
		}
		bw.close();
	}

	public static void mappingGenericName(String outputMappingFile, String outputDataFile) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		Blocker blocker = (Blocker) Class.forName(BLOCKER_PACKAGE + "TokenBlocker").newInstance();
		StringDistanceLearner learner = DistanceLearnerFactory.build("SoftTFIDF");
		MatchData data = new MatchData(outputDataFile);
		MatchExpt expt = new MatchExpt(data, learner, blocker);
		expt.dumpResults(new PrintStream(new FileOutputStream(outputMappingFile)));
	}

	public static void generateDataFile(String outputDataFile) throws IOException {
		Set<String> nlmSet = nlmMap.keySet();
		Set<String> webmdSet = webmdMap.keySet();
		System.out.println("nlm:" + nlmSet.size());
		System.out.println("webmd:" + webmdSet.size());
		if (nlmSet.size() > webmdSet.size()) {
			isWebmdAsBase = false;
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputDataFile));
		for (String str : nlmSet) {
			bw.write("nlm\t1\t" + str + "\n");
		}
		for (String str : webmdSet) {
			bw.write("webmd\t1\t" + str + "\n");
		}
		bw.close();
	}

	public static void loadNLmFile(String inputNlmFile) throws SAXException, IOException, XPathExpressionException, ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		nlmDocument = db.parse(new File(inputNlmFile));
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList) xPath.compile("/items/item/generic_name").evaluate(nlmDocument, XPathConstants.NODESET);
		int duplicate = 0;
		for (int i = 0; null != nodeList && i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String genericName = node.getTextContent();
			if (genericName.equals("")) {
				System.out.println("nlm generica name empty");
				Node nameNode = (Node) xPath.compile("../name").evaluate(node, XPathConstants.NODE);
				nlmMap.put(nameNode.getTextContent().replaceAll("\\s+", " ").toLowerCase().trim(), node.getParentNode());
			}
			else {
				String key = genericName.replaceAll("\\s+", " ").toLowerCase().trim();
				if (nlmMap.containsKey(key))
					duplicate++;
				nlmMap.put(key, node.getParentNode());
			}
		}
		System.out.println("nlm duplicate" + duplicate);
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
				if (webmdMap.containsKey(key))
					duplicate++;
				webmdMap.put(key, node.getParentNode());
			}
		}
		System.out.println("nlm duplicate" + duplicate);
	}
}
