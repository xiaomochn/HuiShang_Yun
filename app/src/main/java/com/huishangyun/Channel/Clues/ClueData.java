package com.huishangyun.Channel.Clues;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;
import android.util.Log;

public class ClueData {
	private static final String TAG = "clueData.java";
	private static final boolean isDebug = true;
	
	// WebService 的命名空间
	// private static String namespace = "http://tempuri.org/";
	private static String namespace = "http://www.yiqiyun.com.cn/";
	// 服务器发布的 url
	/**
	
	/**
	 * 内网
	 */
	private static String url = "http://192.168.0.133:87/MicroWeb/MicroWeb.asmx";
	
	//SoapHeaders身份验证的方法名及用户名和密码
	private static String soapheadername = "MySoapHeader";
	private static String uesrid = "admin";
	private static String password = "admin";
	
    /**
     * 线索列表
     * @param Company_ID 公司ID
     * @param strWhere 查询条件
     * @param Top 查询几条（0则查询全部）
     * @param Sort_Field 排序字段
     * @return 
     */
	public static String getClueListInfo( int Company_ID,
			String strWhere, int Top, String Sort_Field) {		
		final String methodName = "ClueList"; // 函数名
		// 创建 HttpTransportSE 对象 , 通过 HttpTransportSE 类的构造方法可以指定 WebService 的
		// url
		HttpTransportSE transport = new HttpTransportSE(url);
		transport.debug = true;
		// 指定 WebService 的命名空间和函数名
		SoapObject soapObject = new SoapObject(namespace, methodName);
		// 设置调用方法参数的值
		soapObject.addProperty("Company_ID", Company_ID);
		soapObject.addProperty("strWhere", strWhere);
		soapObject.addProperty("Top", Top);
		soapObject.addProperty("Sort_Field", Sort_Field);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		//envelope.headerOut = getSoapHeader();
		envelope.bodyOut = soapObject;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(soapObject);
		// 使用 call 方法调用 WebService 方法 ,第一个参数一般为空
		try {
			transport.call(namespace + methodName, envelope);
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println(e);
		} catch (XmlPullParserException e) {
			// e.printStackTrace();
			System.out.println(e);
		}
		// 获取服务器响应返回的SOAP消息
		SoapObject result = null;
		result = (SoapObject) envelope.bodyIn;
		if (result == null) {
			return null;
		}
		int i = result.getPropertyCount();
		String str = result.getProperty(i - 1).toString();
//		Log.e(TAG,"线索列表str:" + str);
		return str;
	}

	/**
	 * 新增线索
	 * @param ClueID  线索ID（新增则传0）
	 * @param Company_ID 公司id
	 * @param Source 业务来源类型（1：手机客户端；2：网站后台）
	 * @param Title 标题
	 * @param Company 公司名称
	 * @param Name 姓名
	 * @param Tel  固定电话
	 * @param Mobile 手机号码
	 * @param QQ  即时通讯软件账号
	 * @param Email Email
	 * @param Address 地址
	 * @param Note 内容 
	 * @param Manager_ID 提交人编号
	 * @param Manager_Name 提交人姓名
	 * @return
	 */
	public static String getEditClueInfo( int ClueID, int Company_ID,
			String Source, String Title, String Company, String Name, 
			String Tel, String Mobile,String QQ, String Email, String Address,
			String Note, int Manager_ID, String Manager_Name) {		
		final String methodName = "EditClue"; // 函数名
		// 创建 HttpTransportSE 对象 , 通过 HttpTransportSE 类的构造方法可以指定 WebService 的
		// url
		HttpTransportSE transport = new HttpTransportSE(url);
		transport.debug = true;
		// 指定 WebService 的命名空间和函数名
		SoapObject soapObject = new SoapObject(namespace, methodName);
		// 设置调用方法参数的值
		soapObject.addProperty("ClueID", ClueID);
		soapObject.addProperty("Company_ID", Company_ID);
		soapObject.addProperty("Source", Source);
		soapObject.addProperty("Title", Title);		
		soapObject.addProperty("Company", Company);
		soapObject.addProperty("Name", Name);
		soapObject.addProperty("Tel", Tel);
		soapObject.addProperty("Mobile", Mobile);		
		soapObject.addProperty("QQ", QQ);
		soapObject.addProperty("Email", Email);
		soapObject.addProperty("Address", Address);
		soapObject.addProperty("Note", Note);		
		soapObject.addProperty("Manager_ID", Manager_ID);
		soapObject.addProperty("Manager_Name", Manager_Name);		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		//envelope.headerOut = getSoapHeader();
		envelope.bodyOut = soapObject;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(soapObject);
		// 使用 call 方法调用 WebService 方法 ,第一个参数一般为空
		try {
			transport.call(namespace + methodName, envelope);
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println(e);
		} catch (XmlPullParserException e) {
			// e.printStackTrace();
			System.out.println(e);
		}
		// 获取服务器响应返回的SOAP消息
		SoapObject result = null;
		result = (SoapObject) envelope.bodyIn;
		if (result == null) {
			return null;
		}
		int i = result.getPropertyCount();
		String str = result.getProperty(i - 1).toString();
//		Log.e(TAG,"新增线索str:" + str);
		return str;
	}

	private static Element[] getSoapHeader(){
		   Element[] header = new Element[1]; 
	       header[0] = new Element().createElement(namespace, soapheadername); 
	       
	       Element userName = new Element().createElement(namespace, "UserName"); 
	       userName.addChild(Node.TEXT, uesrid); 
	       header[0].addChild(Node.ELEMENT, userName); 
	       
	       Element pass = new Element().createElement(namespace, "PassWord"); 
	       pass.addChild(Node.TEXT, password); 
	       header[0].addChild(Node.ELEMENT, pass); 
	       return header;
	}

	
	}

	
