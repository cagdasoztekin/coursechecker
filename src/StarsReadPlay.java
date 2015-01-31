import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class StarsReadPlay {

	public static void main(String[] args) throws Exception 
	{
		System.out.println( getUrlSource());
	}
	
	 private static String getUrlSource() throws IOException 
	 {
         URL link = new URL("https://stars.bilkent.edu.tr/homepage/plain_offerings");
         URLConnection yc = link.openConnection();
         BufferedReader in = new BufferedReader(new InputStreamReader(
                 yc.getInputStream(), "UTF-8"));
         String inputLine;
         StringBuilder a = new StringBuilder();
         while ((inputLine = in.readLine()) != null)
         {
        	 a.append(inputLine);
         }
         in.close();

         return a.toString();
     }

//	public static void saveImage(String imageUrl, String destFile) throws IOException {
//		URL url = new URL(imageUrl);
//		InputStream is = url.openStream();
//		OutputStream os = new FileOutputStream("/Users/begum/Downloads/serverpictures/" + destFile);
//
//		byte[] b = new byte[2048];
//		int length;
//
//		while ((length = is.read(b)) != -1) {
//			os.write(b, 0, length);
//		}
//
//		is.close();
//		os.close();
//	}
	
	public static ArrayList<String> getFileNames( String allFiles)
	{
		ArrayList proccessedFileNames = new ArrayList<String>();
		String secondToken = "\">";
		String firstToken = "<a href=\"";
		
		
		for( int i = 0; i < allFiles.length(); i++)
		{
			if( i + 9 < allFiles.length())
			{
				if( allFiles.substring(i, i+9).equals(firstToken))
				{
					for( int a = i; a < allFiles.length(); a++)
					{
						if( a + 2 < allFiles.length())
						{
							if( allFiles.substring(a, a+2).equals(secondToken))
							{
								proccessedFileNames.add( allFiles.substring( i+9, a));
								i = a + 4;
								break;
							}
						}
						
					}
				}
			}
		}
		
		proccessedFileNames.remove(0);
		
		return proccessedFileNames;
	}	
}