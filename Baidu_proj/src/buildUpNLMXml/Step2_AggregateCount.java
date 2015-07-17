package buildUpNLMXml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Step2_AggregateCount {

	/*
	 * System in sorted count file
	 */
	public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String readLine = null;
        String previous ="";
        int count=0;
        while ((readLine = br.readLine()) != null) {
                String[] m= readLine.split("\t");
                if(m[0].equals(previous)){
                        count+=Integer.parseInt(m[1]);
                }else{
                        if(!previous.equals("")){
                                System.out.println(previous+"\t"+count);
                        }
                        count=Integer.parseInt(m[1]);
                        previous=m[0];
                }
        }
        System.out.println(previous+"\t"+count);
}

}
