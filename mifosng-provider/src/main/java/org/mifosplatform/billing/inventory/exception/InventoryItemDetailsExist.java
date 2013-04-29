package org.mifosplatform.billing.inventory.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

@SuppressWarnings("serial")
public class InventoryItemDetailsExist extends AbstractPlatformDomainRuleException{

	public InventoryItemDetailsExist(final String msg1,final String msg2,final String serialNumber1,final String serialNumber2){
		super(msg1, msg2,serialNumber1,serialNumber2);
	}
	
}