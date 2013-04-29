package org.mifosplatform.billing.uploadstatus.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.lang.reflect.Type;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.mifosplatform.billing.inventory.command.ItemDetailsCommand;
import org.mifosplatform.billing.inventory.service.InventoryItemDetailsWritePlatformService;
import org.mifosplatform.billing.uploadstatus.command.UploadStatusCommand;
import org.mifosplatform.billing.uploadstatus.domain.UploadStatus;
import org.mifosplatform.billing.uploadstatus.domain.UploadStatusCommandValidator;
import org.mifosplatform.billing.uploadstatus.domain.UploadStatusRepository;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.exception.UnsupportedParameterException;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


@Service
public class UploadStatusWritePlatformServiceImp implements UploadStatusWritePlatformService{

	private PlatformSecurityContext context;
	private UploadStatusRepository uploadStatusRepository;
	private final Gson gsonConverter;
	
	@Autowired
	private InventoryItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService;
	
	@Autowired
	public UploadStatusWritePlatformServiceImp(final PlatformSecurityContext context,UploadStatusRepository uploadStatusRepository) {
		this.context=context;
		this.uploadStatusRepository=uploadStatusRepository;
		this.gsonConverter=new Gson();;
	}
	
	
	@Transactional
	@Override
	public CommandProcessingResult updateUploadStatus(Long orderId) {
		Long processRecords=(long) 0;
		String processStatus=null;
		String errormessage=null;
		try {

			UploadStatus uploadStatus = this.uploadStatusRepository.findOne(orderId);
			
			LocalDate currentDate = new LocalDate();
			currentDate.toDate();
     
			String filePath=uploadStatus.getUploadFilePath();
			
			try {
				
				int i;
				InputStream excelFileToRead = new FileInputStream(filePath);

				XSSFWorkbook wb = new XSSFWorkbook(excelFileToRead);

				XSSFSheet sheet = wb.getSheetAt(0);
				XSSFRow row;
				XSSFCell cell;
				String serialno = "0";
				int countno = Integer.parseInt(serialno);
				if (countno == 0) {
					countno = countno + 2;
				} else if (countno == 1) {
					countno = countno + 1;
				}
				System.out.println("Excel Row No is: " + countno);
				
				Iterator rows = sheet.rowIterator();
				Vector<XSSFCell> v = new Vector<XSSFCell>();
				if (countno > 0) {
					countno = countno - 1;
				}
				while (rows.hasNext()) {

					row = (XSSFRow) rows.next();
					i = row.getRowNum();
					if (i > 0) {
						if (i >= countno) {
							
							Iterator cells = row.cellIterator();
							while (cells.hasNext()) {

								cell = (XSSFCell) cells.next();

								if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
									v.add(cell);
								} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
									v.add(cell);
								} else {
									v.add(cell);
								}

							}
						
							
							
							
							ItemDetailsCommand itemDetailsCommand=new ItemDetailsCommand();
							
							System.out.println(v.elementAt(0).toString());
							if(v.elementAt(0).toString().equalsIgnoreCase("EOF"))
							{
								break;
							}
							else{
							itemDetailsCommand.setItemMasterId(new Double(v.elementAt(0).toString()).longValue());
							
							itemDetailsCommand.setSerialNumber(v.elementAt(1).toString());
							itemDetailsCommand.setGrnId(new Double(v.elementAt(2).toString()).longValue());
							itemDetailsCommand.setProvisioningSerialNumber( v.elementAt(3).toString());
							itemDetailsCommand.setQuality( v.elementAt(4).toString());
							itemDetailsCommand.setRemark(v.elementAt(9).toString());
							itemDetailsCommand.setStatus(v.elementAt(5).toString());
							itemDetailsCommand.setOfficeId(new Double(v.elementAt(6).toString()).longValue());
							itemDetailsCommand.setClientId(new Double(v.elementAt(7).toString()).longValue());
							itemDetailsCommand.setWarranty(new Double(v.elementAt(8).toString()).longValue());
							//ItemDetailsCommandList.add(itemDetailsCommand);
							
							JSONObject json = new JSONObject();
					        
							
							CommandProcessingResult id = this.inventoryItemDetailsWritePlatformService.addMultipleItems(itemDetailsCommand);	
							++processRecords;
	                     processStatus="Processed";
							}
	                        v.removeAllElements();
						}
					}
				
				}

			} catch (Exception e) {
				
				processStatus="New Unprocessed";
				errormessage=e.getMessage();
			}
			
			
			
			// if (order==null || order.getStatus() == 3) {
			// throw new ProductNotFoundException(order.getId());
			// }
			
			uploadStatus.update(currentDate,processStatus,processRecords,errormessage);
			
			this.uploadStatusRepository.save(uploadStatus);
			return new CommandProcessingResult(Long.valueOf(uploadStatus.getId()));
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}
	
	
	
	@Transactional
	@Override
	public CommandProcessingResult addItem(UploadStatusCommand command) {
		UploadStatus uploadStatus;
	
			
			this.context.authenticatedUser();
			UploadStatusCommandValidator validator = new UploadStatusCommandValidator(command);
		                   validator.validateForCreate();
		              
			//List<UploadStatus> availService= this.uploadStatusRepository.findAll();
			 try{
		        	String fileLocation=null;
						fileLocation = FileUtils.saveToFileSystem(command.getInputStream(), command.getFileUploadLocation(),command.getFileName());
		        	
		        	//public UploadStatusCommand(String uploadProcess,String uploadFilePath,LocalDate processDate, String processStatus,Long processRecords,String errorMessage,Set<String> modifiedParameters)
		        	//	UploadStatusCommand uploadStatusCommand=new UploadStatusCommand(name,fileLocation,localdate,"",null,null,null,description,fileName,inputStream,fileUploadLocation);
		        		//  CommandProcessingResult id = this.uploadStatusWritePlatformService.addItem(uploadStatusCommand);
		        			
		        
		        
			
			/*for(UploadStatus uploadstatus:availService){
				String serialNumberFromItemDetails = uploadstatus.getSerialNumber();
				String serialNumberFromItemCommand = command.getSerialNumber();
				if(serialNumberFromItemDetails.equalsIgnoreCase(serialNumberFromItemCommand)){
					throw new ItemDetailsExist(command.getSerialNumber());
				}
			}*/
			
			//create(String uploadProcess,String uploadFilePath,Date processDate,String processStatus,Long processRecords,String errorMessage,char isDeleted){
			
			
			uploadStatus = UploadStatus.create(command.getUploadProcess(), fileLocation, command.getProcessDate(),command.getProcessStatus(),command.getProcessRecords(), command.getErrorMessage(),command.getDescription());
			
			 this.uploadStatusRepository.save(uploadStatus);
			 return new CommandProcessingResult(uploadStatus.getId());
			 
		} catch (DataIntegrityViolationException dve) {
			return new CommandProcessingResult(Long.valueOf(-1));
		}catch (IOException e) {
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
		
		
	}

	private void handleDataIntegrityIssues(DataIntegrityViolationException dve) {
		// TODO Auto-generated method stub

	}
	@Transactional
	@Override
	 public UploadStatusCommand convertJsonToUploadStatusCommand(Object object,String jsonRequestBody) {
	       
	        if(StringUtils.isBlank(jsonRequestBody)){
	            throw new InvalidJsonException();
	        }
	     
	       
	        Type typeOfMap = new TypeToken<Map<String,String>>(){}.getType();
	        Map<String,String> requestMap = gsonConverter.fromJson(jsonRequestBody, typeOfMap);
	        Set<String> supportedParams = new HashSet<String>(Arrays.asList("locale","dateFormat","uploadProcess","uploadFilePath","processDate","processStatus","processRecords","errorMessage","isDeleted"));
	        checkForUnsupportedParameters(requestMap, supportedParams);
	        Set<String> modifiedParameters = new HashSet<String>();
	       
	       String uploadProcess = extractStringParameter("uploadProcess", requestMap, modifiedParameters);
	        String uploadFilePath = extractStringParameter("uploadFilePath", requestMap, modifiedParameters);
	        LocalDate processDate = extractLocalDateParameter("processDate", requestMap, modifiedParameters);
	        String processStatus = extractStringParameter("processStatus", requestMap, modifiedParameters);
	        Long processRecords = extractLongParameter("processRecords", requestMap, modifiedParameters);
	        String errorMessage = extractStringParameter("errorMessage", requestMap, modifiedParameters);
	        String description = extractStringParameter("description", requestMap, modifiedParameters);
	        
	       // UploadStatusCommand(String uploadProcess,String uploadFilePath,Date processDate, String processStatus,Long processRecords,String errorMessage,char isDeleted,Set<String> modifiedParameters)
	        return new UploadStatusCommand(uploadProcess,uploadFilePath,processDate,processStatus,processRecords,errorMessage,modifiedParameters,description,null,null,null);
	    }
	private String extractStringParameter(final String paramName,
			final Map<String, ?> requestMap,
			final Set<String> modifiedParameters) {
		String paramValue = null;
		if (requestMap.containsKey(paramName)) {
			paramValue = (String) requestMap.get(paramName);
			modifiedParameters.add(paramName);
		}

		if (paramValue != null) {
			paramValue = paramValue.trim();
		}

		return paramValue;
	}
	private Long extractLongParameter(final String paramName,
			final Map<String, ?> requestMap,
			final Set<String> modifiedParameters) {
		Long paramValue = null;
		if (requestMap.containsKey(paramName)) {
			String valueAsString = (String) requestMap.get(paramName);
			if (StringUtils.isNotBlank(valueAsString)) {
				paramValue = Long.valueOf(Double.valueOf(valueAsString)
						.longValue());
			}
			modifiedParameters.add(paramName);
		}
		return paramValue;
	}
	private LocalDate extractLocalDateParameter(final String paramName,
			final Map<String, ?> requestMap,
			final Set<String> modifiedParameters) {
		LocalDate paramValue = null;
		if (requestMap.containsKey(paramName)) {
			String valueAsString = (String) requestMap.get(paramName);
			if (StringUtils.isNotBlank(valueAsString)) {
				final String dateFormat = (String) requestMap.get("dateFormat");
				final Locale locale = new Locale(
						(String) requestMap.get("locale"));
				paramValue = convertFrom(valueAsString, paramName, dateFormat,
						locale);
			}
			modifiedParameters.add(paramName);
		}
		return paramValue;
	}
	
		
	private LocalDate convertFrom(final String dateAsString,
			final String parameterName, final String dateFormat,
			final Locale clientApplicationLocale) {

		if (StringUtils.isBlank(dateFormat) || clientApplicationLocale == null) {

			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			if (StringUtils.isBlank(dateFormat)) {
				String defaultMessage = new StringBuilder(
						"The parameter '"
								+ parameterName
								+ "' requires a 'dateFormat' parameter to be passed with it.")
						.toString();
				ApiParameterError error = ApiParameterError.parameterError(
						"validation.msg.missing.dateFormat.parameter",
						defaultMessage, parameterName);
				dataValidationErrors.add(error);
			}
			if (clientApplicationLocale == null) {
				String defaultMessage = new StringBuilder(
						"The parameter '"
								+ parameterName
								+ "' requires a 'locale' parameter to be passed with it.")
						.toString();
				ApiParameterError error = ApiParameterError.parameterError(
						"validation.msg.missing.locale.parameter",
						defaultMessage, parameterName);
				dataValidationErrors.add(error);
			}
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}

		LocalDate eventLocalDate = null;
		if (StringUtils.isNotBlank(dateAsString)) {
			try {
				// Locale locale = LocaleContextHolder.getLocale();
				eventLocalDate = DateTimeFormat
						.forPattern(dateFormat)
						.withLocale(clientApplicationLocale)
						.parseLocalDate(
								dateAsString
										.toLowerCase(clientApplicationLocale));
			} catch (IllegalArgumentException e) {
				List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
				ApiParameterError error = ApiParameterError.parameterError(
						"validation.msg.invalid.date.format", "The parameter "
								+ parameterName
								+ " is invalid based on the dateFormat: '"
								+ dateFormat + "' and locale: '"
								+ clientApplicationLocale + "' provided:",
						parameterName, dateAsString, dateFormat);
				dataValidationErrors.add(error);

				throw new PlatformApiDataValidationException(
						"validation.msg.validation.errors.exist",
						"Validation errors exist.", dataValidationErrors);
			}
		}

		return eventLocalDate;
	}
	private void checkForUnsupportedParameters(Map<String, ?> requestMap,
			Set<String> supportedParams) {
		List<String> unsupportedParameterList = new ArrayList<String>();
		for (String providedParameter : requestMap.keySet()) {
			if (!supportedParams.contains(providedParameter)) {
				unsupportedParameterList.add(providedParameter);
			}
		}

		if (!unsupportedParameterList.isEmpty()) {
			throw new UnsupportedParameterException(unsupportedParameterList);
		}
	}
}

