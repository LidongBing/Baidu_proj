package DrugXmlPreprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Deduplicator {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		HashMap<String, HashMap<Integer, String>> codeTimeFileMap = new HashMap<String, HashMap<Integer, String>>();
		ArrayList<String> xmlFileNames = new ArrayList<String>();
		ArrayList<ArrayList<String>> xmlContainedCodes = new ArrayList<ArrayList<String>>();
		ArrayList<String> keptXmlFiles = new ArrayList<String>();
		ArrayList<String> removedXmlFiles = new ArrayList<String>();
		ArrayList<String> withoutProductCodeFiles = new ArrayList<String>();
		String testDir = "C:\\Users\\User\\Desktop\\Baidu_Drug\\data\\";
		scanDir(args[0], codeTimeFileMap, xmlFileNames, xmlContainedCodes,
				withoutProductCodeFiles);
		removeOldFiles(codeTimeFileMap, xmlFileNames, xmlContainedCodes,
				keptXmlFiles, removedXmlFiles, withoutProductCodeFiles,
				args[1], args[2], args[3]);

	}

	public static void removeOldFiles(
			HashMap<String, HashMap<Integer, String>> codeTimeFileMap,
			ArrayList<String> xmlFileNames,
			ArrayList<ArrayList<String>> xmlContainedCodes,
			ArrayList<String> keptXmlFiles, ArrayList<String> removedXmlFiles,
			ArrayList<String> withoutProductCodeFiles, String outFileKept,
			String outFileRemoved, String fileWithoutProduct)
			throws IOException {
		String fileName = null;
		ArrayList<String> codes = null;
		ArrayList<HashSet<String>> newestFilesList = new ArrayList<HashSet<String>>();
		HashMap<Integer, String> timeFileMap = null;
		Set<Integer> timeSet = null;
		Integer latestTime = 0, tmpTime = 0;
		boolean keepCurrentFile;
		for (int i = 0; i < xmlFileNames.size(); i++) {
			keepCurrentFile = false;
			HashSet<String> newestFiles = new HashSet<String>();
			fileName = xmlFileNames.get(i);
			codes = xmlContainedCodes.get(i); // the drug codes in "fileName"
			for (int j = 0; j < codes.size(); j++) {
				// the Map of effectiveTime and files that have this drug
				timeFileMap = codeTimeFileMap.get(codes.get(j));
				timeSet = timeFileMap.keySet(); // effective time stamps of
												// those files
				Iterator<Integer> iter = timeSet.iterator();
				latestTime = 0;
				while (iter.hasNext()) { // find the latest file
					tmpTime = iter.next();
					if (tmpTime > latestTime) {
						latestTime = tmpTime;
					}
				}
				// if the current file "fileName" contains one drug's latest
				// effective time, keep the file.
				if (timeFileMap.get(latestTime).equals(fileName)) {
					keepCurrentFile = true;
					break;
				} else {
					newestFiles.add(timeFileMap.get(latestTime));
				}
			}
			if (keepCurrentFile == true)
				keptXmlFiles.add(fileName);
			else {
				removedXmlFiles.add(fileName);
				newestFilesList.add(newestFiles);
			}
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(outFileKept));
		for (int i = 0; i < keptXmlFiles.size(); i++) {
			bw.write(keptXmlFiles.get(i));
			bw.newLine();
		}
		bw.close();
		bw = new BufferedWriter(new FileWriter(outFileRemoved));
		for (int i = 0; i < removedXmlFiles.size(); i++) {
			bw.write(removedXmlFiles.get(i) + " " + newestFilesList.get(i).toString());
			bw.newLine();
		}
		bw.close();

		bw = new BufferedWriter(new FileWriter(fileWithoutProduct));
		for (int i = 0; i < withoutProductCodeFiles.size(); i++) {
			bw.write(withoutProductCodeFiles.get(i));
			bw.newLine();
		}
		bw.close();

		System.out.println(keptXmlFiles.size() + " files are kept");
		System.out.println(removedXmlFiles.size()
				+ " files are removed because of effective time");
		System.out.println(withoutProductCodeFiles.size()
				+ " files are removed because of having no product");
	}

	public static void scanDir(String dirStr,
			HashMap<String, HashMap<Integer, String>> codeTimeFileMap,
			ArrayList<String> xmlFileNames,
			ArrayList<ArrayList<String>> xmlContainedCodes,
			ArrayList<String> withoutProductCodeFiles) throws IOException {
		if (!dirStr.endsWith(File.separator))
			dirStr += File.separator;
		File drugDir = new File(dirStr);
		String[] files = drugDir.list();
		int cnt = 0;
		for (int i = 0; i < files.length; i++) {
			if (files[i].endsWith(".xml")) {
				doOneFile(dirStr, files[i], codeTimeFileMap, xmlFileNames,
						xmlContainedCodes, withoutProductCodeFiles);
				if (++cnt % 1000 == 0) {
					System.out.println(cnt + " files are scanned");
				}
			}
		}
		System.out.println(cnt + " files are scanned");

		System.out.println("xmlFileNames.size(): " + xmlFileNames.size());
		System.out.println("xmlContainedCodes.size(): "
				+ xmlContainedCodes.size());
		System.out.println("withoutProductCodeFiles.size(): "
				+ withoutProductCodeFiles.size());

	}

	public static void doOneFile(String dirStr, String fileName,
			HashMap<String, HashMap<Integer, String>> codeMap)
			throws IOException {
		ArrayList<String> productCodes = getProductCodes(dirStr + fileName);
		Integer effTime = getEffectiveTime(dirStr + fileName);
		for (String code : productCodes) {
			if (codeMap.containsKey(code)) {
				codeMap.get(code).put(effTime, fileName);

			} else {
				codeMap.put(code, new HashMap<Integer, String>());
				codeMap.get(code).put(effTime, fileName);
			}
		}

	}

	public static void doOneFile(String dirStr, String fileName,
			HashMap<String, HashMap<Integer, String>> codeTimeFileMap,
			ArrayList<String> xmlFileNames,
			ArrayList<ArrayList<String>> xmlContainedCodes,
			ArrayList<String> withoutProductCodeFiles) throws IOException {
		ArrayList<String> productCodes = getProductCodes(dirStr + fileName);
		Integer effTime = getEffectiveTime(dirStr + fileName);
		if (effTime == 0) {
			effTime = new Integer(
					new SimpleDateFormat("yyyyMMdd").format(new Date()));
			System.out.println(fileName + " does not have effective time");
		}

		for (String code : productCodes) {
			if (codeTimeFileMap.containsKey(code)) {
				codeTimeFileMap.get(code).put(effTime, fileName);

			} else {
				codeTimeFileMap.put(code, new HashMap<Integer, String>());
				codeTimeFileMap.get(code).put(effTime, fileName);
			}
		}
		if (productCodes.size() > 0) {
			xmlFileNames.add(fileName);
			xmlContainedCodes.add(productCodes);
		} else
			withoutProductCodeFiles.add(fileName);
	}

	public static ArrayList<String> getProductCodes(String fileAbsName)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileAbsName));
		ArrayList<String> codeList = new ArrayList<String>();
		String line = br.readLine();
		String code = null;

		while (line != null) {
			if (line.trim().toLowerCase().equals("<manufacturedproduct>")) {
				line = br.readLine();
				if (line.trim().toLowerCase().equals("<manufacturedproduct>")
						|| line.trim().toLowerCase()
								.equals("<manufacturedmedicine>")) {
					line = br.readLine();
					if (line.trim().toLowerCase().startsWith("<code ")) {
						code = line.substring(
								line.indexOf(" code=\"") + " code=\"".length(),
								line.indexOf("\"", line.indexOf(" code=\"")
										+ " code=\"".length() + 2));
						codeList.add(code);
					}
				}

			}
			line = br.readLine();

		}
		br.close();
		return codeList;
	}

	public static Integer getEffectiveTime(String fileAbsName)
			throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(fileAbsName));
		String line = br.readLine();
		String timeStr = null;
		while (line != null) {

			if (line.trim().toLowerCase().startsWith("<effectivetime value=")) {

				timeStr = line.substring(line.indexOf("\"") + 1,
						line.lastIndexOf("\""));
				br.close();
				return new Integer(timeStr);
			}
			if (line.trim().toLowerCase().equals("</author>")) {
				br.close();
				return 0;
			}

			line = br.readLine();
		}
		br.close();
		return 0;
	}

}
