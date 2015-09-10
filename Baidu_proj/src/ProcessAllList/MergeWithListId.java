package ProcessAllList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MergeWithListId {

	//args0:  predict_all_list/all_list_ALL.tok_feat
	//args1: runX_classificationFile
	//args2:labelMap
	//args3: runX_classification_trec_eval
	public static void main(String[] args) throws IOException {
		String allListFile=args[0];
		String classificationFile=args[1];
		String labelMap=args[2];
		BufferedReader br1= new BufferedReader(new FileReader(allListFile));
		BufferedReader br2= new BufferedReader(new FileReader(classificationFile));
		BufferedReader br3= new BufferedReader(new FileReader(labelMap));
		BufferedWriter bw = new BufferedWriter(new FileWriter(args[3]));
		HashMap<String,String> map = new HashMap<>();
		ArrayList<String> idList= new ArrayList<>();
		ArrayList<String> labelList = new ArrayList<>();
		String line ="";
		while((line=br1.readLine())!=null){
			idList.add(line.split("\\s+")[0]);
		}
		br1.close();
		while((line=br2.readLine())!=null){
			labelList.add(line);
		}
		br2.close();
		while((line=br3.readLine())!=null){
			String[] token=line.split("\\s+");
			map.put(token[1], token[0]);
		}
		br3.close();
		for(int i=0; i<idList.size(); i++){
			String[] token=labelList.get(i).split("\\s+");
			String label=token[0];
			String probility=token[1];
			if(!label.equals("-1")){
				bw.write(idList.get(i)+"\t"+label+"\t"+map.get(label)+"\t"+probility+"\n");
			}
		}
		bw.close();
	}
}
