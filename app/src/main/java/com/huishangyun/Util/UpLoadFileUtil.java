package com.huishangyun.Util;

import com.huishangyun.model.Constant;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;

public class UpLoadFileUtil {
	/**
	 *
	 * @return SoapHeaders身份验证
	 */
	private static Element[] getSoapHeader() {
		Element[] header = new Element[1];
		header[0] = new Element().createElement(Constant.namespace, Constant.soapheadername);
		Element userName = new Element().createElement(Constant.namespace, "UserName");
		userName.addChild(Node.TEXT, Constant.uesrid);
		header[0].addChild(Node.ELEMENT, userName);
		Element pass = new Element().createElement(Constant.namespace, "Password");
		pass.addChild(Node.TEXT, Constant.password);
		header[0].addChild(Node.ELEMENT, pass);
		return header;
	}

	/**
	 * 上传附件
	 * @param fileData-附件的base64码
	 * @param modulePage-
	 * @param fileName-文件名
	 * @param companyID-公司编号
	 * @return
	 */
	public static String UpLoadFile(String fileData, String modulePage, String fileName, String companyID) {
		final String methodName = "UpLoadFile";// 函数名
		HttpTransportSE transport = new HttpTransportSE(Constant.url, 20000);
		transport.debug = true;
		// 指定 WebService 的命名空间和函数名
		SoapObject soapObject = new SoapObject(Constant.namespace, methodName);
		soapObject.addProperty("fileData", fileData);
		soapObject.addProperty("modulePage", modulePage);
		soapObject.addProperty("fileName", fileName);
		soapObject.addProperty("companyID", companyID);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.headerOut = getSoapHeader();
		envelope.bodyOut = soapObject;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(soapObject);
		try {
			transport.call(Constant.namespace + methodName, envelope);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			e.printStackTrace();
			L.e("内存溢出");
			return null;
		}
		try {
			SoapObject result = null;
			result = (SoapObject) envelope.bodyIn;
			if (result == null) {
				return null;
			}
			L.v("result = " + result.toString());
			int i = result.getPropertyCount();
			String str = result.getProperty(i - 1).toString();
			L.v("调函数" + methodName + "返回：" + str);
			return str;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * 上传图片并添加水印
	 * @param picData
	 * @param modulePage
	 * @param fileName
	 * @param companyID
	 * @param signText
	 * @return
	 */
	public static String UpLoadImgSignText(String picData, String modulePage, String fileName, String companyID, String signText) {
		final String methodName = "UpLoadImgSignText";// 函数名
		HttpTransportSE transport = new HttpTransportSE(Constant.url, 20000);
		transport.debug = true;
		// 指定 WebService 的命名空间和函数名
		SoapObject soapObject = new SoapObject(Constant.namespace, methodName);
		soapObject.addProperty("picData", picData);
		soapObject.addProperty("modulePage", modulePage);
		soapObject.addProperty("fileName", fileName);
		soapObject.addProperty("companyID", companyID);
		soapObject.addProperty("signText", signText);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.headerOut = getSoapHeader();
		envelope.bodyOut = soapObject;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(soapObject);
		try {
			transport.call(Constant.namespace + methodName, envelope);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			e.printStackTrace();
			L.e("内存溢出");
			return null;
		}
		try {
			SoapObject result = null;
			result = (SoapObject) envelope.bodyIn;
			if (result == null) {
				return null;
			}
			L.v("result = " + result.toString());
			int i = result.getPropertyCount();
			String str = result.getProperty(i - 1).toString();
			L.v("调函数" + methodName + "返回：" + str);
			return str;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
}
