package org.sagebionetworks.web.client.widget.discussion;

import org.sagebionetworks.repo.model.discussion.DiscussionThreadBundle;
import org.sagebionetworks.repo.model.discussion.DiscussionThreadOrder;
import org.sagebionetworks.repo.model.discussion.Forum;
import org.sagebionetworks.web.client.DiscussionForumClientAsync;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.place.LoginPlace;
import org.sagebionetworks.web.client.place.ParameterizedToken;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.utils.CallbackP;
import org.sagebionetworks.web.client.widget.discussion.modal.NewDiscussionThreadModal;
import org.sagebionetworks.web.client.widget.entity.controller.SynapseAlert;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ForumWidget implements ForumWidgetView.Presenter{

	public final static Boolean DEFAULT_MODERATOR_MODE = false;
	public final static Boolean SHOW_THREAD_DETAILS_FOR_THREAD_LIST = false;
	public final static Boolean SHOW_THREAD_DETAILS_FOR_SINGLE_THREAD = true;
	public final static Boolean SHOW_REPLY_DETAILS_FOR_THREAD_LIST = false;
	public final static Boolean SHOW_REPLY_DETAILS_FOR_SINGLE_THREAD = true;

	//used to tell the discussion forum to show a single thread
	public final static String THREAD_ID_KEY = "threadId";
	ForumWidgetView view;

	NewDiscussionThreadModal newThreadModal;
	DiscussionThreadListWidget threadListWidget;
	SynapseAlert synAlert;
	DiscussionForumClientAsync discussionForumClient;
	AuthenticationController authController;
	GlobalApplicationState globalApplicationState;
	DiscussionThreadWidget singleThreadWidget;

	String forumId;
	String entityId;
	Boolean isCurrentUserModerator;
	Callback showAllThreadsCallback;
	CallbackP<Boolean> emptyListCallback;
	Boolean isSingleThread;

	@Inject
	public ForumWidget(
			final ForumWidgetView view,
			SynapseAlert synAlert,
			DiscussionForumClientAsync discussionForumClient,
			DiscussionThreadListWidget threadListWidget,
			NewDiscussionThreadModal newThreadModal,
			AuthenticationController authController,
			GlobalApplicationState globalApplicationState,
			DiscussionThreadWidget singleThreadWidget
			) {
		this.view = view;
		this.synAlert = synAlert;
		this.threadListWidget = threadListWidget;
		this.newThreadModal = newThreadModal;
		this.discussionForumClient = discussionForumClient;
		this.authController = authController;
		this.globalApplicationState = globalApplicationState;
		this.singleThreadWidget = singleThreadWidget;
		view.setPresenter(this);
		view.setThreadList(threadListWidget.asWidget());
		view.setNewThreadModal(newThreadModal.asWidget());
		view.setAlert(synAlert.asWidget());
		view.setSingleThread(singleThreadWidget.asWidget());
		emptyListCallback = new CallbackP<Boolean>(){
			@Override
			public void invoke(Boolean param) {
				view.setEmptyUIVisible(!param);
				view.setThreadHeaderVisible(param);
			}
		};
	}

	public void configure(String entityId, ParameterizedToken params,
			Boolean isCurrentUserModerator, Callback showAllThreadsCallback) {
		this.entityId = entityId;
		this.isCurrentUserModerator = isCurrentUserModerator;
		this.showAllThreadsCallback = showAllThreadsCallback;
		synAlert.clear();
		//are we just showing a single thread, or the full list?
		if (params.containsKey(THREAD_ID_KEY)) {
			String threadId = params.get(THREAD_ID_KEY);
			showThread(threadId);
			isSingleThread = true;
		} else {
			showForum();
			isSingleThread = false;
		}
	}

	public void showThread(final String threadId) {
		view.setSingleThreadUIVisible(true);
		view.setThreadListUIVisible(false);
		view.setNewThreadButtonVisible(false);
		view.setShowAllThreadsButtonVisible(true);
		discussionForumClient.getThread(threadId, new AsyncCallback<DiscussionThreadBundle>(){

			@Override
			public void onFailure(Throwable caught) {
				view.setEmptyUIVisible(true);
				view.setThreadHeaderVisible(false);
				view.setSingleThreadUIVisible(false);
				synAlert.handleException(caught);
			}

			@Override
			public void onSuccess(DiscussionThreadBundle result) {
				view.setEmptyUIVisible(false);
				view.setThreadHeaderVisible(true);
				singleThreadWidget.configure(result, isCurrentUserModerator, new Callback(){

					@Override
					public void invoke() {
						showAllThreadsCallback.invoke();
						configure(entityId, new ParameterizedToken(null), isCurrentUserModerator, showAllThreadsCallback);
					}
				}, SHOW_THREAD_DETAILS_FOR_SINGLE_THREAD, SHOW_REPLY_DETAILS_FOR_SINGLE_THREAD);
				if (singleThreadWidget.isThreadCollapsed()) {
					singleThreadWidget.toggleThread();
				}
			}
		});
	}

	public void showForum() {
		view.setSingleThreadUIVisible(false);
		view.setThreadListUIVisible(true);
		view.setNewThreadButtonVisible(true);
		view.setShowAllThreadsButtonVisible(false);
		discussionForumClient.getForumMetadata(entityId, new AsyncCallback<Forum>(){
			@Override
			public void onFailure(Throwable caught) {
				synAlert.handleException(caught);
			}

			@Override
			public void onSuccess(final Forum forum) {
				forumId = forum.getId();
				newThreadModal.configure(forumId, new Callback(){
					@Override
					public void invoke() {
						threadListWidget.configure(forumId, DEFAULT_MODERATOR_MODE, emptyListCallback);
					}
				});
				threadListWidget.configure(forumId, DEFAULT_MODERATOR_MODE, emptyListCallback);
			}
		});
		view.setModeratorModeContainerVisibility(isCurrentUserModerator);
	}

	@Override
	public void onClickShowAllThreads() {
		synAlert.clear();
		if (showAllThreadsCallback != null) {
			showAllThreadsCallback.invoke();
		}
		showForum();
	}

	@Override
	public void onClickNewThread() {
		if (!authController.isLoggedIn()) {
			view.showErrorMessage(DisplayConstants.ERROR_LOGIN_REQUIRED);
			globalApplicationState.getPlaceChanger().goTo(new LoginPlace(LoginPlace.LOGIN_TOKEN));
		} else {
			newThreadModal.show();
		}
	}

	@Override
	public void onModeratorModeChange() {
		threadListWidget.configure(forumId, view.getModeratorMode(), emptyListCallback);
		newThreadModal.configure(forumId, new Callback(){
			@Override
			public void invoke() {
				threadListWidget.configure(forumId, view.getModeratorMode(), emptyListCallback);
			}
		});
	}

	@Override
	public Widget asWidget(){
		return view.asWidget();
	}

	@Override
	public void sortByReplies() {
		if (!isSingleThread) {
			threadListWidget.sortBy(DiscussionThreadOrder.NUMBER_OF_REPLIES);
		}
	}

	@Override
	public void sortByViews() {
		if (!isSingleThread) {
			threadListWidget.sortBy(DiscussionThreadOrder.NUMBER_OF_VIEWS);
		}
	}

	@Override
	public void sortByActivity() {
		if (!isSingleThread) {
			threadListWidget.sortBy(DiscussionThreadOrder.LAST_ACTIVITY);
		}
	}
}
