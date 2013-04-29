package org.mifosplatform.billing.item.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.item.command.ItemCommand;

public interface ItemWritePlatformService {

	CommandProcessingResult createItem(JsonCommand command);

	CommandProcessingResult updateItem(JsonCommand command, Long itemId);

	CommandProcessingResult deleteItem(Long itemId);

}
