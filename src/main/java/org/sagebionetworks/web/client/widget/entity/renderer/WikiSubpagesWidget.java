package org.sagebionetworks.web.client.widget.entity.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.sagebionetworks.repo.model.BatchResults;
import org.sagebionetworks.repo.model.EntityHeader;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.Reference;
import org.sagebionetworks.repo.model.request.ReferenceList;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiHeader;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiOrderHint;
import org.sagebionetworks.schema.adapter.AdapterFactory;
import org.sagebionetworks.schema.adapter.JSONEntity;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.place.Synapse;
import org.sagebionetworks.web.client.place.Wiki;
import org.sagebionetworks.web.client.transform.NodeModelCreator;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.widget.WidgetRendererPresenter;
import org.sagebionetworks.web.client.widget.entity.renderer.WikiSubpagesViewImpl.GetOrderHintCallback;
import org.sagebionetworks.web.shared.PaginatedResults;
import org.sagebionetworks.web.shared.WikiPageKey;
import org.sagebionetworks.web.shared.exceptions.NotFoundException;
import org.sagebionetworks.web.shared.exceptions.UnknownErrorException;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class WikiSubpagesWidget implements WikiSubpagesView.Presenter, WidgetRendererPresenter {
	
	private WikiSubpagesView view;
	private SynapseClientAsync synapseClient;
	private NodeModelCreator nodeModelCreator;
	private AdapterFactory adapterFactory;
	private WikiPageKey wikiKey; 
	private String ownerObjectName;
	private Place ownerObjectLink;
	private FlowPanel wikiSubpagesContainer;
	private FlowPanel wikiPageContainer;
	
	//true if wiki is embedded in it's owner page.  false if it should be shown as a stand-alone wiki 
	private boolean isEmbeddedInOwnerPage;
	
	@Inject
	public WikiSubpagesWidget(WikiSubpagesView view, SynapseClientAsync synapseClient, NodeModelCreator nodeModelCreator, AdapterFactory adapterFactory) {
		this.view = view;		
		this.synapseClient = synapseClient;
		this.nodeModelCreator = nodeModelCreator;
		this.adapterFactory = adapterFactory;
		
		view.setPresenter(this);
	}	
	@Override
	public void configure(final WikiPageKey wikiKey, Map<String, String> widgetDescriptor, Callback widgetRefreshRequired, Long wikiVersionInView) {
		configure(wikiKey, widgetDescriptor, widgetRefreshRequired, null, null, true);
	}

	public void configure(final WikiPageKey wikiKey, Map<String, String> widgetDescriptor, Callback widgetRefreshRequired, FlowPanel wikiSubpagesContainer, FlowPanel wikiPageContainer, boolean isEmbeddedInOwnerPage) {
		this.wikiPageContainer = wikiPageContainer;
		this.wikiSubpagesContainer = wikiSubpagesContainer;
		this.wikiKey = wikiKey;
		this.isEmbeddedInOwnerPage = isEmbeddedInOwnerPage;
		view.clear();
		//figure out owner object name/link
		if (wikiKey.getOwnerObjectType().equalsIgnoreCase(ObjectType.ENTITY.toString())) {
			//lookup the entity name based on the id
			Reference ref = new Reference();
			ref.setTargetId(wikiKey.getOwnerObjectId());
			List<Reference> allRefs = new ArrayList<Reference>();
			allRefs.add(ref);
			ReferenceList list = new ReferenceList();
			list.setReferences(allRefs);	
			try {
				synapseClient.getEntityHeaderBatch(list.writeToJSONObject(adapterFactory.createNew()).toJSONString(), new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {					
						BatchResults<EntityHeader> headers;
						try {
							headers = nodeModelCreator.createBatchResults(result, EntityHeader.class);
							if (headers.getTotalNumberOfResults() == 1) {
								EntityHeader theHeader = headers.getResults().get(0);
								ownerObjectName = theHeader.getName();
								ownerObjectLink = getLinkPlace(theHeader.getId(), wikiKey.getVersion(), null);
								refreshTableOfContents();
							}	
						} catch (JSONObjectAdapterException e) {
							onFailure(new UnknownErrorException(DisplayConstants.ERROR_INCOMPATIBLE_CLIENT_VERSION));
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {					
						view.showErrorMessage(caught.getMessage());
					}
				});
			} catch (JSONObjectAdapterException e) {
				view.showErrorMessage(DisplayConstants.ERROR_INCOMPATIBLE_CLIENT_VERSION);
			}
		}
	}
	
	public Place getLinkPlace(String entityId, Long entityVersion, String wikiId) {
		if (isEmbeddedInOwnerPage)
			return new Synapse(entityId, entityVersion, Synapse.EntityArea.WIKI, wikiId);
		else
			return new Wiki(entityId, ObjectType.ENTITY.toString(), wikiId);
	}
	
	public void clearState() {
		view.clear();
	}
	
	@Override
	public Widget asWidget() {
		view.setPresenter(this);		
		return view.asWidget();
	}
	
	public void refreshTableOfContents() {
		view.clear();
		synapseClient.getV2WikiHeaderTree(wikiKey.getOwnerObjectId(), wikiKey.getOwnerObjectType(), new AsyncCallback<String>() {
			@Override
			public void onSuccess(String results) {
				try {
					final PaginatedResults<JSONEntity> wikiHeaders = nodeModelCreator.createPaginatedResults(results, V2WikiHeader.class);
					
					synapseClient.getV2WikiOrderHint(wikiKey, new AsyncCallback<V2WikiOrderHint>() {
						@Override
						public void onSuccess(V2WikiOrderHint result) {
							// "Sort" stuff'
							sortHeadersByOrderHint(wikiHeaders, result);
							Tree tree = buildTree(wikiHeaders);
							view.configure(tree, wikiSubpagesContainer, wikiPageContainer);
						}
						@Override
						public void onFailure(Throwable caught) {
							// Failed to get order hint. Just ignore it?
							Tree tree = buildTree(wikiHeaders);
							view.configure(tree, wikiSubpagesContainer, wikiPageContainer);
						}
					});
					
				} catch (JSONObjectAdapterException e) {
					onFailure(new UnknownErrorException(DisplayConstants.ERROR_INCOMPATIBLE_CLIENT_VERSION));
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				//if this is because the wiki header root was not found, and the parent wiki id is null,
				if (caught instanceof NotFoundException) {
					//do nothing, show nothing
					view.clear();
				} else {
					view.showErrorMessage(caught.getMessage());
				}
			}
		});
	}
	
	private void sortHeadersByOrderHint(PaginatedResults<JSONEntity> wikiHeaders, V2WikiOrderHint orderHint) {
		// TODO: Sort headers by order hint.
		List<JSONEntity> headerList = wikiHeaders.getResults();
		List<String> idList = orderHint.getOrderHint();
		if (idList == null) return;
		
		int insertIndex = 0;
		for (int i = 0; i < idList.size(); i++) {
			for (int j = 0; j < headerList.size(); j++) {
				if (((V2WikiHeader) headerList.get(j)).getId().equals(idList.get(i))) {
					// The header was in the order hint. Move that header towards the front.
					JSONEntity toMove = headerList.remove(j);
					headerList.add(insertIndex, toMove);
					insertIndex++;
				}
			}
		}
	}
	
	private Tree buildTree(PaginatedResults<JSONEntity> wikiHeaders) {
		Map<String, SubPageTreeItem> wikiId2TreeItem = new HashMap<String, SubPageTreeItem>();
		
		//now grab all of the headers associated with this level
		for (JSONEntity headerEntity : wikiHeaders.getResults()) {
			V2WikiHeader header = (V2WikiHeader) headerEntity;
			boolean isCurrentPage = header.getId().equals(wikiKey.getWikiPageId());
			Place targetPlace;
			String title;
			if (header.getParentId() == null) {
				targetPlace = ownerObjectLink;
				title = ownerObjectName;
			}
			else {
				targetPlace = getLinkPlace(wikiKey.getOwnerObjectId(), wikiKey.getVersion(), header.getId());
				title = header.getTitle();
			}
			
			SubPageTreeItem item = new SubPageTreeItem(header, title, targetPlace, isCurrentPage);
			wikiId2TreeItem.put(header.getId(), item);
		}
		//now set up the relationships
		Tree tree = new Tree();
		
		for (JSONEntity headerEntity : wikiHeaders.getResults()) {
			V2WikiHeader header = (V2WikiHeader) headerEntity;
			if (header.getParentId() != null){
				//add this as a child							
				SubPageTreeItem parent = wikiId2TreeItem.get(header.getParentId());
				SubPageTreeItem child = wikiId2TreeItem.get(header.getId());
				parent.addItem(child);
				parent.setState(true);
			} else {
				String styleName = wikiId2TreeItem.get(header.getId()).isCurrentPage() ? "active" : "";
				
				tree.addItem(wikiId2TreeItem.get(header.getId()));
			}
		}
		return tree;
	}
	
	@Override
	public Callback getUpdateOrderHintCallback(final GetOrderHintCallback getCurrentOrderListCallback) {
		return new Callback() {
			@Override
			public void invoke() {
				final List<String> newOrderHintIdList = getCurrentOrderListCallback.getCurrentOrderHint();
				synapseClient.getV2WikiOrderHint(wikiKey, new AsyncCallback<V2WikiOrderHint>() {
					@Override
					public void onSuccess(V2WikiOrderHint result) {
						result.setOrderHint(newOrderHintIdList);
						synapseClient.updateV2WikiOrderHint(wikiKey, result, new AsyncCallback<V2WikiOrderHint>() {
							@Override
							public void onSuccess(V2WikiOrderHint result) {
								Window.alert("Updated. Party!");
							}
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Not updated. No party = ^ (");
							}
						});
					}
					@Override
					public void onFailure(Throwable caught) {
						// Failed to get order hint. Just ignore it?
						// TODO:
					}
				});
			}
		};
	}
	
}

class SubPageTreeItem extends TreeItem {
	private V2WikiHeader header;
	private String text;
	private Place targetPlace;
	private boolean isCurrentPage;
	
	public SubPageTreeItem(V2WikiHeader header, String text, Place targetPlace, boolean isCurrentPage) {
		super();
		this.header = header;
		this.text = text;
		this.targetPlace = targetPlace;
		this.isCurrentPage = isCurrentPage;
		if (isCurrentPage) {
			setWidget(new Label(text));	// TODO: Something special = ^ )
		} else {
			Anchor l = new Anchor(text);
			l.addStyleName("link");
//			l.addClickHandler(new ClickHandler() {
//				@Override
//				public void onClick(ClickEvent event) {
//					globalAppState.getPlaceChanger().goTo(treeItem.getTargetPlace());
//				}
//			});
			setWidget(l);
		}
		
		//setState(true);
	}
	
	public String getText()				{	return text;			}
	public Place getTargetPlace()		{	return targetPlace;		}
	public boolean isCurrentPage()		{	return isCurrentPage;	}
	public V2WikiHeader getHeader()		{	return header;			}
}
