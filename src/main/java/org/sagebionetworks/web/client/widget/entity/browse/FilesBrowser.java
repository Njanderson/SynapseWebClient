package org.sagebionetworks.web.client.widget.entity.browse;

import java.util.Set;

import org.sagebionetworks.repo.model.Entity;
import org.sagebionetworks.repo.model.EntityBundle;
import org.sagebionetworks.repo.model.Folder;
import org.sagebionetworks.repo.model.entity.query.EntityQueryResult;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.PortalGinInjector;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.UploadView;
import org.sagebionetworks.web.client.cookie.CookieProvider;
import org.sagebionetworks.web.client.events.EntityUpdatedEvent;
import org.sagebionetworks.web.client.events.EntityUpdatedHandler;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.widget.SynapseWidgetPresenter;
import org.sagebionetworks.web.client.widget.entity.controller.BulkActionController;
import org.sagebionetworks.web.client.widget.entity.controller.EntityActionController;
import org.sagebionetworks.web.client.widget.entity.menu.v2.Action;
import org.sagebionetworks.web.client.widget.entity.menu.v2.ActionMenuWidget;
import org.sagebionetworks.web.client.widget.entity.menu.v2.ActionMenuWidget.ActionListener;
import org.sagebionetworks.web.client.widget.entity.menu.v2.BulkActionMenuWidget;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class FilesBrowser implements FilesBrowserView.Presenter, SynapseWidgetPresenter {
	
	private FilesBrowserView view;
	private String configuredEntityId;
	private SynapseClientAsync synapseClient;
	private EntityUpdatedHandler entityUpdatedHandler;
	GlobalApplicationState globalApplicationState;
	AuthenticationController authenticationController;
	CookieProvider cookies;
	boolean isCertifiedUser,canCertifiedUserAddChild;
	private String currentFolderEntityId;
	private EntityTreeBrowser entityTreeBrowser;
	private PortalGinInjector ginInjector;
	
	@Inject
	public FilesBrowser(FilesBrowserView view,
			SynapseClientAsync synapseClient,
			GlobalApplicationState globalApplicationState,
			AuthenticationController authenticationController,
			CookieProvider cookies, EntityTreeBrowser entityTreeBrowser,
			PortalGinInjector ginInjector) {
		this.ginInjector = ginInjector;
		this.entityTreeBrowser = entityTreeBrowser;
		this.view = view;		
		this.synapseClient = synapseClient;
		this.globalApplicationState = globalApplicationState;
		this.authenticationController = authenticationController;
		this.cookies = cookies;
		view.setTreeBrowserWidget(entityTreeBrowser);
		view.setPresenter(this);
	}	
	
	/**
	 * Configure tree view with given entityId's children as start set
	 * @param entityId
	 */
	public void configure(String entityId, boolean canCertifiedUserAddChild, boolean isCertifiedUser) {
		view.clear();
		this.configuredEntityId = entityId;
		this.isCertifiedUser = isCertifiedUser;
		this.canCertifiedUserAddChild = canCertifiedUserAddChild;
		view.configure(canCertifiedUserAddChild);
		entityTreeBrowser.configure(entityId, new Callback() {
			@Override
			public void invoke() {
				updateBulkActionMenu();
			}
			
		});
		currentFolderEntityId = null;
	}
	
	@Override
	public void fireEntityUpdatedEvent() {
		if (entityUpdatedHandler != null)
			entityUpdatedHandler.onPersistSuccess(new EntityUpdatedEvent());
	}
	
	public void setEntityUpdatedHandler(EntityUpdatedHandler handler) {
		this.entityUpdatedHandler = handler;
	}

	@Override
	public Widget asWidget() {
		view.setPresenter(this);
		return view.asWidget();
	}

	@Override
	public void uploadButtonClicked() {
		uploadButtonClicked(configuredEntityId, view, synapseClient, authenticationController, isCertifiedUser);
	}

	/**
	 * Check for user certification passing record.  NOTE: This should be removed after certification is required 
	 * (we will check the permissions up front and block on click), 
	 * and calls to this method should be replaced with a direct call to "view.showUploadDialog(entityId);"
	 * @param entityId
	 * @param view
	 * @param synapseClient
	 * @param cookies
	 * @param authenticationController
	 * @param isCertificationRequired
	 */
	public static void uploadButtonClicked(
			final String entityId, 
			final UploadView view,
			SynapseClientAsync synapseClient,
			AuthenticationController authenticationController,
			final boolean isCertifiedUser) {
		if (isCertifiedUser)
			view.showUploadDialog(entityId);
		else
			view.showQuizInfoDialog();
	}
	
	@Override
	public void addFolderClicked() {
		if (isCertifiedUser)
			createFolder();
		else
			view.showQuizInfoDialog();
	}
	
	
	public void createFolder() {
		Folder folder = new Folder();
		folder.setParentId(configuredEntityId);
		folder.setEntityType(Folder.class.getName());

		synapseClient.createOrUpdateEntity(folder, null, true, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String newId) {
				currentFolderEntityId = newId;
				view.showFolderEditDialog(newId);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				if(!DisplayUtils.handleServiceException(caught, globalApplicationState, authenticationController.isLoggedIn(), view))
					view.showErrorMessage(DisplayConstants.ERROR_FOLDER_CREATION_FAILED);
			}
		});
	}
	
	@Override
	public void deleteFolder(boolean skipTrashCan) {
		synapseClient.deleteEntityById(currentFolderEntityId, skipTrashCan, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void na) {
				//folder is deleted when folder creation is canceled.  refresh the tree for updated information
				entityTreeBrowser.configure(configuredEntityId, new Callback() {
					@Override
					public void invoke() {
						updateBulkActionMenu();
					}
					
				});
				view.setNewFolderDialogVisible(false);
			}
			@Override
			public void onFailure(Throwable caught) {
				view.showErrorMessage(DisplayConstants.ERROR_FOLDER_DELETE_FAILED);
			}
		});
	}
	
	public void updateFolderName(final Folder folder) {
		synapseClient.updateEntity(folder, new AsyncCallback<Entity>() {
			@Override
			public void onSuccess(Entity result) {
				view.showInfo("Folder '" + folder.getName() + "' Added", "");
				entityTreeBrowser.configure(configuredEntityId, new Callback() {
					@Override
					public void invoke() {
						updateBulkActionMenu();
					}
					
				});
				view.setNewFolderDialogVisible(false);
			}
			@Override
			public void onFailure(Throwable caught) {
				view.showErrorMessage(DisplayConstants.ERROR_FOLDER_RENAME_FAILED);
			}
		});
	}
	
	@Override
	public void updateFolderName(final String newFolderName) {
		synapseClient.getEntity(currentFolderEntityId, new AsyncCallback<Entity>() {
			@Override
			public void onSuccess(Entity result) {
				Folder folder = (Folder) result;
				folder.setName(newFolderName);
				updateFolderName(folder);
			}
			@Override
			public void onFailure(Throwable caught) {
				if(!DisplayUtils.handleServiceException(caught, globalApplicationState, authenticationController.isLoggedIn(), view))
					view.showErrorMessage(DisplayConstants.ERROR_FOLDER_CREATION_FAILED);
			}
		});
	}
	
	/**
	 * For testing purposes
	 * @return
	 */
	public String getCurrentFolderEntityId() {
		return currentFolderEntityId;
	};
	
	/**
	 * For testing purposes
	 * @return
	 */
	public void setCurrentFolderEntityId(String currentFolderEntityId) {
		this.currentFolderEntityId = currentFolderEntityId;
	}

	public void showUploadFile() {
		view.showUploadDialog(this.configuredEntityId);
	}

	@Override
	public void updateBulkActionMenu() {
		Set<EntityQueryResult> selectedEntities = entityTreeBrowser.getSelectedEntities();
	}
	
	/**
	 * Create a new action menu for an entity.
	 * @param bundle
	 * @return
	 */
//	private BulkActionMenuWidget createEntityActionMenu(EntityBundle bundle, String wikiPageId){
		// Create a menu
//		BulkActionMenuWidget actionMenu = ginInjector.createBulkActionMenuWidget();
		// Create a controller.
//		final BulkActionController controller = ginInjector.createBulkActionController();
//		actionMenu.addControllerWidget(controller);
//		controller.configure(actionMenu, bundle, wikiPageId, new EntityUpdatedHandler() {
//			@Override
//			public void onPersistSuccess(EntityUpdatedEvent event) {
//				presenter.fireEntityUpdatedEvent();
//			}
//		});
//		annotationsShown = false;
//		actionMenu.addActionListener(Action.TOGGLE_ANNOTATIONS, new ActionListener() {
//			@Override
//			public void onAction(Action action) {
//				annotationsShown = !annotationsShown;
//				controller.onAnnotationsToggled(annotationsShown);
//				entityMetadata.setAnnotationsVisible(annotationsShown);
//			}
//		});
//		return actionMenu;
//	}
	
}
