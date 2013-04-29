package org.mifosplatform.billing.inventory.service;

import java.util.List;

import org.mifosplatform.billing.inventory.command.ItemDetailsCommand;
import org.mifosplatform.billing.inventory.command.ItemDetailsCommandValidator;
import org.mifosplatform.billing.inventory.domain.InventoryGrn;
import org.mifosplatform.billing.inventory.serialization.InventoryItemCommandFromApiJsonDeserializer;
import org.mifosplatform.billing.inventory.service.InventoryItemDetailsWritePlatformService;
import org.mifosplatform.billing.inventory.domain.InventoryGrnRepository;
import org.mifosplatform.billing.inventory.domain.InventoryItemDetails;
import org.mifosplatform.billing.inventory.domain.InventoryItemDetailsCommandValidator;
import org.mifosplatform.billing.inventory.domain.InventoryItemDetailsRepository;
import org.mifosplatform.billing.inventory.domain.ItemDetails;
import org.mifosplatform.billing.inventory.domain.ItemDetailsRepository;
import org.mifosplatform.billing.inventory.exception.InventoryItemDetailsExist;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class InventoryItemDetailsWritePlatformServiceImp implements InventoryItemDetailsWritePlatformService{
	
	
	private PlatformSecurityContext context;
	private InventoryItemDetailsRepository inventoryItemDetailsRepository;
	private InventoryGrnRepository inventoryGrnRepository;
	private ItemDetailsRepository itemDetailsRepository;
	private InventoryItemCommandFromApiJsonDeserializer inventoryItemCommandFromApiJsonDeserializer;
	@Autowired
	public InventoryItemDetailsWritePlatformServiceImp(final PlatformSecurityContext context,InventoryItemDetailsRepository inventoryItemDetailsRepository,InventoryGrnRepository inventoryitemRopository,ItemDetailsRepository itemDetailsRepository,InventoryItemCommandFromApiJsonDeserializer inventoryItemCommandFromApiJsonDeserializer) {
		this.context=context;
		this.inventoryItemDetailsRepository=inventoryItemDetailsRepository;
		this.inventoryGrnRepository=inventoryitemRopository;
		this.itemDetailsRepository = itemDetailsRepository;
		this.inventoryItemCommandFromApiJsonDeserializer = inventoryItemCommandFromApiJsonDeserializer;
	}
	
	private final static Logger logger = (Logger) LoggerFactory.getLogger(InventoryItemDetailsWritePlatformServiceImp.class);
	
	
	
	@Transactional
	@Override
	public CommandProcessingResult addItem(JsonCommand command) {
		InventoryItemDetails inventoryItemDetails=null;
		try{
			context.authenticatedUser();
			
			this.context.authenticatedUser();
			
			inventoryItemCommandFromApiJsonDeserializer.validateForCreate(command.json());
			
			inventoryItemDetails = InventoryItemDetails.fromJson(command);
			
			
			/*InventoryItemDetailsCommandValidator validator = new InventoryItemDetailsCommandValidator(command);
			validator.validateForCreate();
			
			Integer item = command.integerValueOfParameterNamed("itemMasterId");
			Long itemMasterId = item.longValue();
			Integer w = command.integerValueOfParameterNamed("warranty");
			Long warranty = w.longValue();
			String remarks = command.stringValueOfParameterNamed("remarks");
			Long grnId = command.longValueOfParameterNamed("grnId");
			String serialNumber = command.stringValueOfParameterNamed("serialNumber");
			String provisioningSerialNumber = command.stringValueOfParameterNamed("provisioningSerialNumber");
			String status = command.stringValueOfParameterNamed("status");
			String quality = command.stringValueOfParameterNamed("quality");
			*/
			//to be check by me later ..!
			/*InventoryItemDetailsCommandValidator validator = new InventoryItemDetailsCommandValidator(command);
			validator.validateForCreate();*/
			
			/*List<InventoryItemDetails> availService= this.inventoryItemDetailsRepository.findAll();
			
			for(InventoryItemDetails items:availService){
				String serialNumberFromItemDetails = items.getSerialNumber();
				String serialNumberFromItemCommand = command.getSerialNumber();
				if(serialNumberFromItemDetails.equalsIgnoreCase(serialNumberFromItemCommand)){
					throw new InventoryItemDetailsExist("Item is already existing with item SerialNumber: "+command.getSerialNumber(),"Item is already existing with item SerialNumber",command.getSerialNumber(),command.getSerialNumber());
				}
			}*/
			

			
			


			
			
			 //inventoryItemDetails = new InventoryItemDetails(itemMasterId,serialNumber,grnId,provisioningSerialNumber,quality,status,warranty,remarks);
			 InventoryGrn inventoryGrn = inventoryGrnRepository.findOne(inventoryItemDetails.getGrnId());
			 
			 if(inventoryGrn.getReceivedQuantity()>=1){
				 inventoryGrn.setReceivedQuantity(inventoryGrn.getReceivedQuantity()-1);
				 this.inventoryGrnRepository.save(inventoryGrn);
			 }else{
				 throw new InventoryItemDetailsExist("received.quantity.is.nill.hence.your.item.details.will.not.be.saved","","","");
			 }
			 this.inventoryItemDetailsRepository.save(inventoryItemDetails);
			 
			
		} catch (DataIntegrityViolationException dve){
				handleDataIntegrityIssues(command, dve); 
			return new CommandProcessingResult(Long.valueOf(-1));
		}
			
		return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(inventoryItemDetails.getId()).build();
	}
	
	


	@Transactional
	@Override
	public CommandProcessingResult addMultipleItems(ItemDetailsCommand command) {
		ItemDetails itemDetails;
		try{
			context.authenticatedUser();
			
			this.context.authenticatedUser();
			ItemDetailsCommandValidator validator = new ItemDetailsCommandValidator(command);
			validator.validateForCreate();
			
			List<ItemDetails> availService= this.itemDetailsRepository.findAll();
			
			for(ItemDetails items:availService){
				String serialNumberFromItemDetails = items.getSerialNumber();
				String serialNumberFromItemCommand = command.getSerialNumber();
				if(serialNumberFromItemDetails.equalsIgnoreCase(serialNumberFromItemCommand)){
				throw new InventoryItemDetailsExist("item details already exist","item details already exist","item details already exist",command.getSerialNumber());
					//System.out.println(command.getSerialNumber());
					//return new CommandProcessingResult(Long.valueOf(-1));
					
				}
			}
			
			
			 itemDetails = ItemDetails.create(command.getItemMasterId(), command.getSerialNumber(), command.getGrnId(),command.getProvisioningSerialNumber(), command.getQuality(),command.getStatus(), command.getOfficeId(), command.getClientId(), command.getWarranty(), command.getRemark());
			
			 this.itemDetailsRepository.save(itemDetails);		
			
		} catch (DataIntegrityViolationException dve) {
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
		
		return new CommandProcessingResult(itemDetails.getId());

}
		/*InventoryItemDetails inventoryItemDetails;
		try{
			context.authenticatedUser();
			
			this.context.authenticatedUser();
			ItemDetailsCommandValidator validator = new ItemDetailsCommandValidator(command);
			validator.validateForCreate();
			
			//List<InventoryItemDetails> availService= this.inventoryItemDetailsRepository.findAll();
			
			for(InventoryItemDetails items:availService){
				String serialNumberFromItemDetails = items.getSerialNumber();
				String serialNumberFromItemCommand = command.getSerialNumber();
				if(serialNumberFromItemDetails.equalsIgnoreCase(serialNumberFromItemCommand)){
				throw new ItemDetailsExist(command.getSerialNumber());
					//System.out.println(command.getSerialNumber());
					return new CommandProcessingResult(Long.valueOf(-1));
					
				}
			}
			
			 inventoryItemDetails = new InventoryItemDetails(command.getItemMasterId(),command.getSerialNumber(),command.getGrnId(),command.getProvisioningSerialNumber(),command.getQuality(),command.getStatus(), command.getOfficeId(), command.getClientId(), command.getWarranty(), command.getRemark());
			
			this.inventoryItemDetailsRepository.save(inventoryItemDetails);		
			
		} catch (DataIntegrityViolationException dve) {
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
		
		return new CommandProcessingResult(inventoryItemDetails.getId());
	}*/
	
		private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

	         Throwable realCause = dve.getMostSpecificCause();
	        if (realCause.getMessage().contains("serial_no_constraint")){
	        	throw new PlatformDataIntegrityException("error.msg.inventory.item.duplicate.serialNumber", "Item Details with SerialNumber" + command.stringValueOfParameterNamed("serialNumber")+ " already exists", "SerialNumber", command.stringValueOfParameterNamed("serialNumber"));
	        }


	        logger.error(dve.getMessage(), dve);
	       // throw new PlatformDataIntegrityException("error.msgdeposit.accountt.unknown.data.integrity.issue","Unknown data integrity issue with resource.");   	
	}

}



