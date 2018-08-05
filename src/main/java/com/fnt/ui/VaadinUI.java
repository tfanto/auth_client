package com.fnt.ui;

import javax.servlet.annotation.WebServlet;

import com.fnt.authentication.AppLoginForm;
import com.fnt.authentication.AppLoginRepository;
import com.fnt.useradmin.UserList;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@PushStateNavigation
public class VaadinUI extends UI {

	@Override
	protected void init(VaadinRequest request) {

		Label title = new Label("Menu");
		title.addStyleName(ValoTheme.MENU_TITLE);

		Button btnUserAdmin = new Button("User", e -> getNavigator().navigateTo("userlist"));
		btnUserAdmin.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);

		Button btnLogout = new Button("Logout", e -> logout());
		btnLogout.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);

		CssLayout menu = new CssLayout(title, btnUserAdmin , btnLogout);
		menu.addStyleName(ValoTheme.MENU_ROOT);

		CssLayout viewContainer = new CssLayout();
		viewContainer.setSizeFull();
		HorizontalLayout mainLayout = new HorizontalLayout(menu, viewContainer);
		mainLayout.setExpandRatio(menu, 0);
		mainLayout.setExpandRatio(viewContainer, 1);

		mainLayout.setSizeFull();
		setContent(mainLayout);

		Navigator navigator = new Navigator(this, viewContainer);
		navigator.addView("", DefaultView.class);
		navigator.addView("login", AppLoginForm.class);
		navigator.addView("userlist", UserList.class);

		if (!AppLoginRepository.isAuthenticated()) {
			btnUserAdmin.setVisible(false);
			btnLogout.setVisible(false);
			navigator.navigateTo("login");
		} else {
			btnUserAdmin.setVisible(true);
			btnLogout.setVisible(true);

		}
		navigator.addViewChangeListener(new ViewChangeListener() {
			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {

				if (AppLoginRepository.isAuthenticated()) {
					return true;
				}
				return false;
			}
		});
	}

	private Object logout() {
		AppLoginRepository.logout();
		return null;
	}

	@WebServlet(urlPatterns = "/*", name = "VaadinUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = VaadinUI.class, productionMode = false)
	public static class VaadinUIServlet extends VaadinServlet {
	}

}
