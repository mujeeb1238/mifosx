package org.mifosplatform.portfolio.item.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ItemNotFoundException extends AbstractPlatformResourceNotFoundException {

public ItemNotFoundException() {
super("error.msg.item.id.not.found",
		"Item is Not Found");
}

public ItemNotFoundException(String id){
	super("error.msg.item.id.not.found","Item is Not Found With Id "+id);
}

}
