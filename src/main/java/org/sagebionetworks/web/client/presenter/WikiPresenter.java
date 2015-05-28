package org.sagebionetworks.web.client.presenter;

import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.RssServiceAsync;
import org.sagebionetworks.web.client.place.WikiPlace;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.view.WikiView;
import org.sagebionetworks.web.client.widget.entity.controller.SynapseAlert;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
/**
 * Should be able to create wiki content within Synapse and use the SynapseWikiPresenter to display, rather than caching an external source.
 * @author jayhodgson
 *
 */
@Deprecated
public class WikiPresenter extends AbstractActivity implements WikiView.Presenter, Presenter<WikiPlace> {
		
	private WikiPlace place;
	private WikiView view;
	private AuthenticationController authenticationController;
	private RssServiceAsync rssService;
	private GlobalApplicationState globalApplicationState;
	private SynapseAlert synAlert;
	
	@Inject
	public WikiPresenter(WikiView view, 
			AuthenticationController authenticationController, 
			GlobalApplicationState globalApplicationState, 
			RssServiceAsync rssService,
			SynapseAlert synAlert){
		this.authenticationController = authenticationController;
		this.view = view;
		this.rssService = rssService;
		this.globalApplicationState = globalApplicationState;
		this.synAlert = synAlert;
		view.setPresenter(this);
		view.setSynAlertWidget(synAlert.asWidget());
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		// Install the view
		panel.setWidget(view);
	}

	@Override
	public void setPlace(WikiPlace place) {
		this.place = place;
		this.view.setPresenter(this);
		
		String cacheProviderId = place.toToken();
		//the token is the source page cached content provider id to pull from
		loadSourceContent(cacheProviderId);
	}
	
	@Override
	public void loadSourceContent(String cacheProviderId) {
		synAlert.clear();
		rssService.getCachedContent(cacheProviderId, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				view.showPage(DisplayUtils.fixWikiLinks(DisplayUtils.fixEmbeddedYouTube(result)));
			}
			@Override
			public void onFailure(Throwable caught) {
				synAlert.handleException(caught);
			}
		});
		
	}
	
	@Override
    public String mayStop() {
        view.clear();
        return null;
    }
}
