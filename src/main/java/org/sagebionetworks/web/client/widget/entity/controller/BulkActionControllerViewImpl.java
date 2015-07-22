package org.sagebionetworks.web.client.widget.entity.controller;

import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.utils.Callback;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class BulkActionControllerViewImpl implements BulkActionControllerView {

	public interface Binder extends
	UiBinder<Widget, BulkActionControllerViewImpl> {}
	
	Widget widget;
	
	@Inject
	public BulkActionControllerViewImpl(Binder binder) {
		widget = binder.createAndBindUi(this);
	}
	
	@Override
	public Widget asWidget() {
		return widget;
	}
	
	@Override
	public void showConfirmDialog(String title, String string, Callback callback) {
		DisplayUtils.showConfirmDialog(title, string, callback);
	}

	@Override
	public void showInfo(String title, String message) {
		DisplayUtils.showInfo(title, message);
	}

	@Override
	public void showErrorMessage(String errorMessage) {
		DisplayUtils.showErrorMessage(errorMessage);
	}

}
