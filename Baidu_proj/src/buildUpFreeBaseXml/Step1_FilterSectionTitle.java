package buildUpFreeBaseXml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Step1_FilterSectionTitle {

	/* 
	 * arg[0] inputXmlFile
	 * arg[1] output mappingFile
	 * System out coutFile
	 */
	public static void main(String[] args) throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
		SingleFileRun g = new SingleFileRun();
		g.calculateFrequency(args[0].trim(), args[1]);
		g.print();
	}

}

class SingleFileRun {

	private HashMap<String, Integer> count = new HashMap<String, Integer>();

	public void calculateFrequency(String inputXmlFile, String mappingFile) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(mappingFile, true));
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(new File(inputXmlFile));
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList) xPath.compile("/document/component/structuredBody/component/section/title").evaluate(document, XPathConstants.NODESET);
		for (int i = 0; null != nodeList && i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String oriString = StringUtil.getOriginalTitle(node.getTextContent());
			String name = StringUtil.getParseTitle(oriString);
			if (name != null && !name.equals("")) {
				if (count.containsKey(name)) {
					count.put(name, count.get(name) + 1);
				}
				else {
					bw.write(name + "\t" + oriString + "\n");
					count.put(name, 1);
				}
			}

		}
		bw.flush();
		bw.close();
	}

	public void print() {
		for (Map.Entry<String, Integer> entry : count.entrySet()) {
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}
	}

}
