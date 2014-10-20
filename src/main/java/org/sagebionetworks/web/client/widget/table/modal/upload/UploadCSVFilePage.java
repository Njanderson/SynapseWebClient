package org.sagebionetworks.web.client.widget.table.modal.upload;
/**
 * Abstraction for page that handles the upload of a CSV file.
 *  
 * @author jhill
 *
 */
public interface UploadCSVFilePage extends ModalPage {

	/**
	 * Configure this page before using it.
	 * @param parentId The ID of the parent project of the new table.
	 */
	void configure(String parentId);

}