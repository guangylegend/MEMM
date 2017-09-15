import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class Main {
	public static void main(String[] args) throws IOException{
			
			File trainfile = new File("D:\\WSJ_02-21.pos-chunk");
			File validfile = new File("D:\\WSJ_23.pos");
			String validfileout = "D:\\WSJ_23.chunk";
			String responsefile = "D:\\response.chunk";
			String modelfile = "D:\\WSJ.model";
			
			BufferedReader brtrain = new BufferedReader(new FileReader(trainfile));
	        PrintStream ps=new PrintStream(new FileOutputStream("D:\\WSJ.chunk"));  
	        System.setOut(ps); 
			
			String s = null;
			int index = 0;
			int lastindex = 0;
			Vector<String> token = new Vector<String>();
			Vector<String> pos = new Vector<String>();
			Vector<String> tag = new Vector<String>();
			while((s = brtrain.readLine())!=null){ 
				String tmp[] = s.split("\t");
				if(tmp.length == 3){
					token.add(tmp[0]);
					pos.add(tmp[1]);
					tag.add(tmp[2]);
				}
			}
			
			brtrain.close();
			brtrain = new BufferedReader(new FileReader(trainfile));
			
			while((s = brtrain.readLine())!=null){ 
				String tmp[] = s.split("\t");
				if(tmp.length < 3){
					for(int i=lastindex;i<index;i++){
						String str = token.get(i);
						str += "\t" + "feature1=";
						if(i-2>=lastindex)str += pos.get(i-2);
						if(i-1>=lastindex)str += pos.get(i-1);
						str += pos.get(i);
						str += "\t" + "feature2=";
						if(i-1>=lastindex)str += pos.get(i-1);
						str += pos.get(i);
						if(i+1<index)str += pos.get(i+1);
						str += "\t" + "feature3=";
						str += pos.get(i);
						if(i+1<index)str += pos.get(i+1);
						if(i+2<index)str += pos.get(i+2);
						str += "\t" + "feature4=";
						if(i-1>=lastindex){
							if(i-1>=lastindex)str += pos.get(i-1);
							str += pos.get(i);
							str += tag.get(i-1);
						}
						str += "\t" + "feature5=";
						if(i-1>=lastindex){
							str += pos.get(i);
							if(i+1<index)str += pos.get(i+1);
							str += tag.get(i-1);
						}
						str += "\t" + "feature6=";
						if(i-1>=lastindex){
							str += pos.get(i);
							str += tag.get(i-1);
						}
						str += "\t" + "feature7=";
						if(i-1>=lastindex)str += pos.get(i-1);
						str += pos.get(i);
						str += "\t" + "feature8=";
						if(i-1>=lastindex){
							str += pos.get(i-1);
							str += tag.get(i-1);
						}
						str += "\t" + "feature9=";
						str += token.get(i);
						str += pos.get(i);
						
						
						str += "\t" + tag.get(i);	
						System.out.println(str);
					}
					System.out.println();
					lastindex = index;
				}
				else{
					index++;
				}		
			}
			
			brtrain.close();
			ps.close();
			
			MEtrain.train("D:\\WSJ.chunk", modelfile);
			
			BufferedReader brvalid = new BufferedReader(new FileReader(validfile));
			ps=new PrintStream(new FileOutputStream(validfileout));  
	        System.setOut(ps); 
			
	        s = null;
			index = 0;
			lastindex = 0;
			token = new Vector<String>();
			pos = new Vector<String>();
			while((s = brvalid.readLine())!=null){ 
				String tmp[] = s.split("\t");
				if(tmp.length == 2){
					token.add(tmp[0]);
					pos.add(tmp[1]);
				}
			}
			
			brvalid.close();
			brvalid = new BufferedReader(new FileReader(validfile));
			
			while((s = brvalid.readLine())!=null){ 
				String tmp[] = s.split("\t");
				if(tmp.length < 2){
					for(int i=lastindex;i<index;i++){
						String str = token.get(i);
						str += "\t" + "feature1=";
						if(i-2>=lastindex)str += pos.get(i-2);
						if(i-1>=lastindex)str += pos.get(i-1);
						str += pos.get(i);
						str += "\t" + "feature2=";
						if(i-1>=lastindex)str += pos.get(i-1);
						str += pos.get(i);
						if(i+1<index)str += pos.get(i+1);
						str += "\t" + "feature3=";
						str += pos.get(i);
						if(i+1<index)str += pos.get(i+1);
						if(i+2<index)str += pos.get(i+2);
						str += "\t" + "feature4=";
						if(i-1>=lastindex){
							if(i-1>=lastindex)str += pos.get(i-1);
							str += pos.get(i);
							str += "@@";
						}
						str += "\t" + "feature5=";
						if(i-1>=lastindex){
							str += pos.get(i);
							if(i+1<index)str += pos.get(i+1);
							str += "@@";
						}
						str += "\t" + "feature6=";
						if(i-1>=lastindex){
							str += pos.get(i);
							str += "@@";
						}
						str += "\t" + "feature7=";
						if(i-1>=lastindex)str += pos.get(i-1);
						str += pos.get(i);
						str += "\t" + "feature8=";
						if(i-1>=lastindex){
							str += pos.get(i-1);
							str += "@@";
						}
						str += "\t" + "feature9=";
						str += token.get(i);
						str += pos.get(i);
						
						
						System.out.println(str);
					}
					System.out.println();
					lastindex = index;
				}
				else{
					index++;
				}		
			}
			
			brvalid.close();
			ps.close();
			
			MEtag.tag(validfileout, modelfile, responsefile);
	}
}
