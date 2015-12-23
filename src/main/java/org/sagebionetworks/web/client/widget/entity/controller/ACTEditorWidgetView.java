package org.sagebionetworks.web.client.widget.entity.controller;

import org.sagebionetworks.repo.model.EntityBundle;

import com.google.gwt.user.client.ui.IsWidget;

public interface ACTEditorWidgetView extends IsWidget {
	public interface Presenter {

		void configure(EntityBundle bundle);

		void validateAndGrantAccess();
		
	}

	void setSuggestWidget(IsWidget asWidget);

	void setSynAlertWidget(IsWidget asWidget);

	String getAccessRequirementId();

	void addAccessRequirement(String id);

	void showInfo(String title, String message);

	void show();
	
	void clear();
	
	void hide();

	void setAccessRequirementText(String text);
}
