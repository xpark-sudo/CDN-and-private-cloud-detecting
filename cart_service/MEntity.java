package LoginTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

public class MEntity extends HttpEntityWrapper{
	
	private long contentlength;

	public MEntity(HttpEntity wrappedEntity) {
		super(wrappedEntity);
		// TODO Auto-generated constructor stub
	}

	public void setContentLength(long length)
	{
		this.contentlength=length;
	}
	
	public long getContentLength()
	{
		return this.contentlength;
	}
	
	public String toString()
	{
		InputStream instream=null;
		try{
			instream = this.getContent();
			 Charset charset = null;
		     if (instream == null) {
		            return null;
		     }
		     int i = (int)this.getContentLength();
		     if (i < 0) {
		           i = 4096;
		     }
		     final ContentType contentType = ContentType.get(this);
		     if (contentType != null) {
		            charset = contentType.getCharset();
		     }
		     if (charset == null) {
		            charset = HTTP.DEF_CONTENT_CHARSET;
		     }
		     final Reader reader = new InputStreamReader(instream, charset);
		     final CharArrayBuffer buffer = new CharArrayBuffer(i);
		     final char[] tmp = new char[1024];
		     int l;
		     int length=0;
		     while((l = reader.read(tmp)) != -1) {
		           buffer.append(tmp, 0, l);
		           length+=l;
		     }
		     setContentLength(length*2);
		     return buffer.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try {
				instream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
}
