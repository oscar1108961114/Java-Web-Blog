package model.entity;
public class Article
{
    private int id;
    private String title;
    private String content;
    private String author;
    private long created_at;
    public Article(int id, String title, String content, String author , long created_at)
    {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.created_at = created_at;
    }
    public Article(int id, String title, String content, String author) 
    //當呼叫到這個建構子，由於這個建構子只有4個參數，但初始值有5個，所以會自動呼叫擁有5個參數的建構子，並補上第5個值
    {
    	 this(id , title , content , author , System.currentTimeMillis());
    }
    public Article()
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
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getContent()
	{
		return content;
	}
	public void setContent(String content)
	{
		this.content = content;
	}
	public String getAuthor()
	{
		return author;
	}
	public void setAuthor(String author)
	{
		this.author = author;
	}
	public long getCreated_at()
	{
		return created_at;
	}
	public void setCreated_at(long created_at)
	{
		this.created_at = created_at;
	}
	 
}
