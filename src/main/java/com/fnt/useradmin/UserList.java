package com.fnt.useradmin;

import java.util.List;

import com.fnt.dto.UserDto;
import com.fnt.sys.Fnc;
import com.fnt.sys.RestResponse;
import com.vaadin.data.Binder;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.Button;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.SingleSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class UserList extends Composite implements View {

	private Fnc fnc = new Fnc();

	private static final long serialVersionUID = 4797579884478708862L;

	public static final int CRUD_CREATE = 1;
	public static final int CRUD_EDIT = 2;
	public static final int CRUD_DELETE = 4;

	UserRepository userRepository = new UserRepository();
	// crud
	private Button refresh = new Button("", VaadinIcons.SEARCH);
	private Button btn_add = new Button("", VaadinIcons.PLUS);
	private Button btn_edit = new Button("", VaadinIcons.PENCIL);
	private Button btn_delete = new Button("", VaadinIcons.TRASH);

	private Grid<UserDto> grid = new Grid<>();

	public UserList() {
		initLayout();
		initBehavior();
		search();
		updateHeader();
	}

	public void search() {
		RestResponse<List<UserDto>> rs = userRepository.search();
		grid.setItems(rs.getEntity());
		updateHeader();
	}

	private void initLayout() {

		HorizontalLayout buttons = new HorizontalLayout(btn_add, btn_edit, btn_delete, refresh);
		buttons.setSpacing(false);
		HorizontalLayout header = new HorizontalLayout(buttons);
		header.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		header.setSpacing(true);

		Binder<UserDto> binder = grid.getEditor().getBinder();

		// @formatter:off

		grid.addColumn(UserDto::getLogin)
			.setCaption("Login")
			.setExpandRatio(1).setId("login")
			.setEditorBinding(binder.forField(new TextField())
					.withNullRepresentation("")
					.withValidator(new BeanValidator(UserDto.class, "login"))
					.bind(UserDto::getLogin, UserDto::setLogin));
		
		// @formatter:on
		grid.setSizeFull();

		VerticalLayout layout = new VerticalLayout(header, grid);
		layout.setExpandRatio(grid, 1);
		setCompositionRoot(layout);
		setSizeFull();

	}

	private void initBehavior() {
		grid.asSingleSelect().addValueChangeListener(e -> updateHeader());
		refresh.addClickListener(e -> search());
		btn_add.addClickListener(e -> showAddWindow());
		btn_edit.addClickListener(e -> showEditWindow());
		btn_delete.addClickListener(e -> showRemoveWindow());

	}

	private Object showRemoveWindow() {
		UserForm window = new UserForm(this, userRepository, "Delete", grid.asSingleSelect().getValue(), CRUD_DELETE);
		getUI().addWindow(window);
		return null;
	}

	private Object showEditWindow() {

		SingleSelect<UserDto> selected = grid.asSingleSelect();
		String login = selected.getValue().getLogin();
		RestResponse<UserDto> fetched = userRepository.getLogin(login);
		if (fetched.getStatus().equals(200)) {
			UserDto dto = fetched.getEntity();
			UserForm window = new UserForm(this, userRepository, "Edit", dto, CRUD_EDIT);
			getUI().addWindow(window);
		} else {
			Notification.show("ERROR", fetched.getMsg(), Notification.Type.ERROR_MESSAGE);
		}
		return null;
	}

	private Object showAddWindow() {
		UserForm window = new UserForm(this, userRepository, "Add", new UserDto(), CRUD_CREATE);
		getUI().addWindow(window);
		return null;
	}

	private void updateHeader() {

		boolean selected = !grid.asSingleSelect().isEmpty();
		btn_edit.setEnabled(selected);
		btn_delete.setEnabled(selected);
	}

}
