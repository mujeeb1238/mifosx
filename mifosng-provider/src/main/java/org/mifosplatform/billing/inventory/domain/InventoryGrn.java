package org.mifosplatform.billing.inventory.domain;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name="b_grn")
public class InventoryGrn extends AbstractAuditableCustom<AppUser, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3473332897093868940L;
	
	
	
	
	@Column(name="item_master_id")
	private Long itemMasterId;
		
	@Column(name="orderd_quantity")
	private Long orderdQuantity;
	
	@Column(name="purchase_date")
	private Date purchaseDate;
	
	@Column(name="received_quantity")
	private Long receivedQuantity;
	
	@Column(name="supplier_id")
	private Long supplierId;
	
	
	public InventoryGrn(Long itemMasterId,Long orderdQuantity,Date purchaseDate,Long receivedQuantity,Long supplierId){
		this.itemMasterId = itemMasterId;
		this.orderdQuantity = orderdQuantity;
		this.purchaseDate = purchaseDate;
		this.receivedQuantity = receivedQuantity;
		this.supplierId = supplierId;
		
		
	}
	
	
	


	


	public Long getItemMasterId() {
		return itemMasterId;
	}


	public void setItemMasterId(Long itemMasterId) {
		this.itemMasterId = itemMasterId;
	}


	public Long getOrderdQuantity() {
		return orderdQuantity;
	}


	public void setOrderdQuantity(Long orderdQuantity) {
		this.orderdQuantity = orderdQuantity;
	}


	public Date getPurchaseDate() {
		return purchaseDate;
	}


	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}


	public Long getReceivedQuantity() {
		return receivedQuantity;
	}


	public void setReceivedQuantity(Long receivedQuantity) {
		this.receivedQuantity = receivedQuantity;
	}


	public Long getSupplierId() {
		return supplierId;
	}


	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}


	public InventoryGrn(){}
	
}
