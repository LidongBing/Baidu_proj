package webmd.Preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * merge two corpora
 */
public class RemoveReplicatedSentId {
	private static HashMap<String, String> nlm = new HashMap<>();
	private static HashMap<String, String> webmd = new HashMap<>();
	private static ArrayList<String> nlmKey = new ArrayList<String>();
	private static ArrayList<String> webmdKey = new ArrayList<String>();
	private static ArrayList<String> webmdSentId = new ArrayList<String>();

	public static void main(String[] args) throws IOException {
		String nlmSentDep = args[0];
		String webmdSentDep = args[1];
		String outputFile = args[2];
		String outputSentIdFile = args[3];
		loadDep(nlmSentDep, nlm, nlmKey, "nlm");
		loadDep(webmdSentDep, webmd, webmdKey, "webmd");
		combine(outputFile);
		outputSentId(outputSentIdFile);
	}

	public static void outputSentId(String outputSentIdFile) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputSentIdFile));
		for (String id : webmdSentId) {
			bw.write(id);
			bw.newLine();
		}
		bw.close();
	}

	public static void combine(String outputFile) throws IOException {
		for (String key : webmdKey) {
			if (!nlm.containsKey(key)) {
				nlmKey.add(key);
				nlm.put(key, webmd.get(key));
			}
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		for (String key : nlmKey) {
			bw.write(key + "\n" + nlm.get(key) + "\n");
		}
		bw.close();
	}

	public static void loadDep(String fileName, HashMap<String, String> map,
			ArrayList<String> list, String entity) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = null;
		String buf = "";
		String key = null;
		boolean isCode = true;
		while ((line = br.readLine()) != null) {
			if (line.equals("")) {
				if (isCode) {
					if (entity.equals("webmd")) {
						webmdSentId.add(buf.split("\t")[1]);
					}
					key = buf;
				} else {
					list.add(key);
					map.put(key, buf);
				}
				isCode = !isCode;
				buf = "";

			} else {
				buf += line + "\n";
			}
		}
		br.close();
	}
}
