package FreebaseWikipediaPreprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/*
 * Get Wiki article from the extracted file of WikiExtractor.py
 */
public class GetWikiArticles {
	public static HashSet<String> keptArticleNames = null;
	static long cnt_all = 0;
	static long cnt_disease = 0;

	public static void main(String[] args) throws IOException {
		// System.out.println("a (disease)".replaceAll("\\(.*\\)", ""));

		// System.out.println("b</aaa asdfa>c".replaceAll("<.*>", ""));
		// System.out.println("b & c".replaceAll("&", "and"));
		if (args.length != 3) {
			System.out.println("ERROR: please give the diseaseListFile, wikipediaArticleFile, and outfile.");
			System.exit(0);
		}
		keptArticleNames = loadDiseaseNames(args[0]);
		getArticles(args[1], args[2]);

		// String namefile = "testData/test_idc_cm_disease.txt";
		// String articlefile = "testData/wiki_00";
		// String outfile = "testData/out";
		// keptArticleNames = loadArticleNames(namefile);
		// getArticles(articlefile, outfile);
	}

	public static void getArticles(String infile, String outfile) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(infile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
		bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		bw.newLine();
		bw.write("<items>");
		bw.newLine();
		while (true) {
			ArrayList<String> artLines = getOneArticle(br);
			if (artLines == null)
				break;
			saveOneArticle(bw, artLines);
		}

		bw.write("</items>");
		bw.newLine();
		bw.close();
		br.close();
		System.out.println("all wikipedia articles: " + cnt_all);
		System.out.println("disease articles: " + cnt_disease);
	}

	public static void saveOneArticle(BufferedWriter bw, ArrayList<String> artLines) throws IOException {
		if (artLines == null || artLines.size() == 0)
			return;
		String artName = artLines.get(1);
		if (!keptArticleNames.contains(artName.trim().toLowerCase()))
			return;

		cnt_disease++;
		if (cnt_disease % 100 == 0)
			System.out.println("got disease articles: " + cnt_disease);

		String line0 = artLines.get(0);
		String url = line0.substring(line0.indexOf("http://"), line0.indexOf("\" title="));

		bw.write("<item>");
		bw.newLine();

		bw.write("<name>" + artName + "</name>");
		bw.newLine();

		bw.write("<url>" + url + "</url>");
		bw.newLine();

		String prevSection = "Abstract";
		bw.write("<"+prevSection.toLowerCase()+">## " + prevSection);
		bw.newLine();
		String line;
		for (int i = 2; i < artLines.size(); i++) {
			line = artLines.get(i);

			if (line.startsWith(".. ") && line.endsWith(" ..")) {
				if (prevSection != null) {
					bw.write("</"+prevSection.replaceAll(" ", "_").toLowerCase()+">");
					bw.newLine();
				}
				prevSection = line.replaceAll("\\.\\.", "").replaceAll("<.*>", "").replaceAll("&", "and").replaceAll("[^a-zA-Z]+", " ").replaceAll("\\s+", " ").trim();
				bw.write("<"+prevSection.replaceAll(" ", "_").toLowerCase()+">");
				bw.write("## " + prevSection);
				bw.newLine();
			}
			else if (!line.trim().equals("</doc>") && line.trim().length() > 0) {
				bw.write(line.replaceAll("<.*>", "").replaceAll("&", "and"));
				bw.newLine();
			}
		}
		if (prevSection != null) {
			bw.write("</"+prevSection.replaceAll(" ", "_").toLowerCase()+">");
			bw.newLine();
		}
		bw.write("</item>");
		bw.newLine();

	}

	public static ArrayList<String> getOneArticle(BufferedReader br) throws IOException {
		ArrayList<String> ret = new ArrayList<String>();

		String line = null;
		while ((line = br.readLine()) != null) {
			if (line.startsWith("<doc id=")) {
				ret.add(line.trim());
				break;
			}
		}
		while ((line = br.readLine()) != null) {
			ret.add(line.trim());
			if (line.equals("</doc>"))
				break;
		}
		if (line == null || !line.equals("</doc>"))
			return null;
		cnt_all++;
		return ret;
	}

	public static HashSet<String> loadDiseaseNames(String file) throws IOException {
		HashSet<String> ret = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			ret.add(line.split("\\t")[0].trim().toLowerCase() + " (disease)");
			ret.add(line.split("\\t")[0].trim().toLowerCase());
		}
		br.close();
		return ret;
	}

}
