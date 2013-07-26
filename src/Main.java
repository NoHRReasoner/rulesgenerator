import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {


    public static void main(String[] args) throws IOException, OWLOntologyCreationException {


//        RulesGenerator rulesrg = new RulesGenerator(new File("/Users/vadimivanov/Documents/University/tests/ontologies/fly_anatomy.owl"), 50, 10, 0, 20000, 3, 10);
//        rulesrg.getRules("");
//        System.exit(0);

        /*Map<String,String> ontologies = new HashMap<String, String>();
        ontologies.put("snomed1","ontologies/SnomedFunctSyn.owl");
        ontologies.put("snomed2","ontologies/SnomedFunctSyn.owl");
        ontologies.put("snomed3","ontologies/SnomedFunctSyn.owl");
        ontologies.put("snomed4","ontologies/SnomedFunctSyn.owl");
        ontologies.put("snomed5","ontologies/SnomedFunctSyn.owl");
        ontologies.put("snomed6","ontologies/SnomedFunctSyn.owl");
        ontologies.put("snomed7","ontologies/SnomedFunctSyn.owl");
        ontologies.put("snomed8","ontologies/SnomedFunctSyn.owl");
        ontologies.put("snomed9","ontologies/SnomedFunctSyn.owl");
        ontologies.put("snomed10","ontologies/SnomedFunctSyn.owl");

        Dictionary<String, rulesSettings> settings = new Hashtable<String, rulesSettings>();
        settings.put("snomed1", new rulesSettings(20000));
        settings.put("snomed2", new rulesSettings(40000));
        settings.put("snomed3", new rulesSettings(60000));
        settings.put("snomed4", new rulesSettings(80000));
        settings.put("snomed5", new rulesSettings(100000));
        settings.put("snomed6", new rulesSettings(120000));
        settings.put("snomed7", new rulesSettings(140000));
        settings.put("snomed8", new rulesSettings(160000));
        settings.put("snomed9", new rulesSettings(180000));
        settings.put("snomed10", new rulesSettings(200000));

        rulesSettings _ruleSet;
        for(Map.Entry<String,String> ontology: ontologies.entrySet()){
            System.out.println();
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("Ontology:"+ontology.getKey());

            _ruleSet = settings.get(ontology.getKey());
            RulesGenerator rulesGenerator = new RulesGenerator(new File(ontology.getValue()), _ruleSet.r, _ruleSet.b, _ruleSet.f, _ruleSet.i, _ruleSet.a, _ruleSet.n);
            rulesGenerator.getRules("rules/"+ontology.getKey()+".p");
        }
//
        System.exit(0);

                                      */

        String currentDir=new java.io.File(".").getCanonicalPath();
        JFileChooser file = new JFileChooser(currentDir);

        File _f = new File("/Users/vadimivanov/Documents/University/tests/ontologies/fly_anatomy2.owl");
        if(_f.exists())
            file.setSelectedFile(_f);

        File ont = null;

        if(args.length>0){
            String ontologyPath = args[0];
            ont = new File(ontologyPath);

        }
        if(ont == null || !ont.exists()){
            int result = file.showDialog(null, "Choose ontology");
            if(result == JFileChooser.APPROVE_OPTION){
                ont = file.getSelectedFile();
            }else{
                System.out.println("Choose ontology");
                System.exit(0);
            }
        }


        Scanner inp = new Scanner( System.in ); // System.in через сканер
        System.out.println("enter number of requested rules, r = ");
        int r = inp.nextInt();

        System.out.println("enter number of maximum body atoms per rule, b = ");
        int b = inp.nextInt();

        System.out.println("enter number of requested facts, f = ");
        int f = inp.nextInt();

        System.out.println("enter number of different individuals, i = ");
        int i = inp.nextInt();

        System.out.println("enter number of new predicates, n = ");
        int n = inp.nextInt();

        System.out.println("enter maximum arity of these predicates, a = ");
        int a = inp.nextInt();

        RulesGenerator rulesGenerator = new RulesGenerator(ont, r, b, f, i, a, n);
        rulesGenerator.getRules("");
//            file.setSelectedFile(rulesGenerator.getRules());
//            int val = file.showSaveDialog(file.getParent());
//            if(val == )
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
