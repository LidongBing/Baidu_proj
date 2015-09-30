package ProcessAllList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindBestLabel {

	public static void main(String[] args) throws IOException {
		HashMap<Integer, List<Double>> map = new HashMap<>();

		for (int i = 1; i < args.length; i++) {
			ArrayList<Double> list = new ArrayList<>();
			map.put(i, list);
			BufferedReader br = new BufferedReader(new FileReader(args[i]));
			br.readLine();
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(" ");
				list.add(Double.parseDouble(tokens[1]));
			}
			br.close();
		}
		int size = map.get(1).size();
		BufferedWriter br = new BufferedWriter(new FileWriter(args[0]));
		for (int j = 0; j < size; j++) {
			double max=0;
			int index=-1;
			for (int i = 1; i < args.length; i++) {
				if(map.get(i).get(j)>=max){
					max=map.get(i).get(j);
					if(max>=0.5){
						index=i;
					}
				}
			}
			br.write(index+"\t"+max+"\n");
		}
		br.close();
	}
}
