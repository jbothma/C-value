package uk.co.jbothma.terms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import gate.Annotation;
import gate.AnnotationSet;
import gate.DataStore;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ANNIEConstants;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.persist.SerialDataStore;
import gate.util.GateException;
import gate.util.Out;

public class Demo {
	private static final String dataStorePath = "/home/jdb/workspace/SweSPARKGATEPR/demo/ManskligaRattigheter/";
	
	/**
	 * @param args
	 * @throws GateException 
	 */
	public static void main(String[] args) throws GateException {
		SerialDataStore dataStore;
		Object docID;
		FeatureMap docFeatures;
		Iterator<Annotation> phrasIter;
		AnnotationSet inputAS;
		String inputASName, inputASType;
		gate.Document doc;
		String phrase, word;
		Annotation phrasAnnot;
		AnnotationSet tokAnnots;
		List<Annotation> tokAnnotList;
		CValueSess cvals;
		Collection<Candidate> cands;

		Gate.init();
		cvals = new CValueSess();
		
		// get the datastore
		dataStore = (SerialDataStore) Factory.openDataStore(
				"gate.persist.SerialDataStore", "file:///" + dataStorePath);
		dataStore.open();
		Out.prln("serial datastore opened...");
		
		// get the corpus
		docID = dataStore.getLrIds("gate.corpora.DocumentImpl").get(0);
		docFeatures = Factory.newFeatureMap();
		docFeatures.put(DataStore.LR_ID_FEATURE_NAME, docID);
		docFeatures.put(DataStore.DATASTORE_FEATURE_NAME, dataStore);
		
		//tell the factory to load the Serial Corpus with the specified ID from the specified  datastore
		doc = (gate.Document)
				Factory.createResource("gate.corpora.DocumentImpl", docFeatures);
		System.out.println("Demo: got the document.");
		System.out.println(doc.getAnnotationSetNames());
		
		inputASName = "OntPreprocess";
		inputASType = "NounPhrase";
		
		inputAS = doc.getAnnotations(inputASName);
		
		System.out.println(doc.getAnnotationSetNames());
		System.out.println(doc.getAnnotations(inputASName).getAllTypes());
		System.out.println(doc.getAnnotations(inputASName).get(inputASType).size());
		
		phrasIter = inputAS.get(inputASType).iterator();
		
		while (phrasIter.hasNext()) {
			phrase = "";
			phrasAnnot = (Annotation) phrasIter.next();
			tokAnnots = gate.Utils.getContainedAnnotations(
					inputAS, phrasAnnot, ANNIEConstants.TOKEN_ANNOTATION_TYPE);
			tokAnnotList = gate.Utils.inDocumentOrder(tokAnnots);
			
			for (Annotation tokAnnot : tokAnnotList) {
				if (tokAnnotIsWord(tokAnnot))
				{
					word = tokAnnotString(tokAnnot);
					phrase += word + " ";
				}
			}
			cvals.observe(phrase.toLowerCase());
		}
		
		cvals.calculate();
	}
	private static boolean tokAnnotIsWord(Annotation tokAnnot) {
		return tokAnnot.getFeatures().get(ANNIEConstants.TOKEN_KIND_FEATURE_NAME).equals("word");
	}
	
	private static String tokAnnotString(Annotation tokAnnot) {
		return (String) tokAnnot.getFeatures().get(ANNIEConstants.TOKEN_STRING_FEATURE_NAME);
	}
}
