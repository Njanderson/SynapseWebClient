package org.sagebionetworks.web.client.widget.entity;

import org.sagebionetworks.repo.model.Entity;
import org.sagebionetworks.repo.model.EntityBundle;
import org.sagebionetworks.repo.model.VersionInfo;
import org.sagebionetworks.repo.model.Versionable;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.PortalGinInjector;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.events.EntityUpdatedEvent;
import org.sagebionetworks.web.client.events.EntityUpdatedHandler;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.widget.entity.controller.PreflightController;
import org.sagebionetworks.web.client.widget.entity.controller.SynapseAlert;
import org.sagebionetworks.web.client.widget.pagination.DetailedPaginationWidget;
import org.sagebionetworks.web.client.widget.pagination.PageChangeListener;
import org.sagebionetworks.web.shared.PaginatedResults;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * This widget shows the properties and annotations as a non-editable table grid.
 *
 * @author jayhodgson
 */
public class FileHistoryWidget implements FileHistoryWidgetView.Presenter, IsWidget, PageChangeListener {
	
	private FileHistoryWidgetView view;
	private EntityBundle bundle;
	private EntityUpdatedHandler entityUpdatedHandler;
	private SynapseClientAsync synapseClient;
	private GlobalApplicationState globalApplicationState;
	private AuthenticationController authenticationController;
	public static final Integer VERSION_LIMIT = 100;
	public PreflightController preflightController;
	
	private SynapseAlert modalSynAlert;
	private SynapseAlert deleteSynAlert;
	private DetailedPaginationWidget paginationWidget;
	private PromptTwoValuesModalView modalEditor;
	private PortalGinInjector ginInjector;
	private boolean canEdit;
	private Long versionNumber;
	@Inject
	public FileHistoryWidget(FileHistoryWidgetView view,
			 SynapseClientAsync synapseClient, GlobalApplicationState globalApplicationState, AuthenticationController authenticationController,
			 DetailedPaginationWidget paginationWidget,
			 PreflightController preflightController,
			 final PromptTwoValuesModalView modalEditor,
			 PortalGinInjector ginInjector) {
		super();
		this.modalEditor = modalEditor;
		this.synapseClient = synapseClient;
		this.view = view;
		this.globalApplicationState = globalApplicationState;
		this.authenticationController = authenticationController;
		this.paginationWidget = paginationWidget;
		this.preflightController = preflightController;
		this.modalSynAlert = ginInjector.getSynapseAlertWidget();
		this.deleteSynAlert = ginInjector.getSynapseAlertWidget();
		view.setPaginationWidget(paginationWidget.asWidget());
		this.view.setPresenter(this);
		modalEditor.setPresenter(new PromptTwoValuesModalView.Presenter() {
			@Override
			public void onPrimary() {
				updateVersionInfo(modalEditor.getValue1(), modalEditor.getValue2());
			}
		});
		modalEditor.setSynAlertWidget(modalSynAlert.asWidget());
		view.setSynAlertWidget(deleteSynAlert.asWidget());
	}
	
	public void setEntityBundle(EntityBundle bundle, Long versionNumber) {
		deleteSynAlert.clear();
		this.bundle = bundle;
		this.versionNumber = versionNumber;
		this.canEdit = bundle.getPermissions().getCanCertifiedUserEdit();
		boolean isShowingCurrentVersion = versionNumber == null;
		view.setEntityBundle(bundle.getEntity(), !isShowingCurrentVersion);
		view.setEditVersionInfoButtonVisible(isShowingCurrentVersion && canEdit);
		
		//initialize versions
		onPageChange(0L);
	}

	@Override
	public void updateVersionInfo(String newLabel, String newComment) {
		editCurrentVersionInfo(bundle.getEntity(), newLabel, newComment);
	}

	private void editCurrentVersionInfo(Entity entity, String version, String comment) {
		if (entity instanceof Versionable) {
			final Versionable vb = (Versionable)entity;
			if (version != null && version.equals(vb.getVersionLabel()) &&
				comment != null && comment.equals(vb.getVersionComment())) {
				modalSynAlert.showError("Version info unchanged. You didn't change anything about the version info.");
				return;
			}
			String versionLabel = null;
			if (version!=null)
				versionLabel = version.toString();
			vb.setVersionLabel(versionLabel);
			vb.setVersionComment(comment);
			
			synapseClient.updateEntity(vb,
					new AsyncCallback<Entity>() {
						@Override
						public void onFailure(Throwable caught) {
								modalSynAlert.showError(DisplayConstants.ERROR_UPDATE_FAILED
										+ "\n" + caught.getMessage());
						}
						@Override
						public void onSuccess(Entity result) {
							modalEditor.hide();
							view.showInfo(DisplayConstants.VERSION_INFO_UPDATED, "Updated " + vb.getName());
							fireEntityUpdatedEvent();
						}
					});
		}
	}
	
	@Override
	public void deleteVersion(final Long versionNumber) {
		deleteSynAlert.clear();
		synapseClient.deleteEntityVersionById(bundle.getEntity().getId(), versionNumber, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				deleteSynAlert.handleException(caught);
			}
			@Override
			public void onSuccess(Void result) {
				view.showInfo("Version deleted", "Version "+ versionNumber + " of " + bundle.getEntity().getId() + " " + DisplayConstants.LABEL_DELETED);
				fireEntityUpdatedEvent();
			}
		});
	}
	
	@Override
	public Widget asWidget() {
		// The view is the real widget.
		return view.asWidget();
	}
	
	
	public void fireEntityUpdatedEvent() {
		if (entityUpdatedHandler != null)
			entityUpdatedHandler.onPersistSuccess(new EntityUpdatedEvent());
	}
	
	public void setEntityUpdatedHandler(EntityUpdatedHandler handler) {
		this.entityUpdatedHandler = handler;
	}
	
	@Override
	public void onPageChange(final Long newOffset) {
		view.clearVersions();
		// TODO: If we ever change the offset api to actually take 0 as a valid
		// offset, then we need to remove "+1"
		synapseClient.getEntityVersions(bundle.getEntity().getId(), newOffset.intValue() + 1, VERSION_LIMIT,
				new AsyncCallback<PaginatedResults<VersionInfo>>() {
					@Override
					public void onSuccess(PaginatedResults<VersionInfo> result) {
						PaginatedResults<VersionInfo> paginatedResults;
						paginatedResults = result;
						paginationWidget.configure(VERSION_LIMIT.longValue(), newOffset, paginatedResults.getTotalNumberOfResults(), FileHistoryWidget.this);
						if (versionNumber == null && newOffset == 0 && paginatedResults.getResults().size() > 0) {
							versionNumber = paginatedResults.getResults().get(0).getVersionNumber();
						}
						for (VersionInfo versionInfo : paginatedResults.getResults()) {
							view.addVersion(versionInfo, canEdit, versionInfo.getVersionNumber().equals(versionNumber));
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						view.showErrorMessage(caught.getMessage());
					}
				});
	}

	
	/**
	 * For testing purposes only
	 * @return
	 */
	public Long getVersionNumber() {
		return versionNumber;
	}
	
	@Override
	public void onEditVersionInfoClicked() {
		preflightController.checkUploadToEntity(bundle, new Callback() {
			@Override
			public void invoke() {
				modalSynAlert.clear();
				final Versionable vb = (Versionable)bundle.getEntity();
				modalEditor.configure("Edit Version Info", "Version label", vb.getVersionLabel(), "Version comment", vb.getVersionComment(), DisplayConstants.OK);
				modalEditor.show();
			}
		});
	}
}
