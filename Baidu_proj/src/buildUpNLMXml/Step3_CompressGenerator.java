package buildUpNLMXml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Step3_CompressGenerator {

	/*
	 * arg[0] is inputFile: original (title<tab>count) file which has inlcude all count result
	 * arg[1] is int number: the minimal frequent limitation
	 * arg[2] is outputFile: outputMapping file which has reflect low frequent title to high frequent title
	 * arg[3] is outputFile: countFile has put low frequent title to high frequent title.
	 * arg[4] is inputFile: this mapping file which is original (title<tab>title) file.
	 * 
	 */
	public static void main(String[] args) throws IOException {
		String oriCountFile = args[0];
		int boundary = Integer.parseInt(args[1]);
		String mappingFile = args[2];
		String countFile = args[3];
		CompressGenerator g = new CompressGenerator(oriCountFile, boundary, mappingFile, countFile);
		g.compress(args[4]);
	}

}

class CompressGenerator {
	private HashMap<String, Integer> sum = new HashMap<>();
	private String oriCountFile;
	private int boundary;
	private String mappingFile;
	private String countFile;
	private BufferedWriter bw = null;
	private HashMap<String, String> mappingForOri = new HashMap<String, String>();
	private HashMap<String, String> mappingForNew = new HashMap<String, String>();

	CompressGenerator(String oriCountFile, int boundary, String mappingFile, String countFile) {
		this.oriCountFile = oriCountFile;
		this.boundary = boundary;
		this.mappingFile = mappingFile;
		this.countFile = countFile;
	}

	public void compress(String mFile) throws IOException {
		initialMapping(mFile);

		BufferedReader buf = new BufferedReader(new FileReader(oriCountFile));
		String line = null;
		while ((line = buf.readLine()) != null) {
			parseLine(line);
		}
		buf.close();

		outputMapping();
		outputCount();
	}

	private void outputMapping() throws IOException {
		bw = new BufferedWriter(new FileWriter(mappingFile));
		for (Map.Entry<String, String> entry : mappingForOri.entrySet()) {
			String oldTitle = entry.getValue();
			if (mappingForNew.containsKey(oldTitle)) {
				bw.write(mappingForNew.get(oldTitle) + "\t" + entry.getKey() + "\n");
			}
		}
		bw.flush();
		bw.close();
	}

	private void initialMapping(String mFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(mFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] split = line.split("\t");
			String normalized = split[0];
			String original = split[1];
			mappingForOri.put(original, normalized);
		}
		br.close();
	}

	private void outputCount() throws IOException {
		BufferedWriter buffw = new BufferedWriter(new FileWriter(countFile));
		for (Map.Entry<String, Integer> entry : sum.entrySet()) {
			buffw.write(entry.getKey() + "\t" + entry.getValue() + "\n");
		}
		buffw.flush();
		buffw.close();
	}

	private void parseLine(String line) throws IOException {

		String[] split = line.split("\t");
		String title = split[0];
		Integer count = Integer.valueOf(split[1]);
		if (count >= boundary) {
			sum.put(title, count);
			mappingForNew.put(title, title);
		}
		else {
			String[] spt = title.split("_");
			String testString = spt[0];
			TreeMap<Integer, String> map = new TreeMap<>();
			if (sum.containsKey(testString)) {
				map.put(sum.get(testString), testString);
			}
			for (int i = 1; i < spt.length; i++) {

				testString += "_";
				testString += spt[i];
				if (sum.containsKey(testString)) {
					map.put(sum.get(testString), testString);
				}
			}

			if (!map.isEmpty()) {
				String newTitle = map.lastEntry().getValue();
				mappingForNew.put(title, newTitle);
				sum.put(newTitle, sum.get(newTitle) + count);
			}
		}
	}

}
