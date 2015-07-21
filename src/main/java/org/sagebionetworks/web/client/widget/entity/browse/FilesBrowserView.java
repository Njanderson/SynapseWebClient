package org.sagebionetworks.web.client.widget.entity.browse;

import org.sagebionetworks.web.client.SynapseView;
import org.sagebionetworks.web.client.UploadView;

import com.google.gwt.user.client.ui.IsWidget;

public interface FilesBrowserView extends IsWidget, SynapseView, UploadView {

	/**
	 * Set the presenter.
	 * @param presenter
	 */
	public void setPresenter(Presenter presenter);
	
	/**
	 * Configure the view with the parent id
	 */
	void configure(boolean canCertifiedUserAddChild);

	public void showFolderEditDialog(final String folderEntityId);

	public void setNewFolderDialogVisible(boolean visible);

	/**
	 * Presenter interface
	 */
	public interface Presenter {
		void addFolderClicked();
		void uploadButtonClicked();
		void updateFolderName(String newFolderName);
		void deleteFolder(boolean skipTrashCan);
		void fireEntityUpdatedEvent();
		void updateBulkActionMenu();
	}

	void setTreeBrowserWidget(IsWidget entityTreeBrowser);

	void setBulkActionMenuWidget(IsWidget actionMenu);
}
