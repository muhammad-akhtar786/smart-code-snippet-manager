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

string toLower(string s) {
    transform(s.begin(), s.end(), s.begin(), ::tolower);
    return s;
}

// ---------- Main ----------
int main() {
    HashMap snippets;
    Trie index;

    // ---------- Load JSON ----------
    json snippetData = json::object();
    ifstream jsonIn("snippets.json");
    if (jsonIn.is_open()) {
        try { jsonIn >> snippetData; }
        catch (...) { snippetData = json::object(); }
        jsonIn.close();
    }

    if (!snippetData.is_object())
        snippetData = json::object();

    // Load existing snippets into memory
    for (auto &item : snippetData.items()) {
        if (item.value().is_object()) {
            string key = toLower(trim(item.key()));
            string lang = item.value().value("language", "Unknown");
            string code = item.value().value("code", "");
            snippets.insert(key, "Language: " + lang + "\n\n" + code);
            index.insert(key);
        }
    }

    // ---------- Read input.txt ----------
    ifstream in("input.txt");
    ofstream out("output.txt");

    if (!in.is_open()) {
        out << "ERROR: input.txt not found";
        return 1;
    }

    string headerLine;
    getline(in, headerLine);

    if (headerLine.empty()) {
        out << "NO COMMAND";
        return 0;
    }

    // ---------- Fixed Parsing Logic ----------
    // Java sends: COMMAND|TITLE|LANGUAGE|
    string command, title, language = "Unknown";
    stringstream ss(headerLine);
    string part;

    if (getline(ss, part, '|')) command = toLower(trim(part));
    if (getline(ss, part, '|')) title = trim(part);
    if (getline(ss, part, '|')) {
        string langPart = trim(part);
        if (!langPart.empty()) language = langPart;
    }

    if (title.empty() && command != "search") {
        out << "ERROR: Title cannot be empty";
        return 0;
    }

    string key = toLower(title);

    // ---------- Read multiline code ----------
    string code, line;
    while (getline(in, line)) {
        if (!code.empty()) code += "\n";
        code += line;
    }

    // ---------- COMMAND HANDLING ----------
    if (command == "add") {
        if (title.empty() || code.empty()) {
            out << "ERROR: Title or Code cannot be empty";
        } else {
            snippetData[key] = {
                {"language", language},
                {"code", code}
            };
            snippets.insert(key, "Language: " + language + "\n\n" + code);
            index.insert(key);
            out << "SNIPPET ADDED";
        }
    }
    else if (command == "update") {
        if (!snippetData.contains(key)) {
            out << "ERROR: Snippet not found";
        } else {
            // Keep existing language if update doesn't provide a new one
            string oldLang = snippetData[key].value("language", "Unknown");
            snippetData[key] = {
                {"language", (language == "Unknown") ? oldLang : language},
                {"code", code}
            };
            snippets.insert(key, "Language: " + snippetData[key]["language"].get<string>() + "\n\n" + code);
            out << "SNIPPET UPDATED";
        }
    }
    else if (command == "delete") {
        if (snippetData.contains(key)) {
            snippetData.erase(key);
            snippets.removeKey(key);
            out << "SNIPPET DELETED";
        } else {
            out << "ERROR: Snippet not found";
        }
    }
    else if (command == "search") {
        string result = snippets.find(key);
        out << (result.empty() ? "NOT FOUND" : result);
    }
    else {
        out << "INVALID COMMAND";
    }

    in.close();
    out.close();

    // ---------- Save JSON ----------
    ofstream jsonOut("snippets.json");
    if (jsonOut.is_open()) {
        jsonOut << snippetData.dump(4);
        jsonOut.close();
    }

    // ---------- Clear input.txt ----------
    ofstream clear("input.txt", ios::trunc);
    clear.close();

    return 0;
}
