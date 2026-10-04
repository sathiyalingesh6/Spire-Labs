# Product Catalog & Offline Cart (Spire Labs Android Assessment)

A modern Android application built using **Kotlin**, **Jetpack Compose**, **Clean Architecture**, and **MVI (Model-View-Intent)** pattern. It allows users to browse and search products fetched from the DummyJSON REST API and provides an **offline-first, locally persisted shopping cart** that remains fully functional without an internet connection.

---

## 📱 Features

1. **Product Catalog**:
   - Fetches product list from DummyJSON API (`https://dummyjson.com/products`).
   - Displays product thumbnail, title, price, and customer rating.
   - Comprehensive UI state handling: **Loading**, **Error with Retry**, and **Empty results**.

2. **Live Search with `snapshotFlow` Debounce**:
   - Real-time product search with **300ms debounce powered by Compose `snapshotFlow`**.
   - Typing updates the input field instantly, and only dispatches the search intent to the ViewModel once the user pauses typing for 300ms.
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

### Why MVI (Model-View-Intent) over MVVM?
- **Concise & Deterministic Data Flow**: In traditional MVVM, a screen often binds to multiple scattered `StateFlow`/`LiveData` properties and invokes random ViewModel methods directly. This makes tracking the UI lifecycle, state mutations, and race conditions difficult.
- **Tighter Control & Understandability**: MVI enforces a **single source of truth** (`UiState`) and explicit user actions (`UiIntent`). Every state transition is predictable, unidirectional, and easy to trace or debug.
- **MVI vs MVVM Key Differences**:
  - **Single State Snapshot**: The entire screen state is encapsulated in one immutable data class, eliminating inconsistent intermediate UI states (e.g., loading spinner showing alongside old error message).
  - **Explicit Intent Stream**: UI actions flow as events through a buffered stream into `handleIntent(intent)`, making state transitions testable and reproducible.
- **One-Shot Side Effects**: Handled cleanly via `UiEffect` on a Kotlin coroutines `Channel` for navigation and snackbars, ensuring guaranteed one-time delivery without re-triggering upon device rotation.

### 🚀 Futuristic & Scalable Project Structure
The architecture is structured with long-term scalability and modularity in mind:
- **Plug-and-Play Feature Growth**: Each feature (e.g., `product_list`, `product_detail`, `cart`) is cleanly decoupled into presentation, domain use cases, and data sources. Adding a new feature (such as `checkout`, `wishlist`, or `user_profile`) requires minimal boilerplate without touching existing feature code.
- **Domain Independence**: The `domain` layer has **zero dependencies** on Android frameworks, Retrofit, or Room. Business logic and validation rules are pure Kotlin, ensuring longevity even if UI toolkits or networking libraries change.

### Why `BaseViewModel`?
- **Reduces Boilerplate**: Encapsulates common state management, buffered intent subscription, and effect propagation across all ViewModels (`ProductListViewModel`, `ProductDetailViewModel`, `CartViewModel`).
- **Thread-Safe Atomic State Updates**: Uses `MutableStateFlow.update { it.reduce() }` with atomic Compare-And-Set (CAS) semantics to prevent race conditions when multiple concurrent asynchronous tasks complete simultaneously.

### Why `snapshotFlow` for Search Debouncing?
In `ProductListScreen`, search debouncing is implemented using `snapshotFlow`:
```kotlin
LaunchedEffect(Unit) {
    snapshotFlow { searchInput }
        .distinctUntilChanged()
        .debounce(300.milliseconds)
        .collectLatest { query ->
            viewModel.setIntent(ProductListIntent.Search(query))
        }
}
```
**Why this approach is superior**:
1. **Idiomatic Reactive Bridge**: Concurrently converts Jetpack Compose state reads (`searchInput`) into a standard cold Kotlin `Flow`.
2. **Standard Flow Operators**: Seamlessly utilizes `.debounce(300.milliseconds)` and `.distinctUntilChanged()` without custom timer loops or manual coroutine cancellation.
3. **Zero Intent Churn**: Typing fast only updates local Compose UI state. The ViewModel MVI pipeline receives a single, stable `Search` intent after the user pauses typing.
4. **Leak-Free Lifecycle**: Automatically cancels when the composable leaves the composition.

### Threading & `DispatchersModule`
- **Main-Safe Architecture**: ViewModels launch on `viewModelScope` (which defaults to `Dispatchers.Main.immediate` for UI responsiveness).
- **IO Thread Offloading**: Injected `@IoDispatcher` into `ProductRepositoryImpl` and `CartRepositoryImpl` ensures all network calls, DTO/Entity mapping, and Room disk database operations run on `Dispatchers.IO` using `.flowOn(ioDispatcher)` and `withContext(ioDispatcher)`.
- **Testability**: Injecting dispatchers via `DispatchersModule` allows replacing `Dispatchers.IO` with `StandardTestDispatcher` in unit tests without blocking threads.

---

## 🛠️ Libraries Used

| Category | Library | Purpose |
|---|---|---|
| **Language** | Kotlin 2.2+ | Modern, concise language with Coroutines support |
| **UI Framework** | Jetpack Compose (BOM 2026.02.01) | Declarative Android UI toolkit |
| **Material Design** | Material 3 & Extended Icons | Modern Material 3 theming and iconography |
| **Navigation** | Navigation Compose & Navigation 3 | Single-activity screen navigation with safe backstack checks |
| **DI** | Dagger Hilt (2.60.1) with KSP | Dependency injection for modularity and testability |
| **Networking** | Retrofit 2 & OkHttp 3 (with Logging & Cache) | Type-safe REST client for DummyJSON API |
| **JSON Parsing** | Gson Converter | JSON serialization / deserialization |
| **Local Persistence** | Room Database (SQLite) | Offline-first reactive shopping cart storage |
| **Image Loading** | Coil Compose | Asynchronous image loading with two-tier RAM & disk caching |
| **Concurrency** | Kotlin Coroutines & Flow | Asynchronous programming and reactive data streams |

---

## 🖼️ Image & Network Caching Strategy

To ensure fluid 60/120fps scrolling and avoid redundant network image fetches:
1. **Coil Image Caching (`SpireLabsApplication`)**:
   - Configured custom `ImageLoaderFactory` with:
     - **25% Available RAM Memory Cache** (`MemoryCache`) for immediate re-use on scroll.
     - **50MB Dedicated Disk Cache** (`DiskCache`) in app cache directory.
     - Hardware bitmaps (`allowHardware(true)`) and crossfade enabled.
2. **OkHttp HTTP Response Cache (`NetworkModule`)**:
   - 20MB disk cache on `OkHttpClient` to cache HTTP responses and static assets across app sessions.

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

### 2. Multi-Service Architecture with Retrofit Qualifiers
- **Multiple Microservices / URL Support**: The DI layer is configured with custom qualifiers (`@DummyJsonRetrofit`, `@Named`, etc.) showcasing how the app can seamlessly connect to multiple backend microservices or distinct domain base URLs side-by-side without naming collisions or dependency conflicts.
- **Targeted Client Configuration**: Each Retrofit instance can attach its own specialized OkHttpClient, timeouts, converter factories, and error handlers.

### 3. Token-Based Authentication: Interceptors & OkHttp Authenticator
- **Auth Header Interceptor (`@AuthOkHttp`)**: Automatically appends the Bearer token (`Authorization: Bearer <token>`) at the final network layer on outgoing requests without requiring UI or repository code to pass authentication headers manually.
- **Automatic 401 Expiry Detection & Transparent Retry (`Authenticator`)**:
  - Uses OkHttp's `Authenticator` interface to intercept HTTP `401 Unauthorized` responses.
  - Automatically triggers a synchronous refresh token call, updates the local token storage, and replays the original failed request with the new token completely transparently to the user and caller repository.

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
3. **Full Microservice & Real Auth Endpoints**:
   - While multi-service Retrofit qualifiers and token interceptor/authenticator architectures are established in the DI structure, the assessment API (DummyJSON products) is public. Hooking up production auth endpoints with biometric/encrypted Keystore token persistence would be the next step for an enterprise release.