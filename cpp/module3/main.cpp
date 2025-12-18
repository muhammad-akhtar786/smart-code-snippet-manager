#include "recommendations.h"
#include <iostream>
#include <fstream>
#include <cmath>

using namespace std;

void printHelp()
{
    cout << "Smart Code Snippet Manager - Module 3: Recommendations & Analytics" << endl;
    cout << "Usage: ./module3 <command> [args]" << endl;
    cout << "Commands:" << endl;
    cout << "  load <filename>                    - Load snippets from CSV" << endl;
    cout << "  rec_tag <tag> [limit]              - Get recommendations by tag" << endl;
    cout << "  rec_snippet <id> [limit]           - Get recommendations by snippet" << endl;
    cout << "  top_snippets [n]                   - Get top used snippets" << endl;
    cout << "  trending_tags [n]                  - Get trending tags" << endl;
    cout << "  lang_dist                          - Get language distribution" << endl;
    cout << "  tag_cooccur <tag>                  - Get tag co-occurrence" << endl;
    cout << "  stats                              - Get system statistics" << endl;
    cout << "  export <tag> <output_file>         - Export recommendations to file" << endl;
}

int main(int argc, char *argv[])
{
    if (argc < 2)
    {
        printHelp();
        return 1;
    }

    RecommendationEngine engine;
    string command = argv[1];

    if (command == "load")
    {
        if (argc < 3)
        {
            cerr << "Error: Missing filename" << endl;
            return 1;
        }

        string filename = argv[2];
        if (engine.loadSnippetsFromFile(filename))
        {
            cout << "SUCCESS: Loaded snippets from " << filename << endl;
        }
        else
        {
            cout << "ERROR: Failed to load snippets" << endl;
            return 1;
        }
    }
    else if (command == "rec_tag")
    {
        if (argc < 4)
        {
            cerr << "Error: Missing tag and data file" << endl;
            return 1;
        }

        // First load data
        if (!engine.loadSnippetsFromFile(argv[3]))
        {
            cerr << "ERROR: Failed to load data" << endl;
            return 1;
        }

        string tag = argv[2];
        int limit = argc >= 5 ? atoi(argv[4]) : 10;

        auto recommendations = engine.getRecommendationsByTag(tag, limit);

        cout << "RECOMMENDATIONS_START" << endl;
        for (const auto &p : recommendations)
        {
            string snippetId = p.first;
            double score = p.second;
            auto snippet = engine.getSnippet(snippetId);
            cout << snippetId << "|" << snippet.title << "|" << score << "|";
            for (size_t i = 0; i < snippet.tags.size(); i++)
            {
                cout << snippet.tags[i];
                if (i < snippet.tags.size() - 1)
                    cout << ";";
            }
            cout << "|" << snippet.language << endl;
        }
        cout << "RECOMMENDATIONS_END" << endl;
    }
    else if (command == "rec_snippet")
    {
        if (argc < 4)
        {
            cerr << "Error: Missing snippet ID and data file" << endl;
            return 1;
        }

        if (!engine.loadSnippetsFromFile(argv[3]))
        {
            cerr << "ERROR: Failed to load data" << endl;
            return 1;
        }

        string snippetId = argv[2];
        int limit = argc >= 5 ? atoi(argv[4]) : 10;

        auto recommendations = engine.getRecommendationsBySnippet(snippetId, limit);

        cout << "RECOMMENDATIONS_START" << endl;
        for (const auto &p : recommendations)
        {
            string id = p.first;
            double score = p.second;
            auto snippet = engine.getSnippet(id);
            cout << id << "|" << snippet.title << "|" << score << "|";
            for (size_t i = 0; i < snippet.tags.size(); i++)
            {
                cout << snippet.tags[i];
                if (i < snippet.tags.size() - 1)
                    cout << ";";
            }
            cout << "|" << snippet.language << endl;
        }
        cout << "RECOMMENDATIONS_END" << endl;
    }
    else if (command == "top_snippets")
    {
        if (argc < 3)
        {
            cerr << "Error: Missing data file" << endl;
            return 1;
        }

        if (!engine.loadSnippetsFromFile(argv[2]))
        {
            cerr << "ERROR: Failed to load data" << endl;
            return 1;
        }

        int n = argc >= 4 ? atoi(argv[3]) : 10;
        auto topSnippets = engine.getTopUsedSnippets(n);

        cout << "TOP_SNIPPETS_START" << endl;
        for (const auto &p : topSnippets)
        {
            string id = p.first;
            int count = p.second;
            auto snippet = engine.getSnippet(id);
            cout << id << "|" << snippet.title << "|" << count << "|" << snippet.language << endl;
        }
        cout << "TOP_SNIPPETS_END" << endl;
    }
    else if (command == "trending_tags")
    {
        if (argc < 3)
        {
            cerr << "Error: Missing data file" << endl;
            return 1;
        }

        if (!engine.loadSnippetsFromFile(argv[2]))
        {
            cerr << "ERROR: Failed to load data" << endl;
            return 1;
        }

        int n = argc >= 4 ? atoi(argv[3]) : 10;
        auto trendingTags = engine.getTrendingTags(n);

        cout << "TRENDING_TAGS_START" << endl;
        for (const auto &p : trendingTags)
        {
            string tag = p.first;
            int freq = p.second;
            cout << tag << "|" << freq << endl;
        }
        cout << "TRENDING_TAGS_END" << endl;
    }
    else if (command == "lang_dist")
    {
        if (argc < 3)
        {
            cerr << "Error: Missing data file" << endl;
            return 1;
        }

        if (!engine.loadSnippetsFromFile(argv[2]))
        {
            cerr << "ERROR: Failed to load data" << endl;
            return 1;
        }

        auto langDist = engine.getLanguageDistribution();

        cout << "LANG_DIST_START" << endl;
        for (const auto &p : langDist)
        {
            string lang = p.first;
            int count = p.second;
            cout << lang << "|" << count << endl;
        }
        cout << "LANG_DIST_END" << endl;
    }
    else if (command == "tag_cooccur")
    {
        if (argc < 4)
        {
            cerr << "Error: Missing tag and data file" << endl;
            return 1;
        }

        if (!engine.loadSnippetsFromFile(argv[3]))
        {
            cerr << "ERROR: Failed to load data" << endl;
            return 1;
        }

        string tag = argv[2];
        auto cooccur = engine.getTagCooccurrence(tag);

        cout << "TAG_COOCCUR_START" << endl;
        for (const auto &p : cooccur)
        {
            string t = p.first;
            int freq = p.second;
            cout << t << "|" << freq << endl;
        }
        cout << "TAG_COOCCUR_END" << endl;
    }
    else if (command == "top_snippets_lang")
    {
        if (argc < 4)
        {
            cerr << "Error: Missing language and data file" << endl;
            return 1;
        }

        if (!engine.loadSnippetsFromFile(argv[3]))
        {
            cerr << "ERROR: Failed to load data" << endl;
            return 1;
        }

        string language = argv[2];
        int n = argc >= 5 ? atoi(argv[4]) : 10;
        auto topSnippets = engine.getTopSnippetsByLanguage(language, n);

        cout << "TOP_SNIPPETS_LANG_START" << endl;
        for (const auto &p : topSnippets)
        {
            string id = p.first;
            int count = p.second;
            auto snippet = engine.getSnippet(id);
            cout << id << "|" << snippet.title << "|" << count << "|" << snippet.language << endl;
        }
        cout << "TOP_SNIPPETS_LANG_END" << endl;
    }
    else if (command == "trending_tags_lang")
    {
        if (argc < 4)
        {
            cerr << "Error: Missing language and data file" << endl;
            return 1;
        }

        if (!engine.loadSnippetsFromFile(argv[3]))
        {
            cerr << "ERROR: Failed to load data" << endl;
            return 1;
        }

        string language = argv[2];
        int n = argc >= 5 ? atoi(argv[4]) : 10;
        auto trendingTags = engine.getTrendingTagsByLanguage(language, n);

        cout << "TRENDING_TAGS_LANG_START" << endl;
        for (const auto &p : trendingTags)
        {
            cout << p.first << "|" << p.second << endl;
        }
        cout << "TRENDING_TAGS_LANG_END" << endl;
    }
    else if (command == "trending_snippets")
    {
        if (argc < 3)
        {
            cerr << "Error: Missing data file" << endl;
            return 1;
        }

        if (!engine.loadSnippetsFromFile(argv[2]))
        {
            cerr << "ERROR: Failed to load data" << endl;
            return 1;
        }

        int n = argc >= 4 ? atoi(argv[3]) : 10;
        auto trendingSnippets = engine.getTrendingSnippets(n);

        cout << "TRENDING_SNIPPETS_START" << endl;
        for (const auto &p : trendingSnippets)
        {
            auto snippet = engine.getSnippet(p.first);
            cout << p.first << "|" << snippet.title << "|" << p.second << "|" << snippet.language << endl;
        }
        cout << "TRENDING_SNIPPETS_END" << endl;
    }
    else if (command == "metrics")
    {
        if (argc < 3)
        {
            cerr << "Error: Missing data file" << endl;
            return 1;
        }

        if (!engine.loadSnippetsFromFile(argv[2]))
        {
            cerr << "ERROR: Failed to load data" << endl;
            return 1;
        }

        cout << engine.getRecommendationMetrics();
    }
    else if (command == "stats")
    {
        if (argc < 3)
        {
            cerr << "Error: Missing data file" << endl;
            return 1;
        }

        if (!engine.loadSnippetsFromFile(argv[2]))
        {
            cerr << "ERROR: Failed to load data" << endl;
            return 1;
        }

        cout << "STATS_START" << endl;
        cout << "total_tags|" << engine.getTotalTags() << endl;
        cout << "total_snippets|" << engine.getTotalSnippets() << endl;
        cout << "STATS_END" << endl;
    }
    else if (command == "export")
    {
        if (argc < 5)
        {
            cerr << "Error: Missing arguments" << endl;
            return 1;
        }

        if (!engine.loadSnippetsFromFile(argv[4]))
        {
            cerr << "ERROR: Failed to load data" << endl;
            return 1;
        }

        string tag = argv[2];
        string outputFile = argv[3];

        if (engine.exportRecommendations(tag, outputFile))
        {
            cout << "SUCCESS: Exported recommendations to " << outputFile << endl;
        }
        else
        {
            cout << "ERROR: Failed to export recommendations" << endl;
            return 1;
        }
    }
    else
    {
        cerr << "Error: Unknown command '" << command << "'" << endl;
        printHelp();
        return 1;
    }

    return 0;
}