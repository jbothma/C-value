package uk.co.jbothma.terms;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 * 
 * @author jdb
 *
 */
public class Candidate implements Comparable {
	private int freq, len;
	private String string;

	public Candidate(String string) {
		this.string = string;
		freq = 0;
		len = string.split(" ").length;
	}

	public void observe() {
		freq++;
	}

	public String getString() {
		return string;
	}
	
	public Collection<String> getSubstrings() {
		String substring;
		String[] words = string.split(" ");
		ArrayList<String> substrings = new ArrayList<String>();
		
		for (ArrayList<Integer> idxs : substrIdxs(words.length)) {
			substring = new String();
			for (int i : idxs) {
				substring += words[i-1] + " "; // idxs start from 1, not 0
			}
			substrings.add(substring.trim());
		}
		System.out.println();
		return null;		
	}

	private static ArrayList<ArrayList<Integer>> substrIdxs(int count) {
		ArrayList<ArrayList<Integer>> idxs = new ArrayList<ArrayList<Integer>>();
		
		// substring means we don't want indices of full length hence < count
		for (int ii = 1; ii < count; ii++) {
			idxs.addAll(substrIdxs(count, ii));
		}
		return idxs;
	}	

	private static ArrayList<ArrayList<Integer>> substrIdxs(int count, int len) {
		int items = count-len+1;
		ArrayList<ArrayList<Integer>> idxs = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> item;
		
		for (int ii = 1; ii <= items; ii++) {
			item = new ArrayList<Integer>();
			for (int i2 = 0; i2 < len; i2++) {
				item.add(ii + i2);
			}
			idxs.add(item);
		}
		return idxs;
	}
	
	public int getFrequency() {
		return freq;
	}

	public int getLength() {
		return len;
	}
	
	public String toString() {
		return string;
	}

	@Override
	public int compareTo(Object otherObj) {
		Candidate other = (Candidate) otherObj;
		if (this.len < other.getLength()) {
			return -1;
		} else if (this.len == other.getLength()) {
			if (this.freq < other.freq) {
				return -1;
			} else if (this.freq == other.getFrequency()) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return 1;
		}
	}
}
