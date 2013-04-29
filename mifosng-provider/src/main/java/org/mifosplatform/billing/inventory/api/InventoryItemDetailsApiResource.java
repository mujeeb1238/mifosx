package org.mifosplatform.billing.inventory.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.inventory.data.InventoryGrnData;
import org.mifosplatform.billing.inventory.data.InventoryItemDetailsData;
import org.mifosplatform.billing.inventory.exception.InventoryItemDetailsExist;
import org.mifosplatform.billing.inventory.service.InventoryGrnReadPlatformService;
import org.mifosplatform.billing.inventory.service.InventoryItemDetailsReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/itemdetails")
@Component
@Scope("singleton")
public class InventoryItemDetailsApiResource {
	
	private final Set<String> RESPONSE_DATA_ITEM_DETAILS_PARAMETERS = new HashSet<String>(Arrays.asList("id", "itemMasterId", "serialNumber", "grnId","provisioningSerialNumber", "quality", "status","warranty", "remarks"));
	private final Set<String> RESPONSE_DATA_GRN_DETAILS_PARAMETERS = new HashSet<String>(Arrays.asList("id", "purchaseDate", "supplierId", "itemMasterId","orderdQuantity", "receivedQuantity"));
    private final String resourceNameForPermissions = "INVENTORY";
	
	private final PlatformSecurityContext context;
	private final DefaultToApiJsonSerializer<InventoryItemDetailsData> toApiJsonSerializerForItem;
	private final DefaultToApiJsonSerializer<InventoryGrnData> toApiJsonSerializerForGrn;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final InventoryGrnReadPlatformService inventoryGrnReadPlatformService;

	
/*	@Autowired
	private InventoryItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService;

	@Autowired
	private PortfolioApiDataBillingConversionService apiDataConversionService;

	@Autowired
	private PortfolioApiJsonBillingSerializerService apiJsonSerializerService;
*/
	private final InventoryItemDetailsReadPlatformService itemDetailsReadPlatformService;

	@Autowired
	public InventoryItemDetailsApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<InventoryItemDetailsData> toApiJsonSerializerForItem,ApiRequestParameterHelper apiRequestParameterHelper,PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,final InventoryGrnReadPlatformService inventoryGrnReadPlatformService,final DefaultToApiJsonSerializer<InventoryGrnData> toApiJsonSerializerForGrn,InventoryItemDetailsReadPlatformService itemDetailsReadPlatformService) {
		this.context=context;
	    this.toApiJsonSerializerForItem = toApiJsonSerializerForItem;
	    this.toApiJsonSerializerForGrn = toApiJsonSerializerForGrn;
	    this.apiRequestParameterHelper = apiRequestParameterHelper;
	    this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
	    this.inventoryGrnReadPlatformService = inventoryGrnReadPlatformService;
	    this.itemDetailsReadPlatformService = itemDetailsReadPlatformService;
	}

	/*
	 * for storing item details into item_detail table
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String addItemDetails(final String jsonRequestBody) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createInventoryItem().withJson(jsonRequestBody).build();
		 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	     return this.toApiJsonSerializerForItem.serialize(result);
		
		
		/*final InventoryItemDetailsCommand command = apiDataConversionService.convertJsonToItemDetailsCommand(null, jsonRequestBody);
		CommandProcessingResult id = this.inventoryItemDetailsWritePlatformService.addItem(command);
		return Response.ok().entity(id.getGroupId()).build();*/
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retriveItemDetails(@Context final UriInfo uriInfo) {
		
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final Collection<InventoryItemDetailsData> itemdetails = this.itemDetailsReadPlatformService.retriveAllItemDetails();
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializerForItem.serialize(settings, itemdetails, RESPONSE_DATA_ITEM_DETAILS_PARAMETERS);
		
	/*	Set<String> typicalResponseParameters = new HashSet<String>(Arrays.asList("id", "itemMasterId", "serialNumber", "grnId","provisioningSerialNumber", "quality", "status","warranty", "remarks"));

		Set<String> responseParameters = ApiParameterHelper
				.extractFieldsForResponseIfProvided(uriInfo
						.getQueryParameters());

		if (responseParameters.isEmpty()) {
			responseParameters.addAll(typicalResponseParameters);
		}

		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo
				.getQueryParameters());

		List<InventoryItemDetailsData> inventoryItemDetailsData = this.itemDetailsReadPlatformService
				.retriveAllItemDetails();

		return this.apiJsonSerializerService.serializeItemDetailsDataToJson(
				prettyPrint, responseParameters, inventoryItemDetailsData);*/

	}

	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String grnTemplate(@QueryParam("grnId") final Long grnId,@Context final UriInfo uriInfo) {
		
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		
		InventoryGrnData inventoryGrnData = null;
		boolean val = false;
		if (grnId != null)
			val = this.inventoryGrnReadPlatformService.validateForExist(grnId);
		if (val) {
			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("Grn Details");
			baseDataValidator.reset().parameter("id").value(grnId).notBlank().notNull();
				throw new InventoryItemDetailsExist("No Such GrnId","No Such GrnId",""+grnId,""+grnId);//throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist","Validation errors exist.", dataValidationErrors);
		}
		if (grnId == null) {
			inventoryGrnData = new InventoryGrnData();
			return this.toApiJsonSerializerForGrn.serialize(inventoryGrnData);

		}
		
		inventoryGrnData = this.inventoryGrnReadPlatformService.retriveGrnDetailTemplate(grnId);
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializerForGrn.serialize(settings, inventoryGrnData, RESPONSE_DATA_GRN_DETAILS_PARAMETERS);
		
		/*Set<String> typicalResponseParameters = new HashSet<String>(Arrays.asList("id", "purchaseDate", "supplierId",
						"itemMasterId", "orderdQuantity", "receivedQuantity"));
		Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		if (responseParameters.isEmpty())
			responseParameters.addAll(typicalResponseParameters);

		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());

		InventoryGrnData inventoryGrnData = null;
		boolean val = false;
		if (grnId != null)
			val = this.inventoryGrnReadPlatformService.validateForExist(grnId);
		if (val) {
			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("Grn Details");
			baseDataValidator.reset().parameter("id").value(grnId).notBlank().notNull();
			throw new InventoryItemDetailsExist("No Such GrnId","No Such GrnId",""+grnId,""+grnId);//throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist","Validation errors exist.", dataValidationErrors);
		}
		if (grnId == null) {
			inventoryGrnData = new InventoryGrnData();
			return this.apiJsonSerializerService.serializeGrnDataToJson(
					prettyPrint, responseParameters, inventoryGrnData);

		}

		inventoryGrnData = this.inventoryGrnReadPlatformService
				.retriveGrnDetailTemplate(grnId);
		return this.apiJsonSerializerService.serializeGrnDataToJson(
				prettyPrint, responseParameters, inventoryGrnData);
		*/
	}

	@GET
	@Path("grn")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retriveGrnDetails(@Context final UriInfo uriInfo) {

	/*	Set<String> typicalResponseParameters = new HashSet<String>(
				Arrays.asList("id", "purchaseDate", "supplierId",
						"itemMasterId", "orderdQuantity", "receivedQuantity"));

		Set<String> responseParameters = ApiParameterHelper
				.extractFieldsForResponseIfProvided(uriInfo
						.getQueryParameters());
		if (responseParameters.isEmpty()) {
			responseParameters.addAll(typicalResponseParameters);
		}
		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo
				.getQueryParameters());*/
		
		Collection<InventoryGrnData> inventoryGrnData = this.inventoryGrnReadPlatformService.retriveGrnDetails();
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializerForGrn.serialize(settings, inventoryGrnData, RESPONSE_DATA_GRN_DETAILS_PARAMETERS);
		/*return this.apiJsonSerializerService.serializeGrnDataToJson(
				prettyPrint, responseParameters, inventoryGrnData);*/
	}

}
