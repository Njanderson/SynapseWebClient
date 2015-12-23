package org.sagebionetworks.web.client.widget.entity.controller;

import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.TextBox;
import org.sagebionetworks.web.client.DisplayUtils;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ACTEditorWidgetViewImpl implements ACTEditorWidgetView {

	public interface ACTEditorWidgetViewImplUiBinder extends UiBinder<Widget, ACTEditorWidgetViewImpl> {}
	
	@UiField
	ListBox arList;
	@UiField
	SimplePanel suggestBoxPanel;
	@UiField
	TextBox arText;
	@UiField
	SimplePanel synAlertPanel;
	@UiField
	Modal modal;
	
	Widget widget;
	
	@Inject
	public ACTEditorWidgetViewImpl(ACTEditorWidgetViewImplUiBinder binder) {
		widget = binder.createAndBindUi(this);
	}
	
	@Override
	public Widget asWidget() {
		return widget;
	}


	@Override
	public void setSuggestWidget(IsWidget asWidget) {
		suggestBoxPanel.setWidget(asWidget);
	}

	@Override
	public void setSynAlertWidget(IsWidget asWidget) {
		synAlertPanel.setWidget(asWidget);
	}

	@Override
	public String getAccessRequirementId() {
		return arList.getSelectedValue();
	}

	@Override
	public void addAccessRequirement(String id) {
		arList.addItem(id);
	}

	@Override
	public void showInfo(String title, String message) {
		DisplayUtils.showInfo(title, message);
	}
	
	@Override
	public void setAccessRequirementText(String text) {
		arText.setText(text);
	}

	@Override
	public void show() {
		modal.show();
	}

	@Override
	public void clear() {
		arList.clear();
		arText.clear();
	}

	@Override
	public void hide() {
		modal.hide();
	}
}
