import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {


    public static void main(String[] args) throws IOException, OWLOntologyCreationException {


//        RulesGenerator rulesGenerator = new RulesGenerator(new File("/Users/vadimivanov/Documents/University/tests/ontologies/galen7.owl"), 7000, 10, 7000, 700);
//        rulesGenerator.getRules("galen7.p");


        Map<String,String> ontologies = new HashMap<String, String>();
//        ontologies.put("union","ontologies/union.owl");
        ontologies.put("molecule_role","ontologies/molecule_role.owl");
        ontologies.put("go2","ontologies/go2_1.owl");
        ontologies.put("go1","ontologies/go1.owl");
        ontologies.put("galen8","ontologies/galen8.owl");
        ontologies.put("galen7","ontologies/galen7.owl");
        ontologies.put("galen","ontologies/galen.owl");
        ontologies.put("fma","ontologies/fma.owl");
        ontologies.put("fly_anatomy","ontologies/fly_anatomy.owl");
        ontologies.put("emap","ontologies/emap.owl");
        ontologies.put("chebi","ontologies/chebi.owl");
//        ontologies.put("anatomy","ontologies/anatomy2012EL-obfuscated.owl");
        ontologies.put("snomed","ontologies/SnomedFunctSyn.owl");
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
//        ontologies.put("elgalen","ontologies/EL-GALEN.owl");

        Dictionary<String, rulesSettings> settings = new Hashtable<String, rulesSettings>();
//        settings.put("union", new rulesSettings());
        settings.put("molecule_role", new rulesSettings(5000, 500));
        settings.put("go2", new rulesSettings(35000, 3500));
        settings.put("go1", new rulesSettings(15000, 1500));
        settings.put("galen8", new rulesSettings(80000, 8000));
        settings.put("galen7", new rulesSettings(22000, 2200));
        settings.put("galen", new rulesSettings(20000, 2000));
        settings.put("fma", new rulesSettings(60000, 6000));
        settings.put("fly_anatomy", new rulesSettings(10000, 1000));
        settings.put("emap", new rulesSettings(7000, 700));
        settings.put("chebi", new rulesSettings(35000, 3500));
        settings.put("anatomy", new rulesSettings(20000, 2000));
        settings.put("snomed", new rulesSettings(150000, 15000));
        settings.put("snomed1", new rulesSettings(10000, 1000));
        settings.put("snomed2", new rulesSettings(20000, 2000));
        settings.put("snomed3", new rulesSettings(30000, 3000));
        settings.put("snomed4", new rulesSettings(40000, 4000));
        settings.put("snomed5", new rulesSettings(50000, 5000));
        settings.put("snomed6", new rulesSettings(60000, 6000));
        settings.put("snomed7", new rulesSettings(70000, 7000));
        settings.put("snomed8", new rulesSettings(80000, 8000));
        settings.put("snomed9", new rulesSettings(90000, 9000));
        settings.put("snomed10", new rulesSettings(100000, 10000));
//        settings.put("elgalen", new rulesSettings(70000, 7000));

        rulesSettings _ruleSet;
        for(Map.Entry<String,String> ontology: ontologies.entrySet()){
            System.out.println();
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("Ontology:"+ontology.getKey());

            _ruleSet = settings.get(ontology.getKey());
            RulesGenerator rulesGenerator = new RulesGenerator(new File(ontology.getValue()), _ruleSet.r, _ruleSet.b, _ruleSet.f, _ruleSet.i);
            rulesGenerator.getRules("rules/"+ontology.getKey()+".p");
        }



        /*
        String currentDir=new java.io.File(".").getCanonicalPath();
        JFileChooser file = new JFileChooser(currentDir);
        file.setSelectedFile(new File("/Users/vadimivanov/Downloads/mkn@fct.unl.pt - cities example/cities.owl"));
        int result = file.showDialog(null, "Choose ontology");

        if(result == JFileChooser.APPROVE_OPTION){

            Scanner inp = new Scanner( System.in ); // System.in через сканер
            System.out.println("enter number of requested rules, r = ");
            int r = inp.nextInt();

            System.out.println("enter number of maximum body atoms per rule, b = ");
            int b = inp.nextInt();

            System.out.println("enter number of requested facts, f = ");
            int f = inp.nextInt();

            System.out.println("enter number of different individuals, i = ");
            int i = inp.nextInt();

            RulesGenerator rulesGenerator = new RulesGenerator(file.getSelectedFile(), r, b, f, i);
            rulesGenerator.getRules();
//            file.setSelectedFile(rulesGenerator.getRules());
//            int val = file.showSaveDialog(file.getParent());
//            if(val == )
        }   else{
            System.out.println("choose ontology");
        }
           */
    }

    public static class rulesSettings{
        public rulesSettings(int _f, int _i){

            b = 10;
            r = _f;
            f = _f;
            i = _i;
        }
        public int r;
        public int f;
        public int b;
        public int i;
    }
}
