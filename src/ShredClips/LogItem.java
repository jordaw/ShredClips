package ShredClips;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

class LogItem {
	private final StringProperty text;
	private final StringProperty choice;
	private final StringProperty css;

	public LogItem(String text, String choice) {
		this.text = new SimpleStringProperty(text);
		this.choice = new SimpleStringProperty(choice);
		this.css = new SimpleStringProperty(setCSS(choice));
	}

	public String getText() {
		return text.get();
	}

	public String getChoice() { return choice.get(); }

	public String getCSS() { return css.get(); }

	private String setCSS(String choice) {
		if (choice.equals("Source") || choice.equals("Destination")) {
			return "-fx-control-inner-background: green ; -fx-focus-color: green ;";
		} else if (choice.equals("Move")) {
			return "-fx-control-inner-background: blue; -fx-focus-color: blue ;";
		} else if (choice.equals("Delete") || choice.equals("Error")) {
			return "-fx-control-inner-background: red; -fx-focus-color: red ;";
		}
		return null;
	}
}
