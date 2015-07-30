package PostprocessSSLSolution;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CombineSolutionAndItemOld {
	// <listId, (relation)\t(score)>
	public static HashMap<String, String> listToRelationMap = new HashMap<>();
	// <ListId, itemsArray>
	public static HashMap<String, ArrayList<String>> hasItemMap = new HashMap<>();
	// <listIde, sentId> one sent includes multiple list
	public static HashMap<String, String> listToSentMap = new HashMap<>();
	// <sentId, drugName> one drug includes multiple sentence
	public static HashMap<String, String> sentToDrugMap = new HashMap<>();
	public static HashSet<String> relations = new HashSet<>();
	public static void main(String[] args) throws IOException {
		if(args.length!=5){
			System.out.println("args wrong\n"
					+ "solutionFile = args[0]\n"
					+ " hasItemFile = args[1]\n"
					+ "listToSentFile = args[2]\n"
					+ "sentInfoFile = args[3]\n"
					+ "outputFile = args[4]");
		}
		String solutionFile = args[0];
		String hasItemFile = args[1];
		String listToSentFile = args[2];
		String sentInfoFile = args[3];
		String outputFile = args[4];
		loadSolutionFile(solutionFile);
		loadItem(hasItemFile);
		loadIDmapping(listToSentFile);
		loadSentIDToDrugName(sentInfoFile);
		startTranslate(outputFile);
	}
	
	public static void startTranslate(String outputFile) throws IOException{
		BufferedWriter bw = new BufferedWriter (new FileWriter(outputFile+"_ALL"));
		HashMap<String,BufferedWriter> bwMap= new HashMap<>();
		for(String relation: relations){
			bwMap.put(relation, new BufferedWriter(new FileWriter(outputFile+"_"+relation)));
			System.out.println(relation);
		}
		BufferedWriter tmpbw=null;
		for(Map.Entry<String,String> solution: listToRelationMap.entrySet()){
			String listId=solution.getKey();
			String sentId=listToSentMap.get(listId);
			String drug=sentToDrugMap.get(sentId);
			ArrayList<String> items = hasItemMap.get(listId);
			String relation=solution.getValue().split("\t")[0];
			tmpbw=bwMap.get(relation);
			for(String item: items){
				String line = drug+"\t"+solution.getValue()+"\t"+item;
				bw.write(line+"\n");
				tmpbw.write(line+"\n");
			}
		}
		bw.close();
		for(BufferedWriter tmp: bwMap.values()){
			tmp.close();
		}
	}

	public static void loadSolutionFile(String solutionFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(solutionFile));
		String relation = null;
		String line = null;
		String listId = null;
		String score = null;
		String[] toks = null;

		while ((line = br.readLine()) != null) {
			if (line.startsWith("# proved")) {
				relation = line.substring(line.indexOf('(') + 1, line.indexOf(','));
				relations.add(relation);			
				}
			else {
				toks = line.split("\t");
				score = toks[1];
				listId = toks[2].substring(toks[2].indexOf(',') + 1, toks[2].indexOf(')'));
				relation = toks[2].substring(toks[2].indexOf('(') + 1, toks[2].indexOf(','));
				listToRelationMap.put(listId, relation+"\t"+score);
			}
		}
		br.close();

	}

	public static void loadItem(String hasItemFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(hasItemFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			String listId = tokens[1];
			String item = tokens[2];
			if (hasItemMap.containsKey(listId)) {
				hasItemMap.get(listId).add(item);
			}
			else {
				ArrayList<String> list = new ArrayList<>();
				list.add(item);
				hasItemMap.put(listId, list);
			}
		}
		br.close();
	}

	public static void loadIDmapping(String listToSentFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(listToSentFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			String listId = tokens[0];
			String sentId = tokens[1];
			listToSentMap.put(listId, sentId);
		}
		br.close();
	}

	public static void loadSentIDToDrugName(String sentInfoFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(sentInfoFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			String sentId = tokens[0];
			String drug = tokens[1];
			if (drug.equals("null")) {
				drug = tokens[2];
			}
			sentToDrugMap.put(sentId, drug);
		}
		br.close();
	}

}
