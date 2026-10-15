# Manager101 - Management Models Reference App

Manager101 is a comprehensive, offline-first native Android application built with Jetpack Compose and Material 3. It serves as an executive reference guide to 180 management models, leadership frameworks, decision-making techniques, business strategy, quality improvement, project management, finance, and productivity.

## User Review & Critical Decisions

> [!IMPORTANT]
> Based on your preferences, the following decisions are locked in for implementation:
> - **Visual Theme**: Deep Navy and Emerald Green executive palette with high contrast and polished Material 3 components.
> - **Authentication & Feature Scope**: Full library of 180 models across 9 categories, with Firebase Authentication (Google Sign-In) and cloud-backed favorites persistence, plus full offline access.
> - **Illustrations**: Custom Jetpack Compose Canvas vector diagrams and visual charts for every single model detail page.

- **Confirmed Decision 1**: All 180 category entries across 9 categories will be fully implemented with professional definitions, steps, workplace examples, advantages, limitations, key takeaways, and related models.
- **Confirmed Decision 2**: Firebase Authentication + Firestore will be configured for secure Google Sign-In and cloud sync of saved favorites, while the entire model database is bundled locally for instant offline browsing and searching.

---

## 1. Overview & Core Concept

- **What It Does**: Provides structured, concise, and professional reference guides for 180 essential management and strategic frameworks.
- **Target Audience**: Managers, executives, team leads, business analysts, students, and professionals seeking practical frameworks.
- **Key Value**: Instant offline access to actionable business models accompanied by clean vector canvas illustrations and personal favorite bookmarking.

---

## 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Authentication / Welcome**: Secure Google Sign-In (with guest/offline bypass option if desired) welcoming users to Manager101.
  2. **Home Screen**: App branding, quick search bar, 9 category cards with item counts, and featured models.
  3. **Library / Browse**: Comprehensive alphabetical list or category view with real-time search filtering across names, abbreviations, keywords, and definitions.
  4. **Favorites**: Quick access to bookmarked models synced across sessions.
  5. **Model Detail View**: Clean, scrollable reference card containing model metadata, definition, purpose, when to use, step-by-step guide, workplace example, pros/cons, key takeaway, and **a custom Canvas vector illustration** (e.g., Eisenhower 4-quadrant matrix, SWOT diagram, Porter's Five Forces, PDCA cycle, etc.).
  6. **Settings**: Account info, sign out, privacy policy, terms of use, and app version.

- **Visual Identity & Theme**:
  - **Aesthetic Direction**: Executive professional dark-capable palette featuring Deep Navy (`#0A1128` / `#121E36`), Emerald Green accents (`#10B981` / `#059669`), crisp white cards, and slate gray typography.
  - **Color Palette**: M3 ColorScheme with primary Emerald, secondary Navy container, surface tint, and high readability.
  - **Typography**: Clean system sans-serif hierarchy with bold display titles and readable body text.
  - **Component Styling**: M3 Cards with tonal elevation, pill-shaped category chips, interactive ripple feedback, and edge-to-edge layout with proper window insets.

---

## 3. Key Product Decisions & Architecture

- **Data Strategy**:
  - **Local Model Catalog**: Bundled as a robust static Kotlin repository containing all 180 models across 9 categories, ensuring instant zero-latency offline loading.
  - **User Favorites & Sync**: Firestore user subcollection (`users/{uid}/favorites/{modelId}`) for cloud-backed favorites when authenticated, with local Room/DataStore fallback for offline state.
- **Illustration Strategy**:
  - Custom Jetpack Compose `Canvas` drawing functions mapped to model types (Matrices, Cycles, Flowcharts, Hierarchies, Charts), ensuring lightweight performance and pristine vector rendering without external image assets.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│                      Jetpack Compose UI                │
│ (Home Screen, Library, Search, Detail, Favorites, Auth)│
└──────────────────────────┬─────────────────────────────┘
                           │ StateFlow / ViewModels
┌──────────────────────────▼─────────────────────────────┐
│                    ViewModel Layer                     │
│         (ModelViewModel, AuthViewModel, FavoritesVM)   │
└──────────────────────────┬─────────────────────────────┘
                           │ Repository Pattern
        ┌──────────────────┴──────────────────┐
        ▼                                     ▼
┌───────────────────────────────┐   ┌───────────────────────────────┐
│     Static Model Repository   │   │     Firebase Firestore / Auth │
│   (180 Bundled Models & 9 Cats)│   │  (Cloud Sync for Favorites)   │
└───────────────────────────────┘   └───────────────────────────────┘
```

- **Data Models**:
  - `ManagementModel`: id, categoryId, title, abbreviation, definition, purpose, whenToUse, steps (List), workplaceExample, advantages (List), limitations (List), keyTakeaway, relatedModelIds (List), illustrationType.
  - `Category`: id, name, description, iconRes, modelCount.
  - `FavoriteItem`: userId, modelId, timestamp.
- **Navigation**:
  - Type-safe Compose Navigation routes: `home`, `library`, `category/{id}`, `detail/{modelId}`, `favorites`, `settings`, `auth`.
