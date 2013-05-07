import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: vadimivanov
 * Date: 5/7/13
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class RulesGenerator {

    /** The _ontology. */
    private OWLOntology _ontology;

    private OWLOntologyManager _ontologyManager;

    private OWLAnnotationProperty _ontologyLabel;

    private int numberOfRequstedRules;
    private int numberOfMaxBodyAtomesPerRule;
    private int numberOfRequestFacts;
    private int numberOfDifferentIndividuals;

    private HashSet<String> rules = new HashSet<String>();
    private HashSet<String> classess = new HashSet<String>();
    private HashSet<String> properties = new HashSet<String>();
    private ArrayList<String> listOfClasses;
    private ArrayList<String> listOfProperties;

    private int numberOfClasses;
    private int numberOfProperties;

    private Random random = new Random();

    private String _delimeter="#";
    private String _altDelimeter=":";

    private int min = 1;
    private int iterations=3;

    public RulesGenerator(File ontology, int r, int b, int f, int i) throws OWLOntologyCreationException {

        _ontologyManager=OWLManager.createOWLOntologyManager();
        _ontology=_ontologyManager.loadOntologyFromOntologyDocument(ontology);
        _ontologyLabel = _ontologyManager.getOWLDataFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

        numberOfDifferentIndividuals = i > 0 ? i :1;
        numberOfRequstedRules = r > 0 ? r : 1;
        numberOfMaxBodyAtomesPerRule = b > 2 ? b : 2;
        numberOfRequestFacts = f > 0 ? f : 1;

        collectAllClassesAndRules();
        createFacts();
        createRules();

    }

    private void collectAllClassesAndRules(){
        for (OWLClass owlClass: _ontology.getClassesInSignature()){
            classess.add(getName(owlClass));
        }
        numberOfClasses = classess.size();
        listOfClasses = new ArrayList<String>(classess);
        for (OWLObjectProperty objectProperty: _ontology.getObjectPropertiesInSignature()){
            properties.add(getName(objectProperty));
        }
        numberOfProperties = properties.size();
        listOfProperties = new ArrayList<String>(properties);
    }


    private void createFacts(){
        String rule="";
        for(int f = 1; f<=numberOfRequestFacts; f++){
            if(choseClassOrProperty()==PredicateType.CLASS){
                rule = getRandomClass() + "(a"+randomNumber(numberOfDifferentIndividuals)+").";
            }else{
                rule = getRandomProperty() + "(a"+randomNumber(numberOfDifferentIndividuals)+", a"+randomNumber(numberOfDifferentIndividuals)+").";
            }
            rules.add(rule);
        }
    }

    private void createRules(){
        int b1 = randomNumber(numberOfMaxBodyAtomesPerRule);
        int b2 = randomNumber(b1);
        int v = 1, u = 1;
        int n1, n2;
        String rule;
        for(int j=0; j<numberOfRequstedRules; j++){
            if(choseClassOrProperty()==PredicateType.CLASS){
                rule = getRandomClass()+"(X1) :- ";
                if(choseClassOrProperty()==PredicateType.CLASS){
                    rule += getRandomClass() + "(X1)";
                }else{
                    rule += getRandomProperty() + "(X1, X2)";
                    v=2;
                }
            }else{
                rule = getRandomProperty()+ "(X1, X2) :- ";
                if(b2==1){
                    rule += getRandomProperty()+"(X1, X2)";
                    v=2;
                }else{
                    if(choseClassOrProperty()==PredicateType.CLASS){
                        rule += getRandomClass()+"(X1), "+getRandomClass()+"(X2)";
                        v=2;
                        u=2;
                    }else{
                        rule += getRandomProperty()+"(X1, X2)";
                        v=2;
                    }
                }
            }

            if(u<b2){
                for(int i=1; i<b2-u;i++){
                    if(choseClassOrProperty()==PredicateType.CLASS){
                        n1=randomNumber(v+1);
                        rule +=", "+getRandomClass()+"(X"+n1+")";
                        if(n1 == (v+1))
                            v++;
                    }else{
                        n1 = randomNumber(v);
                        n2 = randomNotEqNumber(v+1,n1);
                        rule +=", "+getRandomProperty()+"(X"+n1+", X"+n2+")";
                        if(n2==(v+1))
                            v++;
                    }
                }
            }
            if(b2<b1){
                for(int i=1; i< (b1-b2);i++){
                    if(choseClassOrProperty()==PredicateType.CLASS){
                        rule +=", not "+getRandomClass()+"(X"+randomNumber(v)+")";
                    }else{
                        rule +=", not "+getRandomProperty()+"(X"+randomNumber(v)+", X"+randomNumber(v)+")";
                    }
                }
            }
            rules.add(rule+".");
        }
    }


    public File getRules() throws IOException {

        FileWriter writer = new FileWriter("result.p");

        for(String str: rules) {
            writer.write(str+"\n");
        }
        writer.close();

        return new File("result.p");
    }



    private int randomNumber(int max){
        return min + (int)(Math.random() * ((max - min) + min));
    }
    private int randomNotEqNumber(int max, int n){
        int j=0, number = 1;
        while (j<iterations && number==n){
            number = randomNumber(max);
            j++;
        }
        return number;
    }

    private String getRandomClass(){

        return listOfClasses.get(randomNumber(numberOfClasses-1));
    }
    private String getRandomProperty(){

        return listOfProperties.get(randomNumber(numberOfProperties-1));
    }


    private String getName(Set<OWLAnnotation> annotations, String owl){
        String name = "";
        if(annotations!=null && annotations.size()>0)   {
            for (OWLAnnotation annotation : annotations) {
                name += annotation.getValue();
            }
        }else{
            name = getRuleFromString(owl);
        }
        return name;
    }
    private String getName(OWLClass _class){
        return getName(_class.getAnnotations(_ontology, _ontologyLabel), _class.toString());

    }
    private String getName(OWLObjectProperty objectProperty){
        return getName(objectProperty.getAnnotations(_ontology, _ontologyLabel), objectProperty.toString());
    }
    private String getRuleFromString(String rule){
        String result="";
        int numInList = 1;
        if(rule.contains(_delimeter))
            result=(rule.split(_delimeter)[numInList]).split(">")[0];
        else if (rule.contains(_altDelimeter)) {
            result=(rule.split(_altDelimeter)[numInList]).split(">")[0];
        }
        else
            result="";

        return result;
    }

    private PredicateType choseClassOrProperty(){
        int i = randomNumber(10);
        if(i<=5)
            return PredicateType.CLASS;
        else
            return PredicateType.PROPERTY;
    }
    public enum PredicateType {
        PROPERTY,
        CLASS
    }
}
