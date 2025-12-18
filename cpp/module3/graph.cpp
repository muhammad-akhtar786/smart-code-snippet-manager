#include "graph.h"
#include <cmath>

using namespace std;

// Adds a new tag node to the graph if it does not already exist
void TagGraph::addTag(const string &tag)
{
    if (adjList.find(tag) == adjList.end())
    {
        adjList[tag] = unordered_set<string>();
        tagFrequency[tag] = 0;
    }
}

// Adds an undirected edge between two tags
void TagGraph::addEdge(const string &tag1, const string &tag2)
{
    addTag(tag1);
    addTag(tag2);
    adjList[tag1].insert(tag2);
    adjList[tag2].insert(tag1); // Undirected graph
}

// Associates a snippet ID with a given tag
void TagGraph::addSnippetToTag(const string &tag, const string &snippetId)
{
    addTag(tag);
    tagToSnippets[tag].push_back(snippetId);
}

// Increments the frequency count of a tag
void TagGraph::incrementTagFrequency(const string &tag)
{
    addTag(tag);
    tagFrequency[tag]++;
}

// Returns a list of all tags in the graph
vector<string> TagGraph::getAllTags() const
{
    vector<string> tags;
    for (const auto &pair : adjList)
    {
        tags.push_back(pair.first);
    }
    return tags;
}

// Returns all neighboring tags of a given tag
vector<string> TagGraph::getNeighbors(const string &tag) const
{
    vector<string> neighbors;
    auto it = adjList.find(tag);
    if (it != adjList.end())
    {
        for (const string &neighbor : it->second)
        {
            neighbors.push_back(neighbor);
        }
    }
    return neighbors;
}

// Returns all snippet IDs associated with a tag
vector<string> TagGraph::getSnippetsForTag(const string &tag) const
{
    auto it = tagToSnippets.find(tag);
    if (it != tagToSnippets.end())
    {
        return it->second;
    }
    return vector<string>();
}

// Returns the frequency of a given tag
int TagGraph::getTagFrequency(const string &tag) const
{
    auto it = tagFrequency.find(tag);
    if (it != tagFrequency.end())
    {
        return it->second;
    }
    return 0;
}

// Performs a breadth-first traversal starting from a tag
vector<string> TagGraph::bfsTraversal(const string &startTag, int maxDepth) const
{
    vector<string> result;
    if (adjList.find(startTag) == adjList.end())
    {
        return result;
    }

    unordered_set<string> visited;
    queue<pair<string, int>> q;

    q.push(make_pair(startTag, 0));
    visited.insert(startTag);

    while (!q.empty())
    {
        auto p = q.front();
        q.pop();
        string currentTag = p.first;
        int depth = p.second;

        result.push_back(currentTag);

        if (depth < maxDepth)
        {
            for (const string &neighbor : adjList.at(currentTag))
            {
                if (visited.find(neighbor) == visited.end())
                {
                    visited.insert(neighbor);
                    q.push(make_pair(neighbor, depth + 1));
                }
            }
        }
    }

    return result;
}

// Helper function for depth-first traversal
void TagGraph::dfsHelper(const string &tag, unordered_set<string> &visited,
                         vector<string> &result, int depth, int maxDepth) const
{
    visited.insert(tag);
    result.push_back(tag);

    if (depth < maxDepth)
    {
        auto it = adjList.find(tag);
        if (it != adjList.end())
        {
            for (const string &neighbor : it->second)
            {
                if (visited.find(neighbor) == visited.end())
                {
                    dfsHelper(neighbor, visited, result, depth + 1, maxDepth);
                }
            }
        }
    }
}

// Performs a depth-first traversal starting from a tag
vector<string> TagGraph::dfsTraversal(const string &startTag, int maxDepth) const
{
    vector<string> result;
    if (adjList.find(startTag) == adjList.end())
    {
        return result;
    }

    unordered_set<string> visited;
    dfsHelper(startTag, visited, result, 0, maxDepth);

    return result;
}

// Finds related tags using BFS and weighted scoring
vector<pair<string, double>> TagGraph::findRelatedTags(const string &tag, int maxResults) const
{
    vector<pair<string, double>> relatedTags;

    if (adjList.find(tag) == adjList.end())
    {
        return relatedTags;
    }

    unordered_map<string, double> tagScores;
    unordered_set<string> visited;
    queue<pair<string, int>> q;

    q.push(make_pair(tag, 0));
    visited.insert(tag);

    // FIX: Add the searched tag FIRST with highest priority (score = 100)
    tagScores[tag] = 100.0; // Exact match gets highest score

    while (!q.empty())
    {
        auto p = q.front();
        q.pop();
        string currentTag = p.first;
        int depth = p.second;

        // Only score related tags (not the exact match itself)
        if (currentTag != tag)
        {
            double depthScore = 1.0 / (depth + 1);
            double freqScore = log(tagFrequency.at(currentTag) + 1);
            double similarity = calculateSimilarity(tag, currentTag);

            tagScores[currentTag] = depthScore * 0.4 + freqScore * 0.3 + similarity * 0.3;
        }

        if (depth < 3)
        {
            for (const string &neighbor : adjList.at(currentTag))
            {
                if (visited.find(neighbor) == visited.end())
                {
                    visited.insert(neighbor);
                    q.push({neighbor, depth + 1});
                }
            }
        }
    }

    for (const auto &pair : tagScores)
    {
        relatedTags.push_back(pair);
    }

    sort(relatedTags.begin(), relatedTags.end(),
         [](const pair<string, double> &a, const pair<string, double> &b)
         {
             return a.second > b.second; // Exact match (score=100) will be first
         });

    if (relatedTags.size() > maxResults)
    {
        relatedTags.resize(maxResults);
    }

    return relatedTags;
}

// Returns the top N most frequent tags
vector<pair<string, int>> TagGraph::getTopTags(int n) const
{
    vector<pair<string, int>> tagList;

    for (const auto &pair : tagFrequency)
    {
        tagList.push_back(pair);
    }

    sort(tagList.begin(), tagList.end(),
         [](const pair<string, int> &a, const pair<string, int> &b)
         {
             return a.second > b.second;
         });

    if (tagList.size() > n)
    {
        tagList.resize(n);
    }

    return tagList;
}

// Calculates Jaccard similarity between two tags

double TagGraph::calculateSimilarity(const string &tag1, const string &tag2) const
{
    auto it1 = adjList.find(tag1);
    auto it2 = adjList.find(tag2);

    if (it1 == adjList.end() || it2 == adjList.end())
    {
        return 0.0;
    }

    const auto &neighbors1 = it1->second;
    const auto &neighbors2 = it2->second;

    int common = 0;
    for (const string &neighbor : neighbors1)
    {
        if (neighbors2.find(neighbor) != neighbors2.end())
        {
            common++;
        }
    }

    int total = neighbors1.size() + neighbors2.size() - common;

    if (total == 0)
        return 0.0;

    return static_cast<double>(common) / total;
}

// Clears the entire graph and associated data
void TagGraph::clear()
{
    adjList.clear();
    tagFrequency.clear();
    tagToSnippets.clear();
}

// Prints the graph structure with tag frequencies
void TagGraph::printGraph() const
{
    cout << "Tag Graph Structure:" << endl;
    cout << "===================" << endl;
    for (const auto &pair : adjList)
    {
        cout << pair.first << " (freq: " << tagFrequency.at(pair.first) << ") -> ";
        for (const string &neighbor : pair.second)
        {
            cout << neighbor << " ";
        }
        cout << endl;
    }
}
