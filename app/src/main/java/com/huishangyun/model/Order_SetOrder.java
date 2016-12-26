package com.huishangyun.model;

/**
 * 创建订单和订单列表公用这个实体类
 * @author Administrator
 *
 */
public class Order_SetOrder {	
	private String Action;//修改定单的时候用
	
	private String OrderID;
	private Integer Status;//订单状态
	private String AddDateTime;//这三个获取列表的时候后台添加的
	
	private Integer Type;//订单类型
	private Integer Member_ID;//客户编号
	private String Member_Name;
	private Integer Manager_ID;//业务员编号
	private String Manager_Name;
	
	private String ReceiveName;//收货人姓名
	private String ReceiveTel;
	private String ReceiveMobile;
	private String ReceivePostCode;//收货人邮编
	private String ReceiveAddress;
	private String ReceiveDate;//要求配送日期
	private String Note;//备注
	private String Array_ID;	
	
	private String SendName;//送货人姓名
	private String SendMobile;
	
	private String Payment;//支付方式
	private float PaymentPrice;//支付金额
	private String Delivery;//配送方式
	private float DeliveryPrice;//配送费
	
	private boolean IsInvoice;//是否开票
	private String InvoiceHead;//发票抬头
	private String InvoicePrice;//开票金额
	private String InvoiceType;//发票类型
	private float SafePrice;//保险费用
	private float Price;//订单金额
	private float PriceModify;//订单修改后金额
	//xsl修改【start】
	private Integer OperationID;//操作人id
	private String OperationName;//登入人 

	
	public String getManager_Name() {
		return Manager_Name;
	}
	public void setManager_Name(String manager_Name) {
		Manager_Name = manager_Name;
	}
	public Integer getOperationID() {
		return OperationID;
	}
	public void setOperationID(Integer operationID) {
		OperationID = operationID;
	}
	public String getOperationName() {
		return OperationName;
	}
	public void setOperationName(String operationName) {
		OperationName = operationName;
	}
	//xsl修改【end】
	
	public String getAction() {
		return Action;
	}
	public void setAction(String action) {
		Action = action;
	}
	
	public String getAddDateTime() {
		return AddDateTime;
	}
	public void setAddDateTime(String addDateTime) {
		AddDateTime = addDateTime;
	}
	public String getOrderID() {
		return OrderID;
	}
	public void setOrderID(String orderID) {
		OrderID = orderID;
	}
	public Integer getStatus() {
		return Status;
	}
	public void setStatus(Integer status) {
		Status = status;
	}
	public String getArray_ID() {
		return Array_ID;
	}
	public void setArray_ID(String array_ID) {
		Array_ID = array_ID;
	}
	
	public Integer getType() {
		return Type;
	}
	public void setType(Integer type) {
		Type = type;
	}
	public Integer getMember_ID() {
		return Member_ID;
	}
	public void setMember_ID(Integer member_ID) {
		Member_ID = member_ID;
	}
	public String getMember_Name() {
		return Member_Name;
	}
	public void setMember_Name(String member_Name) {
		Member_Name = member_Name;
	}
	public String getReceiveName() {
		return ReceiveName;
	}
	public void setReceiveName(String receiveName) {
		ReceiveName = receiveName;
	}
	public String getReceiveTel() {
		return ReceiveTel;
	}
	public void setReceiveTel(String receiveTel) {
		ReceiveTel = receiveTel;
	}
	public String getReceiveMobile() {
		return ReceiveMobile;
	}
	public void setReceiveMobile(String receiveMobile) {
		ReceiveMobile = receiveMobile;
	}
	public String getReceivePostCode() {
		return ReceivePostCode;
	}
	public void setReceivePostCode(String receivePostCode) {
		ReceivePostCode = receivePostCode;
	}
	public String getReceiveAddress() {
		return ReceiveAddress;
	}
	public void setReceiveAddress(String receiveAddress) {
		ReceiveAddress = receiveAddress;
	}
	public String getReceiveDate() {
		return ReceiveDate;
	}
	public void setReceiveDate(String receiveDate) {
		ReceiveDate = receiveDate;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	public String getSendName() {
		return SendName;
	}
	public void setSendName(String sendName) {
		SendName = sendName;
	}
	public String getSendMobile() {
		return SendMobile;
	}
	public void setSendMobile(String sendMobile) {
		SendMobile = sendMobile;
	}
	public String getPayment() {
		return Payment;
	}
	public void setPayment(String payment) {
		Payment = payment;
	}
	public float getPaymentPrice() {
		return PaymentPrice;
	}
	public void setPaymentPrice(float paymentPrice) {
		PaymentPrice = paymentPrice;
	}
	public String getDelivery() {
		return Delivery;
	}
	public void setDelivery(String delivery) {
		Delivery = delivery;
	}
	public float getDeliveryPrice() {
		return DeliveryPrice;
	}
	public void setDeliveryPrice(float deliveryPrice) {
		DeliveryPrice = deliveryPrice;
	}
	public boolean isIsInvoice() {
		return IsInvoice;
	}
	public void setIsInvoice(boolean isInvoice) {
		IsInvoice = isInvoice;
	}
	public String getInvoiceHead() {
		return InvoiceHead;
	}
	public void setInvoiceHead(String invoiceHead) {
		InvoiceHead = invoiceHead;
	}
	public String getInvoicePrice() {
		return InvoicePrice;
	}
	public void setInvoicePrice(String invoicePrice) {
		InvoicePrice = invoicePrice;
	}
	public String getInvoiceType() {
		return InvoiceType;
	}
	public void setInvoiceType(String invoiceType) {
		InvoiceType = invoiceType;
	}
	public float getSafePrice() {
		return SafePrice;
	}
	public void setSafePrice(float safePrice) {
		SafePrice = safePrice;
	}
	public float getPrice() {
		return Price;
	}
	public void setPrice(float price) {
		Price = price;
	}
	public float getPriceModify() {
		return PriceModify;
	}
	public void setPriceModify(float priceModify) {
		PriceModify = priceModify;
	}
	public Integer getManager_ID() {
		return Manager_ID;
	}
	public void setManager_ID(Integer manager_ID) {
		Manager_ID = manager_ID;
	}
	
}
