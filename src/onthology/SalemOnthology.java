//package onthology;
//
//import java.io.File;
//
//import org.semanticweb.owlapi.apibinding.OWLManager;
//import org.semanticweb.owlapi.model.OWLDataFactory;
//import org.semanticweb.owlapi.model.OWLOntology;
//import org.semanticweb.owlapi.model.OWLOntologyCreationException;
//import org.semanticweb.owlapi.model.OWLOntologyManager;
//
//public class SalemOnthology {
//	private OWLOntologyManager ontoManager;
//	private OWLOntology salemOntology;
//	private OWLDataFactory dataFactory;
//	
//	private String ontologyIRIStr;
//
//	public SalemOnthology() {
//		ontoManager = OWLManager.createOWLOntologyManager();
//		dataFactory = ontoManager.getOWLDataFactory();
//		loadOntology();
//
//		ontologyIRIStr = salemOntology.getOntologyID().getOntologyIRI().toString() + "#";
//
//	}
//	
//	private void loadOntology() {
//		File ontology = new File("./onthology/salem.owl");
//
//		try {
//			salemOntology = ontoManager.loadOntologyFromOntologyDocument(ontology);
//		} catch (OWLOntologyCreationException e) {
//			System.out.println(e.getMessage());
//		}
//	}
//
//}
