package org.sagebionetworks.web.client.widget.entity.controller;

import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.Pre;
import org.gwtbootstrap3.extras.bootbox.client.Bootbox;
import org.gwtbootstrap3.extras.bootbox.client.callback.PromptCallback;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.utils.Callback;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class EntityActionControllerViewImpl implements
		EntityActionControllerView {

	public interface Binder extends
			UiBinder<Widget, EntityActionControllerViewImpl> {
	}

	@UiField
	SimplePanel aclPanel;
	@UiField
	SimplePanel markdownEditorPanel;
	@UiField
	SimplePanel provenanceEditorPanel;
	@UiField
	SimplePanel storageLocationEditorPanel;
	@UiField
	Modal infoDialog;
	@UiField
	Pre infoDialogText;
	
	Widget widget;
	
	@Inject
	public EntityActionControllerViewImpl(Binder binder){
		widget = binder.createAndBindUi(this);
	}

	@Override
	public void showErrorMessage(String message) {
		DisplayUtils.showErrorMessage(message);
	}

	@Override
	public void showConfirmDialog(String title, String string, Callback callback) {
		DisplayUtils.showConfirmDialog(title, string, callback);
	}

	@Override
	public void showInfo(String tile, String message) {
		DisplayUtils.showInfo(tile, message);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void addAccessControlListModalWidget(
			IsWidget accessControlListModalWidget) {
		aclPanel.add(accessControlListModalWidget);
	}
	
	@Override
	public void addMarkdownEditorModalWidget(IsWidget modalWidget) {
		markdownEditorPanel.add(modalWidget);
	}
	
	@Override
	public void showPromptDialog(String prompt, PromptCallback callback) {
		Bootbox.prompt(prompt, callback);
	}
	
	@Override
	public void showInfoDialog(String header, String message) {
		infoDialog.setTitle(header);
		infoDialogText.setText(message);
		infoDialog.show();
	}

	@Override
	public void addProvenanceEditorModalWidget(Widget provWidget) {
		provenanceEditorPanel.setWidget(provWidget);
	}

	@Override
	public void addStorageLocationModalWidget(Widget w) {
		storageLocationEditorPanel.setWidget(w);
	}
}
