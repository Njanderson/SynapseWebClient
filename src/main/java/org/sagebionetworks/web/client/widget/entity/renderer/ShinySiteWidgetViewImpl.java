package org.sagebionetworks.web.client.widget.entity.renderer;

import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ShinySiteWidgetViewImpl implements ShinySiteWidgetView {
	
	public interface Binder extends UiBinder<Widget, ShinySiteWidgetViewImpl> {}
	
	Presenter presenter;
	
	@UiField
	Frame shinySiteFrame;
	
	@UiField
	SpanElement siteurl;
	
	@UiField
	HTMLPanel invalidSite;
	
	Widget widget;
	
	@Inject
	public ShinySiteWidgetViewImpl(Binder uiBinder) {
		widget = uiBinder.createAndBindUi(this);
	}
	
	@Override
	public void configure(String siteUrl, int height) {
		shinySiteFrame.setVisible(true);
		invalidSite.setVisible(false);
		siteurl.setInnerText(siteUrl);
		shinySiteFrame.setHeight(String.valueOf(height) + "px");
		shinySiteFrame.setUrl(siteUrl);
	}	
	
	@Override
	public Widget asWidget() {
		return widget;
	}	

	@Override
	public void showInvalidSiteUrl(String siteUrl) {
		siteurl.setInnerText(siteUrl);
		shinySiteFrame.setVisible(false);
		invalidSite.setVisible(true);	
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}		
	
	/*
	 * Private Methods
	 */

}
