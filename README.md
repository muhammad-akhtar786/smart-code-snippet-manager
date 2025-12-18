# SmartCode Snippet Manager - Module 3: Recommendations & Analytics (Professional Edition)

## 🎯 Overview

This module provides intelligent recommendations and analytics for code snippets using advanced graph algorithms and data structures. It features a professional GUI with real-time analytics, trending content detection, and intelligent search.

## 📋 Project Structure

```
├── java/src/com/snippetmanager/module3/
│   ├── RecommendationPanelPro.java       (Search & recommendations)
│   ├── AnalyticsDashboardPro.java        (Professional analytics with charts)
│   ├── TagVisualization.java              (Tag relationship network)
│   ├── MetricsDashboard.java              (Quality metrics display)
│   └── Module3MainWindow.java             (Main application window)
│
├── cpp/module3/
│   ├── main.cpp                           (CLI command dispatcher)
│   ├── recommendations.cpp                (Recommendation algorithms)
│   ├── recommendations.h                  (Header with structures)
│   ├── graph.cpp                          (Graph implementation)
│   ├── graph.h                            (Graph header)
│   └── module3.exe                        (Compiled executable)
│
├── Data/
│   └── sample_snippets.csv                (21 code snippets)
│
└── README.md                               (This file)
```

## ✨ Key Features

### 1. 🔍 Smart Recommendations (Case-Insensitive)

- Tag-based search with **case-insensitive matching** (e.g., "Algorithm", "ALGORITHM", "algorithm" all work)
- Graph-based tag relationship discovery
- Multi-factor relevance scoring
- Configurable result limits
- Language filtering (C++, Java, Python, All)

### 2. 📊 Professional Analytics Dashboard

- **4-Panel Display**:
  - 💻 Language distribution bar chart (unicode visualization)
  - ⭐ Top used snippets ranked list
  - 🔥 Trending tags frequency chart
  - 📈 Trending snippets with time-decay scoring
- Real-time data refresh
- Professional color scheme

### 3. 📈 Quality Metrics Dashboard

- Total recommendations given
- Click-through count
- System accuracy percentage
- Coverage percentage

### 4. 🔗 Tag Network Visualization

- Interactive tag relationship graph
- Co-occurrence pattern analysis
- Trending tags display
- Node size based on frequency

## Data Structures & Algorithms

### Level-2 DSA: Graph

**Purpose**: Represent tag relationships and enable intelligent recommendations

**Operations Used**:

- `addTag()` - Add nodes to the graph
- `addEdge()` - Create connections between co-occurring tags
- `bfsTraversal()` - Breadth-first search for related tags
- `dfsTraversal()` - Depth-first search for tag exploration
- `findRelatedTags()` - Smart recommendation algorithm using BFS with scoring

**Performance Benefits**:

- O(V + E) traversal complexity for BFS/DFS
- Efficient neighbor discovery for recommendations
- Natural representation of tag relationships

### Level-1 DSA: HashMap

**Purpose**: Store snippet metadata and enable O(1) lookups

**Operations Used**:

- `insert()` - Add snippet metadata
- `search()` - Find snippets by ID
- `update()` - Modify usage counts
- `iterate()` - Generate analytics

**Performance Benefits**:

- O(1) average case for snippet lookups
- Efficient frequency counting
- Fast analytics aggregation

### Algorithms

#### 1. BFS for Tag Recommendations

```cpp
// Finds related tags within specified depth
vector<string> bfsTraversal(string startTag, int maxDepth)
```

- Used for breadth-first exploration of tag network
- Ensures closest relationships are prioritized
- Configurable depth for recommendation scope

#### 2. Relevance Scoring

```cpp
// Combines multiple factors for ranking
score = depthScore * 0.4 + frequencyScore * 0.3 + similarityScore * 0.3
```

- Multi-factor scoring algorithm
- Balances distance, popularity, and similarity
- Produces ranked recommendations

#### 3. Heap-based Sorting

- Used for sorting top snippets by usage
- Sorting trending tags by frequency
- O(n log n) complexity for analytics

## 🚀 Quick Start

### 1. Compile Java Files

```bash
cd java/src
javac com/snippetmanager/module3/*.java
```

### 2. Run Application

```bash
cd ../..  # Go back to project root
java -cp java/src com.snippetmanager.module3.Module3MainWindow
```

### 3. Using the GUI

- **🎯 Recommendations Tab**: Enter a tag (case-insensitive!) and click "Get Recommendations"
- **📊 Analytics Tab**: View language distribution, top snippets, and trending content
- **📈 Metrics Tab**: See system performance metrics
- **🔗 Tag Network Tab**: Explore tag relationships

## 🔧 C++ Backend Commands

Test directly from command line:

```bash
# Case-insensitive tag search (try "algorithm", "ALGORITHM", "AlGoRiThM")
./cpp/module3/module3.exe rec_tag algorithm Data/sample_snippets.csv 5

# Language-specific top snippets
./cpp/module3/module3.exe top_snippets_lang C++ Data/sample_snippets.csv 5

# Trending snippets with time-decay scoring
./cpp/module3/module3.exe trending_snippets Data/sample_snippets.csv 5

# System metrics
./cpp/module3/module3.exe metrics Data/sample_snippets.csv
```

## 📊 Data Structures & Algorithms

### Level-2 DSA: Graph

- **Purpose**: Model tag relationships for intelligent discovery
- **Operations**: Add nodes/edges, BFS/DFS traversal
- **Complexity**: O(V + E) for traversal
- **Implementation**: Adjacency list

### Level-1 DSA: HashMap

- **Purpose**: Fast O(1) snippet metadata lookups
- **Operations**: Insert, search, iterate
- **Complexity**: O(1) average case
- **Implementation**: Unordered map with string keys

### Key Algorithms

**1. Case-Insensitive Search**

- Normalizes all tags to lowercase during loading and searching
- User can type "Algorithm", "ALGORITHM", or "algorithm" - all work!

**2. BFS Tag Discovery**

- Finds related tags within 3-hop distance
- Combines depth score, frequency score, and similarity
- Formula: `score = depthScore * 0.4 + frequencyScore * 0.3 + similarity * 0.3`

**3. Time-Decay Trending**

- Recently used snippets ranked higher
- Formula: `score = log(usageCount + 1) * timeDecayFactor`
- Balances popularity with recency

**4. Language-Specific Analytics**

- Filters data by programming language
- Enables language-focused insights
- Supports C++, Java, Python

## 🏗️ System Architecture

### Backend (C++ CLI)

- **main.cpp**: Command dispatcher and argument parser
- **recommendations.cpp**: Core algorithms and business logic
- **recommendations.h**: Data structures (Snippet, RecommendationEngine)
- **graph.cpp**: Tag graph implementation
- **graph.h**: Graph interface and toLowercase() helper

### Frontend (Java Swing)

- **Module3MainWindow.java**: Main application window with 4 tabs
- **RecommendationPanelPro.java**: Professional search interface with language filtering
- **AnalyticsDashboardPro.java**: 4-panel analytics with unicode charts
- **TagVisualization.java**: Interactive tag network display
- **MetricsDashboard.java**: Quality metrics cards

### Communication

- **Protocol**: ProcessBuilder with structured text output
- **Format**: Pipe-separated values between START/END markers
- **Encoding**: UTF-8 for unicode chart characters

## 📁 File Organization

**Final Clean Structure**:

```
SmartCode_Snippet_Manager/
├── java/src/com/snippetmanager/module3/
│   ├── RecommendationPanelPro.java       (385 lines)
│   ├── AnalyticsDashboardPro.java        (352 lines)
│   ├── TagVisualization.java              (410 lines)
│   ├── MetricsDashboard.java              (130 lines)
│   └── Module3MainWindow.java             (217 lines)
│
├── cpp/module3/
│   ├── graph.h, graph.cpp                 (309, 7661 bytes)
│   ├── recommendations.h, recommendations.cpp (2715, 12300 bytes)
│   ├── main.cpp                           (11290 bytes)
│   └── module3.exe                        (262 KB - compiled)
│
├── Data/
│   └── sample_snippets.csv                (21 snippets)
│
└── README.md                               (This file)
```

## ✅ Testing Checklist

- [x] C++ compilation with g++ -std=c++14
- [x] Case-insensitive tag search working
- [x] Java GUI launches successfully
- [x] All 4 tabs functional
- [x] ProcessBuilder paths correct for Windows
- [x] Unicode charts rendering
- [x] Language filtering working
- [x] Professional styling applied
- [x] Metrics tracking functional
- [x] Time-decay scoring implemented

### C++ Implementation (Your Work)

- [x] Graph data structure with adjacency list
- [x] BFS/DFS traversal algorithms
- [x] Recommendation engine with scoring
- [x] HashMap for snippet storage
- [x] Analytics functions
- [x] CLI interface

### Java GUI Implementation (Your Work)

- [x] Recommendation panel with input validation
- [x] Analytics dashboard with tables
- [x] Tag visualization canvas
- [x] Integration layer (ProcessBuilder)
- [x] Error handling and status display

### Integration (Your Work)

- [x] Process-based communication
- [x] Structured output parsing
- [x] Data synchronization
- [x] Error propagation

## Known Limitations

1. No persistent graph storage (rebuilt on each load)
2. Limited to 50 recommendation results maximum
3. Visualization limited to 15 connected tags for clarity
4. No real-time collaboration features (yet)

## Future Enhancements

- [ ] ML-based embedding similarity
- [ ] Real-time graph updates
- [ ] Collaborative filtering
- [ ] Advanced visualization options
- [ ] Export to various formats (JSON, XML)

## Troubleshooting

### C++ Binary Not Found

- Ensure you compiled: `cd cpp/module3 && make`
- Check executable permissions: `chmod +x module3`

### Java Can't Find Classes

- Verify CLASSPATH includes `java/src`
- Compile all Java files first

### No Data Displayed

- Check data file path in settings
- Ensure CSV format matches expected structure
- Verify C++ binary can read the file

## Contact

For questions or issues:

- GitHub: [Repository Link]
- Email: [Your Email]
- Course: DSA - Semester 3

## License

Educational project for DSA course - BSCS Semester 3
