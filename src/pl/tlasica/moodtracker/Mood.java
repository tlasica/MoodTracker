package pl.tlasica.moodtracker;


//TODO: colory można zapisać w resource
//http://stackoverflow.com/questions/3668958/how-to-set-color-for-the-textview-in-android
public enum Mood {

	HAPPY("HAPPY", "#FFB32A", R.drawable.face_happy, "face_happy.png"),
	NEUTRAL("NEUTRAL", "#43CF30", R.drawable.face_neutral, "face_neutral.png"),
	SAD("SAD", "#35A4FF", R.drawable.face_sad, "face_sad.png"),
	ANGRY("ANGRY", "#E43DC0", R.drawable.face_angry, "face_angry.png");
		
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

    public int getImage() { return image; }

    public String getImageFile() { return imageFile; }

	private Mood(String text, String rgb, int img, String imgFile) {
		this.text = text;
		this.color = rgb;
        this.image = img;
        this.imageFile = imgFile;

	}

	private String  text;
	private String  color;
    private int     image;
    private String  imageFile;
	
}
