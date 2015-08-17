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

public class GetDrugCodeName {

	public static void printHelp() {

		System.out.println("Please give two args: infileDir and outfile.");

		System.exit(0);

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length != 2) {
			printHelp();
		}

		scanDir(args[0], args[1]);

	}

	public static void scanDir(String dirStr, String outputFile)
			throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

		if (!dirStr.endsWith(File.separator))
			dirStr += File.separator;
		File drugDir = new File(dirStr);
		String[] files = drugDir.list();
		int cnt = 0;
		for (int i = 0; i < files.length; i++) {
			if (files[i].endsWith(".xml")) {
				ArrayList<String> codeList = new ArrayList<String>();
				ArrayList<String> nameList = new ArrayList<String>();
				getProductCodesNames(dirStr + files[i], codeList, nameList);
				if (codeList.size() != nameList.size()) {

					System.out
							.println("ERROR: codeList.size() != nameList.size()");
					System.exit(0);
				}
				bw.write(files[i]);
				bw.newLine();

				for (int j = 0; j < codeList.size(); j++) {
					bw.write(codeList.get(j) + "\t" + nameList.get(j));
					bw.newLine();
				}
				bw.newLine();
				if (codeList.size() != 0)
					if (++cnt % 1000 == 0) {
						System.out.println(cnt + " files are scanned");
					}
			}
		}

		System.out.println(cnt + " files contain Code and Name");
		bw.close();
	}

	public static void getProductCodesNames(String fileAbsName,
			ArrayList<String> codeList, ArrayList<String> nameList)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileAbsName));
		String line = br.readLine();
		String code = null;
		String name = null;

		while (line != null) {
			if (line.trim().toLowerCase().equals("<manufacturedproduct>")) {
				line = br.readLine();
				if (line.trim().toLowerCase().equals("<manufacturedproduct>")
						|| line.trim().toLowerCase()
								.equals("<manufacturedmedicine>")) {
					line = br.readLine();
					if (line.trim().toLowerCase().startsWith("<code ")) {
						String nameline = br.readLine();
						if (nameline.trim().toLowerCase().startsWith("<name>")) {
							while (!nameline.trim().toLowerCase()
									.contains("</name>")) {
								nameline += br.readLine();
							}
							try {
								code = line.substring(
										line.indexOf(" code=\"")
												+ " code=\"".length(),
										line.indexOf("\"",
												line.indexOf(" code=\"")
														+ " code=\"".length()
														+ 2));
								codeList.add(code);
								name = nameline.substring(
										nameline.indexOf('>') + 1,
										nameline.indexOf("<",
												nameline.indexOf('>')));
								nameList.add(name);

								// System.out.println(code +"\t" +name);
							} catch (Exception e) {
								System.out
										.println("EXCEPTION for line: "
												+ nameline + " in File: "
												+ fileAbsName);
								codeList.clear();
								nameList.clear();

							}
						}
					}
				}

			}
			line = br.readLine();

		}
		br.close();
	}
}
