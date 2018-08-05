package com.fnt.sys;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

public class Fnc {

	public HorizontalLayout createFilterField(TextField field) {
		HorizontalLayout hl = new HorizontalLayout();
		field.addStyleName(ValoTheme.TEXTFIELD_TINY);
		hl.addComponent(field);
		return hl;
	}

	public void createFilterField(HeaderRow row1, HeaderRow row2, HeaderRow row3, String columnname, String caption, CheckBox chk) {
		Label lbl = new Label(caption);
		row1.getCell(columnname).setComponent(lbl);
		row3.getCell(columnname).setComponent(chk);
	}

	public void createFilterField(HeaderRow row1, HeaderRow row2, HeaderRow row3, String columnname, String caption, TextField field, CheckBox chk) {
		Label lbl = new Label(caption);
		field.addStyleName(ValoTheme.TEXTFIELD_TINY);
		row1.getCell(columnname).setComponent(lbl);
		row2.getCell(columnname).setComponent(field);
		row3.getCell(columnname).setComponent(chk);
	}

	public void createFilterField(HeaderRow row1, HeaderRow row2, HeaderRow row3, String columnname, String caption, DateField field, CheckBox chk) {
		field.addStyleName(ValoTheme.DATEFIELD_TINY);
		row1.getCell(columnname).setComponent(new Label(caption));
		row2.getCell(columnname).setComponent(field);
		row3.getCell(columnname).setComponent(chk);
	}

	public HorizontalLayout createPrompt(TextField fld, Button btn) {
		HorizontalLayout layout = new HorizontalLayout();
		fld.addStyleName(ValoTheme.TEXTFIELD_TINY);
		btn.addStyleName(ValoTheme.BUTTON_TINY);
		layout.setSpacing(false);
		layout.addComponent(fld);
		layout.addComponent(btn);
		layout.setComponentAlignment(btn, Alignment.BOTTOM_LEFT);
		return layout;
	}

	public HorizontalLayout createPrompt(TextField fld1, TextField fld2, Button btn) {
		HorizontalLayout layout = new HorizontalLayout();
		fld1.addStyleName(ValoTheme.TEXTFIELD_TINY);
		fld2.addStyleName(ValoTheme.TEXTFIELD_TINY);
		btn.addStyleName(ValoTheme.BUTTON_TINY);
		layout.setSpacing(false);
		layout.addComponent(fld1);
		layout.addComponent(fld2);
		layout.addComponent(btn);
		layout.setComponentAlignment(btn, Alignment.BOTTOM_LEFT);
		return layout;
	}

	public String formatAppMsg(String appMsg) {
		if (appMsg == null)
			return null;
		if (appMsg.startsWith("["))
			appMsg = appMsg.substring(1);
		if (appMsg.endsWith("]"))
			appMsg = appMsg.substring(0, appMsg.length() - 1);
		String parts[] = appMsg.split(",");
		String ret = "";
		for (int i = 0; i < parts.length; i++) {
			ret += parts[i];
			ret += "\n";
		}
		return ret;
	}

	public VerticalLayout createLineFragment(String caption, Component component) {
		VerticalLayout vertical = new VerticalLayout();
		HorizontalLayout captionLayout = new HorizontalLayout();
		HorizontalLayout dataLayout = new HorizontalLayout();
		captionLayout.setSpacing(false);
		dataLayout.setSpacing(false);
		vertical.addComponent(captionLayout);
		vertical.addComponent(dataLayout);
		Label label = new Label(caption);
		captionLayout.addComponent(label);
		dataLayout.addComponent(component);
		return vertical;
	}

	public String getToken(VaadinSession ses) {
		String jwe = null;
		if (ses != null) {
			Object jweObj = ses.getAttribute("jwe");
			if (jweObj instanceof String) {
				jwe = String.valueOf(jweObj);
				return jwe;
			}
		}
		return "";
	}

}
