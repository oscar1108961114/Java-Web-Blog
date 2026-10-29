package model.entity;
public class Personal_Information
{
	private int id;
    private String user_name;
    private String user_dream;
    private String introduction;
    public Personal_Information(int id, String user_name, String user_dream, String introduction)
    {
    	this.id = id;
        this.user_name = user_name;
        this.user_dream = user_dream;
        this.introduction = introduction;
    }
    public Personal_Information()
    {
    	
    }
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getUser_name()
	{
		return user_name;
	}
	public void setUser_name(String user_name)
	{
		this.user_name = user_name;
	}
	public String getUser_dream()
	{
		return user_dream;
	}
	public void setUser_dream(String user_dream)
	{
		this.user_dream = user_dream;
	}
	public String getIntroduction()
	{
		return introduction;
	}
	public void setIntroduction(String introduction)
	{
		this.introduction = introduction;
	}
    
}
