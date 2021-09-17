package ShredClips;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.nio.file.Path;
import java.nio.file.Paths;

class DirectoryItem {
	private final StringProperty fileName;
	private final StringProperty filePath;
	private final Path fullPath;

	public DirectoryItem(String file, String path) {
		this.fileName = new SimpleStringProperty(file);
		this.filePath = new SimpleStringProperty(path);
		this.fullPath = initializeFullPath(path, file);
	}

	public String getFileName() { return fileName.get(); }

	public String getPath() { return filePath.get(); }

	public Path getFullPath() { return fullPath; }

	private Path initializeFullPath(String path, String filename) {
		return Paths.get(path, filename);
	}
}
