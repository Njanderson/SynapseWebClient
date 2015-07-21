package org.sagebionetworks.web.client.widget.entity.menu.v2;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class BulkActionMenuWidgetViewImpl implements BulkActionMenuWidgetView {

	Widget widget;
	
	@UiField
	SimplePanel controllerContainer;
	
	public interface Binder extends UiBinder<Widget, BulkActionMenuWidgetViewImpl> {}
	
	@Inject
	public BulkActionMenuWidgetViewImpl(Binder binder) {
		widget = binder.createAndBindUi(this);
	}
	
	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setControllerWidget(IsWidget controller) {
		controllerContainer.setWidget(controller);
	}

}
