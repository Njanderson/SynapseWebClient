package org.sagebionetworks.web.client.view;

import org.sagebionetworks.web.client.SynapseView;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface ProjectsHomeView extends IsWidget, SynapseView {
	
	/**
	 * Set this view's presenter
	 * @param presenter
	 */
	public void setPresenter(Presenter presenter);
		
	public interface Presenter {

		void createProject(String name);
		
	}

	void setSynAlertWidget(Widget synAlert);

}
