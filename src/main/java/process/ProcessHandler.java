package process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class ProcessHandler {
	public static int process(String cmd, String dir) {
		try {
            ProcessBuilder pb = new
               ProcessBuilder(cmd.split(" "));
               pb.directory(new File(dir));
            final Process p=pb.start();
//            print(p);
			p.waitFor();
			p.destroy();
			return p.exitValue();
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
				return -69;
			}
	}
	public static String getOutputFromProcess(String cmd, String dir) {
		try {
            ProcessBuilder pb = new
               ProcessBuilder(cmd.split(" "));
               pb.directory(new File(dir));
            final Process p=pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            
			p.waitFor();
			return readAll(br);
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
				return null;
			}
	}
	public static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	public static void print(Process p) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        System.out.println(readAll(br));
	}
}
