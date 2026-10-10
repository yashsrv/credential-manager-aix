# Repackages optimized classes into %packageName%.repacked package
# in resulting AIX. Repackaging is necessary to avoid clashes with
# the other extensions that might be using same libraries as you.
-repackageclasses %packageName%.repacked

-android
-optimizationpasses 5
-allowaccessmodification
-mergeinterfacesaggressively
-overloadaggressively
-useuniqueclassmembernames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

-keep class androidx.credentials.playservices.CredentialProviderPlayServicesImpl {
  <init>(android.content.Context);
}
-keep class androidx.credentials.playservices.CredentialProviderMetadataHolder
-keep class androidx.credentials.playservices.controllers.identityauth.HiddenActivity

-dontwarn androidx.annotation.**
-dontwarn com.google.android.apps.common.proguard.**
-dontwarn com.google.android.gms.common.R$**
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**
-dontwarn libcore.io.Memory
-dontwarn org.checkerframework.**
-dontwarn androidx.core.app.PendingIntentCompat