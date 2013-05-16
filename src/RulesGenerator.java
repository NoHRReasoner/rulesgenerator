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
    /** The _ontology ID. */
    private static String _ontologyID;
    private OWLOntologyManager _ontologyManager;

    private OWLAnnotationProperty _ontologyLabel;

    private int numberOfRequstedRules;
    private int numberOfMaxBodyAtomesPerRule;
    private int numberOfRequestFacts;
    private int numberOfDifferentIndividuals;

//    private HashSet<String> rules = new HashSet<String>();
//    private HashSet<String> factsClasses = new HashSet<String>();
    private ArrayList<String> factsClasses;
    private ArrayList<String> factsProperties;
    private ArrayList<String> rules;
    private HashSet<String> classess = new HashSet<String>();
    private HashSet<String> properties = new HashSet<String>();
    private ArrayList<String> listOfClasses;
    private ArrayList<String> listOfProperties;

    private int numberOfClasses;
    private int numberOfProperties;

    private int maxChooser;
    private int minChooser;
    private PredicateType maxChooserType;
    private PredicateType minChooserType;

    private Random random = new Random();

    private String _delimeter="#";
    private String _altDelimeter=":";

    private int min = 1;
    private int iterations=5;

    public RulesGenerator(File ontology, int r, int b, int f, int i) throws OWLOntologyCreationException {

        _ontologyManager=OWLManager.createOWLOntologyManager();
        _ontology=_ontologyManager.loadOntologyFromOntologyDocument(ontology);

        String _ = _ontology.getOntologyID().getOntologyIRI().toString();

        _ontologyID = _.contains("/") ? _.substring(0, _.lastIndexOf("/")) + "/" : "";
        _ontologyLabel = _ontologyManager.getOWLDataFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

        numberOfDifferentIndividuals = i > 0 ? i :1;
        numberOfRequstedRules = r;
        numberOfMaxBodyAtomesPerRule = b > 2 ? b : 2;
        numberOfRequestFacts = f;

        factsClasses = new ArrayList<String>(numberOfRequestFacts+1);
        factsProperties = new ArrayList<String>(numberOfRequestFacts+1);
        rules = new ArrayList<String>(numberOfRequstedRules+1);
        collectAllClassesAndRules();
        createFacts();
        createRules();

    }

    private void collectAllClassesAndRules(){
        for (OWLClass owlClass: _ontology.getClassesInSignature()){
            if(!(owlClass.isOWLThing() || owlClass.isOWLNothing()))
                classess.add(getName(owlClass));
        }
        numberOfClasses = classess.size();
        listOfClasses = new ArrayList<String>(classess);
        for (OWLObjectProperty objectProperty: _ontology.getObjectPropertiesInSignature()){
            properties.add(getName(objectProperty));
        }
        numberOfProperties = properties.size();
        listOfProperties = new ArrayList<String>(properties);


        maxChooser = numberOfClasses + numberOfProperties;

        if(numberOfClasses<numberOfProperties){
            minChooser = numberOfClasses;
            minChooserType = PredicateType.CLASS;
            maxChooserType = PredicateType.PROPERTY;
        }else{
            minChooser = numberOfProperties;
            minChooserType = PredicateType.PROPERTY;
            maxChooserType = PredicateType.CLASS;
        }
    }


    private void createFacts(){
        String rule="";
        for(int f = 1; f<=numberOfRequestFacts; f++){
            if(choseClassOrProperty()==PredicateType.CLASS){
                rule = getRandomClass() + "(a"+randomNumber(numberOfDifferentIndividuals)+").";
                factsClasses.add(rule);
            }else{
                rule = getRandomProperty() + "(a"+randomNumber(numberOfDifferentIndividuals)+", a"+randomNumber(numberOfDifferentIndividuals)+").";
                factsProperties.add(rule);
            }

        }
    }

    private void createRules(){
        int b1,b2;
        int v, u;
        int n1, n2;
        String rule;
        for(int j=0; j<numberOfRequstedRules; j++){
            v = 1;
            u = 1;
            b1 = randomNumber(numberOfMaxBodyAtomesPerRule);
            b2 = randomNumber(b1);
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
                for(int i=1; i < (b1-b2);i++){
                    if(v==1 || choseClassOrProperty()==PredicateType.CLASS){
                        rule +=", not "+getRandomClass()+"(X"+randomNumber(v)+")";
                    }else{
                        n1 = randomNumber(v);
                        n2 = randomNotEqNumber(v,n1);
                        rule +=", not "+getRandomProperty()+"(X"+n1+", X"+n2+")";
                    }
                }
            }
            rules.add(rule+".");
        }
    }


    public File getRules(String resultPath) throws IOException {

        if(resultPath==null || resultPath.length()==0)
            resultPath = "result.p";

        FileWriter writer = new FileWriter(resultPath);

        for(String str: factsClasses) {
            writer.write(str+"\n");
        }

        for(String str: factsProperties) {
            writer.write(str+"\n");
        }
        for(String str: rules) {
            writer.write(str+"\n");
        }
        writer.close();

        return new File(resultPath);
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

        return listOfClasses.get(randomNumber(numberOfClasses)-1);
    }
    private String getRandomProperty(){

        return listOfProperties.get(randomNumber(numberOfProperties)-1);
    }


    private String getName(Set<OWLAnnotation> annotations, String owl){
        String name = "";
        if(annotations!=null && annotations.size()>0)   {
            for (OWLAnnotation annotation : annotations) {
                name += annotation.getValue();
            }
            if(name.length()>0){
                name = "\""+name.replace("^^xsd:string","").replace(",","").replace(":-","").replace("'","").replace("\"","")+"\"";
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
        int numInList = 1;
        rule = rule.replace(_ontologyID,"");
        try{
            String result;
            if(rule.contains(_delimeter))
                result=(rule.split(_delimeter)[numInList]).split(">")[0];
            else if (rule.contains(_altDelimeter))
                result=(rule.split(_altDelimeter)[numInList]).split(">")[0];
            else if(rule.startsWith("<"))
                result = rule.replaceFirst("<","").replace(">","");
            else
                result="";

            return result;
        }catch (Exception e){
            System.out.println("------------------------------------------------------------------------");
            System.out.println(rule);
            System.out.println(Integer.toString(numInList));
            System.out.println("------------------------------------------------------------------------");
            System.out.println(e.toString());
        }
        return rule;
    }

    private PredicateType choseClassOrProperty(){
        int i = randomNumber(maxChooser);
        if(i<=minChooser)
            return minChooserType;
        else
            return maxChooserType;
    }
    public enum PredicateType {
        PROPERTY,
        CLASS
    }
}
