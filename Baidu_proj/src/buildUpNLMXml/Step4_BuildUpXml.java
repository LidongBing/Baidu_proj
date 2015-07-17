package buildUpNLMXml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
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

public class Step4_BuildUpXml {
	/*
	 * args[0] inputXmlFile
	 * args[1] outputXmlFile
	 * args[2] mappingFile for title translate
	 */
	public static void main(String[] args) throws XPathExpressionException, IOException, ParserConfigurationException, SAXException, TransformerException {
		if (!args[0].trim().toLowerCase().endsWith(".xml")) {
			return;
		}
		SingleFileBuildWithSubTitle g = new SingleFileBuildWithSubTitle();
		g.calculateFrequency(args[0].trim(), args[2].trim());
		g.buildUpXml(args[1].trim());
	}
}

class SingleFileBuildWithSubTitle {
	private HashMap<String, ComponentStruct> componentHash = new HashMap<String, ComponentStruct>();
	private Document document = null;
	private String drugName = "";
	private String genericName = "";
	private String inputXmlFile = null;
	private Set<String> ndc = new HashSet<String>();
	private XPath xPath;
	private HashMap<String, String> mapping = new HashMap<String, String>();

	public void calculateFrequency(String inputXmlFile, String mappingFile) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		initialMapping(mappingFile);
		initialVariable(inputXmlFile);
		getName();
		getGenericName();
		getNDCcode();
		getComponents();
	}

	private void initialVariable(String inputXmlFile) throws ParserConfigurationException, IOException, SAXException {
		this.inputXmlFile = inputXmlFile;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		document = db.parse(new File(inputXmlFile));
		xPath = XPathFactory.newInstance().newXPath();
	}

	private void initialMapping(String mappingFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(mappingFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] split = line.split("\t");
			String normalized = split[0];
			String originalWithLow = split[1].toLowerCase();
			mapping.put(originalWithLow, normalized);
		}
		br.close();
	}

	private void getNDCcode() throws XPathExpressionException {

		NodeList nodeList = (NodeList) xPath.compile("//*[name() = 'manufacturedProduct']/manufacturedProduct/code").evaluate(document, XPathConstants.NODESET);
		for (int i = 0; null != nodeList && i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String tmp = node.getAttributes().getNamedItem("code").getNodeValue();
			ndc.add(tmp);
		}
	}

	public String toString() {

		String str = "drugName: " + drugName + "\n" + "genericName: " + genericName + "\n" + ndc + "\n";
		for (Map.Entry<String, ComponentStruct> entry : componentHash.entrySet()) {
			str += "key: " + entry.getKey() + "value: " + entry.getValue() + "\n";
		}
		return str;
	}

	private void getName() throws IOException, XPathExpressionException {
		Node node = (Node) xPath.compile("/document/component/structuredBody/component/section/subject/manufacturedProduct/manufacturedProduct/name").evaluate(document, XPathConstants.NODE);
		if (node == null) {
			node = (Node) xPath.compile("/document/component/structuredBody/component/section/subject/manufacturedProduct/manufacturedMedicine/name").evaluate(document, XPathConstants.NODE);
		}
		drugName = node.getTextContent().trim();
	}

	private void getGenericName() throws IOException, XPathExpressionException {
		Node node = (Node) xPath.compile("//*[name() = 'genericMedicine']").evaluate(document, XPathConstants.NODE);
		genericName = node.getTextContent().trim();
	}

	private void getComponents() throws XPathExpressionException {
		NodeList nodeList = (NodeList) xPath.compile("/document/component/structuredBody/component/section/title").evaluate(document, XPathConstants.NODESET);
		for (int i = 0; null != nodeList && i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String oriString = StringUtil.getOriginalTitle(node.getTextContent());
			String name = StringUtil.getParseTitle(oriString);
			if (name != null && !name.equals("") && mapping.containsKey(oriString.toLowerCase())) {
				Node sectionNode = node.getParentNode();
				Node textNode = (Node) xPath.compile("./text").evaluate(sectionNode, XPathConstants.NODE);
				String text = "";
				if (textNode != null) {
					text = textNode.getTextContent();
					text = StringUtil.trimText(text);
				}
				ComponentStruct componentstruct = new ComponentStruct();
				componentstruct.text = text;
				componentstruct.subTextHash = getSubText(sectionNode);
				componentHash.put(oriString, componentstruct);
			}
		}
	}

	private HashMap<String, String> getSubText(Node sectionNode) throws XPathExpressionException {
		HashMap<String, String> subComponent = new HashMap<>();
		NodeList nodeList = (NodeList) xPath.compile("./component/section").evaluate(sectionNode, XPathConstants.NODESET);

		for (int i = 0; null != nodeList && i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			Node subTitleNode = (Node) xPath.compile("./title").evaluate(node, XPathConstants.NODE);
			if (subTitleNode != null) {
				String subTitle = StringUtil.getOriginalTitle(subTitleNode.getTextContent());
				if (!subTitle.equals("")) {
					String subText = node.getTextContent().replace(subTitleNode.getTextContent(), "").trim();
					subText = StringUtil.trimText(subText);
					subComponent.put(subTitle, subText);
				}

			}
		}
		return subComponent;
	}

	public void buildUpXml(String outXmlFile) throws IOException, ParserConfigurationException, TransformerException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		Element root = doc.createElement("item");
		doc.appendChild(root);

		Element drugNameE = doc.createElement("name");
		drugNameE.appendChild(doc.createTextNode(drugName));
		root.appendChild(drugNameE);

		Element geneNameE = doc.createElement("generic_name");
		geneNameE.appendChild(doc.createTextNode(genericName));
		root.appendChild(geneNameE);

		Element fileNameE = doc.createElement("file_name");
		fileNameE.appendChild(doc.createTextNode(inputXmlFile));
		root.appendChild(fileNameE);

		Element ndcNodes = doc.createElement("ndc_codes");
		for (String str : ndc) {
			Element ndcNode = doc.createElement("ndc_code");
			ndcNode.appendChild(doc.createTextNode(str));
			ndcNodes.appendChild(ndcNode);
		}
		root.appendChild(ndcNodes);

		for (Map.Entry<String, ComponentStruct> entry : componentHash.entrySet()) {
			Element sectionE = doc.createElement(mapping.get(entry.getKey().toLowerCase()));
			sectionE.appendChild(doc.createTextNode("## " + entry.getKey()));
			String text = entry.getValue().text;

			if (!text.equals("")) {
				sectionE.appendChild(doc.createTextNode("\n" + text));
			}
			for (Map.Entry<String, String> subEntry : entry.getValue().subTextHash.entrySet()) {
				Element subSectionE = doc.createElement("subsection");
				subSectionE.appendChild(doc.createTextNode("## " + subEntry.getKey()));
				String subText = subEntry.getValue();
				if (!subText.equals("")) {
					subSectionE.appendChild(doc.createTextNode("\n" + subText));
				}
				sectionE.appendChild(subSectionE);
			}
			root.appendChild(sectionE);

		}
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(outXmlFile));
		transformer.transform(source, result);
	}

	class ComponentStruct {
		String text = "";
		HashMap<String, String> subTextHash = new HashMap<>();
	}

}
