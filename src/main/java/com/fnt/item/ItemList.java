package com.fnt.item;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fnt.entity.Item;
import com.fnt.sys.Fnc;
import com.fnt.sys.RestResponse;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Composite;
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

public class ItemList extends Composite implements View {

	private Fnc fnc = new Fnc();

	private static final long serialVersionUID = 4797579884478708862L;

	public static final int CRUD_CREATE = 1;
	public static final int CRUD_EDIT = 2;
	public static final int CRUD_DELETE = 4;

	ItemRepository itemRepository = new ItemRepository();

	// crud
	private Button btn_refresh = new Button("", VaadinIcons.SEARCH);
	private Button btn_add = new Button("", VaadinIcons.PLUS);
	private Button btn_edit = new Button("", VaadinIcons.PENCIL);
	private Button btn_delete = new Button("", VaadinIcons.TRASH);
	private Label noOfItems = new Label();
	// filter
	private TextField filterItemNumber = new TextField();
	private TextField filterDescription = new TextField();
	private TextField filterOrderingPoint = new TextField();
	private TextField filterInStock = new TextField();
	private TextField filterPrice = new TextField();
	private TextField filterPurchasePrice = new TextField();
	// sorting
	private Label filterSortOrder = new Label();
	private List<String> selectedSort = new ArrayList<>();
	private CheckBox sortItemNumber = new CheckBox();
	private CheckBox sortDescription = new CheckBox();
	private CheckBox sortOrderingPoint = new CheckBox();
	private CheckBox sortInStock = new CheckBox();
	private CheckBox sortPrice = new CheckBox();
	private CheckBox sortPurchasePrice = new CheckBox();

	private Grid<Item> grid = new Grid<>();

	public ItemList() {
		initLayout();
		initBehavior();
		updateHeader();
	}

	private void initLayout() {

		HorizontalLayout buttons = new HorizontalLayout(btn_add, btn_edit, btn_delete, btn_refresh);
		buttons.setSpacing(false);
		HorizontalLayout header = new HorizontalLayout(buttons,  filterSortOrder, noOfItems);
		header.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		header.setSpacing(true);

		// grid.setColumns("itemnumber", "description", "orderingpoint", "instock",
		// "price", "purchaseprice");
		// 'Bean' has two fields: String name and Date date
		// Grid<Bean> grid = new Grid<>();
		// grid.setItems(getBeans());
		// grid.addColumn(Bean::getName).setCaption("Name");
		// DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		// Grid.Column<Bean, Date> dateColumn = grid.addColumn(Bean::getDate, new
		// DateRenderer(df));
		// dateColumn.setCaption("Date");

		// @formatter:off
	       Binder<Item> binder = grid.getEditor().getBinder();
	       
			grid.addColumn(Item::getItemnumber)
				.setCaption("Itemnumber")
				.setExpandRatio(0)
				.setId("itemnumber")
				.setEditorBinding(binder.forField(new TextField())
						.withNullRepresentation("")
						.withValidator(new BeanValidator(Item.class, "itemnumber"))
						.bind(Item::getItemnumber, Item::setItemnumber));

			grid.addColumn(Item::getDescription)
			.setCaption("Description")
			.setExpandRatio(0)
			.setId("description")
			.setEditorBinding(binder.forField(new TextField())
					.withNullRepresentation("")
					.withValidator(new BeanValidator(Item.class, "description"))
					.bind(Item::getDescription, Item::setDescription));
			
			grid.addColumn(Item::getOrderingpoint)
			.setCaption("Orderingpoint")
			.setExpandRatio(0)
			.setId("orderingpoint")
			.setEditorBinding(binder.forField(new TextField())
					.withNullRepresentation("")
	                .withConverter(new StringToIntegerConverter("Please enter a number"))
					.withValidator(new BeanValidator(Item.class, "orderingpoint"))
					.bind(Item::getOrderingpoint, Item::setOrderingpoint));
			
			grid.addColumn(Item::getInstock)
			.setCaption("instock")
			.setExpandRatio(0)
			.setId("instock")
			.setEditorBinding(binder.forField(new TextField())
					.withNullRepresentation("")
	                .withConverter(new StringToIntegerConverter("Please enter a number"))
					.withValidator(new BeanValidator(Item.class, "instock"))
					.bind(Item::getInstock, Item::setInstock));

			grid.addColumn(Item::getPrice, new NumberRenderer(NumberFormat.getCurrencyInstance()))
			.setCaption("Price")
			.setExpandRatio(0)
			.setId("price")
			.setEditorBinding(binder.forField(new TextField())
					.withNullRepresentation("")
	                .withConverter(new StringToDoubleConverter("Please enter a number"))
					.withValidator(new BeanValidator(Item.class, "price"))
					.bind(Item::getPrice, Item::setPrice));
			
			grid.addColumn(Item::getPurchaseprice, new NumberRenderer(NumberFormat.getCurrencyInstance()))
			.setCaption("Purchaseprice")
			.setExpandRatio(1)
			.setId("purchaseprice")
			.setEditorBinding(binder.forField(new TextField())
					.withNullRepresentation("")
	                .withConverter(new StringToDoubleConverter("Please enter a number"))
					.withValidator(new BeanValidator(Item.class, "purchaseprice"))
					.bind(Item::getPurchaseprice, Item::setPurchaseprice));
		
		
			// @formatter:on

		HeaderRow row1 = grid.getDefaultHeaderRow();
		HeaderRow row2 = grid.addHeaderRowAt(grid.getHeaderRowCount());
		HeaderRow row3 = grid.addHeaderRowAt(grid.getHeaderRowCount());

		fnc.createFilterField(row1, row2, row3, "itemnumber", "Itemnumber", filterItemNumber, sortItemNumber);
		fnc.createFilterField(row1, row2, row3, "description", "Description", filterDescription, sortDescription);
		fnc.createFilterField(row1, row2, row3, "orderingpoint", "Orderingpoint", sortOrderingPoint);
		fnc.createFilterField(row1, row2, row3, "instock", "In stock", sortInStock);
		fnc.createFilterField(row1, row2, row3, "price", "Price", sortPrice);
		fnc.createFilterField(row1, row2, row3, "purchaseprice", "Purchaseprice", sortPurchasePrice);

		grid.setSizeFull();
		DataProvider<Item, Void> dp = DataProvider.fromCallbacks(query -> search(query.getOffset(), query.getLimit()).stream(), query -> count());
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

	private int count() {
		String itemNumberStr = filterItemNumber.getValue() == null ? "" : filterItemNumber.getValue().trim();
		String descriptionStr = filterDescription.getValue() == null ? "" : filterDescription.getValue().trim();

		RestResponse<Long> fetched = itemRepository.paginatecount(itemNumberStr, descriptionStr);
		Long numberOfItems = fetched.getEntity();
		noOfItems.setValue("Records : " + numberOfItems);
		return numberOfItems.intValue();
	}

	private Collection<Item> search(int offset, int limit) {
		String itemNumberStr = filterItemNumber.getValue() == null ? "" : filterItemNumber.getValue().trim();
		String descriptionStr = filterDescription.getValue() == null ? "" : filterDescription.getValue().trim();
		String sortOrder = filterSortOrder.getValue();
		RestResponse<List<Item>> fetched = itemRepository.paginatesearch(offset, limit, itemNumberStr, descriptionStr, sortOrder);
		updateHeader();
		return fetched.getEntity();
	}
	
	public void refreshSearch() {
		grid.getDataProvider().refreshAll();
		grid.scrollToStart();
		grid.deselectAll();
	}

	private void initBehavior() {
		grid.asSingleSelect().addValueChangeListener(e -> updateHeader());
		btn_refresh.addClickListener(e -> {
			grid.getDataProvider().refreshAll();
		});
		btn_add.addClickListener(e -> showAddWindow());
		btn_edit.addClickListener(e -> showEditWindow());
		btn_delete.addClickListener(e -> showRemoveWindow());

		sortItemNumber.addValueChangeListener(e -> evaluateSort(sortItemNumber, "ItemNumber"));
		sortDescription.addValueChangeListener(e -> evaluateSort(sortDescription, "Description"));
		sortOrderingPoint.addValueChangeListener(e -> evaluateSort(sortOrderingPoint, "OrderingPoint"));
		sortInStock.addValueChangeListener(e -> evaluateSort(sortInStock, "InStock"));
		sortPrice.addValueChangeListener(e -> evaluateSort(sortPrice, "Price"));
		sortPurchasePrice.addValueChangeListener(e -> evaluateSort(sortPurchasePrice, "PurchasePrice"));
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

	/*
	 * public void search() {
	 * 
	 * String itemNumberStr = filterItemNumber.getValue() == null ? "" :
	 * filterItemNumber.getValue().trim(); String descriptionStr =
	 * filterDescription.getValue() == null ? "" :
	 * filterDescription.getValue().trim();
	 * 
	 * // String orderingPointStr = filterOrderingPoint.getValue() == null ? "" : //
	 * filterOrderingPoint.getValue().trim(); // String inStockStr =
	 * filterInStock.getValue() == null ? "" : // filterInStock.getValue().trim();
	 * 
	 * // String priceStr = filterPrice.getValue() == null ? "" : //
	 * filterPrice.getValue().trim(); // String purchasePriceStr =
	 * filterPurchasePrice.getValue() == null ? "" : //
	 * filterPurchasePrice.getValue().trim();
	 * 
	 * String sortOrder = filterSortOrder.getValue();
	 * 
	 * RestResponse<List<Item>> fetched = itemRepository.search(itemNumberStr,
	 * descriptionStr, sortOrder); if (fetched.getStatus().equals(200)) {
	 * grid.setItems(fetched.getEntity()); updateHeader(); } else {
	 * Notification.show("ERROR", fetched.getMsg(),
	 * Notification.Type.ERROR_MESSAGE); } }
	 */

	private void updateHeader() {
		boolean selected = !grid.asSingleSelect().isEmpty();
		btn_edit.setEnabled(selected);
		btn_delete.setEnabled(selected);
	}

	private void showAddWindow() {
		ItemForm window = new ItemForm(this, itemRepository, "Add", new Item(), CRUD_CREATE);
		getUI().addWindow(window);
	}

	private void showEditWindow() {
		// get from the server, it could have been removed
		SingleSelect<Item> selected = grid.asSingleSelect();
		Long id = selected.getValue().getId();
		RestResponse<Item> fetched = itemRepository.getById(id);

		if (fetched.getStatus().equals(200)) {
			Item obj = fetched.getEntity();
			ItemForm window = new ItemForm(this, itemRepository, "Edit", obj, CRUD_EDIT);
			getUI().addWindow(window);
		} else {
			Notification.show("ERROR", fetched.getMsg(), Notification.Type.ERROR_MESSAGE);
		}
	}

	private void showRemoveWindow() {
		ItemForm window = new ItemForm(this, itemRepository, "Delete", grid.asSingleSelect().getValue(), CRUD_DELETE);
		getUI().addWindow(window);
	}

}
