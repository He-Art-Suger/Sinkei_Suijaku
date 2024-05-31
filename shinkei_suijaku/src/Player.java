import java.io.Serializable;

public class Player implements Serializable
{

	private String myName = ""; //プレイヤ名
	private int turnnum = 0;//手番番号(1～4)
	
	//コンストラクタ
	Player()
	{
		
	}
	
	// プレイヤ名を受付
	public void setName(String name)
	{
		myName = name;
	}
	// プレイヤ名を取得
	public String getName()
	{
		return myName;
	}
	
	// 神経衰弱の手番の受付
	public void setTurnNum(int num)
	{
		turnnum = num;
	}
	// 神経衰弱の手番を取得
	public int getTurnNum()
	{
		return turnnum;
	}
	
}
