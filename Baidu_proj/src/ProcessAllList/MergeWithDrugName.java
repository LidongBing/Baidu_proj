package ProcessAllList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class MergeWithDrugName {
	// args0: runX_classification_trec_eval
	// args1: all_listId_sentId_map.txt
	// args2: all_sentId_info.txte
	// args3: hasItem.txt
	// args4: runX_pred_file_trec_eval
	// args5: run# 
	static HashMap<String, String> classification_Map = null;
	static HashMap<String, String> listID_SentID_Map = null;
	static HashMap<String, HashSet<String>> sentID_genericName_Map = null;
	static HashMap<String, HashSet<String>> listId_Item_Map = null;
	static HashMap<String, HashSet<String>> listId_Drugs = null;
	static String runId=null;

	public static void main(String[] args) throws IOException {
		runId=args[5];
		classification_Map = loadClassification(args[0]);
		listID_SentID_Map = load_listID_SentID_Map(args[1]);
		sentID_genericName_Map = load_sentID_genericName_Map(args[2]);
		listId_Item_Map = loadHashItem(args[3]);
		//System.out.println(sentID_genericName_Map);
		listId_Drugs = listIdToDrugName();
		CombineWithItem(args[4]);
	}
	public static void CombineWithItem(String outputFile) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		for(Map.Entry<String,HashSet<String>> entry : listId_Drugs.entrySet()){
			String listId=entry.getKey();
			HashSet<String> lines = entry.getValue();
			HashSet<String> items= listId_Item_Map.get(listId);
			for(String item : items){
				for(String line : lines){
					String[] tokens=line.split("\t");
					String docName=tokens[0];
					String labelString=tokens[2];
					String probability=tokens[3];
					bw.write(docName+"_"+labelString+"\tQ0\t"+item+"\t0\t"+probability+"\t"+runId+"\n");
				}
			}
		}
		bw.close();
	}
	public static HashMap<String,HashSet<String>> listIdToDrugName(){
		HashMap<String, HashSet<String>> retMap = new HashMap<String, HashSet<String>>();
		for(Map.Entry<String,String> entry : classification_Map.entrySet()){
			String listId=entry.getKey();
			String line=entry.getValue();
			retMap.put(listId, new HashSet<String>());
			HashSet<String> tmpSet = sentID_genericName_Map.get(listID_SentID_Map.get(listId));
			Iterator<String> iterDrugName = tmpSet.iterator();
			while (iterDrugName.hasNext()) {
				String durgName = iterDrugName.next();
				retMap.get(listId).add(line.replace(listId, durgName));
			}
		}
		return retMap;
	}

	public static HashMap<String, String> loadClassification(String file) throws IOException {
		HashMap<String, String> rst = new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			String listId = line.split("\t")[0];
			rst.put(listId, line);
		}
		br.close();
		return rst;
	}

	public static HashMap<String, HashSet<String>> loadHashItem(String hasItemFile) throws IOException {
		HashMap<String, HashSet<String>> retMap = new HashMap<String, HashSet<String>>();
		BufferedReader br = new BufferedReader(new FileReader(hasItemFile));

		String line = null;
		while ((line = br.readLine()) != null) {
			String listID = line.split("\t")[1];
			String listItem = line.split("\t")[2];
			if (!retMap.containsKey(listID))
				retMap.put(listID, new HashSet<String>());
			retMap.get(listID).add(listItem);
		}
		br.close();
		return retMap;

	}

	public static HashMap<String, String> load_listID_SentID_Map(String file) throws IOException {
		HashMap<String, String> retMap = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		while ((line = br.readLine()) != null) {
			retMap.put(line.split("\t")[0], line.split("\t")[1]);
		}
		br.close();
		return retMap;
	}

	public static HashMap<String, HashSet<String>> load_sentID_genericName_Map(String file) throws IOException {
		HashMap<String, HashSet<String>> retMap = new HashMap<String, HashSet<String>>();
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		String[] toks = null;
		String name = null;
		while ((line = br.readLine()) != null) {
			toks = line.split("\t");
			if (!retMap.containsKey(toks[0]))
				retMap.put(toks[0], new HashSet<String>());

			if (!toks[1].equals("null"))
				name = toks[1];
			else
				name = toks[2];

			retMap.get(toks[0]).add(name.trim().replaceAll("\\s+", "_").toLowerCase());
		}
		br.close();
		return retMap;
	}

}
