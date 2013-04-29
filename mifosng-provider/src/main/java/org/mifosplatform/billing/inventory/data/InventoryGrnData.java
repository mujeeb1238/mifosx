package org.mifosplatform.billing.inventory.data;

import java.util.Date;

import org.joda.time.LocalDate;

public class InventoryGrnData {


	
	
	
	private Long id;
	private LocalDate purchaseDate;
	private Long supplierId;
	private Long itemMasterId;
	private Long orderdQuantity;
	private Long receivedQuantity;
	private Long createdById;
	private Date createdDate;
	private Date lastModifiedDate;
	private Long lastModifiedById;
	
	
	
	
	
	
	public InventoryGrnData(){
		this.id=null;
		this.itemMasterId=null;
		this.orderdQuantity=null;
		this.purchaseDate= new LocalDate();
		this.receivedQuantity=null;
		this.supplierId=null;
		
	}
	
	public InventoryGrnData(Long id,LocalDate purchaseDate,Long supplierId,Long itemMasterId,Long orderedQuantity,Long receivedQuantity){
		this.id=id;
		this.itemMasterId=itemMasterId;
		this.orderdQuantity=orderedQuantity;
		this.purchaseDate=purchaseDate;
		this.receivedQuantity=receivedQuantity;
		this.supplierId=supplierId;
		
		
	}
	
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCreatedById() {
		return createdById;
	}

	public void setCreatedById(Long createdById) {
		this.createdById = createdById;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public Long getLastModifiedById() {
		return lastModifiedById;
	}

	public void setLastModifiedById(Long lastModifiedById) {
		this.lastModifiedById = lastModifiedById;
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

	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(LocalDate purchaseDate) {
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

	
	
}
