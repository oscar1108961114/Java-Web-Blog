package model.entity;

public class Member
{
	private int id;
	private String name;
    private String account;
    private String password;
    public Member(int id , String name , String account , String password)
    {
    	this.id = id;
    	this.account = account;
    	this.password = password;
    	this.name = name;
    }
    public Member()
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
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getAccount()
	{
		return account;
	}
	public void setAccount(String account)
	{
		this.account = account;
	}
	public String getPassword()
	{
		return password;
	}
	public void setPassword(String password)
	{
		this.password = password;
	}
}
