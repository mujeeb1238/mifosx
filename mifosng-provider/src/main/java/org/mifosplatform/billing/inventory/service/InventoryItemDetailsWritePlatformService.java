package org.mifosplatform.billing.inventory.service;

import org.mifosplatform.billing.inventory.command.ItemDetailsCommand;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface InventoryItemDetailsWritePlatformService {


	CommandProcessingResult addItem(JsonCommand command);
	
	CommandProcessingResult addMultipleItems(ItemDetailsCommand command);

}
