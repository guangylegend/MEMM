import java.io.*;
import java.util.Collections;
import java.util.Vector;
import java.util.regex.Matcher;
import opennlp.maxent.*;
import opennlp.maxent.io.*;

// reads line with tab separated features
//  writes feature[0] (token) and predicted tag

public class MEtag {

    public static void tag (String dataFileName, String modelFileName, String responseFileName) {
		try {
		    GISModel m = (GISModel) new SuffixSensitiveGISModelReader(new File(modelFileName)).getModel();
		    BufferedReader dataReader = new BufferedReader (new FileReader (dataFileName));
		    PrintWriter responseWriter = new PrintWriter (new FileWriter (responseFileName));
		    String priorTag = "#";
		    String line;
		    Vector<String> token = new Vector<String>();
		    Vector<Vector<Integer>> backtrack = new Vector<Vector<Integer>>();
		    Vector<Vector<Double>> prob = new Vector<Vector<Double>>();
		    while ((line = dataReader.readLine()) != null) {
				if (line.equals("")) {
					if(token.size() > 0){
						Vector<String> output = new Vector<String>();
						int curback = 0;
						for(int i=prob.size()-1;i>=1;i--){
							if(i==prob.size()-1){
								double maxp = 0.0;
								String s = "";
								for(int j=0;j<prob.get(i).size();j++){
									if(prob.get(i).get(j) > maxp){
										s = token.get(i) + "\t" + m.getOutcome(j);
										curback = backtrack.get(i-1).get(j);
										maxp = prob.get(i).get(j);
									}
								}
								output.add(s);
							}
							else{
								String s = "";
								//System.out.println(backtrack.size());
								s = token.get(i) + "\t" + m.getOutcome(curback);
								output.add(s);
								curback = backtrack.get(i-1).get(curback);
							}
						}
						String s = "";
						s = token.get(0) + "\t" + m.getOutcome(curback);
						output.add(s);
						Collections.reverse(output);
						for(int i=0;i<output.size();i++)responseWriter.println(output.get(i));
					}
				    responseWriter.println();
				    priorTag = "#";
				    token = new Vector<String>();
				    backtrack = new Vector<Vector<Integer>>();
				    prob = new Vector<Vector<Double>>();
				} else {
					if(prob.size() == 0){
						Vector<Double> tmp = new Vector<Double>();
						line = line.replaceAll("@@", Matcher.quoteReplacement(priorTag));
						String[] features = line.split("\t");
						double[] outcome = m.eval(features);
						for(int i=0;i<outcome.length;i++)tmp.add(outcome[i]);
						token.add(features[0]);
						prob.add(tmp);
					}
					else{
						String name = "";
						Vector<Double> tmp = new Vector<Double>();
						Vector<Integer> back = new Vector<Integer>();
						for(int i=0;i<3;i++)tmp.add(0.0);
						for(int i=0;i<3;i++)back.add(0);
						for(int i=0;i<prob.lastElement().size();i++){
							double curprob = prob.lastElement().get(i);
							String tag = m.getOutcome(i);
							String tmpline = line.replaceAll("@@", Matcher.quoteReplacement(tag));
							String[] features = tmpline.split("\t");
							name = features[0];
							double[] outcome = m.eval(features);
							for(int j=0;j<outcome.length;j++){
								if(outcome[j]*curprob > tmp.get(j)){
									tmp.set(j, outcome[j]*curprob);
									back.set(j, i);
								}
							}
						}
						backtrack.add(back);
						token.add(name);
						prob.add(tmp);
					}
				}
		    }
		    responseWriter.close();
		} catch (Exception e) {
		    System.out.print("Error in data tagging: ");
		    e.printStackTrace();
		}
    }

}
