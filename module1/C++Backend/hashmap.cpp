#include "hashmap.h"
#include <iostream>

Node::Node(string k, string v) {
    key = k;
    value = v;
    next = nullptr;
}

HashMap::HashMap() {
    for (int i = 0; i < SIZE; i++)
        table[i] = nullptr;
}

HashMap::~HashMap() {
    for (int i = 0; i < SIZE; i++) {
        Node* curr = table[i];
        while (curr) {
            Node* temp = curr;
            curr = curr->next;
            delete temp;
        }
    }
}

int HashMap::hash(string key) {
    int hashVal = 0;
    for (char c : key)
        hashVal = (hashVal + c) % SIZE;
    return hashVal;
}

bool HashMap::contains(string key) {
    int idx = hash(key);
    Node* curr = table[idx];
    while (curr) {
        if (curr->key == key) return true;
        curr = curr->next;
    }
    return false;
}

void HashMap::insert(string key, string value) {
    int idx = hash(key);
    Node* curr = table[idx];

    while (curr) {
        if (curr->key == key) {
            curr->value = value;
            return;
        }
        curr = curr->next;
    }

    Node* newNode = new Node(key, value);
    newNode->next = table[idx];
    table[idx] = newNode;
}

string HashMap::find(string key) {
    int idx = hash(key);
    Node* curr = table[idx];
    while (curr) {
        if (curr->key == key) return curr->value;
        curr = curr->next;
    }
    return "";
}

void HashMap::removeKey(string key) {
    int idx = hash(key);
    Node* curr = table[idx];
    Node* prev = nullptr;

    while (curr) {
        if (curr->key == key) {
            if (prev) prev->next = curr->next;
            else table[idx] = curr->next;
            delete curr;
            return;
        }
        prev = curr;
        curr = curr->next;
    }
}
