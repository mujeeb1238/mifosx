package org.mifosplatform.billing.inventory.service;

import java.util.Collection;

import org.mifosplatform.billing.inventory.data.InventoryItemDetailsData;

public interface InventoryItemDetailsReadPlatformService {

	
	public Collection<InventoryItemDetailsData> retriveAllItemDetails();
	
	public InventoryItemDetailsData retriveIndividualItemDetails();

}
