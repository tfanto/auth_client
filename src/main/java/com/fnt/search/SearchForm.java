package com.fnt.search;

import java.text.DecimalFormat;
import java.util.List;

import com.fnt.customerorder.CustomerOrderRepository;
import com.fnt.dto.SearchData;
import com.fnt.sys.Fnc;
import com.fnt.sys.RestResponse;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.SingleSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderRow;

public class SearchForm extends Window {

	public static final int CUSTOMERS = 1;
	public static final int ITEMS = 2;
	private int searchType = Integer.MIN_VALUE;
	private CustomerOrderRepository searchRepository;
	private TextField search1 = new TextField();
	private TextField search2 = new TextField();
	private String old_search1;
	private String old_search2;
	private String old_search3;

	private TextField searchField1;
	private TextField searchField2;
	private TextField searchField3;

	private static final long serialVersionUID = 1L;

	private Fnc fnc = new Fnc();

	private Button btn_cancel = new Button("Cancel");
	private Button btn_search = new Button("Search", VaadinIcons.SEARCH);
	private Button btn_ok = new Button("Ok", VaadinIcons.CHECK);
	private Button btn_clr = new Button("Clear");

	private static DecimalFormat df2 = new DecimalFormat(".##");

	private Grid<SearchData> grid = new Grid<>(SearchData.class);

	public SearchForm(int searchType, TextField searchField1, TextField searchField2, TextField searchField3, CustomerOrderRepository searchRepository) {
		this.searchRepository = searchRepository;
		search1.setValue(searchField1.getValue());
		search2.setValue(searchField2.getValue());
		old_search1 = searchField1.getValue();
		old_search2 = searchField2.getValue();
		this.searchField1 = searchField1;
		this.searchField2 = searchField2;
		if (searchField3 != null) {
			this.searchField3 = searchField3;
			old_search3 = searchField3.getValue();
		}

		this.searchType = searchType;
		switch (searchType) {
		case CUSTOMERS:
			break;
		case ITEMS:
			break;

		default: {
			searchType = 1;
		}
		}

		initLayout();
		initBehavior();

		btn_ok.setEnabled(false);
		String s1 = search1.getValue() == null ? "" : search1.getValue().trim();
		String s2 = search1.getValue() == null ? "" : search2.getValue().trim();
		if ((s1.length() > 0) || (s2.length() > 0)) {
			search();
		}

	}

	private void initLayout() {

		grid.setColumns("id", "description");

		HeaderRow headerRow = grid.getDefaultHeaderRow();
		headerRow.getCell("id").setComponent(fnc.createFilterField(search1));
		headerRow.getCell("description").setComponent(fnc.createFilterField(search2));
		HorizontalLayout buttons = new HorizontalLayout(btn_cancel, btn_clr, btn_search, btn_ok);
		buttons.setSpacing(true);
		VerticalLayout layout = new VerticalLayout();
		layout.addComponents(grid, buttons);
		setContent(layout);
		setModal(true);
		center();
	}

	private void initBehavior() {

		grid.asSingleSelect().addValueChangeListener(e -> select());
		btn_cancel.addClickListener(e -> {
			searchField1.setValue(old_search1);
			searchField2.setValue(old_search2);
			close();
		});
		btn_search.addClickListener(e -> search());
		btn_ok.addClickListener(e -> close());
		btn_clr.addClickListener(e -> {
			search1.setValue("");
			search2.setValue("");			
		});

	}

	private Object select() {
		boolean sel = !grid.asSingleSelect().isEmpty();
		btn_ok.setEnabled(sel);
		if (sel) {
			SingleSelect<SearchData> selected = grid.asSingleSelect();
			search1.setValue(selected.getValue().getId());
			search2.setValue(selected.getValue().getDescription());
			searchField1.setValue(search1.getValue());
			searchField2.setValue(search2.getValue());
			if (searchField3 != null) {

				String str = selected.getValue().getExtra();
				Double dbl = Double.parseDouble(str);
				String formatted = df2.format(dbl);

				searchField3.setValue(formatted);
			}
		}
		return null;
	}

	private Object search() {

		switch (searchType) {
		case CUSTOMERS: {
			RestResponse<List<SearchData>> resultSet = searchRepository.selectListCustomers(search1.getValue(), search2.getValue());
			grid.setItems(resultSet.getEntity());
			break;
		}
		case ITEMS: {
			RestResponse<List<SearchData>> resultSet = searchRepository.selectListItems(search1.getValue(), search2.getValue());
			grid.setItems(resultSet.getEntity());
			break;
		}
		}

		return null;
	}

}
