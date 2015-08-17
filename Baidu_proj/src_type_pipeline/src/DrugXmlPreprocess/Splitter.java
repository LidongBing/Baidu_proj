package DrugXmlPreprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Splitter {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String infileName = "C:\\Users\\User\\Desktop\\Baidu_Drug\\keptFiles.txt";
		int superCnt = 0;
		int subCnt = 0;
		int fileNo = 25;
		int fileId = 0;
		BufferedReader br = new BufferedReader(new FileReader(infileName));
		String line = br.readLine();
		while (line != null) {
			superCnt++;
			line = br.readLine();

		}
		br.close();

		br = new BufferedReader(new FileReader(infileName));

		BufferedWriter bw = new BufferedWriter(
				new FileWriter("C:\\Users\\User\\Desktop\\Baidu_Drug\\batch"
						+ fileId + ".txt"));

		line = br.readLine();
		while (line != null) {

			bw.write(line);
			bw.newLine();

			if (++subCnt == superCnt / fileNo + 1) {
				bw.close();
				fileId++;
				subCnt = 0;
				bw = new BufferedWriter(new FileWriter(
						"C:\\Users\\User\\Desktop\\Baidu_Drug\\batch" + fileId
								+ ".txt"));
			}
			line = br.readLine();
		}
		bw.close();
		br.close();

	}

}
