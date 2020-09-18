package process;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import database.DatabaseManager;
import json.JsonManager;
import media.Image;
import media.Media;
import media.Video;

public class Downloader {
	/**
	 * 
	 * @param url  an absolute URL giving the base location of the video
	 * @param aUrl an absolute URL giving the base location of the audio
	 * @param dir  a directory that specifies the location where the video will be
	 *             downloaded at
	 * @return exit value of the executed process
	 */
	private static int download(String url, String aUrl, String dir) {
		if (aUrl.equals("NOT_REACHED")) {
			return ProcessHandler.process(String.format("curl -o output.mp4 %s", url), dir);
		}
		return ProcessHandler.process(String.format("ffmpeg -i %s -i %s -c:v copy -c:a aac output.mp4", url, aUrl),
				dir);
	}

	/**
	 * 
	 * @param url an absolute URL giving the base location of the image
	 * @param dir a directory that specifies the location where the image will be
	 *            downloaded at
	 * @return exit value of the executed process
	 */
	private static int download(String url, String dir) {
		return ProcessHandler.process(String.format("curl -o image.jpg %s", url), dir);
	}

	enum DAR {
		APPROPRIATE, NOT_APPROPRIATE
	}

	private static DAR checkDAR(String dir) {
		String[] d_a_r = JsonManager.readJsonFromFile(dir).getJSONArray("streams").getJSONObject(0)
				.getString("display_aspect_ratio").split(":");
		float dar = (float) Integer.parseInt(d_a_r[0]) / (float) Integer.parseInt(d_a_r[1]);
		if (dar > .8 && dar < 1.8) {
			return DAR.APPROPRIATE;
		}
		return DAR.NOT_APPROPRIATE;
	}

	public static String scaleIfNotAppropriateAndReturnNameOfTheFile(String dir) {
		if (checkDAR(dir) != DAR.APPROPRIATE) {
			if (ProcessHandler.process(
					"ffmpeg -i output.mp4 -vf scale=1280:720:force_original_aspect_ratio=decrease,pad=1280:720:(ow-iw)/2:(oh-ih)/2 -threads 0 scaled_output.mp4",
					dir) == 0) {
				remove(dir);
			}
			return "scaled_output.mp4";
		}
		return "output.mp4";
	}

	/**
	 * 
	 * @param db     refers to the table in the database
	 * @param dbConn reference of the DatabaseManager
	 */
	public static void downloadAll(String db, DatabaseManager dbConn) {
		System.out.printf("[info] downloading from '%s'\n", db);
		long start = System.currentTimeMillis();
		List<Media> media = dbConn.byPass_getDownloadables(db);
		media.parallelStream().forEach(content -> {
			switch (content.getClass().getSimpleName()) {
			case "Video":
				try {
					String dir = mkdir(content.getPost_id(), db).concat("/");
					download(((Video) content).getvUrl(), ((Video) content).getaUrl(), dir);
					dbConn.updateDir(content.getId(), dir.concat(scaleIfNotAppropriateAndReturnNameOfTheFile(dir)), db);
				} catch (IOException e) {
					e.printStackTrace();
				}
				dbConn.updateD_status(content.getId(), true, db);
				break;
			case "Image":
				try {
					String dir = mkdir(content.getPost_id(), db).concat("/");
					download(((Image) content).getiUrl(), dir);
					dbConn.updateDir(content.getId(), dir.concat("image.jpg"), db);
				} catch (IOException e) {
					e.printStackTrace();
				}
				dbConn.updateD_status(content.getId(), true, db);
				break;
			default:
				break;
			}
		});
		System.out.printf("[info] download completed for '%s'. time elapsed: %dms.\n", db,
				System.currentTimeMillis() - start);
	}

	private static String mkdir(String postId, String db) throws IOException {
		String dir = System.getProperty("user.dir") + "/downloads/" + db + "/" + postId;
		Path path = Paths.get(dir);
		Files.createDirectories(path);
		return dir;
	}

	public static int remove(String dir) {
		if (Files.exists(Paths.get(dir))) {
			Path video = Paths.get(dir + "/output.mp4");
			Path image = Paths.get(dir + "/image.jpg");
			try {
				if (Files.exists(video)) {
					Files.delete(video);
				}
				if (Files.exists(image)) {
					Files.delete(image);
				}
				return 0;
			} catch (IOException e) {
				e.printStackTrace();
				return -3;
			}
		}
		return -3;
	}
}
