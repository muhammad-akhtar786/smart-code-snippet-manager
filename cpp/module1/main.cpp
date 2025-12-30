#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <algorithm>
#include <vector>
#include <map>
#include <cstdio> // for rename, remove
#include "hashmap.h"
#include "trie.h"

using namespace std;

// ================= DATA STRUCTURES =================
struct Snippet
{
    string language;
    string code;
    string originalKey; // Store original key for JSON output
};

// ================= HELPERS =================
string trim(const string &s)
{
    size_t start = s.find_first_not_of(" \t\n\r");
    size_t end = s.find_last_not_of(" \t\n\r");
    return (start == string::npos) ? "" : s.substr(start, end - start + 1);
}

string toLower(string s)
{
    transform(s.begin(), s.end(), s.begin(), ::tolower);
    return s;
}

string escapeJson(const string &s)
{
    string r;
    for (char c : s)
    {
        if (c == '"')
            r += "\\\"";
        else if (c == '\\')
            r += "\\\\";
        else if (c == '\n')
            r += "\\n";
        else if (c == '\r')
            r += "\\r";
        else if (c == '\t')
            r += "\\t";
        else
            r += c;
    }
    return r;
}

string unescapeJson(const string &s)
{
    string r;
    for (size_t i = 0; i < s.size(); i++)
    {
        if (s[i] == '\\' && i + 1 < s.size())
        {
            char n = s[i + 1];
            if (n == 'n')
            {
                r += '\n';
                i++;
            }
            else if (n == 'r')
            {
                r += '\r';
                i++;
            }
            else if (n == 't')
            {
                r += '\t';
                i++;
            }
            else if (n == '"')
            {
                r += '"';
                i++;
            }
            else if (n == '\\')
            {
                r += '\\';
                i++;
            }
            else
                r += s[i];
        }
        else
            r += s[i];
    }
    return r;
}

// ================= SAFE JSON LOAD =================
bool loadJson(const string &filename, map<string, Snippet> &data)
{
    ifstream file(filename);
    if (!file.is_open())
        return false;

    string line, key, code;
    while (getline(file, line))
    {
        line = trim(line);
        if (line.empty() || line == "{" || line == "}")
            continue;

        // Parse: "key": "code"
        if (line.length() > 0 && line[0] == '"' && line.find("\": \"") != string::npos)
        {
            size_t colonPos = line.find("\": \"");
            key = line.substr(1, colonPos - 1);

            code = line.substr(colonPos + 4);
            // Remove trailing quote and comma
            if (!code.empty() && code.back() == ',')
                code.pop_back();
            if (!code.empty() && code.back() == '"')
                code.pop_back();

            // Store with lowercase key for case-insensitive lookup, but keep original key
            string lowerKey = toLower(key);
            data[lowerKey] = {"C++", unescapeJson(code), key};
        }
    }
    file.close();
    return true;
}

// ================= ATOMIC JSON SAVE =================
bool saveJsonSafe(const string &filename, const map<string, Snippet> &data)
{
    string temp = filename + ".tmp";
    string backup = filename + ".bak";

    // write temp with simple structure
    ofstream out(temp);
    if (!out.is_open())
        return false;

    out << "{\n";
    size_t i = 0;
    for (auto &e : data)
    {
        // Use original key if available, otherwise use lowercase key
        string outputKey = !e.second.originalKey.empty() ? e.second.originalKey : e.first;
        out << "    \"" << outputKey << "\": \"" << escapeJson(e.second.code) << "\"";
        if (++i < data.size())
            out << ",";
        out << "\n";
    }
    out << "}\n";
    out.close();

    // backup old file
    remove(backup.c_str());
    rename(filename.c_str(), backup.c_str());

    // replace with temp
    rename(temp.c_str(), filename.c_str());
    return true;
}

// ================= MAIN =================
int main()
{
    HashMap snippets;
    Trie index;
    map<string, Snippet> snippetData;

    // -------- LOAD JSON SAFELY --------
    if (!loadJson("snippets_large.json", snippetData))
    {
        ofstream out("output.txt");
        out << "ERROR: Failed to load JSON. Data preserved.";
        return 1;
    }

    // Load to memory (using lowercase keys for case-insensitive search)
    map<string, string> lowerToOriginal; // Map lowercase to original case for lookup
    for (auto &e : snippetData)
    {
        string lowerKey = toLower(e.second.originalKey);
        lowerToOriginal[lowerKey] = e.first;
        snippets.insert(lowerKey, "Language: " + e.second.language + "\n\n" + e.second.code);
        index.insert(lowerKey);
    }

    // -------- READ INPUT --------
    ifstream in("input.txt");
    ofstream out("output.txt");
    if (!in.is_open())
    {
        out << "ERROR: input.txt missing";
        return 1;
    }

    string header;
    getline(in, header);
    if (header.empty())
    {
        out << "NO COMMAND";
        return 0;
    }

    string cmd, title, lang = "Unknown";
    stringstream ss(header);
    getline(ss, cmd, '|');
    getline(ss, title, '|');
    getline(ss, lang, '|');

    cmd = toLower(trim(cmd));
    title = trim(title);
    lang = trim(lang);

    string code, line;
    while (getline(in, line))
    {
        if (!code.empty())
            code += "\n";
        code += line;
    }
    code = trim(code); // Trim code to remove trailing whitespace

    bool modified = false;
    string titleLower = toLower(title);

    // -------- COMMANDS --------
    if (cmd == "add")
    {
        if (!title.empty() && !code.empty())
        {
            snippetData[titleLower] = {lang, code, title};
            modified = true;
            out << "SNIPPET ADDED";
        }
        else if (title.empty())
        {
            out << "ERROR: Title cannot be empty";
        }
        else
        {
            out << "ERROR: Code cannot be empty";
        }
    }
    else if (cmd == "update")
    {
        if (snippetData.count(titleLower) > 0)
        {
            snippetData[titleLower].code = code;
            if (lang != "Unknown")
                snippetData[titleLower].language = lang;
            modified = true;
            out << "SNIPPET UPDATED";
        }
        else
        {
            out << "ERROR: Snippet '" << title << "' not found for update";
        }
    }
    else if (cmd == "delete")
    {
        if (snippetData.count(titleLower) > 0)
        {
            snippetData.erase(titleLower);
            modified = true;
            out << "SNIPPET DELETED";
        }
        else
        {
            out << "ERROR: Snippet '" << title << "' not found for deletion";
        }
    }
    else if (cmd == "search")
    {
        if (titleLower.empty())
        {
            out << "ERROR: Search term cannot be empty";
        }
        else
        {
            string result = snippets.find(titleLower);
            if (!result.empty())
            {
                out << result;
            }
            else
            {
                // Try substring search if exact match fails
                bool found = false;
                for (auto &e : snippetData)
                {
                    if (toLower(e.second.originalKey).find(titleLower) != string::npos)
                    {
                        out << "Language: " << e.second.language << "\n\n"
                            << e.second.code;
                        found = true;
                        break;
                    }
                }
                if (!found)
                {
                    out << "NOT FOUND";
                }
            }
        }
    }
    else
    {
        out << "ERROR: Unknown command '" << cmd << "'";
    }

    in.close();
    out.close();

    // -------- SAVE ONLY IF MODIFIED --------
    if (modified)
    {
        saveJsonSafe("snippets_large.json", snippetData);
    }

    // Clear input safely
    ofstream clear("input.txt", ios::trunc);
    clear.close();

    return 0;
}
