package org.mifosplatform.billing.ticketmaster.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.mifosplatform.billing.ticketmaster.command.TicketMasterCommand;
import org.mifosplatform.billing.ticketmaster.domain.TicketDetail;
import org.mifosplatform.billing.ticketmaster.domain.TicketMaster;
import org.mifosplatform.billing.ticketmaster.repository.TicketDetailsRepository;
import org.mifosplatform.billing.ticketmaster.repository.TicketMasterRepository;
import org.mifosplatform.billing.ticketmaster.serialization.TicketMasterCloseFromApiJsonDeserializer;
import org.mifosplatform.billing.ticketmaster.serialization.TicketMasterFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.documentmanagement.command.DocumentCommand;
import org.mifosplatform.infrastructure.documentmanagement.exception.DocumentManagementException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TicketMasterWritePlatformServiceImpl implements TicketMasterWritePlatformService{
	
	private PlatformSecurityContext context;
	private TicketMasterRepository repository;
	private TicketDetailsRepository ticketDetailsRepository;
	private TicketMasterFromApiJsonDeserializer fromApiJsonDeserializer;
	private TicketMasterCloseFromApiJsonDeserializer closeFromApiJsonDeserializer;
	private TicketMasterRepository ticketMasterRepository;


	@Autowired
	public TicketMasterWritePlatformServiceImpl(final PlatformSecurityContext context,
			final TicketMasterRepository repository,final TicketDetailsRepository ticketDetailsRepository, 
			final TicketMasterFromApiJsonDeserializer fromApiJsonDeserializer,final TicketMasterRepository ticketMasterRepository,
			final TicketMasterCloseFromApiJsonDeserializer closeFromApiJsonDeserializer) {
		this.context = context;
		this.repository = repository;
		this.ticketDetailsRepository=ticketDetailsRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.ticketMasterRepository = ticketMasterRepository;
		this.closeFromApiJsonDeserializer = closeFromApiJsonDeserializer;
	}

	private void handleDataIntegrityIssues(TicketMasterCommand command,
			DataIntegrityViolationException dve) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CommandProcessingResult upDateTicketDetails(
			TicketMasterCommand ticketMasterCommand,
			DocumentCommand documentCommand, Long ticketId,InputStream inputStream) {
		
	 	try {
		 String fileUploadLocation = FileUtils.generateFileParentDirectory(documentCommand.getParentEntityType(),
                 documentCommand.getParentEntityId());

         /** Recursively create the directory if it does not exist **/
         if (!new File(fileUploadLocation).isDirectory()) {
             new File(fileUploadLocation).mkdirs();
         }
         String fileLocation=null;
         if(documentCommand.getFileName()!=null){
          fileLocation = FileUtils.saveToFileSystem(inputStream, fileUploadLocation, documentCommand.getFileName());
         }
         Long createdbyId = context.authenticatedUser().getId();
         TicketDetail detail=new TicketDetail(ticketId,ticketMasterCommand.getComments(),fileLocation,ticketMasterCommand.getAssignedTo(),createdbyId);
         /*TicketMaster master = new TicketMaster(ticketMasterCommand.getStatusCode(), ticketMasterCommand.getAssignedTo());*/
         TicketMaster master= this.ticketMasterRepository.findOne(ticketId);
         master.updateTicket(ticketMasterCommand);
         this.ticketMasterRepository.save(master);
         this.ticketDetailsRepository.save(detail);
         return new CommandProcessingResult(detail.getId());

	 	}
catch (DataIntegrityViolationException dve) {
		handleDataIntegrityIssues(ticketMasterCommand, dve);
		return new CommandProcessingResult(Long.valueOf(-1));
	
		
		
		
		
		
	} catch (IOException e) {
         throw new DocumentManagementException(documentCommand.getName());
}
		

	
	
	}

	@Override
	public CommandProcessingResult closeTicket( final JsonCommand command) {
		try {
			this.context.authenticatedUser();

			this.closeFromApiJsonDeserializer.validateForClose(command.json());
			
			TicketMaster ticketMaster=this.repository.findOne(command.entityId());
			
			if (!ticketMaster.getStatus().equalsIgnoreCase("CLOSED")) {
				ticketMaster.closeTicket(command);
				this.repository.save(ticketMaster);
			} else {
				
			}
		}catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssuesforJson(command, dve);
		}
		return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(command.entityId()).build();
	}

	private void handleDataIntegrityIssuesforJson(JsonCommand command,
			DataIntegrityViolationException dve) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String retrieveTicketProblems(Long ticketId) {
		try {
		TicketMaster master=this.repository.findOne(ticketId);
		String description=master.getDescription();
		return description;
		
		}catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(null, dve);
			return "";
				}
	}

	@Transactional
	@Override
	public CommandProcessingResult createTicketMaster(JsonCommand command) {
		 try {
		this.context.authenticatedUser();
		this.fromApiJsonDeserializer.validateForCreate(command.json());
		final TicketMaster ticketMaster = TicketMaster.fromJson(command);
		Long created = context.authenticatedUser().getId();
		ticketMaster.setCreatedbyId(created);
		this.repository.save(ticketMaster);
		return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(ticketMaster.getId()).build();
	} catch (DataIntegrityViolationException dve) {
		/*handleDataIntegrityIssues(command, dve);*/
		return new CommandProcessingResult(Long.valueOf(-1));
	}
	}
}

