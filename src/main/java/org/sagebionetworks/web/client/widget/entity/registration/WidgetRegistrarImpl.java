package org.sagebionetworks.web.client.widget.entity.registration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.PortalGinInjector;
import org.sagebionetworks.web.client.transform.NodeModelCreator;
import org.sagebionetworks.web.client.widget.WidgetEditorPresenter;
import org.sagebionetworks.web.client.widget.WidgetRendererPresenter;

import com.google.inject.Inject;


public class WidgetRegistrarImpl implements WidgetRegistrar {
	
	private HashMap<String, String> contentType2FriendlyName = new HashMap<String, String>();
	
	PortalGinInjector ginInjector;
	NodeModelCreator nodeModelCreator;
	JSONObjectAdapter adapter;
	Map<Character, String> c2h = new HashMap<Character, String>();
	Map<String, Character> h2c = new HashMap<String, Character>();
	
	@Inject
	public WidgetRegistrarImpl(PortalGinInjector ginInjector, NodeModelCreator nodeModelCreator, JSONObjectAdapter adapter) {
		this.ginInjector = ginInjector;
		this.nodeModelCreator = nodeModelCreator;
		this.adapter = adapter;
		initWithKnownWidgets();
		initCharacter2HexCodeMap();
	}
	
	public void initCharacter2HexCodeMap() {
		//encodes { } - _ . ! ~ * ' ( ) [ ] : ; / ? & = + , # $ %
		c2h.put('{',"%7B");
		c2h.put('}', "%7D");
		c2h.put('-', "%2D");
		c2h.put('_', "%5F");
		c2h.put('.', "%2E");
		c2h.put('!', "%21");
		c2h.put('~', "%7E");
		c2h.put('*', "%2A");
		c2h.put('`', "%60");
		c2h.put('\'', "%27");
		c2h.put('(', "%28");
		c2h.put(')', "%29");
		c2h.put('[', "%5B");
		c2h.put(']', "%5D");
		c2h.put(':', "%3A");
		c2h.put(';', "%3B");
		c2h.put('\n', "%0A");	//LF
		c2h.put('\r', "%0D");	//CR
		c2h.put('/', "%2F");
		c2h.put('?', "%3F");
		c2h.put('&', "%26");
		c2h.put('=', "%3D");
		c2h.put('+', "%2B");
		c2h.put(',', "%2C");
		c2h.put('#', "%23");
		c2h.put('$', "%24");
		c2h.put('%', "%25");
		
		//reverse lookup for decode
		for (Iterator iterator = c2h.keySet().iterator(); iterator.hasNext();) {
			Character v = (Character) iterator.next();
			h2c.put(c2h.get(v), v);
		}
	}
	
	@Override
	public void registerWidget(String contentTypeKey, String friendlyName) {
		contentType2FriendlyName.put(contentTypeKey, friendlyName);
	}
	
	/**
	 * Given a widget content type, returns the widget that can be used to edit the WidgetDescriptor (model) for that widget type.
	 * @param widgetClass
	 * @return
	 */
	@Override
	public WidgetEditorPresenter getWidgetEditorForWidgetDescriptor(String entityId, String contentTypeKey, Map<String, String> model) { 
		//use gin to create a new instance of the proper class.
		WidgetEditorPresenter presenter = null;
		if (contentTypeKey.equals(WidgetConstants.YOUTUBE_CONTENT_TYPE)) {
			presenter = ginInjector.getYouTubeConfigEditor();
		} else if (contentTypeKey.equals(WidgetConstants.PROVENANCE_CONTENT_TYPE)) {
			presenter = ginInjector.getProvenanceConfigEditor();
		} else if (contentTypeKey.equals(WidgetConstants.IMAGE_CONTENT_TYPE)) {
			presenter = ginInjector.getImageConfigEditor();
		} else if (contentTypeKey.equals(WidgetConstants.LINK_CONTENT_TYPE)) {
			presenter = ginInjector.getLinkConfigEditor();
		} else if (contentTypeKey.equals(WidgetConstants.API_TABLE_CONTENT_TYPE)) {
			presenter = ginInjector.getSynapseAPICallConfigEditor();
		} //TODO: add other widget descriptors to this mapping as they become available
		
		if (presenter != null)
			presenter.configure(entityId, model);
		return presenter;
	}

	@Override
	public String getWidgetContentType(Map<String, String> model) {
		return model.get("contentType");
	}
	
	/**
	 * Given a widget content type, returns the widget that can be used to edit the WidgetDescriptor (model) for that widget type.
	 * @param widgetClass
	 * @return
	 */
	@Override
	public WidgetRendererPresenter getWidgetRendererForWidgetDescriptor(String entityId, String contentTypeKey, Map<String, String> model) { 
		//use gin to create a new instance of the proper class.
		WidgetRendererPresenter presenter = null;
		if (contentTypeKey.equals(WidgetConstants.YOUTUBE_CONTENT_TYPE)) {
			presenter = ginInjector.getYouTubeRenderer();
		} else if (contentTypeKey.equals(WidgetConstants.PROVENANCE_CONTENT_TYPE)) {
			presenter = ginInjector.getProvenanceRenderer();
		} else if (contentTypeKey.equals(WidgetConstants.IMAGE_CONTENT_TYPE)) {
			presenter = ginInjector.getImageRenderer();
		} else if (contentTypeKey.equals(WidgetConstants.API_TABLE_CONTENT_TYPE)) {
			presenter = ginInjector.getSynapseAPICallRenderer();
		} //TODO: add other widget descriptors to this mapping as they become available
		
		if (presenter != null)
			presenter.configure(entityId, model);
		return presenter;
	}
	@Override
	public String getFriendlyTypeName(String contentTypeKey) {
		String friendlyName = contentType2FriendlyName.get(contentTypeKey);
		if (friendlyName != null)
			return friendlyName;
		else return "Widget";
	}

	
	@Override
	public String getMDRepresentation(String contentType, Map<String, String> model) {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(contentType);
		char prefix = '?';
		for (Iterator iterator = model.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			Object value = model.get(key);
			//only include it in the md representation if the value is not null
			if (value != null) {
				urlBuilder.append(prefix).append(key).append('=').append(encodeValue(value.toString()));
			}
			prefix = '&';
		}
		return urlBuilder.toString();
	}
	
	public String encodeValue(String value) {
		StringBuilder newValue = new StringBuilder(value);
		//and encode everything that URL says that it doesn't encode (and more).
		int totalCount = newValue.length();
		for (int i = 0; i < totalCount; i++) {
			char c = newValue.charAt(i);
			if (c2h.containsKey(c)) {
				newValue.deleteCharAt(i); //-1 character
				String replacement = c2h.get(c);
				newValue.insert(i, replacement); //+(replacement.length()) characters
				totalCount += replacement.length() - 1;
				i+=replacement.length()-1;
			}
		}
		return newValue.toString();
	}
	
	public String decodeValue(String value) {
		//detect the hex codes using a sliding window (of 3 characters)
		//if the input value is less than 3 in length, then there's nothing to decode
		if (value.length() < 3){
			return value;
		}
		//build up the output
		StringBuilder output = new StringBuilder();
		
		int start = 0;
		int end=3;
		for (; end <= value.length();) {
			String currentSubString = value.substring(start, end);
			if (h2c.containsKey(currentSubString)) {
				//found one, add the resolved character to the output and skip ahead
				output.append(h2c.get(currentSubString));
				start += 3;
				end += 3;
			} else {
				//is not one, just append the character at start, and move the window over
				output.append(value.charAt(start));
				start++;
				end++;
			}
		}
		//check to see if we have any left over in the window
		if (end == value.length() + 1 && start == value.length()-2) {
			//our window went outside of the boundary.  append the remaining characters to the output
			output.append(value.substring(start, value.length()));
		}

		return output.toString();
	}
	
	@Override
	public Map<String, String> getWidgetDescriptor(String md) {
		if (md == null || md.length() == 0) throw new IllegalArgumentException(DisplayConstants.INVALID_WIDGET_MARKDOWN_MESSAGE + md);
		int delimeter = md.indexOf("?");
		if (delimeter < 0) {
			throw new IllegalArgumentException(DisplayConstants.INVALID_WIDGET_MARKDOWN_MESSAGE + md);
		}
		String contentTypeKey = md.substring(0, delimeter);
		String allParamsString = md.substring(delimeter+1);
		String[] keyValuePairs = allParamsString.split("&");
		Map<String, String> model = new HashMap<String, String>();
		for (int j = 0; j < keyValuePairs.length; j++) {
			String[] keyValue = keyValuePairs[j].split("=");
			model.put(keyValue[0], decodeValue(keyValue[1]));
		}
		return model;
	}

	@Override
	public String getWidgetContentType(String mdRepresentation) {
		if (mdRepresentation == null || mdRepresentation.length() == 0) throw new IllegalArgumentException(DisplayConstants.INVALID_WIDGET_MARKDOWN_MESSAGE + mdRepresentation);
		String decodedMd = mdRepresentation;
		if (decodedMd == null || decodedMd.length() == 0) throw new IllegalArgumentException(DisplayConstants.INVALID_WIDGET_MARKDOWN_MESSAGE + decodedMd);
		int delimeter = decodedMd.indexOf("?");
		if (delimeter < 0) {
			throw new IllegalArgumentException(DisplayConstants.INVALID_WIDGET_MARKDOWN_MESSAGE + decodedMd);
		}
		return decodedMd.substring(0, delimeter);
	}
	
	private void initWithKnownWidgets() {
		registerWidget(WidgetConstants.YOUTUBE_CONTENT_TYPE, WidgetConstants.YOUTUBE_FRIENDLY_NAME);
		registerWidget(WidgetConstants.PROVENANCE_CONTENT_TYPE, WidgetConstants.PROVENANCE_FRIENDLY_NAME);
		registerWidget(WidgetConstants.IMAGE_CONTENT_TYPE, WidgetConstants.IMAGE_FRIENDLY_NAME);
		registerWidget(WidgetConstants.LINK_CONTENT_TYPE, WidgetConstants.LINK_FRIENDLY_NAME);
		registerWidget(WidgetConstants.API_TABLE_CONTENT_TYPE, WidgetConstants.API_TABLE_FRIENDLY_NAME);
	}
	
	public static String getWidgetMarkdown(String contentType, Map<String, String> widgetDescriptor, WidgetRegistrar widgetRegistrar) throws JSONObjectAdapterException {
		StringBuilder sb = new StringBuilder();
		sb.append(WidgetConstants.WIDGET_START_MARKDOWN);
		sb.append(widgetRegistrar.getMDRepresentation(contentType, widgetDescriptor));
		sb.append("}");
		return sb.toString();
	}

}