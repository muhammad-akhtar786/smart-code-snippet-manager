#ifndef TRIE_H
#define TRIE_H

#include <unordered_map>
#include <string>
using namespace std;

struct TrieNode {
    unordered_map<char, TrieNode*> children;
    bool endOfWord;

    TrieNode();
};

class Trie {
private:
    TrieNode* root;

public:
    Trie();
    void insert(string key);
    bool search(string key);
};

#endif
