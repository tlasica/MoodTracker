package pl.tlasica.moodtracker;


//TODO: colory można zapisać w resource
//http://stackoverflow.com/questions/3668958/how-to-set-color-for-the-textview-in-android
public enum Mood {

	HAPPY("HAPPY", "#FFB32A"),
	NEUTRAL("NEUTRAL", "#43CF30"),
	SAD("SAD", "#35A4FF"),
	ANGRY("ANGRY", "#E43DC0");
		
	public String toString() {
		return text;
	}
	
	public String colorRGB() {
		return color;
	}
	
	public static Mood fromString(String s) {
		for(Mood m: Mood.values()) {
			if (s.equalsIgnoreCase(m.text)) {
				return m;
			}
		}
		return null;
	}
	
	private Mood(String text, String rgb) {
		this.text = text;
		this.color = rgb;
	}

	private String text;
	private String color;
	
}
