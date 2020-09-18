package json;

import org.json.JSONObject;

public class JsonImage extends JsonMedia {
	private String imageUrl;

	public JsonImage(JSONObject json, int index) {
		super(json, index);
		setImageUrl();
	}

	/**
	 * @return the imageUrl
	 */
	String getImageUrl() {
		return imageUrl;
	}

	void setImageUrl() {
		this.imageUrl = super.getJson().getJSONObject("data").getString("url_overridden_by_dest");
	}

}
