package com.weibo.api.api_test;

public class MagicDictionary {
	static String[] classDict = null;

	/** Initialize your data structure here. */
	public MagicDictionary() {

	}

	/** Build a dictionary through a list of words */
	public void buildDict(String[] dict) {
		classDict = dict;
	}

	/** Returns if there is any word in the trie that equals to the given word after modifying exactly one character */
	public boolean search(String word) {
		int count = 0;
		for(String x: classDict){
			count = 0 ;
			if(x.length() != word.length()) continue;
			for(int i=0; i<x.length(); i++){
				if(x.charAt(i) != word.charAt(i))   count++;
				if(count>1) break;
			}
			if(count==1)    return true;
		}

		return false;
	}

	public static void main(String[] args) {
		MagicDictionary dictionary = new MagicDictionary();
		dictionary.buildDict(new String[] {"hello","leetcode"});
		System.out.println(dictionary.search("helle"));
	}
}
