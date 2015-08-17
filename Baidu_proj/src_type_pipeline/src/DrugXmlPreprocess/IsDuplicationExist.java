package DrugXmlPreprocess;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class IsDuplicationExist {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String dir = "C:\\Users\\User\\Desktop\\Baidu_Drug\\data\\";
		scanDir(args[0]);

	}

	public static void scanDir(String dirStr) throws IOException {
		if (!dirStr.endsWith(File.separator))
			dirStr += File.separator;
		File drugDir = new File(dirStr);
		String[] files = drugDir.list();
		HashMap<String, ArrayList<String>> codeMap = new HashMap<String, ArrayList<String>>();
		for (String fileName : files) {
			if (fileName.endsWith(".xml")) {
				doOneFile(dirStr, fileName, codeMap);
			}
		}

		ArrayList<String> codes = new ArrayList(codeMap.keySet());
		for (String code : codes) {

			System.out.println(code + "\t" + codeMap.get(code));
		}
	}

	public static void doOneFile(String dirStr, String fileName,
			HashMap<String, ArrayList<String>> codeMap) throws IOException {
		ArrayList<String> productCodes = getProductCodes(dirStr + fileName);
		for (String code : productCodes) {
			if (codeMap.containsKey(code)) {
				codeMap.get(code).add(fileName);
				System.out
						.println("Find one product code appearing in more files");
				System.out.print(code + " in ");
				System.out.println(codeMap.get(code));
				System.exit(0);

			} else {
				codeMap.put(code, new ArrayList<String>());
				codeMap.get(code).add(fileName);
			}
		}

	}

	public static ArrayList<String> getProductCodes(String fileAbsName)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileAbsName));
		ArrayList<String> codeList = new ArrayList<String>();
		String line = br.readLine();
		String code = null;

		while (line != null) {
			if (line.trim().equals("<manufacturedProduct>")) {
				line = br.readLine();
				if (line.trim().equals("<manufacturedProduct>")) {
					line = br.readLine();
					if (line.trim().startsWith("<code code=")) {
						code = line.substring(line.indexOf("\"") + 1,
								line.indexOf("\"", line.indexOf("\"") + 2));
						codeList.add(code);
					}
				}

			}
			line = br.readLine();

		}
		br.close();
		return codeList;
	}


}
