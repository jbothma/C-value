package uk.co.jbothma.terms;

import gate.Annotation;
import gate.AnnotationSet;
import gate.DataStore;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.Utils;
import gate.persist.SerialDataStore;
import gate.util.GateException;
import gate.util.Out;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Demo {
	private static final String dataStorePath = "/home/jdb/thesis/results/jrc2006_korp_big";
	private static final String outfilePath = "/home/jdb/thesis/results/CValueDemo.txt";
	
	/**
	 * @param args
	 * @throws GateException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws GateException, IOException {
		SerialDataStore dataStore;
		//Object docID;
		FeatureMap docFeatures;
		Iterator<Annotation> phrasIter;
		AnnotationSet inputAS;
		String inputASName, inputASType;
		gate.Document doc;
		String phrase, lemma;
		Annotation phrasAnnot;
		AnnotationSet tokAnnots;
		List<Annotation> tokAnnotList;
		CValueSess cvals;
		ArrayList<Candidate> candList;
		FileWriter fstream = new FileWriter(outfilePath);
		BufferedWriter out = new BufferedWriter(fstream);

		Gate.init();
		cvals = new CValueSess();
		
		// get the datastore
		dataStore = (SerialDataStore) Factory.openDataStore(
				"gate.persist.SerialDataStore", "file:///" + dataStorePath);
		dataStore.open();
		Out.prln("serial datastore opened...");
		
		int docCount = dataStore.getLrIds("gate.corpora.DocumentImpl").size();
		int docIdx = 0;
		
		// get the corpus
		for (Object docID : dataStore.getLrIds("gate.corpora.DocumentImpl")) {
			docFeatures = Factory.newFeatureMap();
			docFeatures.put(DataStore.LR_ID_FEATURE_NAME, docID);
			docFeatures.put(DataStore.DATASTORE_FEATURE_NAME, dataStore);
			
			//tell the factory to load the Serial Corpus with the specified ID from the specified  datastore
			doc = (gate.Document)
					Factory.createResource("gate.corpora.DocumentImpl", docFeatures);
			
			System.out.println("Doc " + (++docIdx) + " / " + docCount + " : " + doc.getName());
			
			inputASName = "Original markups";
			inputASType = "TermCandidate";
			
			inputAS = doc.getAnnotations(inputASName);
			
			phrasIter = inputAS.get(inputASType).iterator();
			
			while (phrasIter.hasNext()) {
				phrase = "";
				phrasAnnot = (Annotation) phrasIter.next();
				tokAnnots = gate.Utils.getContainedAnnotations(
						inputAS, phrasAnnot, "w");
				tokAnnotList = gate.Utils.inDocumentOrder(tokAnnots);
				
				for (Annotation tokAnnot : tokAnnotList) {
					lemma = tokenLemma(doc, tokAnnot);
					// TODO: this is a corpus-specific hack and should be fixed in preprocessing or
					// made customizable for the corpus. Also, it should rather be if the string matches ^|$
					// so that strings that contain characters without whitespace are accepted.
					if (!lemma.contains("|"))
						phrase += lemma + " ";
				}
				cvals.observe(phrase.toLowerCase().trim());
			}

			Factory.deleteResource(doc);
		}
		
		cvals.calculate();
		
		candList = new ArrayList<Candidate>(cvals.getCandidates());
		Collections.sort(candList, new CValueComparator());
		
		for (Candidate cand : candList) {
			String resultLine = 
					cand.getLength() + " " + 
					cand.getFrequency() + "  " + 
					cand.getNesterCount() + " " +
					cand.getFreqNested() + " " +
					cand.getString() + " " +
					cand.getCValue();
			//System.out.println(resultLine);
			out.write(resultLine + "\n");
		}
		
		out.close();
	}
	
	private static String tokenLemma(gate.Document doc, Annotation tokAnnot) {
		String lemma;
		String[] lemmas;
		String lemmaAnnotStr = (String)tokAnnot.getFeatures().get("lemma");
		if (lemmaAnnotStr != null) {
			lemmas = lemmaAnnotStr.split("\\|");
			if (lemmas.length > 1) {
				lemma = lemmas[1];
				if (!lemma.equals("")) {
//					if (lemma.contains("|")) {
//						System.out.println("lemma \"" + lemma + "\"");
//					}
					return lemma;
				}
			}
		}
		// fall back to raw string
		lemma = Utils.stringFor(doc, tokAnnot).toLowerCase();
//		if (lemma.contains("|")) {
//			System.out.println("raw \"" + lemma + "\"");
//		}
		return lemma;
	}
}
