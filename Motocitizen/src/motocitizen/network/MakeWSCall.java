package motocitizen.network;

import java.io.IOException;
import java.util.ArrayList;

import motocitizen.startup.Startup;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class MakeWSCall {
	public String SOAP_ACTION_PREFIX;
	public String NAMESPACE;
	public String SERVER;
	public String METHOD;
	public SoapObject request;

	public MakeWSCall(String met, String srv, String ns, String prefix) {
		NAMESPACE = ns;
		SOAP_ACTION_PREFIX = prefix;
		SERVER = srv;
		METHOD = met;
		request = new SoapObject(NAMESPACE, METHOD);
	}

	public MakeWSCall() {
		this("getEvents", Startup.props.get("ws_server"), "urn:MC", "/");
	}

	public MakeWSCall(String met) {
		this(met, Startup.props.get("ws_server"), "urn:MC", "/");
	}

	public MakeWSCall(String met, String srv) {
		this(met, srv, "/");
	}

	public MakeWSCall(String met, String srv, String ns) {
		this(met, srv, ns, "/");
	}

	public SoapObject call() {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		envelope.bodyOut = request;
		CustomTrustManager.allowAllSSL();

		HttpTransportSE transport = new HttpTransportSE(SERVER, 30000);
		transport.debug = true;
		SoapObject response = new SoapObject();
		ArrayList<HeaderProperty> headerPropertyArrayList = new ArrayList<HeaderProperty>();
		headerPropertyArrayList.add(new HeaderProperty("Connection", "close"));
		headerPropertyArrayList.add(new HeaderProperty("Accept-Encoding", "gzip"));
		try {
			// System.setProperty("http.keepAlive", "false");
			transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope, headerPropertyArrayList);
			// transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD,
			// envelope);
		} catch (HttpResponseException e) {
			response.addProperty("error", "Проблемы с сервером. HttpResponse.");
		} catch (IOException e) {
			e.printStackTrace();
			response.addProperty("error", "Проблемы с сервером. IO.");
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			response.addProperty("error", "Проблемы с сервером. XmlPullParser.");
		}
		/*
		 * String requestDump = transport.requestDump; String responseDump =
		 * transport.responseDump;
		 * 
		 * Log.i("REQ", "Requeste: " + requestDump); Log.i("RES", "Response: " +
		 * responseDump);
		 */
		if (envelope.bodyIn instanceof SoapObject) {
			response = (SoapObject) envelope.bodyIn;
			response.addProperty("error", "ok");
		} else if (envelope.bodyIn instanceof SoapFault) {
			Log.d("Soap", envelope.bodyIn.toString());
		}
		return response;
	}
}
