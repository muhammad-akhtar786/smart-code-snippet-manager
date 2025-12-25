#include "recommendations.h"
#include <fstream>
#include <sstream>
#include <algorithm>
#include <iostream>
#include <cmath>

bool RecommendationEngine::loadSnippetsFromFile(const string &filename)
{
    // Load snippets from JSON file (key-value format)
    ifstream file(filename);
    if (!file.is_open())
    {
        cerr << "Error opening file: " << filename << endl;
        return false;
    }

    string jsonContent;
    string line;
    while (getline(file, line))
    {
        jsonContent += line + "\n";
    }
    file.close();

    // Parse key-value pairs from JSON
    int snippetCount = 0;
    size_t pos = jsonContent.find("{");
    if (pos == string::npos)
        return false;

    pos++;

    while (pos < jsonContent.length())
    {
        // Find next quote (key start)
        pos = jsonContent.find("\"", pos);
        if (pos == string::npos || jsonContent[pos] == '}')
            break;

        pos++;
        size_t keyEnd = jsonContent.find("\"", pos);
        if (keyEnd == string::npos)
            break;

        string key = jsonContent.substr(pos, keyEnd - pos);
        pos = keyEnd + 1;

        // Skip to colon
        pos = jsonContent.find(":", pos);
        if (pos == string::npos)
            break;
        pos++;

        // Skip whitespace
        while (pos < jsonContent.length() && (jsonContent[pos] == ' ' || jsonContent[pos] == '\t' || jsonContent[pos] == '\n'))
            pos++;

        // Should find opening quote for value
        if (pos >= jsonContent.length() || jsonContent[pos] != '"')
            break;

        pos++; // skip opening quote

        // Find closing quote (handle escaped quotes)
        size_t valueStart = pos;
        while (pos < jsonContent.length())
        {
            if (jsonContent[pos] == '\\' && pos + 1 < jsonContent.length())
            {
                pos += 2;
            }
            else if (jsonContent[pos] == '"')
            {
                break;
            }
            else
            {
                pos++;
            }
        }

        if (pos >= jsonContent.length())
            break;

        string value = jsonContent.substr(valueStart, pos - valueStart);
        pos++;

        // Create snippet
        SnippetMetadata snippet;
        snippet.id = key;
        snippet.title = key;
        snippet.language = "C++";
        snippet.code = value; // Store the actual code
        snippet.usageCount = 0;
        snippet.lastModified = "2024-12-25";

        // Auto-generate tags based on key
        string normalizedTitle = toLowercase(key);
        snippet.tags.push_back(normalizedTitle);

        // Add category tags
        if (normalizedTitle.find("sort") != string::npos)
            snippet.tags.push_back("sorting");
        if (normalizedTitle.find("search") != string::npos)
            snippet.tags.push_back("search");
        if (normalizedTitle.find("tree") != string::npos)
            snippet.tags.push_back("tree");
        if (normalizedTitle.find("graph") != string::npos || normalizedTitle.find("dfs") != string::npos || normalizedTitle.find("bfs") != string::npos)
            snippet.tags.push_back("graph");
        if (normalizedTitle.find("knapsack") != string::npos || normalizedTitle.find("fibonacci") != string::npos ||
            normalizedTitle.find("lcs") != string::npos || normalizedTitle.find("edit") != string::npos)
            snippet.tags.push_back("dynamic programming");
        if (normalizedTitle.find("gcd") != string::npos || normalizedTitle.find("lcm") != string::npos ||
            normalizedTitle.find("factorial") != string::npos)
            snippet.tags.push_back("mathematics");
        if (normalizedTitle.find("permutation") != string::npos || normalizedTitle.find("combination") != string::npos ||
            normalizedTitle.find("queens") != string::npos)
            snippet.tags.push_back("backtracking");
        if (normalizedTitle.find("traversal") != string::npos || normalizedTitle.find("inorder") != string::npos ||
            normalizedTitle.find("preorder") != string::npos || normalizedTitle.find("postorder") != string::npos)
            snippet.tags.push_back("traversal");
        if (normalizedTitle.find("sum") != string::npos || normalizedTitle.find("two") != string::npos)
            snippet.tags.push_back("array");

        snippet.tags.push_back("algorithm");
        snippet.tags.push_back("dsa");

        addSnippet(snippet);
        snippetCount++;
    }

    cout << "Successfully loaded " << snippetCount << " snippets from " << filename << endl;
    return true;
}

void RecommendationEngine::addSnippet(const SnippetMetadata &snippet)
{
    snippetStore[snippet.id] = snippet;

    // Update language distribution
    languageDistribution[snippet.language]++;

    // Build tag graph
    for (const string &tag : snippet.tags)
    {
        tagGraph.addTag(tag);
        tagGraph.addSnippetToTag(tag, snippet.id);
        tagGraph.incrementTagFrequency(tag);
    }

    // Create edges between co-occurring tags
    for (size_t i = 0; i < snippet.tags.size(); i++)
    {
        for (size_t j = i + 1; j < snippet.tags.size(); j++)
        {
            tagGraph.addEdge(snippet.tags[i], snippet.tags[j]);
        }
    }
}

vector<pair<string, double>> RecommendationEngine::getRecommendationsByTag(const string &tag, int limit)
{
    vector<pair<string, double>> recommendations;

    // Convert tag to lowercase for case-insensitive search
    string normalizedTag = toLowercase(tag);

    // FIRST: Try exact match
    auto relatedTags = tagGraph.findRelatedTags(normalizedTag, limit * 2);

    // SECOND: If no exact match, find tags containing the search term (substring match)
    if (relatedTags.empty())
    {
        // Find all tags that CONTAIN the search term
        vector<pair<string, int>> matchingTags;
        vector<string> allTags = tagGraph.getAllTags();

        for (const string &graphTag : allTags)
        {
            if (graphTag.find(normalizedTag) != string::npos ||
                normalizedTag.find(graphTag) != string::npos) // Also match if search term contains tag
            {
                int freq = tagGraph.getTagFrequency(graphTag);
                matchingTags.push_back({graphTag, freq});
            }
        }

        // Sort by frequency
        sort(matchingTags.begin(), matchingTags.end(),
             [](const pair<string, int> &a, const pair<string, int> &b)
             {
                 return a.second > b.second;
             });

        // Convert to double scores
        if (!matchingTags.empty())
        {
            for (const auto &p : matchingTags)
            {
                relatedTags.push_back({p.first, (double)p.second});
            }
        }
    }

    // Get snippets for related tags
    unordered_map<string, double> snippetScores;

    for (const auto &p : relatedTags)
    {
        string relatedTag = p.first;
        double score = p.second;
        auto snippets = tagGraph.getSnippetsForTag(relatedTag);

        for (const string &snippetId : snippets)
        {
            auto it = snippetStore.find(snippetId);
            if (it != snippetStore.end())
            {
                // FIXED: Prioritize TAG RELEVANCE (80%) over usage count (20%)
                // This ensures accurate tag-based recommendations
                double tagRelevanceScore = score * 0.8;                   // Primary: Tag matching (80%)
                double usageBonus = log(it->second.usageCount + 1) * 0.2; // Secondary: Usage (20%)
                snippetScores[snippetId] += tagRelevanceScore + usageBonus;
            }
        }
    }

    // Convert to vector and sort
    for (const auto &pair : snippetScores)
    {
        recommendations.push_back(pair);
    }

    sort(recommendations.begin(), recommendations.end(),
         [](const pair<string, double> &a, const pair<string, double> &b)
         {
             return a.second > b.second;
         });

    if (recommendations.size() > limit)
    {
        recommendations.resize(limit);
    }

    return recommendations;
}

vector<pair<string, double>> RecommendationEngine::getRecommendationsBySnippet(const string &snippetId, int limit)
{
    auto it = snippetStore.find(snippetId);
    if (it == snippetStore.end())
    {
        return vector<pair<string, double>>();
    }

    // Get all tags from this snippet
    const auto &tags = it->second.tags;

    // Aggregate recommendations from all tags
    unordered_map<string, double> snippetScores;

    for (const string &tag : tags)
    {
        auto tagRecs = getRecommendationsByTag(tag, limit);

        for (const auto &p : tagRecs)
        {
            string recId = p.first;
            double score = p.second;
            if (recId != snippetId)
            { // Don't recommend itself
                snippetScores[recId] += score;
            }
        }
    }

    // Convert and sort
    vector<pair<string, double>> recommendations;
    for (const auto &pair : snippetScores)
    {
        recommendations.push_back(pair);
    }

    sort(recommendations.begin(), recommendations.end(),
         [](const pair<string, double> &a, const pair<string, double> &b)
         {
             return a.second > b.second;
         });

    if (recommendations.size() > limit)
    {
        recommendations.resize(limit);
    }

    return recommendations;
}

SnippetMetadata RecommendationEngine::getSnippet(const string &id) const
{
    auto it = snippetStore.find(id);
    if (it != snippetStore.end())
    {
        return it->second;
    }
    return SnippetMetadata();
}

vector<pair<string, int>> RecommendationEngine::getTopUsedSnippets(int n)
{
    vector<pair<string, int>> snippets;

    for (const auto &pair : snippetStore)
    {
        snippets.push_back({pair.first, pair.second.usageCount});
    }

    // Heap-based sorting
    sort(snippets.begin(), snippets.end(),
         [](const pair<string, int> &a, const pair<string, int> &b)
         {
             return a.second > b.second;
         });

    if (snippets.size() > n)
    {
        snippets.resize(n);
    }

    return snippets;
}

vector<pair<string, int>> RecommendationEngine::getTrendingTags(int n)
{
    return tagGraph.getTopTags(n);
}

vector<pair<string, int>> RecommendationEngine::getLanguageDistribution()
{
    vector<pair<string, int>> distribution;

    for (const auto &pair : languageDistribution)
    {
        distribution.push_back(pair);
    }

    sort(distribution.begin(), distribution.end(),
         [](const pair<string, int> &a, const pair<string, int> &b)
         {
             return a.second > b.second;
         });

    return distribution;
}

void RecommendationEngine::updateSnippetUsage(const string &snippetId)
{
    auto it = snippetStore.find(snippetId);
    if (it != snippetStore.end())
    {
        it->second.usageCount++;

        // Update recent activity
        recentActivity.push_back({snippetId, it->second.usageCount});
        if (recentActivity.size() > 100)
        {
            recentActivity.erase(recentActivity.begin());
        }
    }
}

vector<pair<string, int>> RecommendationEngine::getTagCooccurrence(const string &tag)
{
    vector<pair<string, int>> cooccurrence;
    auto neighbors = tagGraph.getNeighbors(tag);

    for (const string &neighbor : neighbors)
    {
        int freq = tagGraph.getTagFrequency(neighbor);
        cooccurrence.push_back(make_pair(neighbor, freq));
    }

    sort(cooccurrence.begin(), cooccurrence.end(),
         [](const pair<string, int> &a, const pair<string, int> &b)
         {
             return a.second > b.second;
         });

    return cooccurrence;
}

bool RecommendationEngine::exportRecommendations(const string &tag, const string &outputFile)
{
    ofstream file(outputFile);
    if (!file.is_open())
    {
        return false;
    }

    auto recommendations = getRecommendationsByTag(tag, 20);

    file << "Tag: " << tag << endl;
    file << "Recommendations:" << endl;
    file << "================" << endl;

    for (const auto &p : recommendations)
    {
        string snippetId = p.first;
        double score = p.second;
        auto snippet = getSnippet(snippetId);
        file << "ID: " << snippet.id << endl;
        file << "Title: " << snippet.title << endl;
        file << "Score: " << score << endl;
        file << "Tags: ";
        for (const string &t : snippet.tags)
        {
            file << t << " ";
        }
        file << endl
             << "---" << endl;
    }

    file.close();
    return true;
}

int RecommendationEngine::getTotalTags() const
{
    return tagGraph.getAllTags().size();
}

int RecommendationEngine::getTotalSnippets() const
{
    return snippetStore.size();
}

void RecommendationEngine::clear()
{
    tagGraph.clear();
    snippetStore.clear();
    languageDistribution.clear();
    recentActivity.clear();
}

// NEW METHODS FOR ENHANCED FEATURES

// Language-specific top snippets
vector<pair<string, int>> RecommendationEngine::getTopSnippetsByLanguage(const string &language, int n)
{
    vector<pair<string, int>> snippets;

    for (const auto &p : snippetStore)
    {
        if (p.second.language == language)
        {
            snippets.push_back(make_pair(p.first, p.second.usageCount));
        }
    }

    sort(snippets.begin(), snippets.end(),
         [](const pair<string, int> &a, const pair<string, int> &b)
         {
             return a.second > b.second;
         });

    if (snippets.size() > n)
    {
        snippets.resize(n);
    }

    return snippets;
}

// Language-specific trending tags
vector<pair<string, int>> RecommendationEngine::getTrendingTagsByLanguage(const string &language, int n)
{
    unordered_map<string, int> tagFreq;

    for (const auto &p : snippetStore)
    {
        if (p.second.language == language)
        {
            for (const string &tag : p.second.tags)
            {
                tagFreq[tag]++;
            }
        }
    }

    vector<pair<string, int>> result;
    for (const auto &p : tagFreq)
    {
        result.push_back(make_pair(p.first, p.second));
    }

    sort(result.begin(), result.end(),
         [](const pair<string, int> &a, const pair<string, int> &b)
         {
             return a.second > b.second;
         });

    if (result.size() > n)
    {
        result.resize(n);
    }

    return result;
}

// Time-decay scoring for trending snippets
double calculateTimeDecay(const string &lastModified)
{
    // Simple calculation: newer = higher score
    // In production, parse actual date
    return 1.0; // Placeholder
}

vector<pair<string, double>> RecommendationEngine::getTrendingSnippets(int n)
{
    vector<pair<string, double>> trendingSnippets;

    for (const auto &p : snippetStore)
    {
        double usageScore = log(p.second.usageCount + 1);
        double timeDecay = calculateTimeDecay(p.second.lastModified);
        double trendScore = usageScore * timeDecay;

        trendingSnippets.push_back(make_pair(p.first, trendScore));
    }

    sort(trendingSnippets.begin(), trendingSnippets.end(),
         [](const pair<string, double> &a, const pair<string, double> &b)
         {
             return a.second > b.second;
         });

    if (trendingSnippets.size() > n)
    {
        trendingSnippets.resize(n);
    }

    return trendingSnippets;
}

// Track recommendation metrics
void RecommendationEngine::trackRecommendation(bool clicked)
{
    metrics.totalRecommendations++;
    if (clicked)
    {
        metrics.clickedRecommendations++;
    }
    metrics.accuracy = (metrics.totalRecommendations > 0) ? (double)metrics.clickedRecommendations / metrics.totalRecommendations : 0.0;
}

// Get recommendation metrics as formatted string
string RecommendationEngine::getRecommendationMetrics()
{
    double coverage = (totalSnippets > 0) ? (double)(snippetStore.size()) / totalSnippets * 100 : 0.0;

    string result = "METRICS_START\n";
    result += "total_recommendations|" + to_string(metrics.totalRecommendations) + "\n";
    result += "clicked_count|" + to_string(metrics.clickedRecommendations) + "\n";

    char buffer[50];
    sprintf(buffer, "%.2f", metrics.accuracy * 100);
    result += "accuracy|" + string(buffer) + "%\n";

    sprintf(buffer, "%.2f", coverage);
    result += "coverage|" + string(buffer) + "%\n";

    result += "METRICS_END\n";
    return result;
}