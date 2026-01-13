package artcreator.creator.port;

public interface Creator {

	void sysop(String str);

	void loadImage(String path);

	void applyDefaultParameters();

	Object generatePreview(Object parameters);

	void setParameters(Object parameters);

	void confirmProcessing();
}
