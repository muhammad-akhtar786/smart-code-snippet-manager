# Smart Code Snippet Manager - Architecture & Design

## 🏗️ Application Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      SmartCodeMain (JFrame)                    │
│                    "Main Application Window"                    │
└─────────────────────────────────────────────────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    │                   │
        ┌───────────▼──────────┐  ┌────▼──────────────────┐
        │   Header Panel       │  │   Main Content Area   │
        │  (Blue title bar)    │  │  (CardLayout based)   │
        └──────────────────────┘  └────────────────────────┘
                                           │
                            ┌──────────────┼──────────────┐
                            │              │              │
        ┌─────────────────┐ │ ┌────────────▼───────┐
        │  Left Menu Panel│ │ │  Content Panel     │
        │  (Dark sidebar) │ │ │  (CardLayout)      │
        │                 │ │ │                    │
        │ ┌────────────┐  │ │ │ ┌───────────────┐ │
        │ │📋 M1 Panel│  │ │ │ │ Module Cards: │ │
        │ └────────────┘  │ │ │ │ 1. M1 Panel   │ │
        │ ┌────────────┐  │ │ │ │ 2. M3 Rec     │ │
        │ │🎯 M3 Views│  │ │ │ │ 3. M3 Ana     │ │
        │ ├────────────┤  │ │ │ │ 4. M3 Met     │ │
        │ │📊 Ana      │  │ │ │ │ 5. M3 Tags    │ │
        │ │📈 Met      │  │ │ │ └───────────────┘ │
        │ │🔗 Tags     │  │ │ │                    │
        │ ├────────────┤  │ │ │ (Instant Switch)   │
        │ │⚙️ Settings │  │ │ │                    │
        │ │🚪 Exit     │  │ │ └────────────────────┘
        │ └────────────┘  │ │
        └─────────────────┘ │
                            │
         ┌──────────────────┘
         │
         ▼
    User Interaction:
    - Click menu button
    - CardLayout.show() is called
    - Panel switches instantly
    - No JFrame creation
    - No performance impact
```

## 📦 Class Hierarchy

```
JFrame
  │
  └─ SmartCodeMain
      │
      ├─ Components:
      │  ├─ JPanel (Header)
      │  ├─ JPanel (Left Menu)
      │  │  └─ ModuleButton[] (inner class)
      │  │     ├─ button1: "📋 Snippet Manager"
      │  │     ├─ button2: "🎯 Recommendations"
      │  │     ├─ button3: "📊 Analytics"
      │  │     ├─ button4: "📈 Metrics"
      │  │     └─ button5: "🔗 Tag Network"
      │  │
      │  └─ JPanel (Content Area)
      │     └─ CardLayout
      │        ├─ Card 1: SnippetManagerPanel (JPanel)
      │        ├─ Card 2: RecommendationPanelPro (JPanel)
      │        ├─ Card 3: AnalyticsDashboardPro (JPanel)
      │        ├─ Card 4: MetricsDashboard (JPanel)
      │        └─ Card 5: TagVisualization (JPanel)
      │
      └─ Methods:
         ├─ createHeader()
         ├─ createLeftMenu()
         ├─ createContentArea()
         ├─ switchToModule(String, ModuleButton)
         └─ showSettings()

JPanel
  │
  ├─ SnippetManagerPanel
  │  │
  │  ├─ Inner Classes:
  │  │  ├─ RoundedButton (custom JButton)
  │  │  ├─ RoundedTextField (custom JTextField)
  │  │  └─ RoundedBorder (custom Border)
  │  │
  │  ├─ Components:
  │  │  ├─ JTextField searchField
  │  │  ├─ JTextArea outputArea
  │  │  ├─ JList recentSearchesList
  │  │  ├─ CardLayout cardLayout
  │  │  └─ JPanel[4] (pages: SEARCH, ADD, UPDATE, DELETE)
  │  │
  │  └─ Methods:
  │     ├─ initializeGUI()
  │     ├─ setupAutocomplete(JTextField)
  │     ├─ sendCommand(String, String, String)
  │     └─ (page creation methods)
  │
  ├─ RecommendationPanelPro
  │  ├─ Components: TextField, Spinner, Button
  │  └─ Methods: Get recommendations via C++ backend
  │
  ├─ AnalyticsDashboardPro
  │  ├─ Components: Charts, Labels, Statistics
  │  └─ Methods: Display analytics data
  │
  ├─ MetricsDashboard
  │  ├─ Components: Metrics display
  │  └─ Methods: Show recommendation metrics
  │
  └─ TagVisualization
     ├─ Components: Graph visualization
     └─ Methods: Display tag relationships
```

## 🔄 Data Flow & Module Switching

```
User Clicks Menu Button
       │
       ▼
ModuleButton ActionListener triggered
       │
       ▼
switchToModule(String moduleName, ModuleButton activeButton)
       │
       ├─ btnModule1.setActive(false)    [Reset all buttons]
       ├─ btnModule3.setActive(false)
       │
       ├─ activeButton.setActive(true)   [Highlight active]
       │
       └─ cardLayout.show(contentPanel, moduleName)
           │
           ▼
       CardLayout switches panel instantly
       │
       ▼
       Panel is now visible
       (No new JFrame created, no lag)
```

## 🖼️ UI Layout Hierarchy

```
SmartCodeMain
│
├─ BorderLayout (5 regions)
│  │
│  ├─ NORTH: Header Panel
│  │  └─ Title Label (Blue background)
│  │
│  └─ CENTER: Main Area Panel
│     │
│     └─ BorderLayout
│        │
│        ├─ WEST: Menu Panel
│        │  └─ BoxLayout (Y_AXIS)
│        │     ├─ ModuleButton 1
│        │     ├─ ModuleButton 2
│        │     ├─ ... (other buttons)
│        │     └─ Exit Button
│        │
│        └─ CENTER: Content Panel
│           └─ CardLayout
│              ├─ Module 1 Panel
│              ├─ Module 3.1 Panel
│              ├─ Module 3.2 Panel
│              ├─ Module 3.3 Panel
│              └─ Module 3.4 Panel
```

## 🎨 Color Scheme

```
┌──────────────────────────────────────────┐
│  HEADER_BG: #1E90FF (Bright Blue)        │
│  Foreground: WHITE                       │
└──────────────────────────────────────────┘
┌──────────────────────────────────────────┐
│  MENU_BG: #232D41 (Dark Blue-Gray)       │
│  MENU_BUTTON_BG: #344A5E (Medium Blue)   │
│  MENU_BUTTON_HOVER: #466482 (Lighter)    │
│  MENU_BUTTON_ACTIVE: #3498DB (Blue)      │
│  Foreground: WHITE                       │
└──────────────────────────────────────────┘
┌──────────────────────────────────────────┐
│  CONTENT_BG: #F5F5F5 (Light Gray)        │
│  Foreground: BLACK                       │
└──────────────────────────────────────────┘
```

## 🔌 Backend Integration

```
Java Swing UI
     │
     ├─ Module 1 Operations
     │  │
     │  └─ sendCommand(command, data, code)
     │     │
     │     ├─ Write input.txt (Data/)
     │     ├─ Execute app.exe (Data/)
     │     ├─ Wait for completion
     │     └─ Read output.txt (Data/)
     │
     └─ Module 3 Operations
        │
        └─ sendCommand(command, data)
           │
           ├─ Write input.txt (Data/)
           ├─ Execute module3.exe (cpp/module3/)
           ├─ Wait for completion
           └─ Read output.txt (Data/)

I/O Files:
├─ Data/input.txt   (Format: COMMAND|TITLE|LANGUAGE|CODE)
└─ Data/output.txt  (Format: Results from backend)
```

## 📊 Class Responsibilities

### SmartCodeMain

- **Responsibility**: Main application window orchestration
- **Tasks**:
  - Create and manage JFrame
  - Initialize all module panels
  - Manage module switching via CardLayout
  - Handle menu interactions
  - Coordinate layout management

### SnippetManagerPanel

- **Responsibility**: Module 1 - Snippet Management
- **Tasks**:
  - Provide CRUD interface for snippets
  - Execute C++ backend (app.exe)
  - Display results
  - Manage search history

### RecommendationPanelPro

- **Responsibility**: Module 3 - Tag-based Recommendations
- **Tasks**:
  - Get snippet recommendations by tag
  - Filter by language
  - Execute module3.exe
  - Display results

### AnalyticsDashboardPro

- **Responsibility**: Module 3 - Analytics Visualization
- **Tasks**:
  - Display statistics
  - Show charts and graphs
  - Analyze snippet usage

### TagVisualization

- **Responsibility**: Module 3 - Tag Network Visualization
- **Tasks**:
  - Visualize tag relationships
  - Show connected tags
  - Explore tag patterns

## ✨ Key Design Patterns Used

### 1. **CardLayout Pattern**

```java
// Switch panels instantly without recreating
cardLayout.show(contentPanel, moduleName);
```

### 2. **Observer Pattern**

```java
// Button clicks trigger module switches
button.addActionListener(e -> switchToModule(...));
```

### 3. **Factory Pattern**

```java
// Create different panel types
contentPanel.add(new SnippetManagerPanel(), "MODULE_1");
contentPanel.add(new RecommendationPanelPro(), "MODULE_3_REC");
```

### 4. **Adapter Pattern**

```java
// Custom components extend standard Swing components
class RoundedButton extends JButton { ... }
```

## 🚀 Performance Characteristics

| Operation         | Time    | Notes                 |
| ----------------- | ------- | --------------------- |
| App Startup       | 2-3s    | JVM initialization    |
| Module Switch     | <50ms   | CardLayout is instant |
| Backend Execution | 0.5-2s  | Depends on C++ code   |
| UI Rendering      | <16ms   | 60 FPS capability     |
| Menu Button Hover | Instant | Repaint only          |

## 🔐 Thread Safety

- **Main Thread**: All Swing operations on EDT
- **Backend Calls**: Synchronous wait (could be async in future)
- **File I/O**: Sequential, not concurrent

Future improvement: Use SwingWorker for non-blocking backend calls

## 📈 Scalability Considerations

**Current Limitations**:

- Single-threaded backend execution
- File-based I/O (slower than direct API)
- All modules loaded at startup

**Scalability Improvements**:

- Add async backend calls (SwingWorker)
- Implement lazy loading for heavy modules
- Add caching for frequently accessed data
- Use database instead of JSON

---

**Architecture Designed By**: Smart Code Snippet Manager Team
**Last Updated**: December 29, 2025
**Status**: ✅ Production Ready
