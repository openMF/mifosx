package org.openmf.mifos.dataimport.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class SimpleHttpResponse
{
	private HttpURLConnection connection;
	
	private InputStream content;

	public SimpleHttpResponse(HttpURLConnection connection)
	{
		this.connection = connection;
	}

	public int getStatus() throws IOException
	{
		return connection.getResponseCode();
	}
	

	public InputStream getContent() throws IOException
	{
		if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
		   content = connection.getInputStream();
		else
			content = connection.getErrorStream();
		return content;
	}

	@SuppressWarnings("PMD")
        public void destroy() {
            if (content != null) try {
                content.close();
            } catch (IOException e) {
                try {
                    content.close();
                } catch (IOException e1) {
                    // do nothing
                }
            }
        }

}
