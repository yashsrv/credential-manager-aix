> Google Sign-In for Android is deprecated and will be removed soon, migrate to Android Credential Manager.

This extension supports **Sign in with Google** using Android’s Credential Manager, with automatic sign-in for returning users.

It first looks for accounts that have previously signed in to your app, which keeps the flow quick. If none are found, it falls back to showing all accounts on the device in the system’s bottom sheet.

*Blocks*

<img width="298" height="500" alt="image" src="https://github.com/user-attachments/assets/58900761-c1dc-4de0-a287-3ecd841b751f" />


*Bottom Sheet Flow* vs *Button Flow*

<img width="169" height="250" alt="image" src="https://github.com/user-attachments/assets/4ce7942e-7bc2-404f-af42-30a9d1b104f4" />
<img width="200" height="225" alt="image" src="https://github.com/user-attachments/assets/8e31d956-fe1d-4c9c-b8bd-36e95b042134" />


*Bottom Sheet Flow: Normal vs Auto Select Enabled*

<img width="225" height="500" alt="telegram-cloud-document-5-6219768039437510242-ezgif com-video-to-gif-converter" src="https://github.com/user-attachments/assets/458fbac5-25c7-4bc2-9e97-0766cbd351b6" />

<img width="223" height="500" alt="telegram-cloud-document-5-6219768039437510236-ezgif com-video-to-gif-converter" src="https://github.com/user-attachments/assets/e27b14ae-add2-41b0-9f01-0aaf98145514" />

Auto Select works only when:
1. Only one *authorized* account is on the device.
2. The user hasn’t signed out of the app using `SignOut` block
3. Automatic Sign-in is enabled in their Google Account.

*Button flow*

<img width="223" height="500" alt="telegram-cloud-document-5-6219768039437510243-ezgif com-video-to-gif-converter" src="https://github.com/user-attachments/assets/fa6bf7d7-775f-4c5e-8dfb-8dd6b68dd967" />


*Blocks for reference*

<img width="690" height="447" alt="image" src="https://github.com/user-attachments/assets/82f4b3bc-295b-446f-996e-5c92723ce281" />


*Configure Google Auth Platform (for WebClientId):*
1. In [Google Cloud Console](https://console.cloud.google.com/), create or select a project.
2. Go to APIs & Services → Google Auth Platform. Set the user type to External, fill in the app details, and add your test accounts under Audience → Test users while in Testing.
3. Go to Credentials → Create Credentials → OAuth client ID and create an Android client with your app’s package name and SHA-1. For Play Store builds, use the App signing key SHA-1 from Play Console.
5. Create a Web application client and copy its Client ID.

*Tips for developers:*
1. Always treat `uniqueId` as the user's unique identifier, not email, email can change, so index your database with `uniqueId`.
2. Send & verify the `idToken` with your backend and extract `uniqueId, name, email` and `profilePicture`. Do not trust user provided details.
3. Google has made the [brand verification](https://developers.google.com/identity/protocols/oauth2/production-readiness/brand-verification) compulsory if your app is in production, it is automatic now for non-sensitive scopes, roughly takes 5 minutes. Complete it before you launch your app.
