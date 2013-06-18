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

    private Labels labels;

    private int numberOfRequstedRules;
    private int numberOfMaxBodyAtomesPerRule;
    private int numberOfRequestFacts;
    private int numberOfDifferentIndividuals;
    private int numberOfNewPredicates;
    private int numberOfMaxArityOfNewPredicates;

//    private HashSet<String> rules = new HashSet<String>();
//    private HashSet<String> factsClasses = new HashSet<String>();
    private ArrayList<String> factsClasses;
    private ArrayList<String> factsProperties;
    private ArrayList<String> factsNewPredicates;
    private ArrayList<String> rules;
    private int[] newPropertiesArity;
    private HashSet<String> classess = new HashSet<String>();
    private HashSet<String> properties = new HashSet<String>();
    private ArrayList<String> listOfClasses;
    private ArrayList<String> listOfProperties;

    private int numberOfClasses;
    private int numberOfProperties;

    private int maxChooser;
    private int min = 1;
    private int iterations=5;

    public RulesGenerator(File ontology, int r, int b, int f, int i, int a, int n) throws OWLOntologyCreationException {

        _ontologyManager=OWLManager.createOWLOntologyManager();
        _ontology=_ontologyManager.loadOntologyFromOntologyDocument(ontology);
        _ontologyLabel = _ontologyManager.getOWLDataFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        labels = new Labels(_ontology, _ontologyLabel);


        numberOfDifferentIndividuals = i > 0 ? i :1;
        numberOfRequstedRules = r;
        numberOfMaxBodyAtomesPerRule = b > 2 ? b : 2;
        numberOfRequestFacts = f;
        numberOfNewPredicates = n > 0 ? n : 1;
        numberOfMaxArityOfNewPredicates = a;

        factsClasses = new ArrayList<String>(numberOfRequestFacts+1);
        factsProperties = new ArrayList<String>(numberOfRequestFacts+1);
        factsNewPredicates = new ArrayList<String>(numberOfRequestFacts+1);
        rules = new ArrayList<String>(numberOfRequstedRules+1);
        collectAllClassesAndRules();
        createNewPredicates();
        createFacts();
        createRules();

    }

    private void collectAllClassesAndRules(){
        for (OWLClass owlClass: _ontology.getClassesInSignature()){
            if(!(owlClass.isOWLThing() || owlClass.isOWLNothing()))
                classess.add(labels.getName(owlClass));
        }
        numberOfClasses = classess.size();
        listOfClasses = new ArrayList<String>(classess);
        for (OWLObjectProperty objectProperty: _ontology.getObjectPropertiesInSignature()){
            properties.add(labels.getName(objectProperty));
        }
        numberOfProperties = properties.size();
        listOfProperties = new ArrayList<String>(properties);

        maxChooser = numberOfClasses + numberOfProperties + numberOfNewPredicates;
    }

    private void createNewPredicates(){
        newPropertiesArity = new int[numberOfNewPredicates];
        for(int i = 0; i< newPropertiesArity.length; i++){
            newPropertiesArity[i] = randomNumber(numberOfMaxArityOfNewPredicates);
        }
    }

    private void createFacts(){
        String rule="";
        PredicateType predicateType;
        for(int f = 1; f<=numberOfRequestFacts; f++){
            predicateType = choosePredicateTypeForFacts();
            switch (predicateType){
                case CLASS:
                    rule = getRandomClass() + "(a"+randomNumber(numberOfDifferentIndividuals)+").";
                    factsClasses.add(rule);
                    break;
                case PROPERTY:
                    rule = getRandomProperty() + "(a"+randomNumber(numberOfDifferentIndividuals)+", a"+randomNumber(numberOfDifferentIndividuals)+").";
                    factsProperties.add(rule);
                    break;
                case NEWPREDICATE:
                    int i = randomNumber(numberOfNewPredicates);
                    rule = "p"+i+"(";
                    int a = newPropertiesArity[i-1];
                    for(int j = 1; j<=a; j++){
                        rule +="a"+randomNumber(numberOfDifferentIndividuals)+", ";
                    }
                    rule = rule.substring(0, rule.length()-2);
                    rule+=").";
                    factsNewPredicates.add(rule);
                    break;
                default:
                    break;
            }
        }
    }

    private void createRules(){
        int numberOfBodyAtoms,numberOfPositiveBodyAtoms;
        int variableCounter, usedBodyAtoms;
        int n1, n2;
        int arity;
        String rule;
        PredicateType predicateType;
        Predicate predicate;
        Predicate subPredicate;
        for(int j=0; j<numberOfRequstedRules; j++){
            variableCounter = 0;
            usedBodyAtoms = 1;
            numberOfBodyAtoms = randomNumber(numberOfMaxBodyAtomesPerRule);
            numberOfPositiveBodyAtoms = randomNumber(numberOfBodyAtoms);
            predicateType = choosePredicateTypeForRules();
            rule = "";
            if(predicateType == PredicateType.CLASS){
                rule = getRandomClass()+"(X1) :- ";
                predicateType = choosePredicateTypeForRules();
                if(predicateType==PredicateType.CLASS){
                    rule += getRandomClass() + "(X1)";
                    variableCounter=1;
                }else if(predicateType == PredicateType.PROPERTY){
                    rule += getRandomProperty() + "(X1, X2)";
                    variableCounter=2;
                }else if(predicateType == PredicateType.NEWPREDICATE){
                    subPredicate = getRandomNewPredicate(0);
                    rule += subPredicate.rule;
                    variableCounter = subPredicate.a;
                }
            }else if (predicateType == PredicateType.PROPERTY){
                rule = getRandomProperty()+ "(X1, X2) :- ";
                if(numberOfPositiveBodyAtoms==1){
                    rule += getRandomProperty()+"(X1, X2)";
                    variableCounter=2;
                }else{
                    predicateType = choosePredicateTypeForRules();
                    if(predicateType == PredicateType.CLASS){
                        rule += getRandomClass()+"(X1), "+getRandomClass()+"(X2)";
                        variableCounter=2;
                        usedBodyAtoms=2;
                    }else if (predicateType == PredicateType.PROPERTY){
                        rule += getRandomProperty()+"(X1, X2)";
                        variableCounter=2;
                    }else if (predicateType == PredicateType.NEWPREDICATE){
                        subPredicate = getRandomNewPredicate(0);
                        rule += subPredicate.rule;
                        variableCounter = subPredicate.a;
                        if(subPredicate.a == 1){
                            subPredicate = getRandomNewPredicate(1);
                            rule += subPredicate.rule;
                            variableCounter = subPredicate.a;
                            usedBodyAtoms=2;
                        }
                    }
                }
            }else if (predicateType == PredicateType.NEWPREDICATE){
                subPredicate = getRandomNewPredicate(0);
                rule = subPredicate.rule + " :- ";
                arity = subPredicate.a;
                usedBodyAtoms=0;
                while(variableCounter<=arity){
                    predicateType = choosePredicateTypeForRules();
                    if(predicateType == PredicateType.CLASS){
                        variableCounter++;
                        rule+=getRandomClass()+"(X"+variableCounter+"), ";
                    }else if(predicateType == PredicateType.PROPERTY){
                        rule+=getRandomProperty()+"(X"+(variableCounter+1)+", X"+(variableCounter+2)+"), ";
                        variableCounter+=2;
                    }else if(predicateType == PredicateType.NEWPREDICATE){
                        subPredicate = getRandomNewPredicate(variableCounter);
                        rule += subPredicate.rule+", ";
                        variableCounter = subPredicate.a;
                    }
                    usedBodyAtoms++;
                }
                rule = rule.substring(0, rule.length()-2);
            }

            if(usedBodyAtoms<numberOfPositiveBodyAtoms){
                for(int i=1; i<numberOfPositiveBodyAtoms-usedBodyAtoms;i++){
                    predicateType = choosePredicateTypeForRules();
                    if(predicateType == PredicateType.CLASS){
                        n1=randomNumber(variableCounter+1);
                        rule +=", "+getRandomClass()+"(X"+n1+")";
                        if(n1 == (variableCounter+1))
                            variableCounter++;
                    }else if (predicateType == PredicateType.PROPERTY){
                        n1 = randomNumber(variableCounter);
                        n2 = randomNotEqNumber(variableCounter+1,n1);
                        rule +=", "+getRandomProperty()+"(X"+n1+", X"+n2+")";
                        if(n2==(variableCounter+1))
                            variableCounter++;
                    }else if (predicateType == PredicateType.NEWPREDICATE){
                        subPredicate = getRandomNewPredicateRandom(randomNumber(variableCounter + 1));
                        rule+=", "+subPredicate.rule;
                        if(variableCounter<=subPredicate.max)
                            variableCounter = subPredicate.max + 1;
                    }
                }
            }
            if(numberOfPositiveBodyAtoms<numberOfBodyAtoms){
                for(int i=1; i < (numberOfBodyAtoms-numberOfPositiveBodyAtoms);i++){
                    predicateType = choosePredicateTypeForRules();
                    if(variableCounter==1 || predicateType == PredicateType.CLASS){
                        rule +=", not "+getRandomClass()+"(X"+randomNumber(variableCounter)+")";
                    }else if (predicateType == PredicateType.PROPERTY){
                        n1 = randomNumber(variableCounter);
                        n2 = randomNotEqNumber(variableCounter, n1);
                        rule +=", not "+getRandomProperty()+"(X"+n1+", X"+n2+")";
                    }else if (predicateType == PredicateType.NEWPREDICATE){
                        subPredicate = getRandomNewPredicateRandom(randomNumber(variableCounter));
                        rule+=", not "+subPredicate.rule;
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
        for(String str: factsNewPredicates) {
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
    private int randomNotEqListNumber(int max, final int[] vars){
        int j=0, number = 1;
        while (j<iterations && numberInArray(number, vars)){
            number = randomNumber(max);
            j++;
        }
        return number;
    }
    private boolean numberInArray(int n, int[] nums){
        boolean result = false;
        for(int i=0;i<nums.length; i++){
            if(n==nums[i]){
                result = true;
                break;
            }
        }
        return result;
    }

    private String getRandomClass(){
        return listOfClasses.get(randomNumber(numberOfClasses)-1);
    }
    private String getRandomProperty(){
        return listOfProperties.get(randomNumber(numberOfProperties)-1);
    }
    private Predicate getRandomNewPredicate(int k){
        int np = randomNumber(numberOfNewPredicates);
        String rule = "p"+np+"(";
        int a = newPropertiesArity[np-1]+k;
        for(int i=1+k; i<=a; i++){
            rule+="X"+i+", ";
        }
        rule = rule.substring(0, rule.length()-2);
        rule += ")";
        Predicate predicate = new Predicate();
        predicate.rule = rule;
        predicate.a = a;
        return predicate;
    }
    private Predicate getRandomNewPredicateRandom(int v){
        int np = randomNumber(numberOfNewPredicates);
        String rule = "p"+np+"(";
        int a = newPropertiesArity[np-1];
        int[] vars = new int[a+1];
        int max = 0;
        int x;
        for(int i=0; i<a; i++){
            x = randomNotEqListNumber(v, vars);
            vars[i] = x;
            if(max<x)
                max = x;
            if(x==v)
                v++;
            rule+="X"+x+", ";
        }
        rule = rule.substring(0, rule.length()-2);
        rule += ")";
        Predicate predicate = new Predicate();
        predicate.rule = rule;
        predicate.max = max;
        return predicate;
    }

    private PredicateType choosePredicateTypeForFacts(){
        int i = randomNumber(maxChooser);
        if(i < numberOfNewPredicates)
            return PredicateType.NEWPREDICATE;
        else if(i<=(numberOfNewPredicates+numberOfProperties))
            return PredicateType.PROPERTY;
        else //if(i<=(numberOfNewPredicates+numberOfProperties+numberOfClasses))
            return PredicateType.CLASS;
    }
    private PredicateType choosePredicateTypeForRules(){
        int i = randomNumber(10);
        if(i<=3 && numberOfNewPredicates>0)
            return PredicateType.NEWPREDICATE;
        else{
            i = randomNumber(numberOfClasses + numberOfProperties);
            if(i<numberOfProperties)
                return PredicateType.PROPERTY;
            else
                return PredicateType.CLASS;
        }
    }

    public class Predicate{
        public String rule;
        public int a;
        public int max;
    }
    public enum PredicateType {
        PROPERTY,
        CLASS,
        NEWPREDICATE
    }
}
