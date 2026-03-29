# Clockr
Clockr is a modern Android time tracking application built with Kotlin, Jetpack Compose, and Room. It features an offline-first architecture, foreground service–based timers, and a clean MVVM design for reliable hour tracking.

This app was created to solve my need to track my hours while working as a carpenter.

## Features
- **Offline-First:** All data is stored locally in a Room database.
- **Cloud Sync:** Data is automatically synchronized with Firebase Firestore.
- **Anonymous Authentication:** Users are automatically signed in to secure their own data without needing a login screen.
- **Live Tracking:** Real-time timer with foreground service support.

## Getting Started

To build and run this project, you will need to add your own Firebase configuration:

1.  Create a project in the [Firebase Console](https://console.firebase.google.com/).
2.  Add an Android app with the package name `com.thortech.clockr`.
3.  Download the `google-services.json` file and place it in the `app/` directory of the project.
4.  Enable **Anonymous Authentication** in the Firebase Auth settings.
5.  Enable **Cloud Firestore** and set up your security rules.

### Firestore Security Rules (Recommended)
For production, use the following rules to ensure users can only access their own data:

```javascript
service cloud.firestore {
  match /databases/{database}/documents {
    match /time_entries/{entryId} {
      allow read, write: if request.auth != null && request.auth.uid == (resource == null ? request.resource.data.userId : resource.data.userId);
    }
  }
}
```

## Tech Stack
- **Kotlin** & **Jetpack Compose** (UI)
- **Room** (Local persistence)
- **Firebase Auth** (User management)
- **Cloud Firestore** (Cloud storage & sync)
- **DataStore** (User settings)
- **Coroutines & Flow** (Asynchronous programming)
