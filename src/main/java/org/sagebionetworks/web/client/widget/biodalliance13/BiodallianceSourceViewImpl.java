package org.sagebionetworks.web.client.widget.biodalliance13;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.TextBox;
import org.sagebionetworks.repo.model.Reference;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.DisplayUtils.SelectedHandler;
import org.sagebionetworks.web.client.widget.entity.browse.EntityFinder;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class BiodallianceSourceViewImpl implements IsWidget, BiodallianceSourceView {
	public interface BiodallianceSourceViewImplUiBinder extends UiBinder<Widget, BiodallianceSourceViewImpl> {}
	Widget widget;
	EntityFinder entityFinder;
	@UiField
	TextBox sourceNameTextbox;
	@UiField
	TextBox entityPickerTextbox;
	@UiField
	Input colorPicker;
	@UiField
	TextBox heightField;
	@UiField
	Button moveUpButton;
	@UiField
	Button moveDownButton;
	@UiField
	Button deleteButton;
	Reference currentlySelected;
	Presenter presenter;
	@Inject
	public BiodallianceSourceViewImpl(BiodallianceSourceViewImplUiBinder binder, EntityFinder entityFinder) {
		widget = binder.createAndBindUi(this);
		this.entityFinder = entityFinder;
	}

	public ClickHandler getClickHandler(final TextBox textBox) {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				entityFinder.configure(false, new SelectedHandler<Reference>() {					
					@Override
					public void onSelected(Reference selected) {
						if(selected.getTargetId() != null) {
							setEntity(selected.getTargetId(), selected.getTargetVersionNumber());
							entityFinder.hide();
						} else {
							DisplayUtils.showErrorMessage(DisplayConstants.PLEASE_MAKE_SELECTION);
						}
					}
				});
				entityFinder.show();
			}
		};
	}

	public Reference getEntity() {
		return currentlySelected;
	}

	public void setEntity(String entityId, Long version) {
		currentlySelected = new Reference();
		currentlySelected.setTargetId(entityId);
		currentlySelected.setTargetVersionNumber(version);
		this.entityPickerTextbox.setValue(entityId + "." + version);
	}

	public String getColor() {
		return colorPicker.getValue();
	}

	public void setColor(String color) {
		colorPicker.setValue(color);
	}

	public String getHeight() {
		return heightField.getValue();
	}

	public void setHeight(String height) {
		heightField.setValue(height);
	}
	@Override
	public String getSourceName() {
		return sourceNameTextbox.getValue();
	}
	@Override
	public void setSourceName(String sourceName) {
		sourceNameTextbox.setValue(sourceName);
	}
	@Override
	public Widget asWidget() {
		return widget;
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void initView() {
	}

	@Override
	public void checkParams() throws IllegalArgumentException {
		if (currentlySelected == null) {
			throw new IllegalArgumentException("Must select a file for each source");
		}
	}

	@Override
	public void showLoading() {
	}

	@Override
	public void showInfo(String title, String message) {
	}

	@Override
	public void clear() {
		currentlySelected = null;
	}

	@Override
	public void showErrorMessage(String message) {
	}
}
