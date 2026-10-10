> Google Sign-In for Android is deprecated and will be removed soon, migrate to Android Credential Manager.

This extension supports **Sign in with Google** using Android’s Credential Manager, with automatic sign-in for returning users.

It first looks for accounts that have previously signed in to your app, which keeps the flow quick. If none are found, it falls back to showing all accounts on the device in the system’s bottom sheet.

minSdk: 23

*Blocks*
<img width="381" height="594" alt="image" src="https://github.com/user-attachments/assets/620c7560-ea50-49ea-8e90-ef8e80e71c0c" />

*Flow*


*Configure Google Auth Platform (for WebClientId):*
1. In [Google Cloud Console](https://console.cloud.google.com/), create or select a project.
2. Go to APIs & Services → Google Auth Platform. Set the user type to External, fill in the app details, and add your test accounts under Audience → Test users while in Testing.
3. Go to Credentials → Create Credentials → OAuth client ID and create an Android client with your app’s package name and SHA-1. For Play Store builds, use the App signing key SHA-1 from Play Console.
4. Create a Web application client and copy its Client ID.

*Tips for developers:*
1. Always treat `uniqueId` as the user's unique identifier, not email, email can change, so index your database with `uniqueId`.
2. Send & verify the `idToken` with your backend and extract `uniqueId, name, email` and `profilePicture`. Do not trust user provided details.
3. Google has made the [brand verification](https://developers.google.com/identity/protocols/oauth2/production-readiness/brand-verification) compulsory if your app is in production, it is automatic now for non-sensitive scopes, roughly takes 5 minutes. Complete it before you launch your app.
