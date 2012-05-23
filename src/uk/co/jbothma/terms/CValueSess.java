package uk.co.jbothma.terms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CValueSess {
	private HashMap<String, Candidate> candidates;
	
	public CValueSess() {
		candidates = new HashMap<String, Candidate>();
	}
	
	public void observe(String candStr) {
		Candidate candidate;
		if ((candidate = candidates.get(candStr)) == null) {
			candidate = new Candidate(candStr);
			candidates.put(candStr, candidate);
		}
		candidate.observe();
	}
	
	public Collection<Candidate> getCandidates() {
		return candidates.values();
	}
	
	public void calculate() {
		Collection<Candidate> cands = this.getCandidates();
		List<Candidate> candList = new ArrayList<Candidate>(cands);
		Collections.sort(candList);
		Collections.reverse(candList);
		for (Candidate cand : candList) {
			System.out.println(cand.getLength() + " " + cand.getFrequency() + "  " + cand.getString());
			cand.getSubstrings();
		}
	}
}
