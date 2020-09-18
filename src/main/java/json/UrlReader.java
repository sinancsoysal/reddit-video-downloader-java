package json;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlReader {
	enum Status {
		AVAILABLE, NOT_REACHED
	}

	public static Status connectURL(String url) {
		try {
			URL oracle = new URL(url);

			new InputStreamReader(oracle.openStream());

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return Status.NOT_REACHED;
		} catch (IOException e) {
			System.gc();
			return Status.NOT_REACHED;
		}
		System.gc();
		return Status.AVAILABLE;
	}
}
