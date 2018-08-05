package com.fnt.authentication;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;

public class AppLoginRepository {

	private static final String REST_LOGIN_END_POINT = "http://localhost:8080/auth/rest/login";
	private static final String LOGIN = "login";
	private static final String JWE = "jwe";

	private static Client createClient() {

		Client client = ClientBuilder.newClient();
		client.register(new ContextResolver<ObjectMapper>() {
			@Override
			public ObjectMapper getContext(Class<?> type) {
				ObjectMapper mapper = new ObjectMapper();
				mapper.registerModule(new JavaTimeModule());
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				return mapper;
			}
		});
		return client;
	}

	public static boolean authenticate(String login, String pwd) {

		Client client = null;
		try {
			client = createClient();
			Response response = client.target(REST_LOGIN_END_POINT).path(login).path(pwd).request(MediaType.APPLICATION_JSON).get(Response.class);
			if (response.getStatus() == 200) {
				String jwe = response.getHeaderString("Authorization");
				if (jwe != null) {
					VaadinSession.getCurrent().setAttribute(LOGIN, login);
					VaadinSession.getCurrent().setAttribute(JWE, jwe);
					Page.getCurrent().reload();
					return true;
				}
			}
			VaadinSession.getCurrent().setAttribute(LOGIN, null);
			VaadinSession.getCurrent().setAttribute(JWE, null);
			return false;
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public static boolean isAuthenticated() {
		return VaadinSession.getCurrent().getAttribute(LOGIN) != null;
	}

	public static void logout() {
		VaadinService.getCurrentRequest().getWrappedSession().invalidate();
		Page.getCurrent().setLocation("");
	}

}
