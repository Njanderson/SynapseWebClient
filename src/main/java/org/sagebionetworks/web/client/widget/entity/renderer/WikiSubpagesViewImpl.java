package org.sagebionetworks.web.client.widget.entity.renderer;

import java.util.ArrayList;
import java.util.List;

import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.utils.UnorderedListPanel;
import org.sagebionetworks.web.client.widget.entity.browse.EntityTreeBrowserViewImpl.EntityTreeImageBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class WikiSubpagesViewImpl extends FlowPanel implements WikiSubpagesView {

	private Presenter presenter;
	private GlobalApplicationState globalAppState;
	private static final String SHOW_SUBPAGES_STYLE="col-xs-12 col-md-3 well";
	private static final String SHOW_SUBPAGES_MD_STYLE="col-xs-12 col-md-9";
	private static final String HIDE_SUBPAGES_STYLE="col-xs-12";
	private static final String HIDE_SUBPAGES_MD_STYLE="col-xs-12";
	
	private Button showHideButton;
	private Button editOrderButton;
	private FlowPanel ulContainer;
	private FlowPanel wikiSubpagesContainer;
	private FlowPanel wikiPageContainer;
	boolean isShowingSubpages;
	
	@Inject
	public WikiSubpagesViewImpl(GlobalApplicationState globalAppState) {
		this.globalAppState = globalAppState;
	}
	@Override
	public void clear() {
		super.clear();
	}
	
	@Override
	public void configure(Tree tree, FlowPanel wikiSubpagesContainer, FlowPanel wikiPageContainer) {
		clear();
		
		this.wikiSubpagesContainer = wikiSubpagesContainer;
		this.wikiPageContainer = wikiPageContainer;
		//this widget shows nothing if it doesn't have any pages!
		
		if (tree.getItemCount() == 0)
			return;
		//only show the tree if the root has children
		if (tree.getItemCount() > 0) {
			//traverse the tree, and create anchors
//			final UnorderedListPanel ul = new UnorderedListPanel();
//			ul.addStyleName("notopmargin nav bs-sidenav");
//			addTreeItemsRecursive(ul, getTreeRootChildren(tree));
			showHideButton = DisplayUtils.createButton("");
			editOrderButton = DisplayUtils.createButton("");
			ulContainer = new FlowPanel();
			ulContainer.addStyleName("notopmargin nav bs-sidenav");
			ulContainer.setVisible(true);
			ulContainer.add(new HTML("<h4 class=\"margin-left-15\">Pages</h4>"));
//			ulContainer.add(ul);
			
			editOrderButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					
				}
			});
			
			showHideButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (isShowingSubpages)
						hideSubpages();
					else
						showSubpages();
				}
			});
			
			ulContainer.add(tree);
			
			add(editOrderButton);
			add(ulContainer);
			add(showHideButton);
			
			showSubpages();
		} else {
			hideSubpages();
		}
		clearWidths();
	}
	
//	private void recurseOpenItem(SubPageTreeItem item) {
//		item.setState(true);
//		for (int i = 0; i < item.getChildCount(); i++) {
//			recurseOpenItem((SubPageTreeItem) item.getChild(i));
//		}
//	}
	
	private List<SubPageTreeItem> getTreeRootChildren(Tree tree) {
		List<SubPageTreeItem> result =  new ArrayList<SubPageTreeItem>();
		for (int i = 0; i < tree.getItemCount(); i++) {
			result.add((SubPageTreeItem) tree.getItem(i));
		}
		return result;
	}
	
	/**
	 * Work around the Chrome bug.  See DisplayUtils.clearElementWidth() for more info.
	 */
	private void clearWidths() {
		DisplayUtils.clearElementWidth(getElement());
		if (wikiSubpagesContainer != null) 
			DisplayUtils.clearElementWidth(wikiSubpagesContainer.getElement());
		if (ulContainer != null)
			DisplayUtils.clearElementWidth(ulContainer.getElement());
		if (wikiPageContainer != null)
			DisplayUtils.clearElementWidth(wikiPageContainer.getElement());
	}
	
	@Override
	public void hideSubpages() {
		isShowingSubpages = false;
		// This call to layout is necessary to force the scroll bar to appear on page-load
		if (wikiSubpagesContainer != null){
			wikiSubpagesContainer.removeStyleName(SHOW_SUBPAGES_STYLE);
			wikiSubpagesContainer.addStyleName(HIDE_SUBPAGES_STYLE);
		}
				
		if (showHideButton != null) {
			showHideButton.setText("Show Pages " + DisplayConstants.RIGHT_ARROWS);
			showHideButton.addStyleName("btn btn-default btn-xs left margin-right-40");
		}
		
		if (ulContainer != null)
			DisplayUtils.hide(ulContainer);
		
		if (wikiPageContainer != null) {
			wikiPageContainer.removeStyleName(SHOW_SUBPAGES_MD_STYLE);
			wikiPageContainer.addStyleName(HIDE_SUBPAGES_MD_STYLE);
			wikiPageContainer.setVisible(true);
		}
	}
	
	@Override
	public void showSubpages() {
		isShowingSubpages = true;
		if (wikiSubpagesContainer != null) {
			wikiSubpagesContainer.removeStyleName(HIDE_SUBPAGES_STYLE);
			wikiSubpagesContainer.addStyleName(SHOW_SUBPAGES_STYLE);
		}
		
		if (editOrderButton != null) {
			editOrderButton.setText("Edit Order");
			editOrderButton.addStyleName("btn btn-default btn-xs right");
		}
			
		if (wikiPageContainer != null) {
			wikiPageContainer.removeStyleName(HIDE_SUBPAGES_MD_STYLE);
			wikiPageContainer.addStyleName(SHOW_SUBPAGES_MD_STYLE);
		}
				
		if (showHideButton != null) {
			showHideButton.setText(DisplayConstants.LEFT_ARROWS);
			showHideButton.addStyleName("btn btn-default btn-xs right");		
		}
		
		if (ulContainer != null)
			DisplayUtils.show(ulContainer);
	}
	
	private void addTreeItemsRecursive(UnorderedListPanel ul, List<SubPageTreeItem> children) {
		for (SubPageTreeItem treeItem : children) {
			String styleName = treeItem.isCurrentPage() ? "active" : "";
			ul.add(getListItem(treeItem), styleName);
			if (treeItem.getChildCount() > 0){
				UnorderedListPanel subList = new UnorderedListPanel();
				subList.addStyleName("nav");
				subList.setVisible(true);
				ul.add(subList);
				addTreeItemsRecursive(subList, treeItem.getChildren());
			}
		}
	}
	
	private Widget getListItem(final SubPageTreeItem treeItem) {
		Anchor l = new Anchor(treeItem.getText());
		l.addStyleName("link");
		l.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				globalAppState.getPlaceChanger().goTo(treeItem.getTargetPlace());
			}
		});
		return l;
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}	

	@Override 
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
		
	@Override
	public void showErrorMessage(String message) {
		DisplayUtils.showErrorMessage(message);
	}

	@Override
	public void showLoading() {
	}

	@Override
	public void showInfo(String title, String message) {
		DisplayUtils.showInfo(title, message);
	}
	
	/*
	 * Image Resources for Tree Expand/Collapse Icons
	 */

//	public class SubPageTreeResources implements Tree.Resources {
//		@Override
//	    public ImageResource treeClosed() {
//	        return new ImageResource();
//	    }
//
//	    @Override
//	    public ImageResource treeOpen() {
//	        return iconsImageBundle.arrowDownDir16();
//	    }
//
//		@Override
//		public ImageResource treeLeaf() {
//			return EntityTreeImageBundle.DEFAULT_RESOURCES.treeLeaf();
//		}
//	}
}
