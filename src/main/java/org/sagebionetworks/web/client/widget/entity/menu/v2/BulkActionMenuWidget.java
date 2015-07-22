package org.sagebionetworks.web.client.widget.entity.menu.v2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.sagebionetworks.web.client.widget.entity.menu.v2.ActionMenuWidget.ActionListener;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class BulkActionMenuWidget implements BulkActionMenuWidgetView.Presenter, ActionListener {
	
	private BulkActionMenuWidgetView view;
	
	Map<Action, ActionView> actionViewMap;
	Map<Action, List<ActionListener>> actionListenerMap;
	
	@Inject
	public BulkActionMenuWidget(BulkActionMenuWidgetView view) {
		this.view = view;
		this.actionViewMap = new HashMap<Action, ActionView>();
		this.actionListenerMap = new HashMap<Action, List<ActionListener>>();
		// synchronize with the view
		for (ActionView av : this.view.listActionViews()) {
			Action action = av.getAction();
			if (action == null) {
				throw new IllegalArgumentException(
						"ActionView has a null action");
			}
			if (this.actionViewMap.containsKey(action)) {
				throw new IllegalArgumentException("Action " + action
						+ " was applied to more than one ActionView");
			}
			this.actionViewMap.put(av.getAction(), av);
			av.addActionListener(this);
			this.actionListenerMap.put(av.getAction(),
					new LinkedList<ActionListener>());
		}
	}
	
	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	@Override
	public void addControllerWidget(IsWidget controller) {
		view.setControllerWidget(controller);
	}

	@Override
	public void setActionEnabled(Action action, boolean enabled) {
		getActionView(action).setEnabled(enabled);
	}

	@Override
	public void setActionVisible(Action action, boolean visible) {
		getActionView(action).setVisible(visible);
	}

	@Override
	public void setActionText(Action action, String text) {
		getActionView(action).setText(text);

	}

	@Override
	public void setActionIcon(Action action, IconType icon) {
		getActionView(action).setIcon(icon);
	}
	
	/**
	 * Get the view for the given action from the map.
	 * 
	 * @param action
	 * @throws IllegalArgumentException
	 *             When there is no view mapped to the given action.
	 * @return
	 */
	private ActionView getActionView(Action action) {
		ActionView actionView = this.actionViewMap.get(action);
		if (actionView == null) {
			throw new IllegalArgumentException(
					"No action view found for action: " + action);
		}
		return actionView;
	}

	private List<ActionListener> getActionListeners(Action action) {
		List<ActionListener> list = this.actionListenerMap.get(action);
		if (list == null) {
			throw new IllegalArgumentException(
					"No action list found for action: " + action);
		}
		return list;
	}
	
	@Override
	public void addActionListener(Action action, ActionListener listener) {
		getActionListeners(action).add(listener);
	}

	@Override
	public void onAction(Action action) {
		for (ActionListener listener : getActionListeners(action)) {
			listener.onAction(action);
		}		
	}
	
}
