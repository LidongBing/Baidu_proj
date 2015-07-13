package freebaseSeedExtraction;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.StringTokenizer;

public class GetEntityFromFact {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String infileName = "data\\factsBy_is-a-Disease-or-medical-condition-.txt";
		int targetedField = 0;
		String outfileName = "data\\instanceOf_Disease_is-a-Disease-or-medical-condition" + ".txt";
		HashSet<String> entities = new HashSet<String>();

		BufferedReader br = new BufferedReader(new FileReader(infileName));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outfileName));

		String line = br.readLine();
		String[] tokens;
		while (line != null) {
			tokens = line.split("\t");
			if (!entities.contains(tokens[targetedField])) {
				bw.write(tokens[targetedField]);
				bw.newLine();
				entities.add(tokens[targetedField]);
			}
			line = br.readLine();
		}

		br.close();
		bw.close();

	}

}
