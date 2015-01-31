import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class OfferingsChecker 
{
	public static void main( String[] args) throws IOException, InterruptedException
	{
		Scanner scan = new Scanner(System.in);
		String courseCode = "", courseNumber = "", section = "";
		ArrayList<Course> courses = new ArrayList<Course>();
		ArrayList<Date> lastAvailable = new ArrayList<Date>();
		
		
		double interval = 5000.0;
		
		String fromEmail;
		String toEmail;
		String password;
		
		while(!courseCode.equals("-1") &&  !courseNumber.equals("-1") && !section.equals("-1"))
		{
			System.out.print("Enter course name (ENG): ");
			courseCode = scan.next();
			if(courseCode.equals("-1"))
				break;
			System.out.print("Enter course code (101): ");
			courseNumber = scan.next();
			if(courseNumber.equals("-1"))
				break;
			System.out.print("Enter section (9): ");
			section = scan.next();
			if(section.equals("-1"))
				break;
			if(!courseCode.equals("-1") &&  !courseNumber.equals("-1") && !section.equals("-1"))
			{
				courses.add( new Course(courseCode, courseNumber, section));
				System.out.println("Enter -1 in any of the three to finish adding courses.");
			}
		}
		
		for(int i = 0; i < courses.size(); i++)
		{
			lastAvailable.add(new Date(2000,0,0));
		}
		
		System.out.print("Enter interval to check, in ms (5000) min 2000: ");
		interval = scan.nextInt();
		if(interval < 2000.0)
		{
			interval = 2000.0;
		}
		
		System.out.print("Enter recipient e-mail address (john.doe@ug.bilkent.edu.tr): ");
		toEmail = scan.next();
		System.out.print("Enter a gmail address to send mails from (example@gmail.com): ");
		fromEmail = scan.next();
		System.out.print("Enter password: ");
		password = scan.next();
		
		System.out.println("Initializing...");
		System.out.println("Number of added courses: " + courses.size());
		
		int numOfCourses = courses.size();
		int times = 0;
		int currentQuota = 0;
		boolean anyFound = false;
		String previousMessage = "";
		
		trustTheSiteGoddammit();
		
		while(true)
		{
			for(int i = 0; i < courses.size(); i++)
			{
				currentQuota = checkCourse(courses.get(i));
				if(currentQuota != 0)
				{
					anyFound = true;
					lastAvailable.set(i, new Date());
					System.out.println(currentQuota + " Seats available for " + courses.get(i).getName() + courses.get(i).getNumber() + "-" + courses.get(i).getSection());
				}
				else
				{
					System.out.println("No seats available for " + courses.get(i).getName() + courses.get(i).getNumber() + "-" + courses.get(i).getSection());
					Date date = new Date(2000,0,0);
					boolean never = lastAvailable.get(i).equals(date);
					
					if(never)
						System.out.println("Don't cry, there has never been an available seat since the run of the program");
					else
						System.out.println("Last available seat at " + lastAvailable.get(i).getTime());
				}
				
				courses.get(i).setQuota(currentQuota);
				
//				System.out.println("Sleeping for " + (int)(interval/numOfCourses) + " ms");
				
				Thread.sleep((int)(interval/numOfCourses));
			}
			
			times++;
			System.out.println("Times checked: " + times);
			
			if(anyFound)
			{
				String message = "";
				for(int i = 0; i < courses.size(); i++)
				{
					message += courses.get(i).getName()
							+ courses.get(i).getNumber() + "-"
							+ courses.get(i).getSection() + "\t"
							+ courses.get(i).getQuota();
					message += "\n";
					

				}
				
				if(!(message.equals(previousMessage)))
				{
					sendMail(message, fromEmail, toEmail, password);
				}
				
				previousMessage = message;
			}
			
			anyFound = false;
			currentQuota = 0;
			
			System.out.println("Sleeping for " + (int)(interval/numOfCourses) + " ms");
			
			Thread.sleep((int)(interval/numOfCourses));
		}	
	}
	
	public static int checkCourse(Course course) throws IOException
	{
		int quota;
		String url = "https://stars.bilkent.edu.tr/homepage/print/plainOfferings.php?COURSE_CODE=" + 
		course.getName() + "&SEMESTER=20142";

		String source = getUrlSource(url);

		int count = 0;
		int index = source.indexOf(course.getNumber() + "-" + course.getSection());
		int first = 0, second = 0;
		String find = "</td>";
		for(int i = index; i + find.length() < source.length(); i++)
		{
			if(source.substring(i, i + find.length()).equals(find))
			{
				count++;
			}
			if(count == 11)
			{
				first = i;
			}
			if(count == 12)
			{
				second = i;
			}
		}

		quota = Integer.parseInt(source.substring(first,second+3).replaceAll("[\\D]", ""));
		
		if(quota != 0)
		{
			System.out.println(course.getName() + course.getNumber() + "-" + course.getSection() + ": " + quota);
		}

		return quota;
	}
	
	private static String getUrlSource(String url) throws IOException 
	{
        URL link = new URL(url);
        URLConnection yc = link.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            a.append(inputLine);
        in.close();

        return a.toString();
    }

	public static void trustTheSiteGoddammit()
	{
		TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {

                public java.security.cert.X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                {}
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                {}
            }
        };

        try 
        {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } 
        catch (Exception e) 
        {
            System.out.println(e);
        }        
	}
	
	public static void sendMail( String content, String from, String to, String pass)
	{
		final String username = from;
        final String password = pass;
//        System.out.println("from " + from + " pass " + pass);
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
        	new javax.mail.Authenticator() 
        	{
        		protected PasswordAuthentication getPasswordAuthentication() 
            	{
               		return new PasswordAuthentication(username, password);
            	}
          	});
    	{
    		try
    		{
	            Message message = new MimeMessage(session);
	            message.setFrom(new InternetAddress(from));
	            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse( to));
	            message.setSubject( "AVAILABLE QUOTA!");
	            message.setText(content);

            	Transport.send(message);
            
            	System.out.println("E-Mail sent! to " + to);
            }

         	catch (MessagingException e) 
         	{
        	 	throw new RuntimeException(e);
         	}
    	}
	}
}
