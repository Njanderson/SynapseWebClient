package org.sagebionetworks.web.client.widget.entity.controller;

import java.util.List;

import org.sagebionetworks.repo.model.ACTAccessApproval;
import org.sagebionetworks.repo.model.AccessApproval;
import org.sagebionetworks.repo.model.AccessRequirement;
import org.sagebionetworks.repo.model.EntityBundle;
import org.sagebionetworks.repo.model.UserBundle;
import org.sagebionetworks.repo.model.UserGroupHeader;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.UserProfileClientAsync;
import org.sagebionetworks.web.client.widget.search.SynapseSuggestBox;
import org.sagebionetworks.web.client.widget.search.UserGroupSuggestionProvider;
import org.sagebionetworks.web.client.widget.search.UserGroupSuggestionProvider.UserGroupSuggestion;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class ACTEditorWidget implements ACTEditorWidgetView.Presenter{

	ACTEditorWidgetView view;
	SynapseSuggestBox peopleSuggestWidget;
	UserProfileClientAsync userProfileClient;
	SynapseAlert synAlert;
	SynapseClientAsync synClient;

	public static int IS_VERIFIED = 0x10;
	
	@Inject
	public ACTEditorWidget(ACTEditorWidgetView view, 
			SynapseSuggestBox peopleSuggestBox,
			UserGroupSuggestionProvider provider,
			SynapseAlert synAlert, 
			UserProfileClientAsync userProfileClient,
			SynapseClientAsync synClient) {
		this.view = view;
		this.peopleSuggestWidget = peopleSuggestBox;
		this.userProfileClient = userProfileClient;
		this.synAlert = synAlert;
		this.synClient = synClient;
		peopleSuggestWidget.setSuggestionProvider(provider);
		view.setSuggestWidget(peopleSuggestBox.asWidget());
		view.setSynAlertWidget(synAlert.asWidget());
	}
	
	@Override
	public void configure(EntityBundle bundle) {
		List<AccessRequirement> arList = bundle.getAccessRequirements();
		for (AccessRequirement ar: arList) {
			view.addAccessRequirement(String.valueOf(ar.getId()));
		}
	}
	
	@Override
	public void validateAndGrantAccess() {
		UserGroupSuggestion suggestion = (UserGroupSuggestion)peopleSuggestWidget.getSelectedSuggestion();
		Long accessRequirementId = Long.valueOf(view.getAccessRequirementId());
		int mask = IS_VERIFIED;
		if (suggestion != null && accessRequirementId != null) {
			final Long ownerId = Long.valueOf(suggestion.getHeader().getOwnerId());
			userProfileClient.getUserBundle(Long.valueOf(ownerId), mask, new AsyncCallback<UserBundle>() {
				@Override
				public void onFailure(Throwable caught) {
					synAlert.showError("Could not confirm user is a Verified Synapse User");
				}
				@Override
				public void onSuccess(UserBundle result) {
					if (result.getIsVerified()) {
						doGrantAccess();
					}
				}
			});
		} else {
			synAlert.showError("Please select a user and an access requirement to grant access.");
		}
	}
	
	public void doGrantAccess() {
		UserGroupHeader header = ((UserGroupSuggestion)peopleSuggestWidget.getSelectedSuggestion()).getHeader();
		final String principalId = header.getOwnerId();
		Long accessRequirementId = Long.valueOf(view.getAccessRequirementId());
		ACTAccessApproval approval = new ACTAccessApproval();
		approval.setRequirementId(accessRequirementId);
		approval.setAccessorId(principalId);
		synClient.createAccessApproval(approval, new AsyncCallback<AccessApproval>() {

			@Override
			public void onFailure(Throwable caught) {
				synAlert.showError("Error granting access: " + caught.getMessage());
			}

			@Override
			public void onSuccess(AccessApproval result) {
				view.showInfo("Access Requirement granted", "User " + result.getAccessorId() + " granted access to Access Requirement " + result.getRequirementId());
			}
			
		});
	}

	public void show() {
		clear();
		view.show();
	}
	
	public void clear() {
		view.clear();
		synAlert.clear();
		peopleSuggestWidget.clear();
	}

	public void hide() {
		view.hide();
	}
	
}
