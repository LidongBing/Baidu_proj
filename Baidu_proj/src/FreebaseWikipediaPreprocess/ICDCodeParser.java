package FreebaseWikipediaPreprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * Parse the file: icd10cm_order_2016.txt
 * Format: 00007 A0100   1 Typhoid fever, unspecified                                   Typhoid fever, unspecified
 */
public class ICDCodeParser {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// String line =
		// "00007 A0100   1 Typhoid fever, unspecified                                   Typhoid fever, unspecified";
		// String ICD_CM_code = line.substring(6, 14).trim();
		// String drugName = line.substring(77).trim();
		// System.out.println(ICD_CM_code);
		// System.out.println(drugName);

		String infile = "C:/Users/User/Desktop/Baidu_Drug/ICD10-CM/ICD10CM_FY2016_code_descriptions/icd10cm_order_2016.txt";
		String outfile = "data/idc_cm_disease.txt";
		BufferedReader br = new BufferedReader(new FileReader(infile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
		String line = null;
		while ((line = br.readLine()) != null) {

			String ICD_CM_code = line.substring(6, 14).trim();
			String name = line.substring(77).trim();
			if (name.indexOf(',') != -1) {
				name = name.substring(0, name.indexOf(','));
			}
			bw.write(name + "\t" + ICD_CM_code);
			bw.newLine();
		}

		br.close();
		bw.close();
	}

}
