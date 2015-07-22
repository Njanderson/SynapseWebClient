package org.sagebionetworks.web.client.widget.entity.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.sagebionetworks.repo.model.EntityBundle;
import org.sagebionetworks.repo.model.auth.UserEntityPermissions;
import org.sagebionetworks.repo.model.entity.query.EntityQueryResult;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.widget.entity.browse.FilesBrowser;
import org.sagebionetworks.web.client.widget.entity.menu.v2.Action;
import org.sagebionetworks.web.client.widget.entity.menu.v2.ActionMenuWidget.ActionListener;
import org.sagebionetworks.web.client.widget.entity.menu.v2.BulkActionMenuWidget;
import org.sagebionetworks.web.client.widget.sharing.AccessControlListModalWidget;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class BulkActionController implements BulkActionControllerView.Presenter, ActionListener {

	BulkActionControllerView view;
	PreflightController preflightController;
	SynapseClientAsync synapseClient;
	GlobalApplicationState globalApplicationState;
	AuthenticationController authenticationController;
	AccessControlListModalWidget accessControlListModalWidget;
	BulkActionMenuWidget bulkActionMenu;
	FilesBrowser filesBrowser;
	Set<EntityBundle> selectedEntities;
	boolean isUserAuthenticated;
	
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
		this.selectedEntities = new HashSet<EntityBundle>();
	}
	
	@Override
	public void configure(BulkActionMenuWidget bulkActionMenu, FilesBrowser filesBrowser) {
		this.selectedEntities.clear();
		this.bulkActionMenu = bulkActionMenu;
		this.filesBrowser = filesBrowser;
		isUserAuthenticated = authenticationController.isLoggedIn();
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
	public void refreshMenu(Set<EntityBundle> selectedEntities) {
		if (selectedEntities != null) {
			this.selectedEntities = selectedEntities;
			boolean canDelete = true;
			for (EntityBundle entity: selectedEntities) {
				UserEntityPermissions permissions = entity.getPermissions();
				canDelete = canDelete & permissions.getCanDelete();
			}
			configureDeleteAction(canDelete);
		}
	}
	
	@Override
	public void onAction(Action action) {
		switch(action){
		case DELETE_ENTITY:
			onDeleteEntity();
			break;
		default:
			break;
		}
	}
	
	private void configureDeleteAction(boolean canDelete) {
		bulkActionMenu.setActionVisible(Action.DELETE_ENTITY, canDelete);
		bulkActionMenu.setActionEnabled(Action.DELETE_ENTITY,canDelete);
		bulkActionMenu.setActionText(Action.DELETE_ENTITY, "Delete");
		bulkActionMenu.addActionListener(Action.DELETE_ENTITY, this);
	}
	
	@Override
	public void onDeleteEntity() {
		GWT.debugger();
		// Confirm the delete with the user.
		view.showConfirmDialog("Confirm Delete", "Are you sure you want to delete " + selectedEntities.toString() + "?", new Callback() {
			@Override
			public void invoke() {
				postConfirmedDeleteEntity();
			}
		});
	}

	/**
	 * Called after the user has confirmed the delete of the entity.
	 */
	public void postConfirmedDeleteEntity() {
		// The user has confirmed the delete, the next step is the preflight check.
		final Set<String> deletedEntities = new TreeSet<String>();
		for (final EntityBundle entity: selectedEntities) {
			preflightController.checkDeleteEntity(entity, new Callback() {
				@Override
				public void invoke() {
					synapseClient.deleteEntityById(entity.getEntity().getId(), new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							deletedEntities.add(entity.getEntity().getName());
							view.showInfo("Deletion", entity.getEntity().getName() + "was successfully deleted"); 
						}
						@Override
						public void onFailure(Throwable caught) {
							view.showErrorMessage(DisplayConstants.ERROR_ENTITY_DELETE_FAILURE);			
						}
					});
				}
			});
		}		
	}
	
	/**
	 * After all checks have been made we can do the actual entity delete.
	 */


	@Override
	public Widget asWidget() {
		return view.asWidget();
	}
	
}
