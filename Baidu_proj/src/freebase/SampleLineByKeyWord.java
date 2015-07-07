package freebase;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SampleLineByKeyWord {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String infileName = "C:\\Users\\User\\Desktop\\freebase-easy-14-04-14\\facts.txt";
		// String keyWord = "is-a\tDrug ingredient\t";
		// String keyWord = "Active ingredient\t";
		// String keyWord = "is-a\tSymptom\t";
		// String keyWord = "Symptom of\t";
		// String keyWord = "is-a\tDrug\t";
		// String keyWord = "is-a\tDisease or medical condition\t";
		String keyWord = "Used To Treat";
		// String keyWord = "disease";
		// String keyWord = "Side effect of\t";
		// String keyWord = "side-effect\t";
		// String keyWord = "sideeffect\t";
		String outfileName = "data\\factsBy_"
				+ keyWord.replaceAll(" ", "-").replaceAll("\t", "-") + ".txt";

		BufferedReader br = new BufferedReader(new FileReader(infileName));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outfileName));

		int cnt = 0;
		long maxLineNo = Long.MAX_VALUE;

		keyWord = keyWord.toLowerCase();

		String line = br.readLine();

		System.out.println("getting lines for " + keyWord);
		while (line != null && cnt < maxLineNo) {
			if (line.toLowerCase().contains(keyWord)) {
				cnt++;
				bw.write(line);
				bw.newLine();

				if (cnt % 1000 == 0) {
					System.out.println("get " + cnt + " lines");
				}
			}

			line = br.readLine();
		}

		br.close();
		bw.close();
	}
}
