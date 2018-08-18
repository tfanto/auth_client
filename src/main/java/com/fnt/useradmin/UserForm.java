package com.fnt.useradmin;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import com.fnt.dto.UserDto;
import com.fnt.sys.RestResponse;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.ValidationException;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class UserForm extends Window {

	private static final long serialVersionUID = 1L;
	int crudFunction = 1;
	UserRepository userRepository;
	UserList owner;

	private TextField login = new TextField("User");
	private CheckBox chkAdmin = new CheckBox("Admin");
	private CheckBox chkUser = new CheckBox("User");
	private CheckBox chkGuest = new CheckBox("Guest");
	private CheckBox chkConfirmed = new CheckBox("Confirmed");
	private CheckBox chkBlocked = new CheckBox("Blocked");
	private DateField lastChanged = new DateField();

	private Button btn_cancel = new Button("Cancel");
	private Button btn_save = new Button("Ok", VaadinIcons.CHECK);
	// private Button btn_refresh = new Button("Refresh"); // does not work as
	// expected

	public UserForm(UserList userList, UserRepository userRepository, String string, UserDto user, int crudFunction) {
		this.owner = userList;
		this.userRepository = userRepository;
		this.crudFunction = crudFunction;
		String captionStr = "";
		login.setEnabled(false);
		lastChanged.setEnabled(false);
		switch (crudFunction) {
		case UserList.CRUD_CREATE:
			login.setEnabled(true);
			captionStr = "Create";
			chkConfirmed.setValue(false);
			chkBlocked.setValue(false);
			lastChanged.setValue(LocalDate.now(ZoneId.of("UTC")));
			break;
		case UserList.CRUD_EDIT:
			captionStr = "Edit";
			break;
		case UserList.CRUD_DELETE:
			captionStr = "Delete";
			btn_save.setCaption("Confirm delete");
			break;
		}

		initLayout(captionStr);
		initBehavior(user);
	}

	private void initLayout(String captionStr) {
		// setCaption(captionStr);
		setResizable(false);

		btn_save.addStyleName(ValoTheme.BUTTON_PRIMARY);

		HorizontalLayout buttons = new HorizontalLayout(btn_cancel, btn_save);
		// HorizontalLayout buttons = new HorizontalLayout(btn_refresh, btn_cancel,
		// btn_save);
		buttons.setSpacing(true);

		VerticalLayout checkBoxes = new VerticalLayout();
		checkBoxes.addComponents(chkAdmin, chkUser, chkGuest);

		GridLayout formLayout = new GridLayout(1, 5, login, checkBoxes, chkConfirmed, chkBlocked, lastChanged);
		formLayout.setMargin(true);
		formLayout.setSpacing(true);

		VerticalLayout layout = new VerticalLayout(formLayout, buttons);
		layout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
		setContent(layout);
		setModal(true);
		center();
	}

	private void updateRoles(UserDto user) {

	}

	private void initBehavior(UserDto user) {
		BeanValidationBinder<UserDto> binder = new BeanValidationBinder<>(UserDto.class);
		binder.bindInstanceFields(this);
		binder.readBean(user);

		List<String> roles = user.getRoles();
		if (roles.contains("ADMIN")) {
			chkAdmin.setValue(true);
		} else {
			chkAdmin.setValue(false);
		}
		if (roles.contains("USER")) {
			chkUser.setValue(true);
		} else {
			chkUser.setValue(false);
		}
		if (roles.contains("GUEST")) {
			chkGuest.setValue(true);
		} else {
			chkGuest.setValue(false);
		}

		if (crudFunction == 1) {
			chkBlocked.setValue(false);
			chkConfirmed.setValue(false);
			lastChanged.setValue(LocalDate.now(ZoneId.of("UTC")));
		} else {
			chkBlocked.setValue(user.getBlocked());
			chkConfirmed.setValue(user.getConfirmed());
			lastChanged.setValue(user.getLastlogin().toLocalDate());
		}

		btn_cancel.addClickListener(e -> close());
		btn_save.addClickListener(e -> {
			try {

				binder.validate();
				binder.writeBean(user);
				RestResponse<UserDto> rs = null;
				switch (crudFunction) {
				case UserList.CRUD_CREATE:
					user.clearRoles();
					if (chkAdmin.getValue()) {
						user.addRole("ADMIN");
					}
					if (chkUser.getValue()) {
						user.addRole("USER");
					}
					if (chkGuest.getValue()) {
						user.addRole("GUEST");
					}
					user.setBlocked(chkBlocked.getValue());
					user.setConfirmed(chkConfirmed.getValue());
					rs = userRepository.create(user);
					break;
				case UserList.CRUD_EDIT:
					user.clearRoles();
					if (chkAdmin.getValue()) {
						user.addRole("ADMIN");
					}
					if (chkUser.getValue()) {
						user.addRole("USER");
					}
					if (chkGuest.getValue()) {
						user.addRole("GUEST");
					}
					user.setBlocked(chkBlocked.getValue());
					user.setConfirmed(chkConfirmed.getValue());
					rs = userRepository.update(user);
					break;
				case UserList.CRUD_DELETE:
					// here all rules will be removed anyway
					rs = userRepository.delete(user);
					break;
				default: {
					return;
				}
				}
				if (!rs.getStatus().equals(200)) {
					Notification.show("ERROR", rs.getMsg(), Notification.Type.ERROR_MESSAGE);
				} else {
					close();
					owner.search();
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
