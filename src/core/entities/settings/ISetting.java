package core.entities.settings;

public interface ISetting {
	String getName();
	
	String getDescription();
	
	String getValueString();
	
	String getSaveString();
	
	void set(String[] args);
}
