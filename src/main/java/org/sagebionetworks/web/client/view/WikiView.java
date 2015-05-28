package org.sagebionetworks.web.client.view;

import org.sagebionetworks.web.client.SynapsePresenter;
import org.sagebionetworks.web.client.SynapseView;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface WikiView extends IsWidget, SynapseView {
	public void showPage(String html);
	
	/**
	 * Set this view's presenter
	 * @param presenter
	 */
	public void setPresenter(Presenter presenter);	
	
	public interface Presenter extends SynapsePresenter {
		public void loadSourceContent(String pageId);
	}

	public void setSynAlertWidget(Widget asWidget);

}
