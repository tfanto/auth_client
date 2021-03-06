package com.fnt.useradmin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fnt.dto.UserDto;
import com.fnt.sys.Fnc;
import com.fnt.sys.RestResponse;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;

public class UserRepository {

	private static final String REST_USER_END_POINT = String.valueOf(VaadinServlet.getCurrent().getServletContext().getAttribute("REST_USER_END_POINT"));

	private Fnc fnc = new Fnc();

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

	private ObjectMapper getMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;

	}

	public RestResponse<List<UserDto>> search() {
		Client client = null;
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(REST_USER_END_POINT)
					.path("all")
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))
					.get(Response.class);
			// @formatter:on
			int status = response.getStatus();
			if (status == 200) {
				List<UserDto> theList = response.readEntity(new GenericType<List<UserDto>>() {
				});
				return new RestResponse<>(status, theList);
			} else if (status == 403) {
				return new RestResponse<>(status, new ArrayList<>()); // dont tell the the user why he could be evil
			} else {
				return new RestResponse<>(status, new ArrayList<>());
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public RestResponse<UserDto> getLogin(String login) {
		Client client = null;
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(REST_USER_END_POINT)
					.path(login)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))
					.get(Response.class);
			// @formatter:on
			int status = response.getStatus();
			if (status == 200) {
				String json = response.readEntity(String.class);
				UserDto user;
				try {
					user = getMapper().readValue(json, UserDto.class);
					return new RestResponse<>(status, user);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			} else if (status == 403) {
				return new RestResponse<>(status, response.getStatusInfo().toString());
			} else {
				JsonNode jsonNode = response.readEntity(JsonNode.class);
				String appMsg = jsonNode.path("appMsg").textValue();
				return new RestResponse<>(404, fnc.formatAppMsg(appMsg));
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public RestResponse<UserDto> create(UserDto user) {
		Client client = null;

		List<String> errors = validate(user);
		if (errors.size() > 0) {
			return new RestResponse<>(403, errors.get(0));
		}

		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(REST_USER_END_POINT)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))
					.post(Entity.json(user), Response.class);
			// @formatter:on
			int status = response.getStatus();
			if (status == 200) {
				UserDto theList = response.readEntity(new GenericType<UserDto>() {
				});
				return new RestResponse<>(status, theList);
			} else if (status == 403) {
				return new RestResponse<>(status, response.getStatusInfo().toString());
			} else {
				JsonNode jsonNode = response.readEntity(JsonNode.class);
				String appMsg = jsonNode.path("appMsg").textValue();
				return new RestResponse<>(404, fnc.formatAppMsg(appMsg));
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public RestResponse<UserDto> update(UserDto user) {
		Client client = null;
		user.setLastlogin(null);
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(REST_USER_END_POINT)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))
					.put(Entity.json(user), Response.class);
			// @formatter:on
			int status = response.getStatus();
			if (status == 200) {
				UserDto theList = response.readEntity(new GenericType<UserDto>() {
				});
				return new RestResponse<>(status, theList);
			} else if (status == 403) {
				return new RestResponse<>(status, response.getStatusInfo().toString());
			} else {
				JsonNode jsonNode = response.readEntity(JsonNode.class);
				String appMsg = jsonNode.path("appMsg").textValue();
				return new RestResponse<>(404, fnc.formatAppMsg(appMsg));
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public RestResponse<UserDto> delete(UserDto user) {
		Client client = null;
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(REST_USER_END_POINT)
					.path(user.getLogin())
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))
					.delete( Response.class);
			// @formatter:on
			int status = response.getStatus();
			if (status == 200) {
				return new RestResponse<>(status, user);
			} else if (status == 403) {
				return new RestResponse<>(status, response.getStatusInfo().toString());
			} else {
				JsonNode jsonNode = response.readEntity(JsonNode.class);
				String appMsg = jsonNode.path("appMsg").textValue();
				return new RestResponse<>(404, fnc.formatAppMsg(appMsg));
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public <T> List<String> validate(T e) {

		List<String> ret = new ArrayList<>();

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<T>> errors = validator.validate(e);
		for (ConstraintViolation<T> error : errors) {
			String msg = error.getMessage();
			ret.add(msg);
		}
		return ret;
	}

}
