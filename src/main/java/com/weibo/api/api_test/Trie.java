package com.weibo.api.api_test;

class Trie {
	private static final int LETTER_NUM = 26;
	private boolean isWord;
	private Trie[] letters;

	/** Initialize your data structure here. */
	public Trie() {
		isWord = false;
		letters = new Trie[LETTER_NUM];
	}

	/** Inserts a word into the trie. */
	public void insert(String word) {
		if (word == null || word.length() <= 0) {
			return;
		}

		Trie currentNode = this;
		for (char letter : word.toCharArray()) {
			int letterIndex = letter- 'a';
			if (currentNode.letters[letterIndex] == null) {
				currentNode.letters[letterIndex] = new Trie();
			}

			currentNode = currentNode.letters[letterIndex];
		}

		currentNode.isWord = true;
	}

	/** Returns if the word is in the trie. */
	public boolean search(String word) {
		if (word == null || word.length() <= 0) {
			return false;
		}

		return dfs(this, true, word.toCharArray(), 0);
	}

	/** Returns if there is any word in the trie that starts with the given prefix. */
	public boolean startsWith(String prefix) {
		if (prefix == null || prefix.length() <= 0) {
			return false;
		}

		return dfs(this, false, prefix.toCharArray(), 0);
	}

	private boolean dfs(Trie currentNode, boolean isExactMatch, char[] stringCharArray, int index) {
		if (currentNode == null || index > stringCharArray.length) { return false;}

		if (index == stringCharArray.length) {
			if (!isExactMatch) {
				return true;
			} else {
				return currentNode.isWord;
			}
		}

		int letterIndex = stringCharArray[index] - 'a';
		return dfs(currentNode.letters[letterIndex], isExactMatch, stringCharArray, index + 1);
	}
}