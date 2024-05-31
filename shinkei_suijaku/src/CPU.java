import java.util.Calendar;
import java.util.Random;

public class CPU
{
	private SS ss;
	int Plnum = 1; //現在の手番
	private int cpulevel; //cpuのレベル
	private int cputurn;  //cpuの手番
	private int drawcard = 1; //ひかれたカードが何枚目か
	private int onedrawi; //1枚目に自分がドローしたカードのi
	private int[] num = new int[100]; //その位置にカードがあるか、カードが裏返っているかなど
	private int[] cardorder = new int[64]; //カードの位置ごとの種類
	private int[] cpucardorder = new int[64]; //cpuが覚えてるカードの位置ごとの種類
	private double cpubrain;
	
	//各種カードを何枚持っているか
	private int []numflag = new int[13];
	{
		for(int i = 0; i < 13; i++)
		{
			numflag[i] = 0;
		}
	}
	private int shuffleflag = 0;
	private int reverseflag = 0;
	private int tradeflag = 0;
	private int skipflag = 0;
	private int specialflag = 0;
	
	//コンストラクタ
	CPU(int cpulevel, int cputurn)
	{
		this.cpulevel = cpulevel;
		this.cputurn = cputurn;
		ss = new SS();
		for(int i = 0; i < 64; i++)
		{
			cpucardorder[i] = 0;
		}
		
		if(cpulevel == 2)
		{
			int second = Calendar.getInstance().get(Calendar.SECOND);//秒
		    int minute = Calendar.getInstance().get(Calendar.MINUTE);//分
		    int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);//時
		    int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);//日
		    int month = Calendar.getInstance().get(Calendar.MONTH)+1;//月
		    int year = Calendar.getInstance().get(Calendar.YEAR);//年

		    System.out.println(year + ", " + month + ", " + day + ", " + hour + "," + minute + ", " + second);
		    System.out.println(year * month * day * hour * minute + second);
		    
		    cpubrain = (double)((year * month * day * hour * minute + second) % 100) / 100;
		    
		    System.out.println(cpubrain);
		}
	}
	
	
	public void MemoryInfo(int i)
	{
		boolean memoryflag = false;
		
		if(cpulevel == 1)//よわい
		{
			if(Math.random() <= 0.3)
			{
				memoryflag = true;
			}
			else
			{
				memoryflag = false;
			}
		}
		else if(cpulevel == 2)//普通
		{
			if(Math.random() <= cpubrain)
			{
				memoryflag = true;
			}
			else
			{
				memoryflag = false;
			}
		}
		else if(cpulevel == 3)//つよい
		{
			if(Math.random() <= 1.0)
			{
				memoryflag = true;
			}
			else
			{
				memoryflag = false;
			}
		}
		
		if(memoryflag == true)
		{
			cpucardorder[i] = cardorder[i];
			
			for(int j = 0; j < 13; j++)
			{
				if(cpucardorder[i] % 13 == j && cpucardorder[i] <= 52)
				{
					numflag[j]++;
					System.out.println("numflag" + j + "を記憶:" + numflag[j]);
				}
			}
			if(cpucardorder[i] == 53 || cpucardorder[i] == 54)
			{
				shuffleflag++;
				System.out.println("shuffleflagを記憶:" + shuffleflag);
			}
			else if(cpucardorder[i] == 55 || cpucardorder[i] == 56)
			{
				reverseflag++;
				System.out.println("reverseflagを記憶:" + reverseflag);
			}
			else if(cpucardorder[i] == 57 || cpucardorder[i] == 58)
			{
				tradeflag++;
				System.out.println("tradeflagを記憶:" + tradeflag);
			}
			else if(cpucardorder[i] == 59 || cpucardorder[i] == 60)
			{
				skipflag++;
				System.out.println("skipflagを記憶:" + skipflag);
			}
			else if(cpucardorder[i] == 61 || cpucardorder[i] == 62 || cpucardorder[i] == 63 || cpucardorder[i] == 64)
			{
				specialflag++;
				System.out.println("specialflagを記憶:" + specialflag);
			}
			
			
		}
	}
	
	public void ForgetInfo()
	{
		
		boolean forgetflag = false;
		boolean forgetdo = false;
		
		if(cpulevel == 1)//よわい
		{
			if(Math.random() <= 0.3)
			{
				forgetflag = true;
			}
			else
			{
				forgetflag = false;
			}
		}
		else if(cpulevel == 2)//普通
		{
			if(Math.random() <= (cpubrain / 4))
			{
				forgetflag = true;
			}
			else
			{
				forgetflag = false;
			}
		}
		else if(cpulevel == 3)//つよい
		{
			if(Math.random() <= 0)
			{
				forgetflag = true;
			}
			else
			{
				forgetflag = false;
			}
		}
		
		if(forgetflag == true)
		{
			while(forgetdo == false)
			{
				Random rand = new Random();
				int randnum = rand.nextInt(64);
				
				if(cpucardorder[randnum] != 0 && cpucardorder[randnum] != -1)
				{
					for(int j = 0; j < 13; j++)
					{
						if(cpucardorder[randnum] % 13 == j && cpucardorder[randnum] <= 52)
						{
							numflag[j]--;
							System.out.println("numflag" + j + "を忘却:" + numflag[j]);
						}
					}
					if(cpucardorder[randnum] == 53 || cpucardorder[randnum] == 54)
					{
						shuffleflag--;
						System.out.println("shuffleflagを忘却:" + shuffleflag);
					}
					else if(cpucardorder[randnum] == 55 || cpucardorder[randnum] == 56)
					{
						reverseflag--;
						System.out.println("reverseflagを忘却:" + reverseflag);
					}
					else if(cpucardorder[randnum] == 57 || cpucardorder[randnum] == 58)
					{
						tradeflag--;
						System.out.println("tradeflagを忘却:" + tradeflag);
					}
					else if(cpucardorder[randnum] == 59 || cpucardorder[randnum] == 60)
					{
						skipflag--;
						System.out.println("skipflagを忘却:" + skipflag);
					}
					else if(cpucardorder[randnum] == 61 || cpucardorder[randnum] == 62 || cpucardorder[randnum] == 63 || cpucardorder[randnum] == 64)
					{
						specialflag--;
						System.out.println("specialflagを忘却:" + specialflag);
					}
					
					cpucardorder[randnum] = 0;
					forgetdo = true;
				}
			}
		}
		
	}
	
	public int draw()
	{	
		int k = 0;
		
		boolean doubleflag = false;
		boolean randomflag = false;
		
		if(drawcard == 1)
		{
			if(specialflag >= 2)
			{
				int i = 0;
				
				while(i < 64 && (cpucardorder[i] != 61 && cpucardorder[i] != 62 && cpucardorder[i] != 63 && cpucardorder[i] != 64))
				{
					i++;
				}

				if(i < 64)
				{
					doubleflag = true;
					k = i;
					onedrawi = i;
				}
			}
			if(skipflag >= 2 && doubleflag == false)
			{
				int i = 0;
				
				while(i < 64 && (cpucardorder[i] != 59 && cpucardorder[i] != 60))
				{
					i++;
				}

				if(i < 64)
				{
					doubleflag = true;
					k = i;
					onedrawi = i;
				}
			}
			if(reverseflag >= 2 && doubleflag == false)
			{
				int i = 0;
				
				while(i < 64 && (cpucardorder[i] != 55 && cpucardorder[i] != 56))
				{
					i++;
				}

				if(i < 64)
				{
					doubleflag = true;
					k = i;
					onedrawi = i;
				}
			}
			if(tradeflag >= 2 && doubleflag == false)
			{
				int i = 0;
				
				while(i < 64 && (cpucardorder[i] != 57 && cpucardorder[i] != 58))
				{
					i++;
				}

				if(i < 64)
				{
					doubleflag = true;
					k = i;
					onedrawi = i;
				}
			}
			if(shuffleflag >= 2 && doubleflag == false)
			{
				int i = 0;
				
				while(i < 64 && (cpucardorder[i] != 53 && cpucardorder[i] != 54))
				{
					i++;
				}

				if(i < 64)
				{
					doubleflag = true;
					k = i;
					onedrawi = i;
				}
			}
			
			if(doubleflag == false)
			{
				int i = 0;
				
				for(int j = 0; j < 13; j++)
				{
					if(numflag[j] >= 2)
					{
						while(i < 64 && ( cpucardorder[i] > 52 || (cpucardorder[i] % 13 != j) ))
						{
							i++;
						}

						if(i < 64)
						{
							doubleflag = true;
							k = i;
							onedrawi = i;
						}
						else
						{
							i = 0;
						}
					}
				}
			}
			
			if(doubleflag == false)
			{
				randomflag = true;
			}
			
			if(randomflag == true)
			{
				int i = 0;
				
				do
				{
					Random rand = new Random();
					i = rand.nextInt(64);
					System.out.println("手番" + cputurn + "のCPU乱数(1回目):" + i);
				}
				while(cardorder[i] == -1);
				onedrawi = i;
				k = i;
			}
		}
		else if(drawcard == 2)
		{
			if(specialflag >= 2)
			{
				int i = 0;
				
				while(i < 64 && (cpucardorder[i] != 61 && cpucardorder[i] != 62 
						&& cpucardorder[i] != 63 && cpucardorder[i] != 64) || i == onedrawi)
				{
					i++;
				}

				if(i < 64)
				{
					doubleflag = true;
					k = i;
				}
			}
			if(skipflag >= 2 && doubleflag == false)
			{
				int i = 0;
				
				while(i < 64 && (cpucardorder[i] != 59 && cpucardorder[i] != 60) || i == onedrawi)
				{
					i++;
				}

				if(i < 64)
				{
					doubleflag = true;
					k = i;
				}
			}
			if(reverseflag >= 2 && doubleflag == false)
			{
				int i = 0;
				
				while(i < 64 && (cpucardorder[i] != 55 && cpucardorder[i] != 56) || i == onedrawi)
				{
					i++;
				}

				if(i < 64)
				{
					doubleflag = true;
					k = i;
				}
			}
			if(tradeflag >= 2 && doubleflag == false)
			{
				int i = 0;
				
				while(i < 64 && (cpucardorder[i] != 57 && cpucardorder[i] != 58) || i == onedrawi )
				{
					i++;
				}
				
				if(i < 64)
				{
					doubleflag = true;
					k = i;
				}
			}
			if(shuffleflag >= 2 && doubleflag == false)
			{
				int i = 0;
				
				while(i < 64 && (cpucardorder[i] != 53 && cpucardorder[i] != 54) || i == onedrawi)
				{
					i++;
				}

				if(i < 64)
				{
					doubleflag = true;
					k = i;
				}
			}
			
			if(doubleflag == false)
			{
				for(int j = 0; j < 13; j++)
				{
					int i = 0;
					
					if(numflag[j] >= 2)
					{
						while(i < 64 && ( cpucardorder[i] > 52 || (cpucardorder[i] % 13 != j) || i == onedrawi) )
						{
							i++;
						}
						
						if(i < 64)
						{
							doubleflag = true;
							k = i;
						}
						else
						{
							i = 0;
						}
					}
				}
			}
			
			if(doubleflag == false)
			{
				randomflag = true;
			}
			
			if(randomflag == true)
			{
				int i = 0;
				
				do
				{
					Random rand = new Random();
					i = rand.nextInt(64);
					System.out.println("手番" + cputurn + "のCPU乱数(2回目):" + i);
				}
				while(i == onedrawi || cardorder[i] == -1);
				
				k = i;
			}
			onedrawi = 0;
			
		}
		
		if(drawcard == 1)
		{
			System.out.println("手番" + cputurn + "のCPU:draw(1回目) complete");
		}
		else if(drawcard == 2)
		{
			System.out.println("手番" + cputurn + "のCPU:draw(2回目) complete");
		}
		
		return k;
	}
	
	public void cardopen(int i)
	{
		if(cpucardorder[i] == 0)
		{
			MemoryInfo(i);
		}
		
		if(drawcard == 1)
		{
			ss.trycard(i);
			ss.check(ss.addressnum(i));
			drawcard = 2;
		}
		else if(drawcard == 2)
		{
			ss.trycard(i);
			ss.check(ss.addressnum(i));
			
			ForgetInfo();
			
			drawcard = 1;
		}
	}
	
	public int bringPlnum()
	{
		return Plnum;
	}
	
	public void numin(int m, int a)
	{
		num[m] = a;
	}
	
	public void cardorderin(int n, int a)
	{
		cardorder[n] = a;
	}
	
	public void cpucardordershuffle()
	{
		for(int i = 0; i < 64; i++)
		{
			if(cardorder[i] == -1)
			{
				cpucardorder[i] = -1;
			}
			else
			{
				cpucardorder[i] = 0;
			}
		}
		
		for(int j = 0; j < 13; j++)
		{
			numflag[j] = 0;
		}
		shuffleflag = 0;
		reverseflag = 0;
		tradeflag = 0;
		skipflag = 0;
		specialflag = 0;
		
	}
	
	class SS
	{
		int turn=1;
		int card=64;
		boolean Switch = true;	//ターンの流れが正順ならtrue、逆順ならfalse
	    
	    SS()
	    {
	    	
	    }
	    
	    public int addressnum(int i)
	    { //i番目のボタンがnumの何番目に調べる
	    	int k=0,l=-1;
	    	while(k<100)
	    	{
	    		if(num[k]!=0)
	    		{
	    			l++;		
	    		}
	    		if(l==i)
	    		{
	    			break;
	    		}
	    		k++;	
	    	}
	    	return k;
	    }
	    
	    public int addresscardorder(int n)
	    {	//n番目のnumに何番目のボタンがあるか調べる
	    	int k=0,l=-1;
	    	while(k<=n)
	    	{
	    		if(num[k]!=0)
	    		{
	    			l++;
	    		}
	    		k++;
	    	}
	    	return l;
	    	
	    }
	    
	    public int judge(int k,int n)
	    {		//numのk番目とn番目がどれで同じカードか判定
	    	int card1 = cardorder[addresscardorder(k)];
	    	int card2 = cardorder[addresscardorder(n)];
	    	System.out.println("手番" + cputurn + "のCPU:judge1:" + cardorder[addresscardorder(k)]);
	    	System.out.println("手番" + cputurn + "のCPU:judge2:" + cardorder[addresscardorder(n)]);
	    	
	    	if(card1 <= 52 && card2 <= 52) {
	    		if(card1%13 == card2%13) {
	    			return 1;
	    		}else {
	    			return 0;
	    		}
	    	}else if(card1 == 53 || card1 == 54){
	    		if(card2 == 53 || card2 == 54) {
	    			return 2;
	    		}else {
	    			return 0;
	    		}
	    	}else if(card1 == 55 || card1 == 56){
	    		if(card2 == 55 || card2 == 56) {
	    			return 3;
	    		}else {
	    			return 0;
	    		}
	    	}else if(card1 == 57 || card1 == 58){
	    		if(card2 == 57 || card2 == 58) {
	    			return 4;
	    		}else {
	    			return 0;
	    		}
	    	}else if(card1 == 59 || card1 == 60){
	    		if(card2 == 59 || card2 == 60) {
	    			return 5;
	    		}else {
	    			return 0;
	    		}
	    	}else if(card1>=61 && card1<=64) {
	    		if(card2>=61 && card2<=64) {
	    			return 6;
	    		}else {
	    			return 0;
	    		}
	    	}else {
	    		return -1;
	    	}
	    }
	    
	    public int check(int n) {	//めくられているカードがあるか
	    	int k=0;
	    	while(k<100) {
	    		if(num[k] == 2 && k != n) {	//ほかにめくっているカードがあるなら
	    			int J = judge(k,n);
	    			if(J==1) {	//ノーマルカードが一致したとき
	    				pickcard(addresscardorder(k),addresscardorder(n));
	    				cardleft();
	    				return 1;
	    			}else if(J==2){	//シャッフル
	    				pickcard(addresscardorder(k),addresscardorder(n));
	    				cardleft();
	    				return 1;
	    			}else if(J==3){	//リバース
	    				pickcard(addresscardorder(k),addresscardorder(n));
	    				turnswitch();	//手順を反転
	    				cardleft();
	    				return 1;
	    			}else if(J==4){	//手札交換
	    				pickcard(addresscardorder(k),addresscardorder(n));
	    				cardleft();
	    				return 1;
	    			}else if(J==5){	//スキップ
	    				pickcard(addresscardorder(k),addresscardorder(n));
	    				turnadd();
	    				Playeroder(-1);	//手順をスキップ
	    				cardleft();
	    				return 1;
	    				
	    			}else if(J==6){	//スペシャルカード
	    				pickcard(addresscardorder(k),addresscardorder(n));
	    				cardleft();
	    				return 1;
	    			}else {
	    				offcard(addresscardorder(k));
	    				offcard(addresscardorder(n));
	    				turnadd();
	    				Playeroder(0);
	    				return 1;
	    			}
	    		}
	    		k++;
	    	}
	    	System.out.println("手番" + cputurn + "のCPU:check complete");
	    	return 0;
	    }
	    
	    public void turnadd()	//ターンを経過させる
	    {
	    	turn++;
	    	System.out.println("手番" + cputurn + "のCPU:turnadd complete");
	    }
	    
	    public void Playeroder(int skip)	//-1を引数にすればスキップ
	    {
	    	if(Switch==true)
	    	{
	    		if(skip==-1)
	    		{
	    			Plnum = Plnum + 2;
	    			turn++;
	    		}
	    		else
	    		{
	    			Plnum++;
	    		}
	    	}
	    	else if(Switch==false)
	    	{
	    		if(skip==-1)
	    		{
	    			Plnum = Plnum - 2;
	    			turn++;
	    			if(Plnum == -1)
	    			{
	    				Plnum = 3;
	    			}
	    		}
	    		else
	    		{
	    			Plnum--;
	    		}
	    	}
	    	
	    	if(Plnum%4==0)
	    	{
	    		Plnum = 4;
	    	}
	    	else
	    	{
	    		Plnum = Plnum%4;
	    	}
	    }
	    
	    public void cardleft()		//取ったカードを引く
	    {
	    	card = card - 2;
	    }
	    
	    public void turnswitch() 
	    {
	    	if(Switch==true)
	    	{
	    		Switch = false;
	    	}
	    	else if(Switch==false)
	    	{
	    		Switch = true;
	    	}
	    }
	    
	    public void trycard(int i)	//i個目のボタンのカードを裏返す
	    {	
	    	num[addressnum(i)] = 2;	//i個目のボタンの位置nを２にする
	    }
	    
	    public void offcard(int i)	//	i個目のボタンのカードを元に戻す
	    {
	    	num[addressnum(i)] = 1;
	    } 
	    
	    public void pickcard(int i1,int i2)	//そろった２枚のカードをとる
	    {
	    	
	    	for(int j = 0; j < 13; j++)
			{
				if(cpucardorder[i1] != 0 && cpucardorder[i1] % 13 == j && cpucardorder[i1] <= 52)
				{
					numflag[j]--;
				}
			}
			if(cpucardorder[i1] == 53 || cpucardorder[i1] == 54)
			{
				shuffleflag--;
			}
			else if(cpucardorder[i1] == 55 || cpucardorder[i1] == 56)
			{
				reverseflag--;
			}
			else if(cpucardorder[i1] == 57 || cpucardorder[i1] == 58)
			{
				tradeflag--;
			}
			else if(cpucardorder[i1] == 59 || cpucardorder[i1] == 60)
			{
				skipflag--;
			}
			else if(cpucardorder[i1] == 61 || cpucardorder[i1] == 62 || cpucardorder[i1] == 63 || cpucardorder[i1] == 64)
			{
				specialflag--;
			}
			
			for(int j = 0; j < 13; j++)
			{
				if(cpucardorder[i2] != 0 && cpucardorder[i2] % 13 == j && cpucardorder[i2] <= 52)
				{
					numflag[j]--;
				}
			}
			if(cpucardorder[i2] == 53 || cpucardorder[i2] == 54)
			{
				shuffleflag--;
			}
			else if(cpucardorder[i2] == 55 || cpucardorder[i2] == 56)
			{
				reverseflag--;
			}
			else if(cpucardorder[i2] == 57 || cpucardorder[i2] == 58)
			{
				tradeflag--;
			}
			else if(cpucardorder[i2] == 59 || cpucardorder[i2] == 60)
			{
				skipflag--;
			}
			else if(cpucardorder[i2] == 61 || cpucardorder[i2] == 62 || cpucardorder[i2] == 63 || cpucardorder[i2] == 64)
			{
				specialflag--;
			}
			
			cardorder[i1] = -1;
	    	cardorder[i2] = -1;
	    	cpucardorder[i1] = -1;
	    	cpucardorder[i2] = -1;
	    	num[addressnum(i1)] = 3;
	    	num[addressnum(i2)] = 3;
	    	System.out.println("アイコンを消した");
	    }
	    
	    
	}
	
}