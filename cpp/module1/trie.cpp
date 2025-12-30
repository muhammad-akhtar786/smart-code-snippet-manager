#include "trie.h"

TrieNode::TrieNode() {
    endOfWord = false;
}

Trie::Trie() {
    root = new TrieNode();
}

void Trie::insert(string key) {
    TrieNode* current = root;
    for (char c : key) {
        if (current->children.count(c) == 0)
            current->children[c] = new TrieNode();
        current = current->children[c];
    }
    current->endOfWord = true;
}

bool Trie::search(string key) {
    TrieNode* current = root;
    for (char c : key) {
        if (current->children.count(c) == 0)
            return false;
        current = current->children[c];
    }
    return current->endOfWord;
}
