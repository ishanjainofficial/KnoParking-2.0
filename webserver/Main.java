import java.io.*;

public class Main {
	public static void main(String[] args) throws Exception {
		Writer writer = null;
		String string = "hello";
		File file = new File("/Users/ishan/webserver/public/knoparking/id.txt");
		PrintWriter out = new PrintWriter(file);
		out.println(string);
		out.close();
		System.out.printf("Wrote %s to %s\n", string, file.toString());
	}
}
