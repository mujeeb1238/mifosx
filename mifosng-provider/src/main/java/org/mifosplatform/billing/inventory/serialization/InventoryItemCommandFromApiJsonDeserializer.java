/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.billing.inventory.serialization;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Deserializer for code JSON to validate API request.
 */
@Component
public final class InventoryItemCommandFromApiJsonDeserializer {

    /**
     * The parameters supported for this command.
     */
	private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("itemMasterId", "serialNumber", "grnId","provisioningSerialNumber", "quality", "status","warranty", "remarks","locale"));
    
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public InventoryItemCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("item");

        final JsonElement element = fromApiJsonHelper.parse(json);

        final Integer item = fromApiJsonHelper.extractIntegerNamed("itemMasterId", element, fromApiJsonHelper.extractLocaleParameter(element.getAsJsonObject()));
        Long itemMasterId = null;
        if(item!=null){
        	itemMasterId = item.longValue();
        }
        		
        final Integer w = fromApiJsonHelper.extractIntegerNamed("warranty", element, fromApiJsonHelper.extractLocaleParameter(element.getAsJsonObject())); 
        Long warranty = null;
        if(w!=null){
        warranty = w.longValue();
        }
        
        final Integer g = fromApiJsonHelper.extractIntegerNamed("grnId", element, fromApiJsonHelper.extractLocaleParameter(element.getAsJsonObject()));
        Long grnId = null;
        if(g!=null){
        grnId = w.longValue();
        }
        
        final String remarks = fromApiJsonHelper.extractStringNamed("remarks", element);
        final String serialNumber = fromApiJsonHelper.extractStringNamed("serialNumber", element);
        final String provisioningSerialNumber  = fromApiJsonHelper.extractStringNamed("provisioningSerialNumber",element);
        final String status  = fromApiJsonHelper.extractStringNamed("status", element);
        
        
        final String quality = fromApiJsonHelper.extractStringNamed("quality", element);
        
		baseDataValidator.reset().parameter("itemMasterId").value(itemMasterId).notNull();
		baseDataValidator.reset().parameter("remarks").value(remarks).notNull().notBlank();
		baseDataValidator.reset().parameter("serialNumber").value(serialNumber).notBlank().notNull();
		baseDataValidator.reset().parameter("provisioningSerialNumber").value(provisioningSerialNumber).notBlank().notNull();
		baseDataValidator.reset().parameter("status").value(status).notNull();
		baseDataValidator.reset().parameter("quality").value(quality).notNull();
		baseDataValidator.reset().parameter("grnId").value(grnId).notBlank();
		baseDataValidator.reset().parameter("warranty").value(warranty).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("code");

        final JsonElement element = fromApiJsonHelper.parse(json);
        if (fromApiJsonHelper.parameterExists("name", element)) {
            final String name = fromApiJsonHelper.extractStringNamed("name", element);
            baseDataValidator.reset().parameter("name").value(name).notBlank().notExceedingLengthOf(100);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }
}