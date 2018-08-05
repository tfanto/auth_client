package com.fnt.customer;

import java.util.List;

import com.fnt.entity.Customer;
import com.fnt.sys.RestResponse;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.ValidationException;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class CustomerForm extends Window {

	private static final long serialVersionUID = -1559073890684080775L;

	int crudFunction = 1;
	CustomerRepository customerRepository;
	CustomerList owner;

	private TextField customerNumber = new TextField("Customer number");
	private TextField name = new TextField("Name");
	private TextField description = new TextField("Description");

	private Button btn_cancel = new Button("Cancel");
	private Button btn_save = new Button("Ok", VaadinIcons.CHECK);
//	private Button btn_refresh = new Button("Refresh"); // does not work as expected

	public CustomerForm(CustomerList owner, CustomerRepository customerRepository, String caption, Customer user,
			int crudFunction) {
		this.owner = owner;
		this.customerRepository = customerRepository;

		this.crudFunction = crudFunction;

		switch (crudFunction) {
		case CustomerList.CRUD_DELETE:
			btn_save.setCaption("Confirm delete");
			break;
		}

		initLayout(caption);
		initBehavior(user);
	}

	@SuppressWarnings("unused")
	private void fetchCustomer(Long id) {

		RestResponse<Customer> fetched = customerRepository.getById(id);
		if (fetched.getStatus().equals(200)) {
			Customer fetchedUser = fetched.getEntity();
			initBehavior(fetchedUser);
		} else {
			Notification.show("ERROR", fetched.getMsg(), Notification.Type.ERROR_MESSAGE);
		}
	}

	private void initLayout(String caption) {
		setCaption(caption);
		btn_save.addStyleName(ValoTheme.BUTTON_PRIMARY);

		HorizontalLayout buttons = new HorizontalLayout(btn_cancel, btn_save);
		 //HorizontalLayout buttons = new HorizontalLayout(btn_refresh, btn_cancel, btn_save);
		buttons.setSpacing(true);

		GridLayout formLayout = new GridLayout(3, 3, customerNumber, name, description);
		formLayout.setMargin(true);
		formLayout.setSpacing(true);

		VerticalLayout layout = new VerticalLayout(formLayout, buttons);
		layout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
		setContent(layout);
		setModal(true);
		center();
	}

	private void initBehavior(Customer customer) {
		BeanValidationBinder<Customer> binder = new BeanValidationBinder<>(Customer.class);
		binder.bindInstanceFields(this);
		binder.readBean(customer);

	//	 btn_refresh.addClickListener(e -> fetchCustomer(customer.getId()));

		btn_cancel.addClickListener(e -> close());
		btn_save.addClickListener(e -> {
			try {
				binder.validate();
				binder.writeBean(customer);
				RestResponse<Customer> rs = null;
				switch (crudFunction) {
				case CustomerList.CRUD_CREATE:
					rs = customerRepository.create(customer);
					break;
				case CustomerList.CRUD_EDIT:
					rs = customerRepository.update(customer);
					break;
				case CustomerList.CRUD_DELETE:
					rs = customerRepository.delete(customer);
					break;
				default: {
					return;
				}
				}
				if (!rs.getStatus().equals(200)) {
					Notification.show("ERROR", rs.getMsg(), Notification.Type.ERROR_MESSAGE);
				} else {
					close();
					owner.refreshSearch();
				}
			} catch (ValidationException ex) {
				List<BindingValidationStatus<?>> errors = ex.getFieldValidationErrors();
				String msg = "";
				for (BindingValidationStatus<?> error : errors) {
					msg += error.getResult().get().getErrorMessage();
					// TODO close but no cigar where are the field names ???
					msg += "\n";
				}
				Notification.show("ERROR", msg, Notification.Type.ERROR_MESSAGE);
			}
		});
	}
}
