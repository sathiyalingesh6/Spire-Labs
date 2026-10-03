# Product Catalog & Offline Cart (Spire Labs Android Assessment)

A modern Android application built using **Kotlin**, **Jetpack Compose**, **Clean Architecture**, and **MVI (Model-View-Intent)** pattern. It allows users to browse and search products fetched from the DummyJSON REST API and provides an **offline-first, locally persisted shopping cart** that remains fully functional without an internet connection.

---

## 📱 Features

1. **Product Catalog**:
   - Fetches product list from DummyJSON API (`https://dummyjson.com/products`).
   - Displays product thumbnail, title, price, and customer rating.
   - Comprehensive UI state handling: **Loading**, **Error with Retry**, and **Empty results**.

2. **Live Search**:
   - Real-time product search with **300ms debounce** to avoid excessive network requests.
   - Instant search clearing and empty state feedback.

3. **Product Details**:
   - Displays detailed product information: thumbnail, title, description, category, brand, stock availability, rating, and price.
   - Stock validation: Prevents adding items to cart when out of stock.
   - Live badge on cart icon reflecting current cart items count.

4. **Offline Shopping Cart**:
   - **Offline-First Persistence**: Backed by a local Room Database (`spire_labs_db`).
   - Increase and decrease item quantities with stock-limit safeguards.
   - Remove individual items or clear the entire cart.
   - Dynamic calculations of total items and total price.
   - Fully accessible and editable in Airplane Mode / without internet connectivity.

---

## 🏗️ Architecture: Clean Architecture + MVI

The application follows the principles of **Clean Architecture** combined with **MVI (Model-View-Intent)** to enforce unidirectional data flow (UDF) and separation of concerns.

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  Compose UI  ◄─── (UiState / UiEffect) ───  ViewModel      │
│              ───  (UiIntent / Actions) ───►                 │
└──────────────────────────────┬──────────────────────────────┘
                               │ (invokes)
┌──────────────────────────────▼──────────────────────────────┐
│                       Domain Layer                          │
│     Use Cases (Interactors)  ───►  Repository Interfaces    │
│     Domain Models (Product, CartItem)                       │
└──────────────────────────────▲──────────────────────────────┘
                               │ (implements)
┌──────────────────────────────┴──────────────────────────────┐
│                        Data Layer                           │
│  Remote Data Source (Retrofit)   Local Data Source (Room)   │
│  DTOs & Mappers                  Entities & DAOs            │
└─────────────────────────────────────────────────────────────┘
```

### Why MVI (Model-View-Intent)?
- **Unidirectional Data Flow (UDF)**: Data flows in only one direction. The View sends `UiIntent`s to the ViewModel, the ViewModel updates a single immutable `UiState`, and the View renders that state.
- **MVI vs MVVM Difference**:
  - In standard MVVM, the View directly calls multiple ViewModel methods and observes multiple separate `LiveData`/`StateFlow` streams. This often leads to fragmented state transitions and potential race conditions.
  - In MVI, all user interactions are codified as explicit `UiIntent` events, and the screen is represented by a single immutable `UiState` snapshot. This makes the UI deterministic, easily testable, and reproducible.
- **One-Shot Side Effects**: Handled via `UiEffect` on a Kotlin coroutines `Channel` for events like Navigation and Snackbars, preventing duplicate triggers on configuration changes.

### Why `BaseViewModel`?
- **Reduces Boilerplate**: Encapsulates common state management, intent subscription, and effect propagation across all ViewModels (`ProductListViewModel`, `ProductDetailViewModel`, `CartViewModel`).
- **Thread-Safe Atomic State Updates**: Uses `MutableStateFlow.update { it.reduce() }` with atomic Compare-And-Set (CAS) semantics to prevent race conditions when multiple concurrent asynchronous tasks complete simultaneously.

---

## 🛠️ Libraries Used

| Category | Library | Purpose |
|---|---|---|
| **Language** | Kotlin 2.2+ | Modern, concise language with Coroutines support |
| **UI Framework** | Jetpack Compose (BOM 2026.02.01) | Declarative Android UI toolkit |
| **Material Design** | Material 3 & Extended Icons | Modern Material 3 theming and iconography |
| **Navigation** | Navigation Compose & Navigation 3 | Single-activity screen navigation and backstack |
| **DI** | Dagger Hilt (2.60.1) with KSP | Dependency injection for modularity and testability |
| **Networking** | Retrofit 2 & OkHttp 3 (with Logging) | Type-safe REST client for DummyJSON API |
| **JSON Parsing** | Gson Converter | JSON serialization / deserialization |
| **Local Persistence** | Room Database (SQLite) | Offline-first reactive shopping cart storage |
| **Image Loading** | Coil Compose | Asynchronous image loading with caching |
| **Concurrency** | Kotlin Coroutines & Flow | Asynchronous programming and reactive data streams |

---

## 🔒 Network & Security Design Decisions

### 1. `BuildConfig.BASE_URL` & `gradle.properties`
- The API base URL is defined in `gradle.properties`:
  ```properties
  BASE_URL="https://dummyjson.com/"
  ```
- It is injected into `BuildConfig` in `app/build.gradle.kts`:
  ```kotlin
  val baseUrl = project.findProperty("BASE_URL") as? String ?: "\"https://dummyjson.com/\""
  buildConfigField("String", "BASE_URL", baseUrl)
  ```
- **Rationale**: Allows seamless switching between environments (e.g., Development, Staging, Production) without changing application code.
- **Design Decision & Safety Note**: For this assessment, the public API endpoint is stored in `gradle.properties` so the repository builds and runs immediately upon cloning. In enterprise production projects, sensitive keys and endpoints should be placed in `local.properties` (git-ignored) or injected via CI/CD environment secrets for security.

### 2. Dependency Injection Qualifiers
- **Retrofit Qualifier (`@DummyJsonRetrofit`)**: Distinguishes the Retrofit instance configured for DummyJSON, allowing other Retrofit instances (e.g. for authentication, microservices) to be added without collision.
- **OkHttp Qualifiers (`@DefaultOkHttp` & `@AuthOkHttp`)**:
  - `@DefaultOkHttp`: Public client with logging interceptor.
  - `@AuthOkHttp`: Configured for authenticated requests (e.g., attaching Bearer token/refresh token interceptors).

---

## 💾 Local Storage Approach

The shopping cart is designed with an **offline-first** strategy using Android Room:
- **`CartEntity`**: Stores product ID, title, price, thumbnail, quantity, and available stock.
- **`CartDao`**: Exposes `Flow<List<CartEntity>>`. Any local modification (increment, decrement, deletion, clear) instantly triggers a new emission to the ViewModel and Compose UI.
- **Stock Limit Guarding**: Quantity increment operations are capped at the product's available `stock`.

---

## 🚀 Setup & Build Instructions

### Prerequisites
- **Android Studio**: Ladybug / Meerkat (or newer)
- **JDK**: Version 17 or 21 (bundled JBR recommended)
- **Android SDK**: Compile SDK 37 (Minimum SDK 24)

### Steps to Run
1. **Clone the Repository**:
   ```bash
   git clone <repository_url>
   cd Spire-Labs
   ```
2. **Open Project in Android Studio**:
   - Open Android Studio $\rightarrow$ **File** $\rightarrow$ **Open** $\rightarrow$ select the `Spire-Labs` folder.
3. **Gradle Sync**:
   - Let Android Studio download dependencies and sync Gradle files.
4. **Run the App**:
   - Select an emulator or connected physical Android device.
   - Press **Run (Shift + F10)**.

---

## ⚖️ Known Limitations & Future Improvements

1. **Product Catalog Offline Caching**:
   - Currently, product browsing and search fetch directly from the network, while the cart is fully offline-persisted. A local Room cache for products could be added to support full catalog browsing offline.
2. **Pagination (Paging 3)**:
   - The DummyJSON API supports `limit` and `skip` query parameters. Implementing the Android Paging 3 library would enable infinite scrolling for very large product catalogs.