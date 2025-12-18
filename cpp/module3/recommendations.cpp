#include "recommendations.h"
#include <fstream>
#include <sstream>
#include <algorithm>
#include <iostream>
#include <cmath>

bool RecommendationEngine::loadSnippetsFromFile(const string &filename)
{
    ifstream file(filename);
    if (!file.is_open())
    {
        cerr << "Error: Cannot open file " << filename << endl;
        return false;
    }

    string line;
    getline(file, line); // Skip header

    while (getline(file, line))
    {
        stringstream ss(line);
        SnippetMetadata snippet;

        getline(ss, snippet.id, ',');
        getline(ss, snippet.title, ',');

        string tagsStr;
        getline(ss, tagsStr, ',');

        // Parse tags (assuming semicolon-separated)
        stringstream tagStream(tagsStr);
        string tag;
        while (getline(tagStream, tag, ';'))
        {
            if (!tag.empty())
            {
                // Normalize tag to lowercase for case-insensitive search
                string normalizedTag = toLowercase(tag);
                snippet.tags.push_back(normalizedTag);
            }
        }

        getline(ss, snippet.language, ',');

        string usageStr;
        getline(ss, usageStr, ',');
        snippet.usageCount = usageStr.empty() ? 0 : stoi(usageStr);

        getline(ss, snippet.lastModified, ',');

        addSnippet(snippet);
    }

    file.close();
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

    // Get related tags using BFS/DFS
    auto relatedTags = tagGraph.findRelatedTags(normalizedTag, limit * 2);

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