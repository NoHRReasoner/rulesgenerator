import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {


    public static void main(String[] args) throws IOException, OWLOntologyCreationException {

        String currentDir=new java.io.File(".").getCanonicalPath();
        JFileChooser file = new JFileChooser(currentDir);

        File _f = new File("/Users/vadimivanov/Documents/University/tests/ontologies/cities.owl");
        if(_f.exists())
            file.setSelectedFile(_f);

        File ont = null;
        if(args.length>0){
            String ontologyPath = args[0];
            ont = new File(ontologyPath);
        }
        String resultPath = args.length > 1 && args[1] != null ? args[1] : "result.p";

        if(ont == null || !ont.exists()){
            int result = file.showDialog(null, "Choose ontology");
            if(result == JFileChooser.APPROVE_OPTION){
                ont = file.getSelectedFile();
            }else{
                System.out.println("Choose ontology");
                System.exit(0);
            }
        }

        int r, b, f, i, n, a;

        if (args.length > 7) {
            r = Integer.parseInt(args[2]);
            b = Integer.parseInt(args[3]);
            f = Integer.parseInt(args[4]);
            i = Integer.parseInt(args[5]);
            n = Integer.parseInt(args[6]);
            a = Integer.parseInt(args[7]);
        }
        else {
            Scanner inp = new Scanner( System.in );
            System.out.print("enter number of requested rules, r = ");
            r = inp.nextInt();
            System.out.print("enter number of maximum body atoms per rule, b = ");
            b = inp.nextInt();
            System.out.print("enter number of requested facts, f = ");
            f = inp.nextInt();
            System.out.print("enter number of different individuals, i = ");
            i = inp.nextInt();
            System.out.print("enter number of new predicates, n = ");
            n = inp.nextInt();
            System.out.print("enter maximum arity of these predicates, a = ");
            a = inp.nextInt();

            System.out.print("enter name of the result file, for example 'result': ");
            String path = inp.next();
            if (path != null && !path.isEmpty()) {
                resultPath = path + ".p";
            }
            inp.close();
        }
        RulesGenerator rulesGenerator = new RulesGenerator(ont, r, b, f, i, a, n);
        rulesGenerator.getRules(resultPath);
        System.exit(0);
    }

    public static class rulesSettings{
        public rulesSettings(int axioms){

            b = 10;
            r = (int) (axioms*0.2);
            f = axioms*2;
            i = (int) (axioms*0.05);
            n = (int) (0.1*axioms);
            a = 3;
        }
        public int r;
        public int f;
        public int b;
        public int i;
        public int n;
        public int a;
    }
}
