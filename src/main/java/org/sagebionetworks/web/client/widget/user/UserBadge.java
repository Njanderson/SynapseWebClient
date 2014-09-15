package org.sagebionetworks.web.client.widget.user;

import java.util.Map;

import org.sagebionetworks.markdown.constants.WidgetConstants;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.schema.adapter.AdapterFactory;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.cache.ClientCache;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.widget.SynapseWidgetPresenter;
import org.sagebionetworks.web.client.widget.WidgetRendererPresenter;
import org.sagebionetworks.web.shared.WebConstants;
import org.sagebionetworks.web.shared.WikiPageKey;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class UserBadge implements UserBadgeView.Presenter, SynapseWidgetPresenter, WidgetRendererPresenter {
	
	private UserBadgeView view;
	SynapseClientAsync synapseClient;
	AdapterFactory adapterFactory;
	private Integer maxNameLength;
	ClientCache clientCache;
	
	@Inject
	public UserBadge(UserBadgeView view, SynapseClientAsync synapseClient, AdapterFactory adapterFactory, ClientCache clientCache) {
		this.view = view;
		this.synapseClient = synapseClient;
		this.adapterFactory = adapterFactory;
		this.clientCache = clientCache;
		view.setPresenter(this);
	}
	
	public void setMaxNameLength(Integer maxLength) {
		this.maxNameLength = maxLength;
	}
	
	public void configure(UserProfile profile) {
		view.setProfile(profile, maxNameLength);
	}
	
	@Override
	public void configure(WikiPageKey wikiKey, Map<String, String> widgetDescriptor, Callback widgetRefreshRequired, Long wikiVersionInView) {
		//get the user id from the descriptor, and pass to the other configure
		configure(widgetDescriptor.get(WidgetConstants.USERBADGE_WIDGET_ID_KEY));
	}
	
	public void configure(final String principalId) {
		if (principalId != null && principalId.trim().length() > 0) {
			view.showLoading();
			
			UserBadge.getUserProfile(principalId, adapterFactory, synapseClient, clientCache, new AsyncCallback<UserProfile>() {
				@Override
				public void onSuccess(UserProfile profile) {
					view.setProfile(profile, maxNameLength);
				}
				@Override
				public void onFailure(Throwable caught) {
					view.showLoadError(principalId);
				}
			});
		}
	}
	
	/**
	 * When the username is clicked, call this clickhandler instead of the default behavior
	 * @param clickHandler
	 */
	public void setCustomClickHandler(ClickHandler clickHandler) {
		view.setCustomClickHandler(clickHandler);
	}	

	public static void getUserProfile(final String principalId, final AdapterFactory adapterFactory, SynapseClientAsync synapseClient, final ClientCache clientCache, final AsyncCallback<UserProfile> callback) {
		String profileString = clientCache.get(principalId + WebConstants.USER_PROFILE_SUFFIX);
		if (profileString != null) {
			parseProfile(profileString, adapterFactory, callback);
		} else {
		synapseClient.getUserProfile(principalId, new AsyncCallback<UserProfile>() {			
			@Override
			public void onSuccess(UserProfile result) {
					JSONObjectAdapter adapter = adapterFactory.createNew();
					try {
						result.writeToJSONObject(adapter);
						clientCache.put(principalId + WebConstants.USER_PROFILE_SUFFIX, adapter.toJSONString());
						callback.onSuccess(result);
					} catch (JSONObjectAdapterException e) {
						onFailure(e);
					}
				}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}
	}
	
	public static void parseProfile(String profileString, AdapterFactory adapterFactory, AsyncCallback<UserProfile> callback) {
		try {
			UserProfile profile = new UserProfile(adapterFactory.createNew(profileString));
			callback.onSuccess(profile);
		} catch (JSONObjectAdapterException e) {
			callback.onFailure(e);
		}

	}
	
	@SuppressWarnings("unchecked")
	public void clearState() {
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

}
