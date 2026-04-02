# Lufious - Implementation Status

## Overview

Native Android application for plant care management, built with Kotlin and Jetpack Compose.

## Tech Stack

| Area | Technology |
|------|-----------|
| Language | Kotlin 2.2.0 |
| UI | Jetpack Compose |
| Architecture | Clean Architecture + MVVM/MVI |
| DI | Dagger Hilt 2.56.1 |
| Backend | Firebase Auth + Firestore |
| Networking | Retrofit 2 + OkHttp + Kotlinx Serialization |
| Caching | SharedPreferences (via LocalCacheManager) |
| Async | Kotlin Coroutines + Flow |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 35 (Android 15) |

---

## Project Structure

```
app/src/main/java/ai/lufious/app/
├── core/
│   ├── firebase/          # Firestore integration (FirestoreManager)
│   ├── local_cache/       # SharedPreferences-based caching
│   ├── theme/             # Compose colors + typography
│   └── utils/             # Validators, dimensions, routes, effects
├── di/                    # Hilt modules (AppModule, DispatcherModule)
├── navgraph/              # Navigation graphs
├── presentation/
│   ├── auth/
│   │   ├── login/         # Login selection + email login screens
│   │   └── signup/        # Signup screen (stub)
│   ├── home/              # Home screen (stub)
│   ├── onboarding/        # Welcome/onboarding screen
│   ├── splash/            # Splash + auth check
│   └── utils/             # Shared UI components
├── MainActivity.kt
└── LufiousApplication.kt
```

---

## Screens

### Splash Screen
- Checks local cache for existing user session on app launch
- Routes to **Onboarding** if no session, **Home** if authenticated

### Onboarding Screen
- App branding + mascot image
- Tagline: "Smart AI care for every leaf, root, and bloom"
- Two CTAs: "GET STARTED" (signup) and "I ALREADY HAVE AN ACCOUNT" (login)

### Login Selection Screen
- Header: "Log Back Into Your Lufious Account"
- Options: Google Sign-In, Email/Password
- Facebook login structure present but UI incomplete
- Mascot image + toast notifications for feedback

### Email Login Screen
- Card-based layout with rounded corners
- Email + password fields (password has visibility toggle)
- Real-time validation error display
- Loading state during auth
- "Don't have an account?" link to signup
- Back button to onboarding

### Signup Screen
- **Status: Stub** — ViewModel + data layer ready, UI not yet built

### Home Screen
- **Status: Stub** — Route defined, no UI content

---

## Navigation

```
AppNavHost
├── Onboarding (start destination)
├── authNavGraph
│   ├── Login Selection
│   ├── Email Login
│   └── Signup
└── mainNavGraph
    └── Home
```

All transitions use fade in/out animations. Back stack is cleared when moving between auth and main graphs.

**Routes:**
```
splash, onboarding, get_started
auth/login, auth/login/email, signup
main/home, main/profile
```

---

## Authentication

### Email/Password Login
1. Validate email format + password (min 6 chars)
2. Check Firestore: email must exist in `users` collection
3. `FirebaseAuth.signInWithEmailAndPassword()`
4. Cache `UserModel` + auth token in SharedPreferences
5. Navigate to Home

### Email/Password Signup
1. Validate inputs
2. Check Firestore: email must NOT already exist
3. `FirebaseAuth.createUserWithEmailAndPassword()`
4. Cache user + navigate to Home

### Google Sign-In
1. Launch Google Sign-In intent
2. Receive ID token → `GoogleAuthProvider.getCredential()`
3. Extract email, run Firestore validation
4. `FirebaseAuth.signInWithCredential()`
5. Cache + navigate

### Facebook Login
- Data flow and use cases implemented
- UI integration incomplete

### Session Persistence
- `UserModel` serialized to JSON and stored in SharedPreferences
- Auth token stored separately; refreshed via `getIdToken(true)` on login

---

## Data Layer

### Models
```kotlin
UserModel(uid, email, displayName, phoneNumber, photoUrl, creationTimestamp, lastSignInTimestamp)
ValidationResult: Valid | Invalid(reason)
Result<T>: Success(data) | Error(message)
```

### Repository Interface (AuthRepository)
```
loginWithEmail / signupWithEmail
loginWithGoogle / signupWithGoogle
loginWithFacebook
signOut
currentUser: UserModel?
```

### Firestore (FirestoreManager)
- `getDocument<T>()`, `setDocument()`, `deleteDocument()`
- `checkIfDocumentExists()` — used for email duplicate detection

### Local Cache (LocalCacheManager)
- `saveUser / getUser / clearUser`
- `saveAuthToken / getAuthToken`
- `clearAll`

---

## UI Components

### ShadowButton
- 3D press animation with configurable shadow
- Loading spinner state
- Debounce protection (800ms)
- Optional icons, disabled state, uppercase text

### CommonTextField
- Password visibility toggle
- Validation error display
- Configurable keyboard actions (Next / Done)

### ResponsiveDimensions
- Scales all dimensions relative to 375×812dp design spec
- `wR()`, `hR()`, `R()`, `widthFraction()`, `heightFraction()`

### Theme
- Primary: `#35A924` (Leaf Green)
- Background: `#b8d53d` (Yellow-Green)
- Accent: `#4CAF50` (Light Green)
- Status colors: health green, warning orange, critical red

---

## State Management (MVI Pattern)

```
UI → ViewModel.send(Event)
        ↓
   handleEvent()
        ↓
   _state.update { ... }      ← StateFlow (UI rebuilds)
   _effect.emit(UiEffect)     ← SharedFlow (one-time events)
```

**UiEffects:**
- `Navigate(route)`, `ShowError(message)`
- `LaunchGoogleSignIn`, `LaunchFacebookSignIn`

---

## Build Variants

| Flavor | Suffix | Purpose |
|--------|--------|---------|
| `dev` | `.dev` | Development |
| `prod` | — | Production |

---

## Implementation Status

| Feature | Status |
|---------|--------|
| Splash + auth check | Done |
| Onboarding screen | Done |
| Email login (UI + logic) | Done |
| Google Sign-In | Done |
| Email signup (logic) | Done |
| Email signup (UI) | Not started |
| Facebook login | Partial (data layer only) |
| Home screen | Stub |
| Profile screen | Route only |
| Plant management | Not started |
| REST API integration | Not started (placeholder URLs) |
| Push notifications | Not started |
| Tests | Structure only |
