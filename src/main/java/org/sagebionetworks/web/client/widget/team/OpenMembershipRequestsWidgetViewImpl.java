package org.sagebionetworks.web.client.widget.team;

import java.util.List;

import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.DisplayUtils.ButtonType;
import org.sagebionetworks.web.client.PortalGinInjector;
import org.sagebionetworks.web.client.SageImageBundle;
import org.sagebionetworks.web.client.widget.user.BadgeSize;
import org.sagebionetworks.web.client.widget.user.UserBadge;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;

public class OpenMembershipRequestsWidgetViewImpl extends FlowPanel implements
		OpenMembershipRequestsWidgetView {
	private Presenter presenter;
	private SageImageBundle sageImageBundle;
	private PortalGinInjector ginInjector;
	private FlowPanel mainContainer;
	
	@Inject
	public OpenMembershipRequestsWidgetViewImpl(SageImageBundle sageImageBundle, PortalGinInjector ginInjector) {
		this.sageImageBundle = sageImageBundle;
		this.ginInjector = ginInjector;
		mainContainer = new FlowPanel();
		mainContainer.addStyleName("highlight-box");
		mainContainer.getElement().setAttribute("highlight-box-title", DisplayConstants.PENDING_JOIN_REQUESTS);
	}
	
	
	@Override
	public void showLoading() {
		clear();
		add(DisplayUtils.getLoadingWidget(sageImageBundle));
	}

	@Override
	public void showInfo(String title, String message) {
		DisplayUtils.showInfo(title, message);
	}

	@Override
	public void showErrorMessage(String message) {
		DisplayUtils.showErrorMessage(message);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void configure(List<UserProfile> profiles, List<String> requestMessages) {
		clear();
		mainContainer.clear();
		FlowPanel singleRow = DisplayUtils.createRowContainerFlowPanel();
		for (int i = 0; i < profiles.size(); i++) {
			FlowPanel lc = new FlowPanel();
			final UserProfile profile = profiles.get(i);
			Column userBadgeColumn = new Column(ColumnSize.XS_8, ColumnSize.SM_9, ColumnSize.MD_10);
			userBadgeColumn.addStyleName("margin-top-15");
			UserBadge renderer = ginInjector.getUserBadgeWidget();
			renderer.configure(profile, requestMessages.get(i));
			renderer.setSize(BadgeSize.LARGE);
			userBadgeColumn.add(renderer.asWidget());
			
			Button joinButton = DisplayUtils.createButton(DisplayConstants.ACCEPT, ButtonType.PRIMARY);
			joinButton.addStyleName("right margin-top-15 margin-right-15 btn-lg");
			joinButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					presenter.acceptRequest(profile.getOwnerId());
				}
			});
			Column buttonContainer = new Column(ColumnSize.XS_4, ColumnSize.SM_3, ColumnSize.MD_2);
			buttonContainer.add(joinButton);
			lc.add(userBadgeColumn);
			lc.add(buttonContainer);
			
			singleRow.add(lc);
		}
		mainContainer.add(singleRow);
		if (profiles.size() > 0)
			add(mainContainer);
	}
}
