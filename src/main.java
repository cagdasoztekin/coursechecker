import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class main
{
  public static void main(String[] args)
//    throws IOException
  {
	Scanner scan = new Scanner(System.in);
	while(scan.hasNext())
	{
		System.out.println(scan.next());
	}
//    FileReader reader = new FileReader("text.txt");
//    while (reader.read() != -1)
//    {
//      System.out.print((char)reader.read());
//    }
  }
}