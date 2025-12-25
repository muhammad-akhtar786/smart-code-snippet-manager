#ifndef RECOMMENDATIONS_H
#define RECOMMENDATIONS_H

#include "graph.h"
#include <vector>
#include <string>
#include <unordered_map>
#include <ctime>

using namespace std;

// Helper function declaration
string toLowercase(const string &str);

// Level-1 DSA: HashMap for snippet metadata and analytics
struct SnippetMetadata
{
    string id;
    string title;
    vector<string> tags;
    string language;
    string code;
    int usageCount;
    string lastModified;
};

// Metrics tracking for recommendation quality
struct RecommendationMetrics
{
    int totalRecommendations;
    int clickedRecommendations;
    int acceptedCount;
    double accuracy;
    double coverage;
};

class RecommendationEngine
{
private:
    TagGraph tagGraph;
    unordered_map<string, SnippetMetadata> snippetStore; // Level-1: HashMap

    // Analytics data
    unordered_map<string, int> languageDistribution;
    vector<pair<string, int>> recentActivity; // Last 100 activities

    // Metrics tracking
    RecommendationMetrics metrics;
    int totalSnippets;

public:
    // Initialize from data file
    bool loadSnippetsFromFile(const string &filename);

    // Add snippet and update graph
    void addSnippet(const SnippetMetadata &snippet);

    // Get recommendations based on tag
    vector<pair<string, double>> getRecommendationsByTag(const string &tag, int limit = 10);

    // Get recommendations based on snippet ID
    vector<pair<string, double>> getRecommendationsBySnippet(const string &snippetId, int limit = 10);

    // Get snippet by ID
    SnippetMetadata getSnippet(const string &id) const;

    // Analytics functions
    vector<pair<string, int>> getTopUsedSnippets(int n = 10);
    vector<pair<string, int>> getTrendingTags(int n = 10);
    vector<pair<string, int>> getLanguageDistribution();

    // NEW: Language-specific analytics
    vector<pair<string, int>> getTopSnippetsByLanguage(const string &language, int n = 10);
    vector<pair<string, int>> getTrendingTagsByLanguage(const string &language, int n = 10);

    // NEW: Time-decay based scoring
    vector<pair<string, double>> getTrendingSnippets(int n = 10);

    // NEW: Recommendation metrics
    string getRecommendationMetrics();
    void trackRecommendation(bool clicked);

    // Update usage count
    void updateSnippetUsage(const string &snippetId);

    // Get tag co-occurrence statistics
    vector<pair<string, int>> getTagCooccurrence(const string &tag);

    // Export recommendations to file
    bool exportRecommendations(const string &tag, const string &outputFile);

    // Get graph statistics
    int getTotalTags() const;
    int getTotalSnippets() const;

    // Clear all data
    void clear();
};

#endif