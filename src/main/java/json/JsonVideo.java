package json;

import org.json.JSONObject;

class JsonVideo extends JsonMedia {
	private String v_url;
	private String a_url;
	private int duration;

	public JsonVideo(JSONObject json, int index) {
		super(json, index);
		setV_url();
		setA_url();
		setDuration();
	}

	/**
	 * @return the v_url
	 */
	String getV_url() {
		return v_url;
	}

	void setV_url() {
		this.v_url = super.getJson().getJSONObject("data").getJSONObject("media").getJSONObject("reddit_video")
				.getString("fallback_url");
	}

	/**
	 * @return the a_url
	 */
	String getA_url() {
		return a_url;
	}

	void setA_url() {
		String url = getAudioUrl();
		UrlReader.Status status = UrlReader.connectURL(url);
		if (status != UrlReader.Status.NOT_REACHED) {
			this.a_url = url;
		} else {
			this.a_url = status.toString();
		}
	}

	private String getAudioUrl() {
		return getV_url().split("DASH_")[0].concat("DASH_audio.mp4?source=fallback");
	}

	/**
	 * @return the duration
	 */
	int getDuration() {
		return duration;
	}

	void setDuration() {
		this.duration = super.getJson().getJSONObject("data").getJSONObject("media").getJSONObject("reddit_video")
				.getInt("duration");
	}

}
