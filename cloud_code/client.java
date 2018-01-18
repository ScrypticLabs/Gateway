/*
* @Author: Abhi
* @Date:   2016-06-12 20:36:10
* @Last Modified by:   Abhi
* @Last Modified time: 2016-06-12 20:46:52
*/

import java.util.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
 
public class client {
	public static void main(String[] args) throws IOException {
		URL url = new URL("http://localhost:8080/resttest/services/Order /3");
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setDoOutput(true);
		httpCon.setRequestMethod("POST");
		OutputStreamWriter out = new OutputStreamWriter(
		  httpCon.getOutputStream());
		System.out.println(httpCon.getResponseCode());
		System.out.println(httpCon.getResponseMessage());
		out.close();
	}
}