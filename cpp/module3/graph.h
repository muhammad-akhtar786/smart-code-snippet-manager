#ifndef GRAPH_H
#define GRAPH_H

#include <iostream>
#include <vector>
#include <unordered_map>
#include <unordered_set>
#include <queue>
#include <algorithm>
#include <string>
#include <cctype>
#include <algorithm>

using namespace std;

// Helper function for case-insensitive string conversion
inline string toLowercase(const string &str)
{
    string result = str;
    transform(result.begin(), result.end(), result.begin(), ::tolower);
    return result;
}

// Level-2 DSA: Graph for tag relationships
class TagGraph
{
private:
    // Adjacency list representation
    unordered_map<string, unordered_set<string>> adjList;

    // Store tag weights/frequencies
    unordered_map<string, int> tagFrequency;

    // Store snippet IDs associated with each tag
    unordered_map<string, vector<string>> tagToSnippets;

public:
    // Add a tag node
    void addTag(const string &tag);

    // Add edge between two tags (co-occurrence)
    void addEdge(const string &tag1, const string &tag2);

    // Associate snippet with tag
    void addSnippetToTag(const string &tag, const string &snippetId);

    // Increment tag usage frequency
    void incrementTagFrequency(const string &tag);

    // Get all tags
    vector<string> getAllTags() const;

    // Get neighbors of a tag
    vector<string> getNeighbors(const string &tag) const;

    // Get snippets associated with a tag
    vector<string> getSnippetsForTag(const string &tag) const;

    // Get tag frequency
    int getTagFrequency(const string &tag) const;

    // BFS traversal from a starting tag
    vector<string> bfsTraversal(const string &startTag, int maxDepth = 2) const;

    // DFS traversal from a starting tag
    vector<string> dfsTraversal(const string &startTag, int maxDepth = 2) const;

    // Find related tags using BFS (for recommendations)
    vector<pair<string, double>> findRelatedTags(const string &tag, int maxResults = 10) const;

    // Get top N most frequent tags
    vector<pair<string, int>> getTopTags(int n = 10) const;

    // Calculate similarity score between two tags
    double calculateSimilarity(const string &tag1, const string &tag2) const;

    // Clear all data
    void clear();

    // Print graph structure (for debugging)
    void printGraph() const;

private:
    // Helper for DFS
    void dfsHelper(const string &tag, unordered_set<string> &visited,
                   vector<string> &result, int depth, int maxDepth) const;
};

#endif