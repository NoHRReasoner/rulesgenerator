import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.util.Set;

public class Labels {
    /** The _ontology ID. */
    private static String _ontologyID;
    private OWLOntology _ontology;
    private OWLAnnotationProperty _ontologyLabel;
    private String _delimeter="#";
    private String _altDelimeter=":";

    public Labels(OWLOntology ontology, OWLAnnotationProperty ontologyLabel){
        _ontology = ontology;
        _ontologyLabel = ontologyLabel;
        String _ = _ontology.getOntologyID().getOntologyIRI().toString();

        _ontologyID = _.contains("/") ? _.substring(0, _.lastIndexOf("/")) + "/" : "";


    }
    public String getName(Set<OWLAnnotation> annotations, String owl){
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
    public String getName(OWLClass _class){
        return getName(_class.getAnnotations(_ontology, _ontologyLabel), _class.toString());

    }
    public String getName(OWLObjectProperty objectProperty){
        return getName(objectProperty.getAnnotations(_ontology, _ontologyLabel), objectProperty.toString());
    }
    public String getRuleFromString(String rule){
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

}
