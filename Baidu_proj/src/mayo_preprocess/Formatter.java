package mayo_preprocess;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * Convert the format from:
 mayoclinic_export.syntax:
 <items>
 ..<item>
 ....<name/>
 ....<sections>
 ......<value>
 ........<name/>
 ........<url/>
 ........<text/>
 ......</value>
 ......<value>...
 ....</sections>
 ..</item>
 ..<item>...
 </items>
 To:
 Export.syntax:
 <items>
 ..<item>
 ....<name/>
 ....<section1>## section1
 ....</section1>
 ....<section2>## section2
 ....</section2>
 ..</item>
 ..<item>...
 </items>
 */
public class Formatter {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// System.out.println("a  b**c".replaceAll("\\*\\*", " "));
		if (args.length != 2) {
			System.out
					.println("ERROR: please give two parameters: inputfile, and outputfile.");
			System.exit(0);
		}

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		Document inputDoc = db.parse(new File(args[0]));
		Document newDoc = db.newDocument();

		Element newRootElement = newDoc.createElement("items");
		newDoc.appendChild(newRootElement);

		Node itemsNode = inputDoc.getFirstChild();
		if (itemsNode == null
				|| !itemsNode.getNodeName().equalsIgnoreCase("items")) {
			System.err.println("ERROR: the root node should be <items>");
			System.exit(0);
		}
		NodeList itemList = itemsNode.getChildNodes();
		for (int i = 0; i < itemList.getLength(); i++) {
			if (itemList.item(i).getNodeName().equalsIgnoreCase("item")) {
				Element newItemElement = newDoc.createElement("item");
				newRootElement.appendChild(newItemElement);

				NodeList itemChildren = itemList.item(i).getChildNodes();
				for (int j = 0; j < itemChildren.getLength(); j++) {
					Node iChild = itemChildren.item(j);
					if (iChild.getNodeName().equalsIgnoreCase("name")) {
						newItemElement.appendChild(newDoc.adoptNode(iChild
								.cloneNode(true)));
					}
					if (iChild.getNodeName().equalsIgnoreCase("sections")) {
						NodeList sectionNodes = iChild.getChildNodes();
						for (int k = 0; k < sectionNodes.getLength(); k++) {
							Node sectionNode = sectionNodes.item(k);
							if (sectionNode.getNodeName().equalsIgnoreCase(
									"value")) {
								String sectionTitle = null;
								String sectionText = null;
								NodeList secChildren = sectionNode
										.getChildNodes();
								for (int l = 0; l < secChildren.getLength(); l++) {
									if (secChildren.item(l).getNodeName()
											.equalsIgnoreCase("name"))
										sectionTitle = secChildren.item(l)
												.getTextContent();
									if (secChildren.item(l).getNodeName()
											.equalsIgnoreCase("text"))
										sectionText = secChildren.item(l)
												.getTextContent();
								}
								// String sectionTitle = sectionNode.get
								if (sectionTitle != null) {
									Element newSection = newDoc
											.createElement(sectionTitle.trim()
													.toLowerCase()
													.replaceAll("\\s+", "_"));
									newSection.appendChild(newDoc
											.createTextNode("## "
													+ sectionTitle));
									sectionText = sectionText.replaceAll("  ",
											" ");
									sectionText = sectionText.replaceAll("\\n",
											" ");
									sectionText = sectionText.replaceAll("  ",
											"\n");
									sectionText = sectionText.replaceAll(
											"\\*\\*", " ** ");
									newSection
											.appendChild(newDoc
													.createTextNode("\n"
															+ sectionText));
									newItemElement.appendChild(newSection);
								}
							}
						}
					}
				}
			}
		}

		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(newDoc);
		StreamResult result = new StreamResult(new File(args[1]));
		transformer.transform(source, result);

	}

}
