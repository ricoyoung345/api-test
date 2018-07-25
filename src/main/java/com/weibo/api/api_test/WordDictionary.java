package com.weibo.api.api_test;

public class WordDictionary {

	private static final int LETTER_NUM = 26;
	private boolean isWord;
	private WordDictionary[] letters;

	/** Initialize your data structure here. */
	public WordDictionary() {
		isWord = false;
		letters = new WordDictionary[LETTER_NUM];
	}

	/** Adds a word into the data structure. */
	public void addWord(String word) {
		if (word == null || word.length() <= 0) {
			return;
		}

		WordDictionary currentNode = this;
		for (char letter : word.toCharArray()) {
			int letterIndex = letter- 'a';
			if (currentNode.letters[letterIndex] == null) {
				currentNode.letters[letterIndex] = new WordDictionary();
			}

			currentNode = currentNode.letters[letterIndex];
		}

		currentNode.isWord = true;
	}

	/** Returns if the word is in the data structure. A word could contain the dot character '.' to represent any one letter. */
	public boolean search(String word) {
		if (word == null || word.length() <= 0) {
			return false;
		}

		return dfs(this, word.toCharArray(), 0);
	}

	private boolean dfs(WordDictionary currentDict, char[] stringCharArray, int index) {
		if (currentDict == null || index > stringCharArray.length) {
			return false;
		}

		if (index == stringCharArray.length) {
			return currentDict.isWord;
		}

		char letter = stringCharArray[index];
		boolean result = false;
		if (letter == '.') {
			for (WordDictionary wordDictionary : currentDict.letters) {
				if (wordDictionary != null) {
					result |= dfs(wordDictionary, stringCharArray, index + 1);
				}
			}
		} else {
			int letterIndex = letter - 'a';
			result = dfs(currentDict.letters[letterIndex], stringCharArray, index + 1);
		}

		return result;
	}
}
