package com.yashsrv.gcm;

import android.app.Activity;
import android.net.Uri;
import android.os.CancellationSignal;

import androidx.core.content.ContextCompat;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.credentials.exceptions.GetCredentialCancellationException;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;

@DesignerComponent(
	version = 1,
	versionName = "1.0",
	description = "Google Credential Manager. Developed by Yash Srivastava.",
	iconName = "icon.png"
)
public class GCM extends AndroidNonvisibleComponent {
	private final Activity activity;
	private final CredentialManager credentialManager;

	private String webClientId = "";
	private boolean autoSelect = false;
	private CancellationSignal cancellationSignal;

	public GCM(ComponentContainer container) {
		super(container.$form());
		activity = container.$context();
		credentialManager = CredentialManager.create(activity);
	}

	// Properties
	@DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
		defaultValue = "")
	@SimpleProperty("The Web application OAuth client ID from Google Cloud Console.")
	public void WebClientId(String webClientId) {
		this.webClientId = webClientId;
	}

	@SimpleProperty("Returns the Web application OAuth client ID.")
	public String WebClientId() {
		return webClientId;
	}

	@DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN,
		defaultValue = "False")
	@SimpleProperty("Enable automatic sign-in for returning users.")
	public void AutoSelect(boolean enabled) {
		this.autoSelect = enabled;
	}

	@SimpleProperty("Returns whether AutoSelect is enabled.")
	public boolean AutoSelect() {
		return autoSelect;
	}

	// Functions
	@SimpleFunction("Shows the Google account chooser and signs the user in. "
			+ "Triggers SignInSuccessful on success, SignInCancelled if the user dismisses "
			+ "the chooser, or ErrorOccurred on failure.")
	public void SignInWithGoogle() {
		if (webClientId == null || webClientId.trim().isEmpty()) {
			ErrorOccurred("WebClientId is not set");
			return;
		}
		requestGoogleId(true);
	}

	@SimpleFunction("Clears the credential state kept by Credential Manager so the"
			+ "user is not signed back in automatically. Triggers SignedOut when done.")
	public void SignOut() {
		credentialManager.clearCredentialStateAsync(
			new ClearCredentialStateRequest(),
			new CancellationSignal(),
			ContextCompat.getMainExecutor(activity),
			new CredentialManagerCallback<Void, ClearCredentialException>() {
				@Override
				public void onResult(Void result) {
					SignedOut();
				}

				@Override
				public void onError(ClearCredentialException err) {
					ErrorOccurred(err.getMessage());
				}
			}
		);
	}

	private void requestGoogleId(boolean authorizedOnly) {
		// Cancel any flow still in progress
		if (cancellationSignal != null) cancellationSignal.cancel();
		cancellationSignal = new CancellationSignal();

		GetGoogleIdOption option = new GetGoogleIdOption.Builder()
			.setServerClientId(webClientId)
			.setFilterByAuthorizedAccounts(authorizedOnly)
			.setAutoSelectEnabled(autoSelect)
			.build();

		GetCredentialRequest request = new GetCredentialRequest
			.Builder()
			.addCredentialOption(option)
			.build();

		credentialManager.getCredentialAsync(
			activity,
			request,
			cancellationSignal,
			ContextCompat.getMainExecutor(activity),
			new CredentialManagerCallback<GetCredentialResponse,GetCredentialException>() {
				@Override
				public void onResult(GetCredentialResponse result) {
					Credential cred = result.getCredential();
					if (cred instanceof CustomCredential && cred.getType()
							.equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
						try {
							GoogleIdTokenCredential googleCred = GoogleIdTokenCredential
								.createFrom(cred.getData());
							
							String name = googleCred.getDisplayName();
							if (name == null) name = "";

							Uri profilePictureUri = googleCred.getProfilePictureUri();
							String profilePicture = profilePictureUri == null ? "" : profilePictureUri.toString();
								
							SignInSuccessful(
								googleCred.getUniqueId(),
								name,
								googleCred.getEmail(),
								profilePicture,
								googleCred.getIdToken()
							);
						} catch (Exception err) {
							ErrorOccurred(err.getMessage());
						}
					} else {
						ErrorOccurred("Received an unexpected type of credential.");
					}
				}
				@Override
				public void onError(GetCredentialException err) {
					if (authorizedOnly && err instanceof NoCredentialException) {
						requestGoogleId(false);
					} else if (err instanceof GetCredentialCancellationException) {
						SignInCancelled();
					} else {
						ErrorOccurred(err.getMessage());
					}
				}
			}
		);
	}

	// Events
	@SimpleEvent("Fired after a successful Google sign-in. "
			+ "name and profilePicture are empty if the account has none.")
	public void SignInSuccessful(String uniqueId, String name, String email,
			String profilePicture, String idToken) {
		form.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				EventDispatcher.dispatchEvent(GCM.this, "SignInSuccessful",
					uniqueId, name, email, profilePicture, idToken);
			}
		});
	}

	@SimpleEvent("Fired when the user closes the account chooser without selecting an account.")
	public void SignInCancelled() {
		form.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				EventDispatcher.dispatchEvent(GCM.this, "SignInCancelled");
			}
		});
	}

	@SimpleEvent("Fired after the credential state has been cleared successfully.")
	public void SignedOut() {
		form.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				EventDispatcher.dispatchEvent(GCM.this, "SignedOut");
			}
		});
	}

	@SimpleEvent("Fired when sign-in or sign-out fails. "
			+ "errorMessage describes what went wrong.")
	public void ErrorOccurred(String errorMessage) {
		form.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				EventDispatcher.dispatchEvent(GCM.this, "ErrorOccurred", errorMessage);
			}
		});
	}
}