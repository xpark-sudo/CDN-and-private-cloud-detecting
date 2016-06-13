package LoginTest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	private static String file = "./Log/Log.txt";

	public Log() {

	}

	public static int getDateTime(String content) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSS");
		String dateNowStr = sdf.format(d);
		recordLogFile(content + dateNowStr + "\n");
		return 0;
	}

	public static int recordLogFile(String content) {
		// record begin end time record jd yhd content
		FileWriter writer = null;
		//BufferedWriter bw = null;
		try {
			writer = new FileWriter(file, true);
			Charset.forName("UTF-8").encode(content);
			String str = new String(content.getBytes(), "UTF-8");
			//bw = new BufferedWriter(writer);
			//bw.append(str);
			 writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				 if(writer!=null){
				//bw.close();
				writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return 0;
	}
}