package org.mifosplatform.billing.uploadstatus.service;

import org.mifosplatform.billing.uploadstatus.command.UploadStatusCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;


public interface UploadStatusWritePlatformService {


	CommandProcessingResult addItem(UploadStatusCommand command);
	CommandProcessingResult updateUploadStatus(Long orderId);
	UploadStatusCommand convertJsonToUploadStatusCommand(Object object,String jsonRequestBody);
	
	
	

}
