package org.sagebionetworks.web.client.widget.entity.browse;

import org.sagebionetworks.web.client.SynapseView;
import org.sagebionetworks.web.client.widget.entity.EntityBadge;
import org.sagebionetworks.web.client.widget.entity.MoreTreeItem;

import com.google.gwt.user.client.ui.IsWidget;

public interface EntityTreeBrowserView extends IsWidget, SynapseView {

	/**
	 * Set the presenter.
	 * @param presenter
	 */
	void setPresenter(Presenter presenter);
	
	/**
	 * Rather than linking to the Entity Page, a clicked entity
	 * in the tree will become selected.
	 */
	void makeSelectable();
	
	/**
	 * Presenter interface
	 */
	public interface Presenter {

		void setSelection(String id);

		int getMaxLimit();
		
		void expandTreeItemOnOpen(final EntityBadge target);
		
		void clearRecordsFetchedChildren();

		void addMoreButton(MoreTreeItem moreItem, String parentId,
				EntityBadge parent, long offset);

		void getChildren(String parentId, EntityBadge parent, long offset);

	}

	void appendRootEntityTreeItem(EntityBadge childToAdd);

	void appendChildEntityTreeItem(EntityBadge childToAdd,
			EntityBadge parent);

	void configureEntityTreeItem(EntityBadge childToAdd);

	void placeChildMoreTreeItem(MoreTreeItem childToCreate,
			EntityBadge parent, long offset);

	void placeRootMoreTreeItem(MoreTreeItem childToCreate,
			String parentId, long offset);


	void showEmptyUI();

	int getRootCount();

	void hideEmptyUI();
	
	void setLoadingVisible(boolean isShown);


}
