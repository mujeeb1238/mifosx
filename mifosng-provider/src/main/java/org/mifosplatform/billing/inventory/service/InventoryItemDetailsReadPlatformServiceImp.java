package org.mifosplatform.billing.inventory.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.mifosplatform.billing.inventory.data.InventoryItemDetailsData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class InventoryItemDetailsReadPlatformServiceImp implements InventoryItemDetailsReadPlatformService {

	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	
	@Autowired
	InventoryItemDetailsReadPlatformServiceImp(final TenantAwareRoutingDataSource dataSource,final PlatformSecurityContext context){
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}
	
	private class ItemDetailsMapper implements RowMapper<InventoryItemDetailsData>{

		@Override
		public InventoryItemDetailsData mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long id = rs.getLong("id");
			Long itemMasterId = rs.getLong("itemMasterId");
			String serialNumber = rs.getString("serialNumber");
			Long grnId = rs.getLong("grnId");
			String provisioningSerialNumber = rs.getString("provisioningSerialNumber");
			String quality= rs.getString("quality");
			String status = rs.getString("status");
			Long warranty = rs.getLong("warranty");
			String remarks = rs.getString("remarks");
	
			return new InventoryItemDetailsData(id,itemMasterId,serialNumber,grnId,provisioningSerialNumber,quality,status,warranty,remarks);
		}
		public String schema(){
			String sql = "id as id,item_master_id as itemMasterId,serial_no as serialNumber,grn_id as grnId,provisioning_serialno as provisioningSerialNumber,quality as quality,status as status, warranty as warranty,remarks as remarks from b_item_detail";
			return sql;
		}
		
	}



	@Override
	public Collection<InventoryItemDetailsData> retriveAllItemDetails() {
		// TODO Auto-generated method stub
		context.authenticatedUser();
		ItemDetailsMapper itemDetails = new ItemDetailsMapper();
		String sql = "select "+itemDetails.schema();
		return this.jdbcTemplate.query(sql, itemDetails, new Object[] {});
	}


	@Override
	public InventoryItemDetailsData retriveIndividualItemDetails() {
		// TODO Auto-generated method stub
		context.authenticatedUser();
		return null;
	}

}
