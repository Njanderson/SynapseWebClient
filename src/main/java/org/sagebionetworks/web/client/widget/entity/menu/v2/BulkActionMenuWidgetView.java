package org.sagebionetworks.web.client.widget.entity.menu.v2;

import org.gwtbootstrap3.client.ui.constants.IconType;

import com.google.gwt.user.client.ui.IsWidget;


public interface BulkActionMenuWidgetView extends IsWidget {
	public interface Presenter extends IsWidget {

		void addControllerWidget(IsWidget controller);

		void setActionEnabled(Action action, boolean enabled);

		void setActionVisible(Action action, boolean visible);

		void setActionText(Action action, String text);

		void setActionIcon(Action action, IconType icon);
		
	}

	void setControllerWidget(IsWidget controller);

}
