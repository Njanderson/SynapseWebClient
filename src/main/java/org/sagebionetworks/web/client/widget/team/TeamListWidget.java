package org.sagebionetworks.web.client.widget.team;

import java.util.Map;

import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.schema.adapter.AdapterFactory;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.security.AuthenticationController;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TeamListWidget implements TeamListWidgetView.Presenter{

	private TeamListWidgetView view;
	private GlobalApplicationState globalApplicationState;
	private SynapseClientAsync synapseClient;
	private AuthenticationController authenticationController;
	private AdapterFactory adapterFactory;
	private RequestCountCallback requestCountCallback;
	public 	interface RequestCountCallback {
		void invoke(String teamId, Long requestCount);
	}

	@Inject
	public TeamListWidget(TeamListWidgetView view, 
			SynapseClientAsync synapseClient, 
			GlobalApplicationState globalApplicationState, 
			AuthenticationController authenticationController,
			AdapterFactory adapterFactory) {
		this.view = view;
		view.setPresenter(this);
		this.globalApplicationState = globalApplicationState;
		this.synapseClient = synapseClient;
		this.authenticationController = authenticationController;
		this.adapterFactory = adapterFactory;
	}
	
	@Override
	public void goTo(Place place) {
		globalApplicationState.getPlaceChanger().goTo(place);
	}
	
	public void configure(Map<Team, Long> teams, boolean isBig, boolean isRequestCountVisible) {
		configure(teams, isBig, isRequestCountVisible, null);
	}
	
	public void configure(Map<Team, Long> teams, boolean isBig, boolean isRequestCountVisible, RequestCountCallback requestCountCallback) {
		this.requestCountCallback = requestCountCallback;
		
		view.configure(teams.keySet(), isBig);
		//then asynchronously load the request counts
		if (isRequestCountVisible) {
			for (Team team : teams.keySet()) {
				//only inform the view if the resulting count is positive
				if (teams.get(team) > 0)
					view.setRequestCount(team.getId(), teams.get(team));
				if (requestCountCallback != null)
					requestCountCallback.invoke(team.getId(), teams.get(team));
			}
		}
	}
	
	public static void getTeams(String userId, SynapseClientAsync synapseClient, final AdapterFactory adapterFactory, final AsyncCallback<Map<Team, Long>> teamCallback) {
		synapseClient.getTeamsForUserWithRequestCount(userId, new AsyncCallback<Map<Team, Long>>() {
			@Override
			public void onSuccess(Map<Team, Long> teams) {
				teamCallback.onSuccess(teams);
			}
			@Override
			public void onFailure(Throwable caught) {
				teamCallback.onFailure(caught);
			}
		});
	}
	
//	public void queryForRequestCount(final String teamId) {
//		synapseClient.getOpenRequestCount(authenticationController.getCurrentUserPrincipalId(), teamId, new AsyncCallback<Long>() {
//			@Override
//			public void onSuccess(Long result) {
//				if (result != null) {
//					//only inform the view if the resulting count is positive
//					if (result > 0)
//						view.setRequestCount(teamId, result);
//					if (requestCountCallback != null)
//						requestCountCallback.invoke(teamId, result);
//				}
//			}
//			@Override
//			public void onFailure(Throwable caught) {
//				if(!DisplayUtils.handleServiceException(caught, globalApplicationState, authenticationController.isLoggedIn(), view)) {					
//					view.showErrorMessage(caught.getMessage());
//				} 
//			}
//		});
//	}
	
	public Widget asWidget() {
		view.setPresenter(this);
		return view.asWidget();
	}
	
	public void clear() {
		view.clear();
	}
	
	public void showLoading() {
		view.showLoading();
	}

}
