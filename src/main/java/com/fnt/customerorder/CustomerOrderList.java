package com.fnt.customerorder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fnt.dto.CustomerOrderHeadListView;
import com.fnt.entity.CustomerOrderHead;
import com.fnt.entity.Item;
import com.fnt.sys.Fnc;
import com.fnt.sys.RestResponse;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Composite;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.SingleSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.themes.ValoTheme;

public class CustomerOrderList extends Composite implements View {

	private static final long serialVersionUID = 4797579884478708862L;
	private Fnc fnc = new Fnc();

	public static final int CRUD_CREATE = 1;
	public static final int CRUD_EDIT = 2;
	public static final int CRUD_DELETE = 4;

	CustomerOrderRepository customerOrderRepository = new CustomerOrderRepository();

	// crud
	private Button btn_refresh = new Button("", VaadinIcons.SEARCH);
	private Button btn_add = new Button("", VaadinIcons.PLUS);
	private Button btn_edit = new Button("", VaadinIcons.PENCIL);
	private Button btn_delete = new Button("", VaadinIcons.TRASH);
	private Label noOfItems = new Label();

	// filter
	private TextField filterOrderNumber = new TextField();
	private TextField filterCustomerNumber = new TextField();
	private TextField filterName = new TextField();
	private DateField filterDate = new DateField();
	private TextField filterStatus = new TextField();
	private TextField filterChangedBy = new TextField();

	// sorting
	private Label filterSortOrder = new Label();
	private List<String> selectedSort = new ArrayList<>();

	private CheckBox sortOrderNumber = new CheckBox();
	private CheckBox sortCustomerNumber = new CheckBox();
	private CheckBox sortName = new CheckBox();
	private CheckBox sortDate = new CheckBox();
	private CheckBox sortStatus = new CheckBox();
	private CheckBox sortChangedBy = new CheckBox();

	private Grid<CustomerOrderHeadListView> grid = new Grid<>();
	private CustomerOrderHead currentOrderHead = null;

	public CustomerOrderList() {
		initLayout();
		initBehavior();
		// search();
		updateHeader();
	}

	private void initLayout() {

		HorizontalLayout buttons = new HorizontalLayout(btn_add, btn_edit, btn_delete, btn_refresh);
		buttons.setSpacing(false);
		HorizontalLayout header = new HorizontalLayout(buttons, filterSortOrder, noOfItems);
		header.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		header.setSpacing(true);

		// @formatter:off
	       Binder<CustomerOrderHeadListView> binder = grid.getEditor().getBinder();
	       
			grid.addColumn(CustomerOrderHeadListView::getDate)
				.setCaption("date")
				.setExpandRatio(0)
				.setId("date")
				.setEditorBinding(binder.forField(new DateField())
						.withValidator(new BeanValidator(CustomerOrderHeadListView.class, "date"))
						.bind(CustomerOrderHeadListView::getDate, CustomerOrderHeadListView::setDate));

			grid.addColumn(CustomerOrderHeadListView::getCustomernumber)
			.setCaption("Customernumber")
			.setExpandRatio(0)
			.setId("customernumber")
			.setEditorBinding(binder.forField(new TextField())
					.withNullRepresentation("")
					.withValidator(new BeanValidator(Item.class, "customernumber"))
					.bind(CustomerOrderHeadListView::getCustomernumber, CustomerOrderHeadListView::setCustomernumber));

			grid.addColumn(CustomerOrderHeadListView::getName)
			.setCaption("Name")
			.setExpandRatio(0)
			.setId("name")
			.setEditorBinding(binder.forField(new TextField())
					.withNullRepresentation("")
					.withValidator(new BeanValidator(Item.class, "name"))
					.bind(CustomerOrderHeadListView::getName, CustomerOrderHeadListView::setName));

			grid.addColumn(CustomerOrderHeadListView::getChangedby)
			.setCaption("Changedby")
			.setExpandRatio(0)
			.setId("changedby")
			.setEditorBinding(binder.forField(new TextField())
					.withNullRepresentation("")
					.withValidator(new BeanValidator(Item.class, "changedby"))
					.bind(CustomerOrderHeadListView::getChangedby, CustomerOrderHeadListView::setChangedby));
			
			grid.addColumn(CustomerOrderHeadListView::getStatus, new NumberRenderer())
			.setCaption("Status")
			.setExpandRatio(1)
			.setId("status")
			.setEditorBinding(binder.forField(new TextField())
					.withNullRepresentation("")
	                .withConverter(new StringToIntegerConverter("Please enter a number"))
					.withValidator(new BeanValidator(Item.class, "status"))
					.bind(CustomerOrderHeadListView::getStatus, CustomerOrderHeadListView::setStatus));
		
			// @formatter:on

		HeaderRow row1 = grid.getDefaultHeaderRow();
		HeaderRow row2 = grid.addHeaderRowAt(grid.getHeaderRowCount());
		HeaderRow row3 = grid.addHeaderRowAt(grid.getHeaderRowCount());

		fnc.createFilterField(row1, row2, row3, "customernumber", "Customer no", filterCustomerNumber, sortCustomerNumber);
		fnc.createFilterField(row1, row2, row3, "name", "Name", filterName, sortName);
		fnc.createFilterField(row1, row2, row3, "date", "Date", filterDate, sortDate);
		fnc.createFilterField(row1, row2, row3, "status", "Status", filterStatus, sortStatus);
		fnc.createFilterField(row1, row2, row3, "changedby", "Changedby", filterChangedBy, sortChangedBy);

		grid.setSizeFull();
		DataProvider<CustomerOrderHeadListView, Void> dp = DataProvider.fromCallbacks(query -> search(query.getOffset(), query.getLimit()).stream(), query -> count());
		grid.setDataProvider(dp);


		for (@SuppressWarnings("rawtypes")
		Grid.Column column : grid.getColumns()) {
			column.setSortable(false);
		}
		VerticalLayout layout = new VerticalLayout(header, grid);
		layout.setExpandRatio(grid, 1);
		setCompositionRoot(layout);
		setSizeFull();
	}

	private void initBehavior() {

		grid.asSingleSelect().addValueChangeListener(e -> updateHeader());
		btn_refresh.addClickListener(e -> {
			grid.getDataProvider().refreshAll();
		});
		btn_add.addClickListener(e -> showAddWindow());
		btn_edit.addClickListener(e -> showEditWindow());
		btn_delete.addClickListener(e -> showRemoveWindow());

		sortCustomerNumber.addValueChangeListener(e -> evaluateSort(sortCustomerNumber, "CustomerNumber"));
		sortName.addValueChangeListener(e -> evaluateSort(sortName, "Name"));
		sortDate.addValueChangeListener(e -> evaluateSort(sortDate, "Date"));
		sortStatus.addValueChangeListener(e -> evaluateSort(sortStatus, "Status"));
		sortChangedBy.addValueChangeListener(e -> evaluateSort(sortChangedBy, "ChangedBy"));

		showSort();
	}

	private Object evaluateSort(CheckBox checkBox, String fieldName) {
		Boolean val = checkBox.getValue();
		if (val) {
			selectedSort.add(fieldName);
		} else {
			selectedSort.remove(fieldName);
		}
		showSort();
		return null;
	}

	private void showSort() {

		String theSort = "";
		for (String dta : selectedSort) {
			theSort += dta;
			theSort += ",";
		}
		if (theSort.endsWith(",")) {
			theSort = theSort.substring(0, theSort.length() - 1);
		}
		filterSortOrder.setValue(theSort);
	}
	
	public int count() {

		String filterCustomerNumberStr = filterCustomerNumber.getValue() == null ? "" : filterCustomerNumber.getValue().trim();
		String filterNameStr = filterName.getValue() == null ? "" : filterName.getValue().trim();
		LocalDate filterDateStr = filterDate.getValue();
		String filterStatusStr = filterStatus.getValue() == null ? "" : filterStatus.getValue().trim();
		String filterChangedByStr = filterChangedBy.getValue() == null ? "" : filterChangedBy.getValue().trim();
		RestResponse<Long> fetched = customerOrderRepository.paginatecount(filterCustomerNumberStr, filterNameStr, filterDateStr, filterStatusStr, filterChangedByStr);
		Long numberOfItems = fetched.getEntity();
		noOfItems.setValue("Records : " + numberOfItems);
		return numberOfItems.intValue();
	}


	public Collection<CustomerOrderHeadListView> search(int offset, int limit) {

		String filterCustomerNumberStr = filterCustomerNumber.getValue() == null ? "" : filterCustomerNumber.getValue().trim();
		String filterNameStr = filterName.getValue() == null ? "" : filterName.getValue().trim();
		LocalDate filterDateStr = filterDate.getValue();
		String filterStatusStr = filterStatus.getValue() == null ? "" : filterStatus.getValue().trim();
		String filterChangedByStr = filterChangedBy.getValue() == null ? "" : filterChangedBy.getValue().trim();
		String sortOrder = filterSortOrder.getValue();
		RestResponse<List<CustomerOrderHeadListView>> fetched = customerOrderRepository.paginatesearch(offset, limit, filterCustomerNumberStr, filterNameStr, filterDateStr, filterStatusStr, filterChangedByStr, sortOrder);
		updateHeader();
		return fetched.getEntity();
	}
	
	public void refreshSearch() {
		grid.getDataProvider().refreshAll();
		grid.scrollToStart();
		grid.deselectAll();
	}


	private void updateHeader() {
		boolean selected = !grid.asSingleSelect().isEmpty();
		btn_edit.setEnabled(selected);
		btn_delete.setEnabled(selected);
	}

	private void showAddWindow() {
		currentOrderHead = null;
		CustomerOrderForm window = new CustomerOrderForm(this, customerOrderRepository, "Add", new CustomerOrderHeadListView(), CRUD_CREATE);
		getUI().addWindow(window);
	}

	private void showEditWindow() {

		SingleSelect<CustomerOrderHeadListView> selected = grid.asSingleSelect();
		Long id = selected.getValue().getId();
		RestResponse<CustomerOrderHead> fetched = customerOrderRepository.getById(id);
		// ensure order is still there
		if (fetched.getStatus().equals(200)) {
			currentOrderHead = fetched.getEntity();
			CustomerOrderForm window = new CustomerOrderForm(this, customerOrderRepository, "Edit", selected.getValue(), CRUD_EDIT); //
			getUI().addWindow(window);
		} else {
			currentOrderHead = null;
			Notification.show("ERROR", fetched.getMsg(), Notification.Type.ERROR_MESSAGE);
		}

	}

	private void showRemoveWindow() {

		SingleSelect<CustomerOrderHeadListView> selected = grid.asSingleSelect();
		Long id = selected.getValue().getId();
		// ensure order is still there
		RestResponse<CustomerOrderHead> fetched = customerOrderRepository.getById(id);
		if (fetched.getStatus().equals(200)) {
			currentOrderHead = fetched.getEntity();
			CustomerOrderForm window = new CustomerOrderForm(this, customerOrderRepository, "Delete", selected.getValue(), CRUD_DELETE);
			getUI().addWindow(window);
		} else {
			currentOrderHead = null;
			Notification.show("ERROR", fetched.getMsg(), Notification.Type.ERROR_MESSAGE);
		}
	}

	public void setCurrentOrderHead(CustomerOrderHead currentOrderHead) {
		this.currentOrderHead = currentOrderHead;
	}

	public CustomerOrderHead getCurrentOrderHead() {
		return this.currentOrderHead;
	}

}
