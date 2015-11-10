package org.sagebionetworks.web.client.widget.verification;

import java.util.List;

import org.sagebionetworks.repo.model.wiki.WikiHeader;
import org.sagebionetworks.web.client.SynapseView;

import com.google.gwt.user.client.ui.Widget;

public interface VerificationSubmissionModalView extends SynapseView {
	void clear();
	void show();
	void hide();
	void setSynAlert(Widget w);
	void setFileHandleList(Widget w);
	void setWikiPage(Widget w);
	void setWikiPageVisible(boolean visible);
	void setPresenter(Presenter presenter);
	void setTitle(String title);
	void setEmails(List<String> emails);
	void setFirstName(String fname);
	void setLastName(String lname);
	void setOrganization(String organization);
	void setLocation(String location);
	void setOrcID(String href);
	void setSubmitButtonVisible(boolean visible);
	void setCancelButtonVisible(boolean visible);
	void setOKButtonVisible(boolean visible);
	void setApproveButtonVisible(boolean visible);
	void setRejectButtonVisible(boolean visible);
	void setSuspendButtonVisible(boolean visible);
	void setSuspendedReason(String reason);
	void popupError(String message);
	void openWindow(String url);
	/**
	 * Presenter interface
	 */
	public interface Presenter {
		void submitVerification();
		
		void approveVerification();
		void rejectVerification();
		
		void suspendVerification();
	}
}
