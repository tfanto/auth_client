package com.fnt.customerorder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fnt.customer.CustomerRepository;
import com.fnt.dto.CustomerOrderHeadListView;
import com.fnt.dto.CustomerOrderLineListView;
import com.fnt.dto.SearchData;
import com.fnt.entity.CustomerOrderHead;
import com.fnt.entity.CustomerOrderLine;
import com.fnt.item.ItemRepository;
import com.fnt.sys.Fnc;
import com.fnt.sys.RestResponse;
import com.vaadin.server.VaadinSession;

public class CustomerOrderRepository {

	private Fnc fnc = new Fnc();

	private String REST_CUSTOMER_ORDER_END_POINT = "http://localhost:8080/server2/rest/customerorder";

	private CustomerRepository customerRepository = new CustomerRepository();
	private ItemRepository itemRepository = new ItemRepository();

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

	public RestResponse<List<CustomerOrderHeadListView>> paginatesearch(Integer offset, Integer limit, String filterCustomerNumberStr, String filterNameStr, LocalDate filterDate, String filterStatusStr, String filterChangedByStr,
			String sortOrderStr) {
		Encoder encoder = Base64.getUrlEncoder();

		String customernumber = encoder.encodeToString(filterCustomerNumberStr.getBytes());
		String name = encoder.encodeToString(filterNameStr.getBytes());

		String formattedDateString = "";
		if (filterDate != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			formattedDateString = filterDate.format(formatter);
		}

		String offs = encoder.encodeToString(String.valueOf(offset).getBytes());
		String lim = encoder.encodeToString(String.valueOf(limit).getBytes());
		String orderstatus = encoder.encodeToString(filterStatusStr.getBytes());
		String changedby = encoder.encodeToString(filterChangedByStr.getBytes());
		String sortorder = encoder.encodeToString(sortOrderStr.getBytes());
		String theDate = encoder.encodeToString(formattedDateString.getBytes());

		Client client = null;
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(REST_CUSTOMER_ORDER_END_POINT)
					.path("paginatesearch")
					.queryParam("offset", offs)
					.queryParam("limit", lim)
					.queryParam("customernumber", customernumber)
					.queryParam("name", name)
					.queryParam("date", theDate)
					.queryParam("orderstatus", orderstatus)
					.queryParam("changedby", changedby)
					.queryParam("sortorder", sortorder)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))					
					.get(Response.class);
			// @formatter:on
			int status = response.getStatus();
			if (status == 200) {
				List<CustomerOrderHeadListView> theList = response.readEntity(new GenericType<List<CustomerOrderHeadListView>>() {
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

	public RestResponse<Long> paginatecount(String filterCustomerNumberStr, String filterNameStr, LocalDate filterDate, String filterStatusStr, String filterChangedByStr) {
		Encoder encoder = Base64.getUrlEncoder();

		String customernumber = encoder.encodeToString(filterCustomerNumberStr.getBytes());
		String name = encoder.encodeToString(filterNameStr.getBytes());

		String formattedDateString = "";
		if (filterDate != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			formattedDateString = filterDate.format(formatter);
		}

		String orderstatus = encoder.encodeToString(filterStatusStr.getBytes());
		String changedby = encoder.encodeToString(filterChangedByStr.getBytes());
		String theDate = encoder.encodeToString(formattedDateString.getBytes());

		Client client = null;
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(REST_CUSTOMER_ORDER_END_POINT)
					.path("paginatecount")
					.queryParam("customernumber", customernumber)
					.queryParam("name", name)
					.queryParam("date", theDate)
					.queryParam("orderstatus", orderstatus)
					.queryParam("changedby", changedby)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))					
					.get(Response.class);
			// @formatter:on
			int status = response.getStatus();
			if (status == 200) {
				Long rs = response.readEntity(new GenericType<Long>() {
				});
				return new RestResponse<>(status, rs);
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

	public RestResponse<CustomerOrderHead> getById(Long id) {
		Client client = null;
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(REST_CUSTOMER_ORDER_END_POINT)
					.path(String.valueOf(id))
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))					
					.get(Response.class);
			// @formatter:on
			int status = response.getStatus();
			if (status == 200) {
				CustomerOrderHead obj = response.readEntity(new GenericType<CustomerOrderHead>() {
				});
				return new RestResponse<>(status, obj);
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

	public RestResponse<CustomerOrderHead> createHead(LocalDate orderDate, String customerNumber) {

		Encoder encoder = Base64.getUrlEncoder();
		String formattedDateString = "";
		if (orderDate != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			formattedDateString = orderDate.format(formatter);
		}

		String theCustomerNumber = encoder.encodeToString(customerNumber.getBytes());
		String theDate = encoder.encodeToString(formattedDateString.getBytes());

		Client client = null;
		try {
			// @formatter:off
			client = createClient();
			Response response = client
					.target(REST_CUSTOMER_ORDER_END_POINT).path("header")
					.queryParam("customernumber", theCustomerNumber)
					.queryParam("date", theDate)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))					
					.post(null,Response.class);
			// @formatter:on
			int status = response.getStatus();
			if (status == 200) {
				CustomerOrderHead obj = response.readEntity(new GenericType<CustomerOrderHead>() {
				});
				return new RestResponse<>(status, obj);
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

	public RestResponse<CustomerOrderHead> updateHead(Long ordernumber, LocalDate orderDate, String customerNumber) {

		Encoder encoder = Base64.getUrlEncoder();

		String formattedDateString = "";
		if (orderDate != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			formattedDateString = orderDate.format(formatter);
		}

		String theCustomerNumber = encoder.encodeToString(customerNumber.getBytes());
		String theDate = encoder.encodeToString(formattedDateString.getBytes());

		Client client = null;
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(REST_CUSTOMER_ORDER_END_POINT).path("header")
					.queryParam("ordernumber", String.valueOf(ordernumber))
					.queryParam("customernumber", theCustomerNumber)
					.queryParam("date", theDate)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))					
					.put(null,Response.class);
			// @formatter:on
			int status = response.getStatus();
			if (status == 200) {
				CustomerOrderHead obj = response.readEntity(new GenericType<CustomerOrderHead>() {
				});
				return new RestResponse<>(status, obj);
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

	public RestResponse<CustomerOrderHead> delete(CustomerOrderHead obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public RestResponse<List<SearchData>> selectListCustomers(String value, String value2) {
		return customerRepository.selectList(value, value2);
	}

	public RestResponse<List<SearchData>> selectListItems(String value, String value2) {
		return itemRepository.selectList(value, value2);
	}

	public RestResponse<CustomerOrderLine> addCustomerOrderLine(String internalordernumber, String itemnumber, String units, String priceperitem) {

		Encoder encoder = Base64.getUrlEncoder();

		String theInternalordernumber = encoder.encodeToString(internalordernumber.getBytes());
		String theItemnumber = encoder.encodeToString(itemnumber.getBytes());
		Integer.parseInt(units);
		priceperitem = priceperitem.replaceAll(",", ".");
		Double.parseDouble(priceperitem);
		String theUnits = encoder.encodeToString(units.getBytes());
		String thePriceperitem = encoder.encodeToString(priceperitem.getBytes());

		Client client = null;
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(REST_CUSTOMER_ORDER_END_POINT)
					.path("line")
					.queryParam("internalordernumber", theInternalordernumber)
					.queryParam("itemnumber", theItemnumber)
					.queryParam("units", theUnits)
					.queryParam("priceperitem", thePriceperitem)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))					
					.post(null,Response.class);
			// @formatter:on
			int status = response.getStatus();
			if (status == 200) {
				CustomerOrderLine obj = response.readEntity(new GenericType<CustomerOrderLine>() {
				});
				return new RestResponse<>(status, obj);
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

	public RestResponse<List<CustomerOrderLineListView>> searchOrderlinesFor(String currentInternalCustomerOrdernumber) {

		Encoder encoder = Base64.getUrlEncoder();

		String theInternalordernumber = encoder.encodeToString(currentInternalCustomerOrdernumber.getBytes());

			Client client = null;
			try {
				client = createClient();
				// @formatter:off
				Response response = client
						.target(REST_CUSTOMER_ORDER_END_POINT)
						.path(String.valueOf("linesfororder"))
						.path(String.valueOf(theInternalordernumber))
						.request(MediaType.APPLICATION_JSON)
						.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))					
						.get(Response.class);
				// @formatter:on
			int status = response.getStatus();
			if (status == 200) {
				List<CustomerOrderLineListView> obj = response.readEntity(new GenericType<List<CustomerOrderLineListView>>() {
				});
				return new RestResponse<>(status, obj);
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

}
