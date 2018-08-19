package com.fnt.ui;

import com.vaadin.navigator.View;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Label;

public class DefaultView extends Composite implements View {

	private static final long serialVersionUID = -6227569496408485760L;

	public DefaultView() {
		setCompositionRoot(new Label(""));
	}

}
