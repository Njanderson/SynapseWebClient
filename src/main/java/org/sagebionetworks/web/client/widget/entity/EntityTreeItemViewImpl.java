package org.sagebionetworks.web.client.widget.entity;

import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Tooltip;
import org.sagebionetworks.repo.model.entity.query.EntityQueryResult;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.PortalGinInjector;
import org.sagebionetworks.web.client.SageImageBundle;
import org.sagebionetworks.web.client.SynapseJSNIUtils;
import org.sagebionetworks.web.client.view.bootstrap.table.Table;
import org.sagebionetworks.web.client.widget.provenance.ProvViewUtil;
import org.sagebionetworks.web.shared.KeyValueDisplay;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class EntityTreeItemViewImpl extends Composite implements EntityTreeItemView {
	
	private Presenter presenter;
	SynapseJSNIUtils synapseJSNIUtils;
	SageImageBundle sageImageBundle;
	Widget modifiedByWidget;
	Widget widget;
	public interface Binder extends UiBinder<Widget, EntityTreeItemViewImpl> {	}

	@UiField
	Tooltip tooltip;
	@UiField
	SimplePanel iconContainer;
	@UiField
	FlowPanel entityContainer;
	@UiField
	TextBox idField;
	@UiField
	CheckBox selectedCheckbox;
	@UiField
	SimplePanel modifiedByField;
	@UiField
	Label modifiedOnField;
	@UiField
	Table entityTable;
	
	Image iconPicture;
	ClickHandler nonDefaultClickHandler;
	
	boolean isPopoverInitialized;
	boolean isPopover;
	
	@Inject
	public EntityTreeItemViewImpl(final Binder uiBinder,
			SynapseJSNIUtils synapseJSNIUtils,
			SageImageBundle sageImageBundle, 
			PortalGinInjector ginInjector) {
		this.widget = uiBinder.createAndBindUi(this);
		this.synapseJSNIUtils = synapseJSNIUtils;
		this.sageImageBundle = sageImageBundle;
//		initWidget(uiBinder.createAndBindUi(this));
		idField.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				idField.selectAll();
			}
		});
		selectedCheckbox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				presenter.entitySelectedChange();
			}
		});
	}
	
	@Override
	public boolean getIsSelected() {
		return selectedCheckbox.getValue();
	}
	
	@Override
	public void setEntity(final EntityQueryResult entityHeader) {
		clear();
		if(entityHeader == null)  throw new IllegalArgumentException("Entity is required");
		
		if(entityHeader != null) {
			isPopoverInitialized = false;
			
			final Anchor anchor = new Anchor();
			anchor.setText(entityHeader.getName());
			anchor.addStyleName("link");
			isPopover = false;
			anchor.addMouseOverHandler(new MouseOverHandler() {
				
				@Override
				public void onMouseOver(MouseOverEvent event) {
					showPopover(anchor, entityHeader.getId(), entityHeader.getName());
					isPopover = true;
				}
			});
			anchor.addMouseOutHandler(new MouseOutHandler() {
				@Override
				public void onMouseOut(MouseOutEvent event) {
					isPopover = false;
					tooltip.hide();
				}
			});
			
			anchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					entityClicked(entityHeader, event);
				}
			});
			
			ClickHandler clickHandler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					anchor.fireEvent(event);
				}
			};
			
			ImageResource icon = presenter.getIconForType(entityHeader.getEntityType());
			iconPicture = new Image(icon);
			iconPicture.setWidth("16px");
			iconPicture.setHeight("16px");
			iconPicture.addStyleName("imageButton displayInline");
			iconPicture.addClickHandler(clickHandler);
			iconContainer.setWidget(iconPicture);
			entityContainer.add(anchor);
			idField.setText(entityHeader.getId());
		} 		
	}
	
	public void showPopover(final Anchor anchor, final String entityId, final String entityName) {
		if (!isPopoverInitialized) {
			presenter.getInfo(entityId, new AsyncCallback<KeyValueDisplay<String>>() {						
				@Override
				public void onSuccess(KeyValueDisplay<String> result) {
					renderPopover(ProvViewUtil.createEntityPopoverHtml(result).asString());
				}
				
				@Override
				public void onFailure(Throwable caught) {
					renderPopover(DisplayConstants.DETAILS_UNAVAILABLE);						
				}
				
				private void renderPopover(final String content) {
					isPopoverInitialized = true;
					if (entityContainer.isAttached()) {
						tooltip.setTitle(content);
						tooltip.reconfigure();
						if (isPopover)
							tooltip.show();
					}
				}
			});
		} else {
			tooltip.show();	
		}
	}

	@Override
	public void showLoadError(String principalId) {
		clear();
		entityContainer.add(new HTML(DisplayConstants.ERROR_LOADING));		
	}
	
	@Override
	public void showLoading() {
		clear();
		entityContainer.add(new HTML(DisplayUtils.getLoadingHtml(sageImageBundle)));
	}

	@Override
	public void showInfo(String title, String message) {
	}

	@Override
	public void showErrorMessage(String message) {
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;		
	}
	
	@Override
	public void clear() {
		iconContainer.clear();
		entityContainer.clear();
	}
	
	@Override
	public void showLoadingIcon() {
		iconContainer.setWidget(new Image(sageImageBundle.loading16()));
	}
	
	@Override
	public void hideLoadingIcon() {
		iconContainer.setWidget(iconPicture);
	}
	
	@Override
	public void setClickHandler(ClickHandler handler) {
		nonDefaultClickHandler = handler;
	}
	
	@Override
	public void setModifiedByWidget(Widget w) {
		modifiedByField.setWidget(w);
		this.modifiedByWidget = w;
	}
	
	@Override
	public void setModifiedOn(String modifiedOnString) {
		modifiedOnField.setText(modifiedOnString);
	}
	private void entityClicked(EntityQueryResult entityHeader, ClickEvent event) {
		if (nonDefaultClickHandler == null) {
			presenter.entityClicked(entityHeader);
		} else {
			nonDefaultClickHandler.onClick(event);
		}
	}
	
	@Override
	public void setModifiedByWidgetVisible(boolean visible) {
		modifiedByWidget.setVisible(visible);
	}
	
	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public TreeItem asTreeItem() {
		return new TreeItem(widget);
	}

}
