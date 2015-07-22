package org.sagebionetworks.web.client.widget.entity.controller;

import java.util.Set;

import org.sagebionetworks.repo.model.EntityBundle;
import org.sagebionetworks.repo.model.entity.query.EntityQueryResult;
import org.sagebionetworks.web.client.events.EntityUpdatedHandler;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.widget.entity.browse.FilesBrowser;
import org.sagebionetworks.web.client.widget.entity.menu.v2.Action;
import org.sagebionetworks.web.client.widget.entity.menu.v2.BulkActionMenuWidget;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface BulkActionControllerView extends IsWidget {

	public interface Presenter extends IsWidget {

		void addSelected(EntityQueryResult header);

		void removeSelected(EntityQueryResult header);

		void configure(BulkActionMenuWidget bulkActionMenu,
				FilesBrowser filesBrowser);

		void refreshMenu(Set<EntityBundle> selectedEntities);

		void onAction(Action action);

		void onDeleteEntity();

	}

	void showConfirmDialog(String title, String string, Callback callback);

	void showInfo(String string, String string2);

	void showErrorMessage(String errorEntityDeleteFailure);

}
