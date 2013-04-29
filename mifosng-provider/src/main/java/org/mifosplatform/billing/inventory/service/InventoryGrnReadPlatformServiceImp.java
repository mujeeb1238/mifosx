package org.mifosplatform.billing.inventory.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.inventory.data.InventoryGrnData;
import org.mifosplatform.billing.inventory.domain.InventoryGrnRepository;
import org.mifosplatform.billing.inventory.exception.InventoryItemDetailsExist;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class InventoryGrnReadPlatformServiceImp implements InventoryGrnReadPlatformService{

	
	private final JdbcTemplate jdbcTemplate;
	private final InventoryGrnRepository inventoryGrnRepository;
	
	@Autowired
	public InventoryGrnReadPlatformServiceImp(final TenantAwareRoutingDataSource dataSource,InventoryGrnRepository inventoryGrnRepository){
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.inventoryGrnRepository = inventoryGrnRepository;
		
	}
	
	
	

	@Override
	public Collection<InventoryGrnData> retriveGrnDetails() {

		GrnMapper grn = new GrnMapper();
		String sql = "select "+grn.schema()+" from b_grn";
		return jdbcTemplate.query(sql,grn,new Object[]{});
	}

	
	
	private class GrnMapper implements RowMapper<InventoryGrnData>{

		@Override
		public InventoryGrnData mapRow(ResultSet rs, int rowNum)
			throws SQLException {
			
			Long id = rs.getLong("id");
			LocalDate purchaseDate =JdbcSupport.getLocalDate(rs,"purchaseDate");
			Long supplierId = rs.getLong("supplierId");
			Long itemMasterId = rs.getLong("itemMasterId");
			Long orderedQuantity = rs.getLong("orderdQuantity");
			Long receivedQuantity = rs.getLong("receivedQuantity");
			return new InventoryGrnData(id,purchaseDate,supplierId,itemMasterId,orderedQuantity,receivedQuantity);
			
		}
		
		public String schema(){
			String sql = " id as id,purchase_date as purchaseDate,supplier_id as supplierId,item_master_id as itemMasterId,orderd_quantity as orderdQuantity,received_quantity as receivedQuantity";
			return sql;
		}
		
	}

	public boolean validateForExist(final Long grnId){
		
		boolean exist = inventoryGrnRepository.exists(grnId);
		if(!exist){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public InventoryGrnData retriveGrnDetailTemplate(final Long grnId){
		GrnMapper grn = null;
		String sql = null;
		try{
		grn = new GrnMapper();
		sql = "select "+grn.schema()+" from b_grn where id = ?";
		}catch(Exception e){
			 throw new InventoryItemDetailsExist("Item is already existing with item SerialNumber:","Item is already existing with item SerialNumber:",""+grnId,""+grnId);
		}
		return jdbcTemplate.queryForObject(sql,grn,new Object[]{grnId});
	}
	
	
	
	
	
}
