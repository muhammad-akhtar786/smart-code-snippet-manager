#ifndef HASHMAP_H
#define HASHMAP_H

#include <string>
using namespace std;

struct Node {
    string key;
    string value;
    Node* next;

    Node(string k, string v);
};

class HashMap {
private:
    static const int SIZE = 100;
    Node* table[SIZE];
    int hash(string key);

public:
    HashMap();
    ~HashMap();

    void insert(string key, string value);
    string find(string key);
    bool contains(string key);
    void removeKey(string key);
};

#endif
