package com.fnt.customer;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;

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
import com.fnt.dto.SearchData;
import com.fnt.entity.Customer;
import com.fnt.sys.Fnc;
import com.fnt.sys.RestResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;

public class CustomerRepository {

	private Fnc fnc = new Fnc();

	private static final String REST_CUSTOMER_END_POINT = "http://localhost:8080/server2/rest/customer";

	public Client createClient() {

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

	public RestResponse<List<Customer>> paginatesearch(Integer offset, Integer limit, String customernumber, String name, String sortorder) {

		Encoder encoder = Base64.getEncoder();

		String offs = encoder.encodeToString(String.valueOf(offset).getBytes());
		String lim = encoder.encodeToString(String.valueOf(limit).getBytes());

		String cn = encoder.encodeToString(customernumber.getBytes());
		String n = encoder.encodeToString(name.getBytes());

		String so = encoder.encodeToString(sortorder.getBytes());

		Client client = null;
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(REST_CUSTOMER_END_POINT)
					.path("paginatesearch")
					.queryParam("offset", offs)
					.queryParam("limit", lim)
					.queryParam("customernumber", cn)
					.queryParam("name", n)
					.queryParam("sortorder", so)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))
					.get(Response.class);
			// @formatter:on
			int status = response.getStatus();
			if (status == 200) {
				List<Customer> theList = response.readEntity(new GenericType<List<Customer>>() {
				});
				return new RestResponse<>(status, theList);
			} else if(status == 403){
				return new RestResponse<>(status, response.getStatusInfo().toString());				
			} else {
				return new RestResponse<>(status, new ArrayList<>());
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public RestResponse<Long> paginatecount(String customerNumberStr, String nameStr) {
		Encoder encoder = Base64.getEncoder();

		String cn = encoder.encodeToString(customerNumberStr.getBytes());
		String n = encoder.encodeToString(nameStr.getBytes());

		Client client = null;
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(REST_CUSTOMER_END_POINT)
					.path("paginatecount")
					.queryParam("customernumber", cn)
					.queryParam("name", n)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))
					.get(Response.class);
			// @formatter:on
			int status = response.getStatus();
			if (status == 200) {
				Long theList = response.readEntity(new GenericType<Long>() {
				});
				return new RestResponse<>(status, theList);
			} else if (status == 403) { // forbidden
				Notification.show(response.getStatusInfo().toString(), Notification.Type.ERROR_MESSAGE);
				return new RestResponse<>(status, 0L);
			} else {
				JsonNode jsonNode = response.readEntity(JsonNode.class);
				String appMsg = jsonNode.path("appMsg").textValue();
				return new RestResponse<>(status, fnc.formatAppMsg(appMsg));
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public RestResponse<Customer> getById(Long id) {

		String idStr = String.valueOf(id);

		Client client = null;
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(REST_CUSTOMER_END_POINT)
					.path(idStr)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))
					.get(Response.class);
			// @formatter:off
			int status = response.getStatus();
			if (status == 200) {
				Customer data = response.readEntity(new GenericType<Customer>() {
				});
				return new RestResponse<>(status, data);
			} else if(status == 403){
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

	public RestResponse<Customer> create(Customer customer) {
		Client client = null;
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(REST_CUSTOMER_END_POINT)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))					
					.post(Entity.entity(customer, MediaType.APPLICATION_JSON), Response.class);
			int status = response.getStatus();
			// @formatter:off
			if (status == 200) {
				Customer data = response.readEntity(new GenericType<Customer>() {
				});
				return new RestResponse<>(status, data);
			} else if(status == 403){
				return new RestResponse<>(status, response.getStatusInfo().toString());				
			} else {
				JsonNode jsonNode = response.readEntity(JsonNode.class);
				String appMsg = jsonNode.path("appMsg").textValue();
				return new RestResponse<>(status, fnc.formatAppMsg(appMsg));
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public RestResponse<Customer> update(Customer customer) {
		Client client = null;
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(REST_CUSTOMER_END_POINT)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))					
					.put(Entity.entity(customer, MediaType.APPLICATION_JSON), Response.class);
			int status = response.getStatus();
			// @formatter:off
			if (status == 200) {
				Customer data = response.readEntity(new GenericType<Customer>() {
				});
				return new RestResponse<>(status, data);
			} else if(status == 403){
				return new RestResponse<>(status, response.getStatusInfo().toString());				
			}else {
				JsonNode jsonNode = response.readEntity(JsonNode.class);
				String appMsg = jsonNode.path("appMsg").textValue();
				return new RestResponse<>(status, fnc.formatAppMsg(appMsg));
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public RestResponse<Customer> delete(Customer customer) {
		String idStr = String.valueOf(customer.getId());

		Client client = null;
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(REST_CUSTOMER_END_POINT)
					.path(idStr)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))					
					.delete();
			// @formatter:off
			int status = response.getStatus();
			if(status == 403) {
				return new RestResponse<>(status, response.getStatusInfo().toString());								
			}
			if (status != 200) {
				JsonNode jsonNode = response.readEntity(JsonNode.class);
				String appMsg = jsonNode.path("appMsg").textValue();
				return new RestResponse<>(status, fnc.formatAppMsg(appMsg));
			} else {
				return new RestResponse<>(status);
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public RestResponse<List<SearchData>> selectList(String value, String value2) {
		Encoder encoder = Base64.getEncoder();

		String v1 = encoder.encodeToString(value.getBytes());
		String v2 = encoder.encodeToString(value2.getBytes());

		Client client = null;
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(REST_CUSTOMER_END_POINT)
					.path("prompt")
					.queryParam("customernumber", v1)
					.queryParam("name", v2)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))					
					.get(Response.class);
			// @formatter:off
			int status = response.getStatus();
			if (status == 200) {
				List<SearchData> theList = response.readEntity(new GenericType<List<SearchData>>() {
				});
				return new RestResponse<>(status, theList);
			} else if(status == 403){
				return new RestResponse<>(status, response.getStatusInfo().toString());				
			} else {
				return new RestResponse<>(200, new ArrayList<>());
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

}
