package json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import media.Media;

class JsonManagerTest {
	@DisplayName("Parsing JSON From Url Test")
	@Test
	void testParseUrls() {
		List<Media> media = JsonManager.parseUrls("https://www.reddit.com/");
		assertNotNull(media, "a parsed url must not be null");
		assertEquals(-1, media.get(new Random().nextInt(media.size())).getId());
	}
	
	@DisplayName("ffprobe Results Test")
	@Test
	void testReadJsonFromFile() {
		String dir = "/home/eniax/eclipse-workspace/redditVideoHandler/src/test/resources";
		assertNotNull(JsonManager.readJsonFromFile(dir), "ffprobe results must not be null");
		assertEquals(JsonManager.readJsonFromFile(dir).getJSONArray("streams").getJSONObject(0).get("display_aspect_ratio"),
				"25:36", "input file has DAR[ 25 : 36] ");
	}
}
