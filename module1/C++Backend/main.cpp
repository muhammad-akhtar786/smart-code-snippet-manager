

#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <algorithm>
#include "hashmap.h"
#include "trie.h"
#include "json.hpp"

using namespace std;
using json = nlohmann::json;

// ---------- Helpers ----------
string trim(const string &s) {
    size_t start = s.find_first_not_of(" \t\n\r");
    size_t end = s.find_last_not_of(" \t\n\r");
    return (start == string::npos) ? "" : s.substr(start, end - start + 1);
}

string toLower(const string &s) {
    string result = s;
    transform(result.begin(), result.end(), result.begin(), ::tolower);
    return result;
}

// ---------- Main ----------
int main() {
    HashMap snippets;
    Trie index;

    // ---- Load JSON ----
    json snippetData = json::object();
    ifstream jsonIn("snippets.json");
    if (jsonIn.is_open()) {
        try {
            jsonIn >> snippetData;
        } catch (...) {
            snippetData = json::object();
        }
        jsonIn.close();
    }

    if (!snippetData.is_object())
        snippetData = json::object();

    // Populate structures
    for (auto &item : snippetData.items()) {
        string key = toLower(trim(item.key()));
        snippets.insert(key, item.value());
        index.insert(key);
    }

    // ---- Read input.txt ----
    ifstream in("input.txt");
    ofstream out("output.txt");

    if (!in.is_open()) {
        out << "ERROR: input.txt not found";
        return 1;
    }

    string header;
    getline(in, header);

    if (header.empty()) {
        out << "NO COMMAND";
        return 0;
    }

    string command, key;
    stringstream ss(header);
    getline(ss, command, '|');
    getline(ss, key, '|');

    command = toLower(trim(command));
    key = toLower(trim(key));

    // ---- ADD ----
    if (command == "add") {
        string value, line;

        // Read full multiline snippet
        while (getline(in, line)) {
            if (!value.empty()) value += "\n";
            value += line;
        }

        snippets.insert(key, value);
        index.insert(key);
        snippetData[key] = value;

        out << "ADDED / UPDATED";
    }
    // ---- SEARCH ----
    else if (command == "search") {
        string result = snippets.find(key);
        out << (result.empty() ? "NOT FOUND" : result);
    }
    // ---- DELETE ----
    else if (command == "delete") {
        snippets.removeKey(key);
        if (snippetData.contains(key))
            snippetData.erase(key);

        out << "DELETED";
    }
    else {
        out << "INVALID COMMAND";
    }

    in.close();
    out.close();

    // ---- Save JSON ----
    ofstream jsonOut("snippets.json");
    if (jsonOut.is_open()) {
        jsonOut << snippetData.dump(4);
        jsonOut.close();
    }

    // ---- Clear input.txt (IMPORTANT) ----
    ofstream clear("input.txt", ios::trunc);
    clear.close();

    return 0;
}
