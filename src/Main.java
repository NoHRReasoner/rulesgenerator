import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, OWLOntologyCreationException {

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


    }
}
