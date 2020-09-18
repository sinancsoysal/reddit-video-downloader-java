package json;

import org.json.JSONObject;

class JsonMedia {
	private JSONObject json;
	private String title;
	private String id;

	JsonMedia(JSONObject json, int index) {
		this.json = json.getJSONObject("data").getJSONArray("children").getJSONObject(index);
	}

	/**
	 * @return the title
	 */
	String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	void setTitle() {
		this.title = this.json.getJSONObject("data").getString("title");
	}

	/**
	 * @return the id
	 */
	String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	void setId() {
		this.id = this.json.getJSONObject("data").getString("id");
	}

	/**
	 * @return the json
	 */
	JSONObject getJson() {
		return json;
	}

	/**
	 * @param json the json to set
	 */
	void setJson(JSONObject json) {
		this.json = json;
	}
}
