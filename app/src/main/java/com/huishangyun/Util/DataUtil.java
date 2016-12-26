package com.huishangyun.Util;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import com.huishangyun.model.Constant;

/**
 * 
 * @author pan
 * 
 */
public class DataUtil {

	/**
	 * 
	 * @return SoapHeaders身份验证
	 */
	private static Element[] getSoapHeader() {
		Element[] header = new Element[1];
		header[0] = new Element().createElement(Constant.app_namespace, Constant.app_soapheadername);
		Element userName = new Element().createElement(Constant.app_namespace, "UserName");
		userName.addChild(Node.TEXT, Constant.app_uesrid);
		header[0].addChild(Node.ELEMENT, userName);
		Element pass = new Element().createElement(Constant.app_namespace, "Password");
		pass.addChild(Node.TEXT, Constant.app_password);
		header[0].addChild(Node.ELEMENT, pass);
		return header;
	}
	/**
	 * 
	 * @param methodName-接口方法名
	 * @param json-传入的数据
	 * @return 服务器返回的数据
	 * http://192.168.0.133:8283/yqy/1.0/yqy_wsdl.asmx（内网）
	 * http://is.yiqiyun.com.cn/yqy/1.0/yqy_wsdl.asmx
	 * http://192.168.0.16/yqy/1.0/yqy_wsdl.asmx(陈总内网)
	 */
	public static String callWebService(String methodName, String json) {
		HttpTransportSE transport = new HttpTransportSE(
				Constant.app_url, 5000);
		transport.debug = true;
		SoapObject soapObject = new SoapObject(Constant.app_namespace, methodName);
		soapObject.addProperty("request", json);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.headerOut = getSoapHeader();
		envelope.bodyOut = soapObject;
		envelope.dotNet = true;
		try {
			transport.call(Constant.app_namespace + methodName, envelope);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try {
			SoapObject result = null;
			result = (SoapObject) envelope.bodyIn;
			if (result == null) {
				return null;
			}
			int i = result.getPropertyCount();
			String str = result.getProperty(i - 1).toString();
			return str;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		
	}		

}