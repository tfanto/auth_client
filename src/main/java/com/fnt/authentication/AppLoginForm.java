package com.fnt.authentication;

import com.vaadin.navigator.View;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Composite;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class AppLoginForm extends Composite implements View {

	private static final long serialVersionUID = 1L;

	public interface LoginListener {
		void logInClicked(AppLoginForm loginForm);
	}

	private TextField username;
	private PasswordField password;

	private String usernameCaption = "User";
	private String passwordCaption = "Password";
	private String loginButtonCaption = "Log in";

	@SuppressWarnings("serial")
	public AppLoginForm() {
		LoginForm loginForm = new LoginForm() {
			@Override
			protected Component createContent(TextField username, PasswordField password, Button loginButton) {
				AppLoginForm.this.username = username;
				AppLoginForm.this.password = password;

				username.setCaption(null);
				password.setCaption(null);

				username.setPlaceholder(usernameCaption);
				password.setPlaceholder(passwordCaption);
				loginButton.setCaption(loginButtonCaption);

				return new VerticalLayout(username, password, loginButton);
			}
		};

		loginForm.addLoginListener(this::logInClicked);
		setCompositionRoot(loginForm);
	}

	public String getUsername() {
		return username.getValue();
	}

	public String getPassword() {
		return password.getValue();
	}

	public void setUsernameCaption(String usernameCaption) {
		this.usernameCaption = usernameCaption;
	}

	public void setPasswordCaption(String passwordCaption) {
		this.passwordCaption = passwordCaption;
	}

	public void setLoginButtonCaption(String loginButtonCaption) {
		this.loginButtonCaption = loginButtonCaption;
	}

	private void logInClicked(LoginForm.LoginEvent loginEvent) {
		boolean ok = AppLoginRepository.authenticate(username.getValue(), password.getValue());
		if(!ok) {
			Notification.show("Bad credentials", Notification.Type.ERROR_MESSAGE);			
		}
		else {
			getUI().getNavigator().navigateTo("");
		}
	}

}
