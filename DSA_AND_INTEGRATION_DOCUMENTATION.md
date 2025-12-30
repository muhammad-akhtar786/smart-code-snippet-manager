# Smart Code Snippet Manager - DSA & Integration Documentation

## Table of Contents

1. [Overview](#overview)
2. [Data Structures & Algorithms Used](#data-structures--algorithms-used)
3. [Architecture & Integration](#architecture--integration)
4. [Module-by-Module Analysis](#module-by-module-analysis)
5. [Data Flow Diagram](#data-flow-diagram)
6. [Complete Integration Guide](#complete-integration-guide)

---

## Overview

**Project Name:** Smart Code Snippet Manager (Unified Edition)

**Purpose:** A comprehensive code snippet management system that uses advanced DSA to organize, search, and recommend code snippets based on tags, languages, and usage patterns.

**Tech Stack:**

- **Backend:** C++ (Module 1 & Module 3)
- **Frontend:** Java Swing
- **Data Storage:** JSON files
- **Communication:** Process-based (ProcessBuilder in Java)

---

## Data Structures & Algorithms Used

### Summary Table

| DSA                       | Module   | File(s)                             | Purpose                                | Why Used                                  | Status      |
| ------------------------- | -------- | ----------------------------------- | -------------------------------------- | ----------------------------------------- | ----------- |
| **HashMap**               | Module 1 | `cpp/module1/hashmap.cpp/h`         | Fast O(1) tag lookup & snippet storage | Quick retrieval of snippets by tag        | ✅ USED     |
| **Trie**                  | Module 1 | `cpp/module1/trie.cpp/h`            | Prefix-based search & autocomplete     | Efficient tag prefix matching             | ✅ USED     |
| **Graph (Tag Graph)**     | Module 3 | `cpp/module3/graph.cpp/h`           | Tag relationship mapping               | Finding related tags & recommendations    | ✅ USED     |
| **Recommendation Engine** | Module 3 | `cpp/module3/recommendations.cpp/h` | Algorithm combining graph + scoring    | Multi-criteria snippet ranking            | ✅ USED     |
| **Binary Search Tree**    | -        | -                                   | Sorted data retrieval                  | Not used (HashMap sufficient)             | ❌ NOT USED |
| **Heap/Priority Queue**   | -        | -                                   | Top-K element extraction               | Not used (Sorting sufficient)             | ❌ NOT USED |
| **Dynamic Programming**   | -        | -                                   | Optimization problems                  | Not needed (linear algorithms sufficient) | ❌ NOT USED |

---

## Data Structures & Algorithms - Detailed Analysis

### 1. **HashMap (Module 1)**

#### What It Is:

A hash table data structure that stores key-value pairs with O(1) average-case lookup time.

#### Where It's Used:

```
📁 cpp/module1/
├── hashmap.cpp
├── hashmap.h
└── main.cpp (uses HashMap)
```

#### Why It's Used:

- **Fast Lookup:** O(1) average time to find snippets by tag
- **Space Efficient:** No sorted overhead like BST
- **Real-time Responsiveness:** Instant tag-based search results

#### How It Works:

```cpp
// From hashmap.h/cpp
class HashMap {
    SnippetNode** table;  // Hash table array
    int capacity;
    int size;

    // Core operations
    void insert(string tag, SnippetData snippet);    // O(1)
    SnippetData* search(string tag);                  // O(1)
    void remove(string tag);                          // O(1)
    int hash(string key);                             // Hash function
};
```

#### Key Features:

- **Collision Handling:** Chain-based collision resolution
- **Dynamic Resizing:** Automatically expands when load factor exceeds threshold
- **Search Complexity:** O(1) average, O(n) worst case

#### Integration:

- Stores snippets indexed by primary tag
- Used in `main.cpp` for quick tag lookups
- Feeds data to Trie for prefix search
- Provides base data for recommendation engine

---

### 2. **Trie (Module 1)**

#### What It Is:

A tree-based data structure for efficient string searching with O(k) complexity where k = string length.

#### Where It's Used:

```
📁 cpp/module1/
├── trie.cpp
├── trie.h
└── main.cpp (uses Trie)
```

#### Why It's Used:

- **Prefix Search:** Auto-complete and partial tag matching
- **Lexicographic Ordering:** Natural tag ordering
- **Space Sharing:** Common prefixes share memory

#### How It Works:

```cpp
// From trie.h/cpp
struct TrieNode {
    TrieNode* children[26];  // For a-z characters
    vector<string> tags;     // Tags at this node
    bool isEnd;
};

class Trie {
    TrieNode* root;

    void insert(string tag);                          // O(k) where k = tag length
    vector<string> search(string prefix);             // O(k + m) k=prefix, m=results
    void suggestTags(string prefix);
};
```

#### Key Features:

- **Prefix Matching:** Finds all tags starting with given prefix
- **Auto-complete:** Suggests tags while user types
- **No Hash Collisions:** Deterministic, no collision handling needed

#### Integration:

- Works in tandem with HashMap
- Takes data from HashMap: `hashmap → trie`
- Enables search-as-you-type functionality in UI
- Returns tag suggestions to RecommendationPanelPro

---

### 3. **Graph (Tag Relationship Graph) - Module 3**

#### What It Is:

An undirected graph representing relationships between tags. Tags are nodes, edges represent co-occurrence or similarity.

#### Where It's Used:

```
📁 cpp/module3/
├── graph.cpp
├── graph.h
└── recommendations.cpp (uses Graph)
```

#### Why It's Used:

- **Relationship Discovery:** Find related tags and snippets
- **Recommendation Engine:** Core structure for suggesting snippets
- **Pattern Recognition:** Identify commonly co-used tags
- **Knowledge Graph:** Represent semantic relationships between concepts

#### How It Works:

**Graph Structure:**

```cpp
// From graph.h/cpp
struct TagNode {
    string tag;
    map<string, double> adjacentTags;  // Tag -> Weight (co-occurrence)
};

class TagGraph {
    map<string, TagNode> nodes;        // All tags

    // Core operations
    void addTag(string tag);                          // O(1)
    void addRelation(string tag1, string tag2, double weight); // O(log n)
    vector<string> findRelated(string tag, int maxResults);    // BFS/DFS
};
```

**Graph Algorithms Used:**

1. **Breadth-First Search (BFS)** for finding related tags:

```cpp
// Pseudo-code from graph.cpp
vector<string> findRelated(string startTag) {
    queue<string> q;
    set<string> visited;
    vector<string> results;

    q.push(startTag);
    visited.insert(startTag);

    while (!q.empty()) {
        string current = q.front();
        q.pop();
        results.push_back(current);

        // Explore neighbors
        for (auto neighbor : graph[current].adjacentTags) {
            if (visited.find(neighbor) == visited.end()) {
                visited.insert(neighbor);
                q.push(neighbor);
            }
        }
    }
    return results;  // Returns related tags in BFS order
}
```

2. **Weighted Edge Traversal** for ranking related tags:

```cpp
// Edges have weights based on co-occurrence frequency
// Higher weight = stronger relationship
// Used for sorting results by relevance
```

#### Key Features:

- **Undirected Edges:** Relationships are bidirectional
- **Weighted Edges:** Strength of relationship represented by weight
- **Dynamic Addition:** Can add tags and relationships at runtime
- **Multi-source Discovery:** Find all tags connected to a source

#### Integration:

- Built from snippet data during initialization
- Takes input from Module 1 (all tags)
- Provides related tags to RecommendationEngine
- Enables "fuzzy" search when exact tag not found

---

### 4. **Recommendation Engine - Module 3**

#### What It Is:

A hybrid algorithm combining graph traversal, scoring functions, and ranking to provide intelligent snippet recommendations.

#### Where It's Used:

```
📁 cpp/module3/
├── recommendations.cpp
├── recommendations.h
└── main.cpp (uses RecommendationEngine)
```

#### Why It's Used:

- **Multi-Criteria Ranking:** Combines multiple factors for better recommendations
- **Intelligent Matching:** Finds snippets even when exact tag doesn't exist
- **User-Relevant Results:** Scores based on usage patterns, recency, and tag relationships
- **Scalability:** Efficient even with large snippet databases

#### Algorithms Used:

**1. Exact Tag Matching with Scoring:**

```cpp
// From recommendations.cpp
vector<Snippet> getRecommendationsByTag(string tag, int limit) {
    vector<Snippet> results;
    // Direct lookup in data structure
    vector<Snippet> matches = findSnippetsByTag(tag);

    // Score each match
    for (Snippet& snippet : matches) {
        double score = calculateScore(snippet);  // Complex scoring
        results.push_back({snippet, score});
    }

    // Sort by score descending
    sort(results.begin(), results.end(),
         [](const Snippet& a, const Snippet& b) {
             return a.score > b.score;
         });

    return vector(results.begin(), results.begin() + limit);
}
```

**2. Graph-Based Tag Recommendation (Fuzzy Search):**

```cpp
// When exact tag not found
vector<string> relatedTags = tagGraph.findRelated(searchTag, limit * 3);

vector<Snippet> fuzzyResults;
for (string relTag : relatedTags) {
    vector<Snippet> snippets = findSnippetsByTag(relTag);
    for (Snippet& s : snippets) {
        s.score *= relatednessFactor;  // Adjust for indirectness
        fuzzyResults.push_back(s);
    }
}
```

**3. Scoring Function (Multi-Factor):**

```cpp
// Combined scoring formula
double calculateScore(Snippet& snippet) {
    double score = 0.0;

    // Factor 1: Base relevance (0.4 weight)
    score += 0.4 * getTagRelevance(snippet.tags);

    // Factor 2: Usage count (0.3 weight)
    score += 0.3 * getNormalizedUsage(snippet.usageCount);

    // Factor 3: Recency decay (0.2 weight)
    score += 0.2 * calculateTimeDecay(snippet.lastModified);

    // Factor 4: Language match bonus (0.1 weight)
    score += 0.1 * getLanguageMatchBonus(snippet.language);

    return score;  // Final score 0.0 - 1.0
}
```

**4. Levenshtein Distance (String Similarity):**

```cpp
// In RecommendationPanelPro.java
private int levenshteinDistance(String s1, String s2) {
    int[][] dp = new int[s1.length() + 1][s2.length() + 1];

    // Initialize base cases
    for (int i = 0; i <= s1.length(); i++)
        dp[i][0] = i;
    for (int j = 0; j <= s2.length(); j++)
        dp[0][j] = j;

    // Fill DP table
    for (int i = 1; i <= s1.length(); i++) {
        for (int j = 1; j <= s2.length(); j++) {
            if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1];  // No operation
            } else {
                dp[i][j] = 1 + Math.min(
                    Math.min(dp[i - 1][j],     // Delete
                             dp[i][j - 1]),    // Insert
                    dp[i - 1][j - 1]           // Replace
                );
            }
        }
    }

    return dp[s1.length()][s2.length()];
}

// Used to match typos: "seach" → "search"
// Distance threshold = 2 characters for fuzzy matching
```

#### Key Features:

- **Multi-Criteria Scoring:** Combines 4+ factors
- **Graph-Based Fallback:** Works when exact match fails
- **Efficient Retrieval:** O(n log n) due to sorting
- **Language-Aware:** Can filter by programming language

#### Integration:

- Receives data from Graph (related tags)
- Receives data from HashMap (snippets)
- Returns ranked results to Java frontend
- Enables both exact and fuzzy search

---

## Architecture & Integration

### System Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                  JAVA FRONTEND (Swing)                  │
│  ┌──────────────────────────────────────────────────┐   │
│  │  RecommendationPanelPro.java                     │   │
│  │  - UI Components                                 │   │
│  │  - User Input Handling                           │   │
│  │  - Display Results                               │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────┐   │
│  │  AnalyticsDashboardPro.java                      │   │
│  │  - Metrics Visualization                         │   │
│  │  - Statistics Display                            │   │
│  └──────────────────────────────────────────────────┘   │
└────────────┬──────────────────────────────────────────┬──┘
             │ ProcessBuilder                           │
             │ Command-line invocation                 │
             ↓                                           ↓
┌─────────────────────────────────────────────────────────┐
│              C++ BACKEND (Data Processing)               │
│                                                          │
│  ┌──────────────────────────────────────────────────┐   │
│  │  Module 1 (Search & Indexing)                    │   │
│  │  ├── HashMap: Fast O(1) tag lookup               │   │
│  │  └── Trie: Prefix-based search                   │   │
│  └──────────────────────────────────────────────────┘   │
│                                                          │
│  ┌──────────────────────────────────────────────────┐   │
│  │  Module 3 (Recommendations)                      │   │
│  │  ├── TagGraph: Tag relationships                 │   │
│  │  └── RecommendationEngine: Intelligent ranking   │   │
│  └──────────────────────────────────────────────────┘   │
└────────────┬──────────────────────────────────────────┬──┘
             │ Standard I/O (stdout)                    │
             │ Delimited text format                   │
             ↓                                           ↓
┌─────────────────────────────────────────────────────────┐
│          DATA FILES (JSON format, disk storage)          │
│                                                          │
│  Data/snippets.json                                     │
│  - Raw snippet definitions                              │
│  - Structure: [{id, title, code, tags, language}]       │
│                                                          │
│  Data/input.txt                                         │
│  - Query input from C++ program                         │
│                                                          │
│  Data/output.txt                                        │
│  - Processing results                                   │
└─────────────────────────────────────────────────────────┘
```

---

## Module-by-Module Analysis

### Module 1: Search & Indexing System

**Location:** `cpp/module1/`

**Files:**

- `main.cpp` - Entry point, orchestration
- `hashmap.cpp/h` - HashMap implementation
- `trie.cpp/h` - Trie implementation
- `json.hpp` - JSON parsing library

**DSA Used:**
| DSA | File | Purpose |
|-----|------|---------|
| HashMap | hashmap.cpp/h | O(1) snippet storage by tag |
| Trie | trie.cpp/h | O(k) prefix-based search |

**Data Flow in Module 1:**

```
JSON File (snippets.json)
        ↓
   Parse JSON
        ↓
   For each snippet:
   ├── Extract tags
   │   └── Insert into HashMap (tag → snippet)
   │   └── Insert into Trie (each tag character)
   └── Build inverted index
        ↓
   In-Memory Data Structures Ready
   (HashMap + Trie populated)
        ↓
   Query Processing:
   ├── Exact tag match → HashMap lookup (O(1))
   └── Prefix search → Trie search (O(k))
```

**Algorithm Execution:**

1. **HashMap Operations:**

```cpp
// Initialization
HashMap tagIndex(capacity);

// Insertion
for (each snippet) {
    for (each tag in snippet.tags) {
        tagIndex.insert(tag, snippet);  // O(1) average
    }
}

// Lookup (when searching)
SnippetData* result = tagIndex.search(userTag);  // O(1)
```

2. **Trie Operations:**

```cpp
// Building
Trie tagTrie;
for (each unique tag) {
    tagTrie.insert(tag);  // O(k) where k = tag length
}

// Searching (auto-complete)
vector<string> suggestions = tagTrie.search("se");  // Returns "search", "security", etc.
```

---

### Module 3: Recommendation Engine

**Location:** `cpp/module3/`

**Files:**

- `main.cpp` - Entry point, query handler
- `graph.cpp/h` - Tag relationship graph
- `recommendations.cpp/h` - Scoring and ranking
- `json.hpp` - JSON parsing
- `MakeFile` - Build configuration

**DSA Used:**
| DSA | File | Algorithm | Purpose |
|-----|------|-----------|---------|
| Graph | graph.cpp/h | BFS/DFS | Tag relationship discovery |
| Recommendation Engine | recommendations.cpp/h | Weighted scoring + sorting | Snippet ranking |
| Levenshtein Distance | recommendations.cpp/h | Dynamic Programming | Fuzzy string matching |

**Data Flow in Module 3:**

```
JSON File (snippets.json)
        ↓
   Parse JSON
        ↓
   Build Graph:
   For each snippet pair:
   ├── Extract tags
   ├── If tags share snippet
   └── Add/increase edge weight
        ↓
   Graph Built (nodes=tags, edges=relationships)
        ↓
   User Query: "search for tag X"
        ├─→ Exact Match?
        │   ├─ YES → Get snippets for tag X
        │   └─→ Score & Rank (scoring function)
        │
        └─→ Fuzzy Match?
            ├─ Find related tags (BFS on graph)
            ├─ Get snippets for related tags
            ├─ Reduce score for indirectness
            └─→ Combine & Rank results
                ↓
            Return top-K snippets (sorted by score)
```

**Graph Algorithm (BFS for Related Tags):**

```cpp
// From graph.cpp
vector<pair<string, double>> findRelated(string tag, int maxResults) {
    if (nodes.find(tag) == nodes.end()) {
        return {};  // Tag not found
    }

    queue<pair<string, int>> q;         // (tag, distance)
    map<string, int> visited;           // tag → distance
    vector<pair<string, double>> results;

    q.push({tag, 0});
    visited[tag] = 0;

    while (!q.empty() && results.size() < maxResults) {
        auto [current, dist] = q.front();
        q.pop();

        // Get adjacent tags with weights
        for (auto& [neighbor, weight] : nodes[current].adjacentTags) {
            if (visited.find(neighbor) == visited.end()) {
                visited[neighbor] = dist + 1;
                q.push({neighbor, dist + 1});

                // Weight decreases with distance
                double score = weight / (1.0 + dist);
                results.push_back({neighbor, score});
            }
        }
    }

    // Sort by score descending
    sort(results.begin(), results.end(),
         [](const auto& a, const auto& b) { return a.second > b.second; });

    return results;
}
```

**Scoring Algorithm:**

```cpp
// From recommendations.cpp
double scoreSnippet(const Snippet& snippet, const string& queryTag) {
    double score = 0.0;

    // 1. Tag Match Relevance (40%)
    double tagRelevance = 0.0;
    int matchCount = 0;
    for (string& tag : snippet.tags) {
        if (tag == queryTag) matchCount++;
    }
    tagRelevance = min(1.0, (double)matchCount / snippet.tags.size());
    score += 0.4 * tagRelevance;

    // 2. Usage Frequency (30%)
    double usageScore = min(1.0, (double)snippet.usageCount / maxUsage);
    score += 0.3 * usageScore;

    // 3. Recency Bonus (20%)
    double timeDecay = calculateTimeDecay(snippet.lastModified);
    score += 0.2 * timeDecay;

    // 4. Language Match (10%)
    double langBonus = 0.0;
    if (snippet.language == userLanguageFilter) {
        langBonus = 1.0;
    }
    score += 0.1 * langBonus;

    return score;  // Returns 0.0 to 1.0
}
```

---

## Data Flow Diagram

### Complete Data Journey: User Query → Results

```
┌─────────────────────────────────────┐
│  User Types Tag in Java UI          │
│  Example: "search"                  │
└────────────┬────────────────────────┘
             │
             ↓
┌─────────────────────────────────────┐
│  RecommendationPanelPro.java        │
│  - getRecommendations()             │
│  - Creates ProcessBuilder           │
│  - Launches: cpp/module3/app.exe    │
│    with args: "rec_tag" "search" 10 │
└────────────┬────────────────────────┘
             │
             ↓ Standard I/O Stream
┌─────────────────────────────────────┐
│  C++ Backend (module3/main.cpp)     │
│  Receives: tag="search", limit=10   │
└────────────┬────────────────────────┘
             │
             ├────────────────────────────────┐
             ↓                                 ↓
     ┌──────────────────┐         ┌──────────────────┐
     │  Exact Match?    │         │  Load JSON File  │
     │  HashMap lookup  │         │  snippets.json   │
     │  O(1)            │         │                  │
     └──────────────────┘         └──────────────────┘
             │                             │
          YES│                             │
             ├─────────────────┬──────────┘
             ↓                 │
     ┌──────────────────┐     │
     │  Found snippets  │     NO
     │  with tag        │     │
     │  "search"        │     ↓
     └──────────────────┘  ┌──────────────────────┐
             │             │  Build Tag Graph     │
             │             │  From all snippets   │
             │             │  (BFS algorithm)     │
             │             └──────────────────────┘
             │                     │
             │                     ↓
             │             ┌──────────────────────┐
             │             │  Find Related Tags   │
             │             │  "search", "query",  │
             │             │  "lookup"            │
             │             └──────────────────────┘
             │                     │
             │                     ↓
             │             ┌──────────────────────┐
             │             │  Get snippets for    │
             │             │  all related tags    │
             │             │  (with lower score)  │
             │             └──────────────────────┘
             │                     │
             └─────────────┬───────┘
                          ↓
             ┌─────────────────────────────┐
             │  Score All Snippets         │
             │  Using scoring function:    │
             │  - Tag relevance (40%)      │
             │  - Usage count (30%)        │
             │  - Recency (20%)            │
             │  - Language match (10%)     │
             └────────────┬────────────────┘
                          ↓
             ┌─────────────────────────────┐
             │  Sort by Score (O(n log n)) │
             │  Take top 10 results        │
             └────────────┬────────────────┘
                          ↓
             ┌─────────────────────────────┐
             │  Format Output              │
             │  RECOMMENDATIONS_START      │
             │  id|title|score|tags|lang|code
             │  ...                        │
             │  RECOMMENDATIONS_END        │
             └────────────┬────────────────┘
                          │
                          ↓ stdout stream
                          │
┌─────────────────────────────────────┐
│  Java Frontend                      │
│  Reads output from process          │
│  Parses delimited text              │
│  Creates RecommendationItem objects │
└────────────┬────────────────────────┘
             │
             ↓
┌─────────────────────────────────────┐
│  Update UI                          │
│  Display recommendation cards       │
│  - Rank #1, title, tags, score      │
│  - Tags in scrollable area          │
│  - Language badge                   │
└─────────────────────────────────────┘
```

---

## Complete Integration Guide

### 1. Frontend-Backend Communication

#### Communication Method: Process-Based (ProcessBuilder)

**Why Process-Based?**

- Language Separation: Java and C++ can work independently
- Memory Isolation: No memory conflicts
- Platform Compatibility: Works across OS
- Easy Testing: Each component testable in isolation

#### Communication Protocol:

**Java → C++ (Input):**

```
ProcessBuilder pb = new ProcessBuilder(
    "cpp/module3/app.exe",     // Executable
    "rec_tag",                  // Command type
    "search",                   // Tag to search
    "10"                        // Result limit
);
pb.directory(dataDirectory);    // Working directory
Process process = pb.start();
```

**Command Types:**

```
1. rec_tag <tag> <limit>
   Purpose: Get recommendations for specific tag
   Example: rec_tag search 10
   Returns: Top 10 snippets for "search" tag

2. top_snippets <limit>
   Purpose: Get most used/trending snippets
   Example: top_snippets 15
   Returns: Top 15 trending snippets

3. tag_suggestions <prefix>
   Purpose: Get tag autocomplete suggestions
   Example: tag_suggestions se
   Returns: Tags starting with "se"
```

**C++ → Java (Output):**

```
Standard Output Format:

For Recommendations:
RECOMMENDATIONS_START
id|title|score|tags|language|code
snippet1|quicksearch|0.92|search;quick;algorithm|Java|code...
snippet2|binarysearch|0.87|search;binary;efficient|C++|code...
RECOMMENDATIONS_END

For Top Snippets:
TOP_SNIPPETS_START
id|title|score|tags|language|code
...
TOP_SNIPPETS_END
```

#### Data Parsing in Java:

```java
// From RecommendationPanelPro.java
private List<RecommendationItem> searchTag(String tag, int limit, String language) throws Exception {
    List<RecommendationItem> recommendations = new ArrayList<>();
    ProcessBuilder pb = new ProcessBuilder(
        getExecutablePath(),
        "rec_tag",
        tag,
        String.valueOf(limit)
    );

    pb.directory(getDataDirectory());
    pb.redirectErrorStream(true);
    Process process = pb.start();

    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream()))) {
        String line;
        boolean capturing = false;

        while ((line = reader.readLine()) != null) {
            if (line.equals("RECOMMENDATIONS_START")) {
                capturing = true;  // Start parsing
            } else if (line.equals("RECOMMENDATIONS_END")) {
                capturing = false; // Stop parsing
            } else if (capturing) {
                // Parse delimiter-separated values
                String[] parts = line.split("\\|", 6);
                if (parts.length >= 5) {
                    RecommendationItem item = new RecommendationItem(
                        parts[0],                      // id
                        parts[1],                      // title
                        Double.parseDouble(parts[2]),  // score
                        parts[3],                      // tags
                        parts[4],                      // language
                        parts.length > 5 ? parts[5] : "" // code
                    );

                    // Apply language filter
                    if ("All".equals(language) || language.equals(item.language)) {
                        recommendations.add(item);
                    }
                }
            }
        }
    }

    process.waitFor();
    return recommendations;
}
```

---

### 2. Data File Integration

#### JSON Data Structure:

**File:** `Data/snippets.json`

```json
[
  {
    "id": "001",
    "title": "Binary Search Algorithm",
    "code": "function binarySearch(arr, target) {\n  // implementation\n}",
    "tags": "search;binary;algorithm;efficient",
    "language": "JavaScript",
    "usageCount": 245,
    "lastModified": "2025-12-20"
  },
  {
    "id": "002",
    "title": "Quick Sort",
    "code": "void quickSort(vector<int>& arr) {\n  // implementation\n}",
    "tags": "sort;quick;divide-and-conquer;algorithm",
    "language": "C++",
    "usageCount": 189,
    "lastModified": "2025-12-18"
  }
]
```

#### JSON Parsing Flow (C++ Backend):

```cpp
// Module 3: main.cpp
#include "json.hpp"
using json = nlohmann::json;

int main(int argc, char* argv[]) {
    // Read JSON file
    ifstream file("Data/snippets.json");
    json data = json::parse(file);

    // Parse each snippet
    vector<Snippet> snippets;
    for (const auto& item : data) {
        Snippet s;
        s.id = item["id"];
        s.title = item["title"];
        s.code = item["code"];
        s.tags = splitTags(item["tags"]);  // "tag1;tag2" → ["tag1", "tag2"]
        s.language = item["language"];
        s.usageCount = item["usageCount"];
        s.lastModified = item["lastModified"];

        snippets.push_back(s);
    }

    // Build data structures
    buildHashMap(snippets);
    buildTagGraph(snippets);
    buildTrie(snippets);

    // Process query
    string command = argv[1];
    if (command == "rec_tag") {
        string tag = argv[2];
        int limit = stoi(argv[3]);
        getRecommendations(tag, limit);
    }
}
```

---

### 3. Complete Request-Response Cycle

#### Scenario: User searches for tag "algorithm"

**Step 1: User Input**

```
User types "algorithm" in search box
Selects language: "All"
Selects result limit: 10
Clicks "Get Recommendations"
```

**Step 2: Java Frontend Processing**

```java
// RecommendationPanelPro.java::getRecommendations()
String tag = "algorithm";
int limit = 10;
String language = "All";

// Disable button to prevent multiple queries
recommendBtn.setEnabled(false);
statusLabel.setText("🔄 Searching...");

// Launch C++ backend in separate thread
new Thread(() -> {
    try {
        List<RecommendationItem> recommendations = searchTag(tag, limit, language);
        SwingUtilities.invokeLater(() -> {
            displayRecommendations(recommendations, tag, language);
        });
    } catch (Exception e) {
        statusLabel.setText("✗ Error: " + e.getMessage());
    }
}).start();
```

**Step 3: C++ Backend Processing**

```
Input: command "rec_tag", tag "algorithm", limit 10

[Module 3 Backend]
1. Load JSON: Data/snippets.json
2. Look up "algorithm" in HashMap
3. Found snippets:
   - Binary Search (score: 0.95)
   - Quick Sort (score: 0.92)
   - Merge Sort (score: 0.88)
   - ... (more results)

4. Score each snippet using scoring function:
   - Relevance of "algorithm" tag: 40%
   - Usage count ranking: 30%
   - Recency bonus: 20%
   - Language match: 10%

5. Sort by score (descending)
6. Return top 10 as formatted output
```

**Step 4: Output Formatting**

```
RECOMMENDATIONS_START
001|Binary Search|0.95|search;binary;algorithm|JavaScript|function...
002|Quick Sort|0.92|sort;quick;algorithm|C++|void quickSort...
...
RECOMMENDATIONS_END
```

**Step 5: Java Frontend Parsing**

```java
// Read process output line by line
// Parse pipe-delimited values
// Create RecommendationItem objects
// Filter by language if needed
// Return List<RecommendationItem>
```

**Step 6: UI Display**

```
Display cards in scrollable panel:

#1 Binary Search | Score: 0.95
   Tags: 🏷️ search
         🏷️ binary
         🏷️ algorithm
   Language: JavaScript

#2 Quick Sort | Score: 0.92
   Tags: 🏷️ sort
         🏷️ quick
         🏷️ algorithm
   Language: C++

... (more cards with scrolling)
```

**Step 7: User Interaction**

```
User clicks on card → displayCodeView(item)
Code area shows full code with syntax
User clicks "Copy Code" → copies to clipboard
```

---

## Why Specific DSAs Were Chosen

### HashMap ✅ **USED**

- **Why:** Need O(1) average-case lookup for tags
- **Alternative:** Binary Search Tree (O(log n)) - too slow
- **Not used:** Hash Set - need to store snippets, not just existence check

### Trie ✅ **USED**

- **Why:** Prefix-based search and autocomplete
- **Alternative:** HashMap + filtering - requires scanning all tags
- **Not used:** Suffix Tree - overkill for this use case

### Graph ✅ **USED**

- **Why:** Model tag relationships and find related tags
- **Alternative:** Adjacency Matrix - wastes space with sparse graph
- **Not used:** Shortest Path (Dijkstra) - not needed (find neighbors, not shortest path)

### Recommendation Engine ✅ **USED**

- **Why:** Multi-criteria ranking provides better UX
- **Algorithms:**
  - BFS: Find related tags efficiently
  - Sorting: O(n log n) for final ranking
  - Levenshtein: Handle typos in search
- **Alternative:** Simple frequency count - ignores recency and usage patterns

### **NOT USED** and Why:

| DSA                 | Why Not Used                              |
| ------------------- | ----------------------------------------- |
| Binary Search Tree  | HashMap is faster (O(1) vs O(log n))      |
| Heap/Priority Queue | Already sorting with std::sort            |
| Dynamic Programming | No overlapping subproblems in this domain |
| Segment Tree        | No range queries needed                   |
| Union-Find          | No connectivity questions                 |
| Fenwick Tree        | No cumulative frequency queries           |
| Suffix Array        | Trie is simpler and sufficient            |
| AVL Tree            | HashMap better for this use case          |

---

## DSA Complexity Analysis

### Module 1 Operations:

| Operation       | HashMap  | Trie                  | Complexity | Use Case               |
| --------------- | -------- | --------------------- | ---------- | ---------------------- |
| Insert          | O(1) avg | O(k)                  | Fast       | Loading data from JSON |
| Search (exact)  | O(1) avg | O(k)                  | Very Fast  | Direct tag lookup      |
| Search (prefix) | O(n)     | O(k+m)                | Fast       | Autocomplete           |
| Delete          | O(1) avg | O(k)                  | Fast       | Removing snippets      |
| Space           | O(n)     | O(ALPHABET_SIZE \* k) | Linear     | In-memory storage      |

### Module 3 Operations:

| Operation            | Complexity | Time           | Use Case            |
| -------------------- | ---------- | -------------- | ------------------- |
| Build Graph          | O(n²)      | Initialization | One-time at startup |
| BFS (find related)   | O(V + E)   | ~1-5ms         | Fuzzy search        |
| Score snippets       | O(n log n) | ~10-50ms       | Ranking results     |
| Total recommendation | O(n log n) | ~50-200ms      | Complete query      |

---

## File Structure Reference

```
e:\DSA\smart-code-snippet-manager\
│
├── cpp/module1/              # Search & Indexing
│   ├── main.cpp              # Entry point
│   ├── hashmap.cpp/h         # HashMap implementation
│   ├── trie.cpp/h            # Trie implementation
│   ├── json.hpp              # JSON library
│   └── app.exe               # Compiled executable
│
├── cpp/module3/              # Recommendation Engine
│   ├── main.cpp              # Entry point, query processing
│   ├── graph.cpp/h           # Tag graph with BFS
│   ├── recommendations.cpp/h # Scoring & ranking algorithms
│   ├── json.hpp              # JSON library
│   ├── MakeFile              # Build configuration
│   └── app.exe               # Compiled executable
│
├── java/src/
│   └── com/snippetmanager/
│       ├── SmartCodeMain.java              # Main entry point
│       ├── module1/
│       │   └── SnippetManagerPanel.java    # Module 1 UI
│       └── module3/
│           ├── RecommendationPanelPro.java # Main recommendation UI
│           ├── AnalyticsDashboardPro.java  # Analytics display
│           ├── MetricsDashboard.java       # Metrics visualization
│           └── TagVisualization.java       # Tag cloud visualization
│
├── Data/
│   ├── snippets.json         # Main data file (JSON format)
│   ├── snippets_large.json   # Large dataset for testing
│   ├── input.txt             # Query input (if used)
│   └── output.txt            # Query output (if used)
│
├── compile.bat               # Batch compilation script
├── run.bat                   # Run script
└── DSA_AND_INTEGRATION_DOCUMENTATION.md  # This file
```

---

## Summary Table: All DSAs at a Glance

| #   | DSA/Algorithm        | Module | File                        | Status | Purpose        | Time        | Space   |
| --- | -------------------- | ------ | --------------------------- | ------ | -------------- | ----------- | ------- |
| 1   | HashMap              | 1      | hashmap.cpp/h               | ✅     | Tag indexing   | O(1)        | O(n)    |
| 2   | Trie                 | 1      | trie.cpp/h                  | ✅     | Prefix search  | O(k)        | O(k\*n) |
| 3   | Graph (Tag Graph)    | 3      | graph.cpp/h                 | ✅     | Relationships  | O(1) lookup | O(V+E)  |
| 4   | BFS                  | 3      | graph.cpp                   | ✅     | Related tags   | O(V+E)      | O(V)    |
| 5   | Scoring Function     | 3      | recommendations.cpp         | ✅     | Ranking        | O(n)        | O(n)    |
| 6   | Sorting              | 3      | recommendations.cpp         | ✅     | Result order   | O(n log n)  | O(1)    |
| 7   | Levenshtein Distance | Java   | RecommendationPanelPro.java | ✅     | Fuzzy matching | O(m\*n)     | O(m\*n) |
| 8   | Inverted Index       | 1      | Implicit in HashMap         | ✅     | Reverse lookup | O(1)        | O(n)    |
| 9   | Time Decay Function  | 3      | recommendations.cpp         | ✅     | Recency bonus  | O(1)        | O(1)    |

---

## Conclusion

The Smart Code Snippet Manager uses a **carefully selected combination of DSAs** tailored to the problem:

1. **HashMap + Trie** provide fast search capabilities
2. **Graph** models tag relationships for discovering related content
3. **Multi-criteria scoring** ensures relevant recommendations
4. **ProcessBuilder** enables clean separation between Java UI and C++ backend
5. **JSON storage** provides persistent, human-readable data format

This architecture balances **performance, maintainability, and scalability** while avoiding unnecessary complexity from unused data structures.

---

## Quick Reference for Developers

### Adding a New Feature:

1. Identify if it's search-related (Module 1) or recommendation-related (Module 3)
2. Implement in C++, expose via command-line interface
3. Call from Java using ProcessBuilder
4. Parse output and update UI

### Modifying Scoring Function:

- File: `cpp/module3/recommendations.cpp::calculateScore()`
- Adjust weights: tag relevance (40%), usage (30%), recency (20%), language (10%)
- Recompile: `g++ -o app.exe main.cpp graph.cpp recommendations.cpp`

### Adding New Tags or Snippets:

- Update: `Data/snippets.json`
- No code changes needed
- Restart application for reindexing

### Performance Optimization:

- HashMap collision: Check load factor, adjust capacity
- Graph edges: Use lazy loading for large graphs
- Scoring: Cache results for repeated queries

---

**Document Version:** 1.0  
**Last Updated:** December 30, 2025  
**Project:** Smart Code Snippet Manager - Unified Edition
