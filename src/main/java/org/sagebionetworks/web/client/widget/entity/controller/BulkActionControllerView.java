package org.sagebionetworks.web.client.widget.entity.controller;

import org.sagebionetworks.repo.model.entity.query.EntityQueryResult;
import org.sagebionetworks.web.client.events.EntityUpdatedHandler;
import org.sagebionetworks.web.client.widget.entity.browse.FilesBrowser;
import org.sagebionetworks.web.client.widget.entity.menu.v2.BulkActionMenuWidget;

public interface BulkActionControllerView {

	public interface Presenter {

		void configure(BulkActionMenuWidget bulkActionMenu,
				FilesBrowser filesBrowser, EntityUpdatedHandler handler);

		void refreshMenu();

		void addSelected(EntityQueryResult header);

		void removeSelected(EntityQueryResult header);

	}

}
