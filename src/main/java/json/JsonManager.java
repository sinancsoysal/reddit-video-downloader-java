package json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import media.Image;
import media.Media;
import media.Video;
import process.ProcessHandler;

public class JsonManager {

	private static JSONObject readJsonFromUrl(String url) {
		JSONObject json = null;
		try {
			URL oracle = new URL(url); // URL to Parse
			HttpURLConnection yc = (HttpURLConnection) oracle.openConnection();
			yc.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36 OPR/54.0.2952.64");
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

			json = new JSONObject(ProcessHandler.readAll(in));
			yc.disconnect();
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 
	 * @param dir a directory that specifies the location where the file located at
	 * @return ffprobe results of the specified file in JSON format
	 */
	public static JSONObject readJsonFromFile(String dir) {
		return new JSONObject(ProcessHandler.getOutputFromProcess(
				"ffprobe -v quiet -print_format json -show_format -show_streams output.mp4", dir));
	}

	private static Video setDetailsVideo(JSONObject json, int index) {
		JsonVideo video = new JsonVideo(json, index);
		return new Video(-1, video.getId(), video.getTitle(), null, video.getV_url(), video.getA_url());
	}

	private static Image setDetailsImage(JSONObject json, int index) {
		JsonImage image = new JsonImage(json, index);
		return new Image(-1, image.getId(), image.getTitle(), null, image.getImageUrl());
	}

	enum wut {
		VIDEO, IMAGE, IDK_WTF_IsThis
	}

	private static wut checkObject(JSONObject json, int index) {
		JSONObject obj = json.getJSONObject("data").getJSONArray("children").getJSONObject(index).getJSONObject("data");

		if (obj.isNull("media") && !(obj.isNull("url_overridden_by_dest"))
				&& obj.getString("url_overridden_by_dest").contains(".jpg")) {
			return wut.IMAGE;
		} else if (!(obj.isNull("media")) && obj.getJSONObject("media").has("reddit_video")) {
			return wut.VIDEO;
		}
		return wut.IDK_WTF_IsThis;
	}

	/**
	 * Returns a Media list that can then be downloaded to a local file. The url
	 * argument must specify an absolute <a href="#{@link}">{@link URL}</a>.
	 * <p>
	 * This method always returns a list, whether or not the acceptable URLs exists.
	 * If an URL doesn't exists when this applet attempts to parse media URL from
	 * JSON object, the data won't be added to the list.
	 * 
	 * @param url an absolute URL giving the base location of JSON data
	 * @return list of media at the specified URL
	 */
	public static List<Media> parseUrls(String url) {
		System.out.printf("[info] parsing process started on '%s'\n", url);
		if (url.endsWith("/")) {
			url += ".json";
		} else {
			url += "/.json";
		}
		List<Media> media = new ArrayList<>();
		long startTime = System.currentTimeMillis();

		JSONObject json = readJsonFromUrl(url);
		int jsonLen = json.getJSONObject("data").getJSONArray("children").length();

		for (int i = 0; i < jsonLen; i++) {
			switch (checkObject(json, i)) {
			case VIDEO:
				if (!(new JsonVideo(json, i).getDuration() > 60)) {
					media.add(setDetailsVideo(json, i));
				}
				break;
			case IMAGE:
				media.add(setDetailsImage(json, i));
				break;
			default:
				break;
			}
		}
		System.out.printf("[info] %d/%d url successfully parsed from '%s'. time elapsed: %dms.\n", media.size(),
				jsonLen, url, System.currentTimeMillis() - startTime);
		return media;
	}
}
