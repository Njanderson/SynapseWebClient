package org.sagebionetworks.web.client.widget.entity.controller;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.sagebionetworks.repo.model.entity.query.EntityQueryResult;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.events.EntityUpdatedHandler;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.widget.entity.browse.FilesBrowser;
import org.sagebionetworks.web.client.widget.entity.menu.v2.Action;
import org.sagebionetworks.web.client.widget.entity.menu.v2.BulkActionMenuWidget;
import org.sagebionetworks.web.client.widget.sharing.AccessControlListModalWidget;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class BulkActionController implements BulkActionControllerView.Presenter, IsWidget {

	BulkActionControllerView view;
	PreflightController preflightController;
	SynapseClientAsync synapseClient;
	GlobalApplicationState globalApplicationState;
	AuthenticationController authenticationController;
	AccessControlListModalWidget accessControlListModalWidget;
	BulkActionMenuWidget bulkActionMenu;
	FilesBrowser filesBrowser;
	EntityUpdatedHandler handler;
	Set<EntityQueryResult> selectedEntities;
	
	@Inject
	public BulkActionController(BulkActionControllerView view,
		PreflightController preflightController,
		SynapseClientAsync synapseClient,
		GlobalApplicationState globalApplicationState,
		AuthenticationController authenticationController,
		AccessControlListModalWidget accessControlListModalWidget) {
		this.view = view;
		this.accessControlListModalWidget = accessControlListModalWidget;
		this.preflightController = preflightController;
		this.synapseClient = synapseClient;
		this.globalApplicationState = globalApplicationState;
		this.authenticationController = authenticationController;
		this.selectedEntities = new HashSet<EntityQueryResult>();
	}
	
	@Override
	public void configure(BulkActionMenuWidget bulkActionMenu, FilesBrowser filesBrowser, EntityUpdatedHandler handler) {
		this.selectedEntities.clear();
		this.bulkActionMenu = bulkActionMenu;
		this.filesBrowser = filesBrowser;
		this.handler = handler;
		refreshMenu();
	}
	
	@Override
	public void addSelected(EntityQueryResult header) {
//		adjustActions(header);
//		refreshMenu
//		selectedEntities.add(header);
	}
	
	@Override
	public void removeSelected(EntityQueryResult header) {
//		adjustActions(header);
//		refreshMenu
//		if (selectedEntities.contains(header)) {
//			selectedEntities.remove(header);
//		}
	}
	
	private void adjustActions(EntityQueryResult header) {
		// should just add or remove based on this single change instead of re-iteration
	}

	@Override
	public void refreshMenu() {
		configureDeleteAction();
	}

	private void configureDeleteAction() {
//		bulkActionMenu.setActionVisible(Action.DELETE_ENTITY, permissions.getCanDelete());
//		bulkActionMenu.setActionEnabled(Action.DELETE_ENTITY, permissions.getCanDelete());
//		bulkActionMenu.setActionText(Action.DELETE_ENTITY, DELETE_PREFIX+enityTypeDisplay);
//		bulkActionMenu.addActionListener(Action.DELETE_ENTITY, this);
	}

	@Override
	public Widget asWidget() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
