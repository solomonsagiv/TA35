package myJson;

public interface IJsonData {
	public MyJson getAsJson();
	public void loadFromJson(MyJson json);
	public MyJson getResetJson();
	public MyJson getFullResetJson();
}
