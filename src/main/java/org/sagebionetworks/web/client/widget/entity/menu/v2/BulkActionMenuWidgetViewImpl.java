package org.sagebionetworks.web.client.widget.entity.menu.v2;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.gwtbootstrap3.client.ui.DropDownMenu;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class BulkActionMenuWidgetViewImpl implements BulkActionMenuWidgetView {

	Widget widget;
	
	@UiField
	SimplePanel controllerContainer;
	@UiField
	DropDownMenu actionsDropDown;
	
	public interface Binder extends UiBinder<Widget, BulkActionMenuWidgetViewImpl> {}
	
	@Inject
	public BulkActionMenuWidgetViewImpl(Binder binder) {
		widget = binder.createAndBindUi(this);
	}
	
	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setControllerWidget(IsWidget controller) {
		controllerContainer.setWidget(controller);
	}

	@Override
	public Iterable<ActionView> listActionViews() {
		List<ActionView> list = new LinkedList<ActionView>();
		recursiveSearch(list, actionsDropDown);
		return list;
	}
	/**
	 * Recursive function to find all ActionView within this widget.
	 * 
	 * @param results
	 * @param toSearch
	 */
	private static void recursiveSearch(List<ActionView> results, ComplexPanel toSearch){
		Iterator<Widget> childIterator = toSearch.iterator();
		if(childIterator != null){
			while(childIterator.hasNext()){
				Widget child = childIterator.next();
				if(child instanceof ActionView){
					results.add((ActionView) child);
				}else if(child instanceof ComplexPanel){
					ComplexPanel container = (ComplexPanel) child;
					recursiveSearch(results, container);
				}
			}
		}
	}

}
