package onthology;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OntologyExtractor {
    
    private OWLOntology ontology;
    private OWLReasoner reasoner;
    
    public OntologyExtractor() throws OWLOntologyCreationException {
        
    	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        ontology = manager.loadOntologyFromOntologyDocument(new File("./onthology/Salem.owx"));
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        reasoner = reasonerFactory.createReasoner(ontology);
    }
    
   
    public IRI findClassIRIByFragment(String fragmentName) {
        Set<OWLClass> classes = ontology.getClassesInSignature();
        
        for (OWLClass owlClass : classes) {
            IRI iri = owlClass.getIRI();
            if (fragmentName.equals(iri.getFragment())) {
                return iri;
            }
        }
        
        return null;
    }
    
    public Set<OWLNamedIndividual> getIndividuals(IRI classIRI){
         OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
         OWLClass owlClass = factory.getOWLClass(classIRI);
         Set<OWLNamedIndividual> individuals = reasoner.getInstances(owlClass, false).getFlattened();

		 return individuals;
    }
    
    public Map<String, Map<String, String>> getIndividualsOfClass(IRI classIRI) {
   	 	Map<String, Map<String, String>> result = new HashMap<>();

        
        Set<OWLNamedIndividual> individuals = getIndividuals(classIRI);
// 	   int agentCount = 0;

        for (OWLNamedIndividual individual : individuals) {
//			if (agentCount >= 3) {
//        	    }
            Map<String, String> properties = new HashMap<>();
//            agentCount ++;
            

            for (OWLDataPropertyAssertionAxiom axiom : ontology.getDataPropertyAssertionAxioms(individual)) {
                String property = getClassFriendlyName(axiom.getProperty());
                String value = getClassFriendlyDataPropertyName(axiom.getObject());
        
                properties.put(property, value);
            }
            for (OWLObjectPropertyAssertionAxiom axiom : ontology.getObjectPropertyAssertionAxioms(individual)) {
                String property =  getClassFriendlyName(axiom.getProperty());
                String person = getClassFriendlyName(axiom.getObject());
                		
            		

                	property = property.substring((property.indexOf("#") + 1));
                    properties.put(person,property);
                    }
            
            String individualName = getClassFriendlyName(individual);
            result.put(individualName, properties);
        }
        
        return result;
    }
    
    private String getClassFriendlyName(Object property) {
		String label = property.toString();
		label = label.substring((label.indexOf("#") + 1),label.length() - 1);

		return label;
	}
    private String getClassFriendlyDataPropertyName(Object property) {
		String label = property.toString();
		Pattern pattern = Pattern.compile("\"([^\"]*)");
		Matcher matcher = pattern.matcher(label);

		if (matcher.find()) {
			label = matcher.group(1);
		}


		return label;
	}
    public void close() {
        reasoner.dispose();
    }
}