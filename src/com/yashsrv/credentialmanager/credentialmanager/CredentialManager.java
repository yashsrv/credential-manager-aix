package com.yashsrv.credentialmanager.credentialmanager;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.annotations.SimpleFunction;

@DesignerComponent(
	version = 1,
	versionName = "1.0",
	description = "Developed by Yash Srivastava using Fast.",
	iconName = "icon.png"
)
public class CredentialManager extends AndroidNonvisibleComponent {

	public CredentialManager(ComponentContainer container) {
		super(container.$form());
	}

	@SimpleFunction("Returns sum of two integer numbers.")
	public int SumNumber(int firstNumber, int secondNumber) {
		return firstNumber + secondNumber;
	}
}
