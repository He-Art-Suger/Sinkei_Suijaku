import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class server2
{
	private CPU []cpu = new CPU[3];
	private int port; // サーバの待ち受けポート
	private int connectnum = 0;
	private int playercount = 0;
	private int cpunum;
	private int Plnum = 1;
	private int []countroopnum = new int[3];
	{
		countroopnum[0] = 0;
		countroopnum[1] = 0;
		countroopnum[2] = 0;
	}
	private String []playername = new String[4];
	private String []cpuname = new String[4];
	private boolean[] online = new boolean[4]; // 接続状態管理用配列
	private PrintWriter[] out = new PrintWriter[4]; // データ送信用オブジェクト
	private Receiver[] receiver = new Receiver[4]; // データ受信用オブジェクト
	
	//コンストラクタ
	server2(int port)
	{
		this.port = port;
		online[0] = false; 
		online[1] = false;
		online[2] = false;
		online[3] = false;
	}
	
	//情報の送受信を行う内部クラス
	class Receiver extends Thread
	{
		private InputStreamReader sisr; // 受信データ用文字ストリーム
		private BufferedReader br; // 文字ストリーム用のバッファ
		private int TurnNum; // プレイヤ自身を識別するための番号（1～4）

		// 内部クラスReceiverのコンストラクタ
		Receiver(Socket socket, int TurnNum)
		{
			try
			{
				this.TurnNum = TurnNum; // プレイヤを識別するための番号を割り振る（1～4）
				online[TurnNum - 1] = true;
				sisr = new InputStreamReader(socket.getInputStream());
				br = new BufferedReader(sisr);
				System.out.println("クライアント" + TurnNum + " からデータを受信する準備ができました。");

				out[TurnNum - 1].println(TurnNum);
				out[TurnNum - 1].flush();
			}
			catch (IOException e)
			{
				System.err.println("データ受信時にエラーが発生しました: " + e);
				online[TurnNum - 1] = false;
			}
		}

		// 内部クラス Receiverのrunメソッド(start によりスレッドが動く)
		public void run()
		{
			
			try
			{
				String inputLine1;
				boolean try1 = false;
				
				inputLine1 = br.readLine();
				
				if(inputLine1 != null)
				{
					playername[TurnNum - 1] = inputLine1;
					playercount++;
				}
				
				while(try1 == false)
				{
					
					inputLine1 = br.readLine();
					
					if(inputLine1.equals("-2"))//再読み込み
					{
						if(connectnum == 1)
						{
							out[0].println(connectnum);
							out[0].flush();
						}
						else if(connectnum == 2)
						{
							out[0].println(connectnum);
							out[0].flush();
							out[1].println(connectnum);
							out[1].flush();
						}
						else if(connectnum == 3)
						{
							out[0].println(connectnum);
							out[0].flush();
							out[1].println(connectnum);
							out[1].flush();
							out[2].println(connectnum);
							out[2].flush();
						}
						else if(connectnum == 4)
						{
							out[0].println(connectnum);
							out[0].flush();
							out[1].println(connectnum);
							out[1].flush();
							out[2].println(connectnum);
							out[2].flush();
							out[3].println(connectnum);
							out[3].flush();
						}
					}
					else if(inputLine1.equals("0"))//決定
					{
						if(connectnum == 1)
						{
							out[0].println(0);
							out[0].flush();
							cpunum = 3;
						}
						else if(connectnum == 2)
						{
							out[0].println(0);
							out[0].flush();
							out[1].println(0);
							out[1].flush();
							cpunum = 2;
						}
						else if(connectnum == 3)
						{
							out[0].println(0);
							out[0].flush();
							out[1].println(0);
							out[1].flush();
							out[2].println(0);
							out[2].flush();
							cpunum = 1;
						}
						else if(connectnum == 4)
						{
							out[0].println(0);
							out[0].flush();
							out[1].println(0);
							out[1].flush();
							out[2].println(0);
							out[2].flush();
							out[3].println(0);
							out[3].flush();
							cpunum = 0;
						}
						try1 = true;
					}
					else if(inputLine1.equals("hostdecided"))
					{
						try1 = true;
					}
				}
			}
			catch (IOException e)
			{
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
			
			
			
			if(TurnNum == 1 && connectnum != 4)
			{
				try
				{
					String inputLine2;
					
					if(connectnum <= 3)
					{
						inputLine2 = br.readLine();
						
						if(Integer.parseInt(inputLine2) == 101)
						{
							if(cpunum == 1)
							{
								cpu[0] = new CPU(1, 4);
							}
							else if(cpunum == 2)
							{
								cpu[0] = new CPU(1, 3);
							}
							else if(cpunum == 3)
							{
								cpu[0] = new CPU(1, 2);
							}
							cpuname[0] = "CPU1：よわい";
							for(int i = 0; i < connectnum; i++)
							{
								out[i].println(101);
								out[i].flush();
							}
						}
						else if(Integer.parseInt(inputLine2) == 102)
						{
							if(cpunum == 1)
							{
								cpu[0] = new CPU(2, 4);
							}
							else if(cpunum == 2)
							{
								cpu[0] = new CPU(2, 3);
							}
							else if(cpunum == 3)
							{
								cpu[0] = new CPU(2, 2);
							}
							cpuname[0] = "CPU1：ふつう";
							for(int i = 0; i < connectnum; i++)
							{
								out[i].println(102);
								out[i].flush();
							}
						}
						else if(Integer.parseInt(inputLine2) == 103)
						{
							if(cpunum == 1)
							{
								cpu[0] = new CPU(3, 4);
							}
							else if(cpunum == 2)
							{
								cpu[0] = new CPU(3, 3);
							}
							else if(cpunum == 3)
							{
								cpu[0] = new CPU(3, 2);
							}
							cpuname[0] = "CPU1：つよい";
							for(int i = 0; i < connectnum; i++)
							{
								out[i].println(103);
								out[i].flush();
							}
						}
						
					}
					
					if(connectnum <= 2)
					{
						inputLine2 = br.readLine();
						
						if(Integer.parseInt(inputLine2) == 201)
						{
							if(cpunum == 2)
							{
								cpu[1] = new CPU(1, 4);
							}
							else if(cpunum == 3)
							{
								cpu[1] = new CPU(1, 3);
							}
							cpuname[1] = "CPU2：よわい";
							for(int i = 0; i < connectnum; i++)
							{
								out[i].println(201);
								out[i].flush();
							}
						}
						else if(Integer.parseInt(inputLine2) == 202)
						{
							if(cpunum == 2)
							{
								cpu[1] = new CPU(2, 4);
							}
							else if(cpunum == 3)
							{
								cpu[1] = new CPU(2, 3);
							}
							cpuname[1] = "CPU2：ふつう";
							for(int i = 0; i < connectnum; i++)
							{
								out[i].println(202);
								out[i].flush();
							}
						}
						else if(Integer.parseInt(inputLine2) == 203)
						{
							if(cpunum == 2)
							{
								cpu[1] = new CPU(3, 4);
							}
							else if(cpunum == 3)
							{
								cpu[1] = new CPU(3, 3);
							}
							cpuname[1] = "CPU2：つよい";
							for(int i = 0; i < connectnum; i++)
							{
								out[i].println(203);
								out[i].flush();
							}
						}
					}
					
					if(connectnum == 1)
					{
						inputLine2 = br.readLine();
						
						if(Integer.parseInt(inputLine2) == 301)
						{
							cpu[2] = new CPU(1, 4);
							cpuname[2] = "CPU3：よわい";
							for(int i = 0; i < connectnum; i++)
							{
								out[i].println(301);
								out[i].flush();
							}
						}
						else if(Integer.parseInt(inputLine2) == 302)
						{
							cpu[2] = new CPU(2, 4);
							cpuname[2] = "CPU3：ふつう";
							for(int i = 0; i < connectnum; i++)
							{
								out[i].println(302);
								out[i].flush();
							}
						}
						else if(Integer.parseInt(inputLine2) == 303)
						{
							cpu[2] = new CPU(3, 4);
							cpuname[2] = "CPU3：つよい";
							for(int i = 0; i < connectnum; i++)
							{
								out[i].println(303);
								out[i].flush();
							}
						}
					}
					for(int i = 0; i < connectnum; i++)
					{
						out[i].println(-3);
						out[i].flush();
						for(int j = 0; j < playercount; j++)
						{
							out[i].println(playername[j]);
							out[i].flush();
						}
						for(int j = 0; j < cpunum; j++)
						{
							out[i].println(cpuname[j]);
							out[i].flush();
						}
					}
					
					
				}
				catch (IOException e)
				{
					System.err.println("データ受信時にエラーが発生しました: " + e);
				}
			}
			
			try
			{
				String inputLine3;
				boolean try2 = false;
				while (try2 == false)
				{// データを受信し続ける
					
					if(cpunum != 0 && TurnNum == 1)
					{
						Plnum = cpu[0].bringPlnum();
						System.out.println("------------------------------------------------------------");
						System.out.println("現在のターン：" + cpu[0].Plnum);
					}
					
					if(TurnNum == 1) //ホストクライアントに対応
					{
						if(Plnum == TurnNum)
						{
							inputLine3 = br.readLine();// データを一行分読み込む
							
							if (inputLine3 != null)
							{ // データを受信したら
								System.out.println("ホスト" + TurnNum + "から正常メッセージ " + inputLine3 + " が届きました．他のクライアントへ転送します.");
								
								if(inputLine3.equals("start"))
								{
									for(int i = 0; i < connectnum; i++)
									{
										out[i].println("start"); // 受信データを対戦相手クライアントのバッファに書き出す
										out[i].flush(); // 受信データを対戦相手へ転送する
									}
									int m, n, l;
									for(m = 0; m < 100; m++)
									{
										inputLine3 = br.readLine();
										for(int i = 1; i < connectnum; i++)
										{
											out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
											out[i].flush(); // 受信データを対戦相手へ転送する
											for(int j = 0; j < cpunum; j++)
											{
												cpu[j].numin(m, Integer.parseInt(inputLine3));
											}
										}
									}
									for(n = 0; n < 64; n++)
									{
										inputLine3 = br.readLine();
										for(int i = 1; i < connectnum; i++)
										{
											out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
											out[i].flush(); // 受信データを対戦相手へ転送する
											for(int j = 0; j < cpunum; j++)
											{
												cpu[j].cardorderin(n, Integer.parseInt(inputLine3));
											}
										}
									}
									for(l = 0; l < 64; l++)
									{
										inputLine3 = br.readLine();
										for(int i = 1; i < connectnum; i++)
										{
											out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
											out[i].flush(); // 受信データを対戦相手へ転送する
										}
									}
								}
								
								if(inputLine3.equals("GameSet"))
								{	//ゲーム終了のメッセージを受け取ったら
									System.out.println("クライアント" + TurnNum + "からメッセージGameSetが届きました．クライアントとの接続を切ります.");
									online[TurnNum - 1] = false;
									try2 = true;
									br.close();
									sisr.close();
									
									try2 = true;
								}
								
								if(inputLine3.equals("retire"))
								{	//ゲーム終了のメッセージを受け取ったら;
									inputLine3 = br.readLine();
									for(int i = 0; i < connectnum; i++)
									{
										if(i !=Integer.parseInt(inputLine3) - 1)
										{
											out[i].println("retire");
											out[i].flush();
										}
									}
									online[TurnNum - 1] = false;
									try2 = true;
									br.close();
									sisr.close();
									
								}
								
								
								if(inputLine3.equals("normal"))
								{
									for(int a = 0; a < 2; a++)
									{
										System.out.println("normal" + (a + 1) + "回目");
										if(a == 1)
										{
											inputLine3 = br.readLine();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											if(i + 1 != Plnum)
											{
												out[i].println("normal");
												out[i].flush();
											}
										}
										for(int j = 0; j < connectnum; j++)
										{
											if(j + 1 != Plnum)
											{
												out[j].println(Integer.parseInt(inputLine3));
												out[j].flush();
											}
										}
										
										if(cpunum == 1)
										{
											cpu[0].cardopen(Integer.parseInt(inputLine3));
										}
										else if(cpunum == 2)
										{
											cpu[0].cardopen(Integer.parseInt(inputLine3));
											cpu[1].cardopen(Integer.parseInt(inputLine3));
										}
										else if(cpunum == 3)
										{
											cpu[0].cardopen(Integer.parseInt(inputLine3));
											cpu[1].cardopen(Integer.parseInt(inputLine3));
											cpu[2].cardopen(Integer.parseInt(inputLine3));
										}
									}
									
									System.out.println("特殊コマンド");
									inputLine3 = br.readLine();
									
									if(inputLine3.equals("shuffle"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										if(inputLine3.equals("shuffle2"))
										{
											int m, n, l;
											for(m = 0; m < 100; m++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].numin(m, Integer.parseInt(inputLine3));
													}
												}
											}
											for(n = 0; n < 64; n++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].cardorderin(n, Integer.parseInt(inputLine3));
													}
												}
											}
											for(l = 0; l < 64; l++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
												}
											}
										}
									}
									else if(inputLine3.equals("trade"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
									}
									else if(inputLine3.equals("noaction"))
									{
										System.out.println("noaction:ターン" + Plnum);
									}
									Plnum = cpu[0].bringPlnum();
								}
								
							}
						}
						else if(Plnum == 2)
						{
							if(cpunum == 3)// CPUの行動
							{
								Thread.sleep(4000);
								
								int a = cpu[0].draw();
								cpu[0].cardopen(a);
								cpu[1].cardopen(a);
								cpu[2].cardopen(a);
								for(int k = 0; k < connectnum; k++)
								{
									out[k].println("normal");
									out[k].flush();
								}
								for(int j = 0; j < connectnum; j++)
								{
									out[j].println(a);
									out[j].flush();
								}
								
								Thread.sleep(2200);
								
								a = cpu[0].draw();
								cpu[0].cardopen(a);
								cpu[1].cardopen(a);
								cpu[2].cardopen(a);
								for(int k = 0; k < connectnum; k++)
								{
									out[k].println("normal");
									out[k].flush();
								}
								for(int j = 0; j < connectnum; j++)
								{
									out[j].println(a);
									out[j].flush();
								}
								
								inputLine3 = br.readLine();// データを一行分読み込む
								
								if (inputLine3 != null)
								{ // データを受信したら
									System.out.println("クライアント" + TurnNum + "からメッセージ " + inputLine3 + " が届きました．他のクライアントへ転送します.");
									
									
									if(inputLine3.equals("GameSet"))
									{	//ゲーム終了のメッセージを受け取ったら
										System.out.println("クライアント" + TurnNum + "からメッセージGameSetが届きました．クライアントとの接続を切ります.");
										online[TurnNum - 1] = false;
										try2 = true;
										br.close();
										sisr.close();
										
										try2 = true;
									}
									
									if(inputLine3.equals("retire"))
									{	//ゲーム終了のメッセージを受け取ったら;
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											if(i !=Integer.parseInt(inputLine3) - 1)
											{
												out[i].println("retire");
												out[i].flush();
											}
										}
										online[TurnNum - 1] = false;
										try2 = true;
										br.close();
										sisr.close();
										
									}
									
									if(inputLine3.equals("shuffle"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										if(inputLine3.equals("shuffle2"))
										{
											int m, n, l;
											for(m = 0; m < 100; m++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].numin(m, Integer.parseInt(inputLine3));
													}
												}
											}
											for(n = 0; n < 64; n++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].cardorderin(n, Integer.parseInt(inputLine3));
													}
												}
											}
											for(l = 0; l < 64; l++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
												}
											}
										}
									}
									else if(inputLine3.equals("trade"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
									}
									else if(inputLine3.equals("noaction"))
									{
										System.out.println("noaction:ターン" + Plnum);
									}
									
								}
								Plnum = cpu[0].bringPlnum();
							}
							else //他プレーヤーの行動
							{
								inputLine3 = br.readLine();// データを一行分読み込む
								
								if (inputLine3 != null)
								{ // データを受信したら
									System.out.println("クライアント" + TurnNum + "からメッセージ " + inputLine3 + " が届きました．他のクライアントへ転送します.");
									
									
									if(inputLine3.equals("GameSet"))
									{	//ゲーム終了のメッセージを受け取ったら
										System.out.println("クライアント" + TurnNum + "からメッセージGameSetが届きました．クライアントとの接続を切ります.");
										online[TurnNum - 1] = false;
										try2 = true;
										br.close();
										sisr.close();
										
										try2 = true;
									}
									
									if(inputLine3.equals("retire"))
									{	//ゲーム終了のメッセージを受け取ったら;
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											if(i !=Integer.parseInt(inputLine3) - 1)
											{
												out[i].println("retire");
												out[i].flush();
											}
										}
										online[TurnNum - 1] = false;
										try2 = true;
										br.close();
										sisr.close();
										
									}
									
									if(inputLine3.equals("shuffle"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										if(inputLine3.equals("shuffle2"))
										{
											int m, n, l;
											for(m = 0; m < 100; m++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].numin(m, Integer.parseInt(inputLine3));
													}
												}
											}
											for(n = 0; n < 64; n++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].cardorderin(n, Integer.parseInt(inputLine3));
													}
												}
											}
											for(l = 0; l < 64; l++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
												}
											}
										}
									}
									else if(inputLine3.equals("trade"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
									}
									else if(inputLine3.equals("noaction"))
									{
										System.out.println("noaction:ターン" + Plnum);
									}
									
								}
								Plnum = cpu[0].bringPlnum();
							}
							
							
						}
						else if(Plnum == 3)
						{
							if(cpunum == 3)
							{
								Thread.sleep(4000);
								
								int a = cpu[1].draw();
								cpu[0].cardopen(a);
								cpu[1].cardopen(a);
								cpu[2].cardopen(a);
								for(int k = 0; k < connectnum; k++)
								{
									out[k].println("normal");
									out[k].flush();
								}
								for(int j = 0; j < connectnum; j++)
								{
									out[j].println(a);
									out[j].flush();
								}
								
								Thread.sleep(2200);
								
								a = cpu[1].draw();
								cpu[0].cardopen(a);
								cpu[1].cardopen(a);
								cpu[2].cardopen(a);
								for(int k = 0; k < connectnum; k++)
								{
									out[k].println("normal");
									out[k].flush();
								}
								for(int j = 0; j < connectnum; j++)
								{
									out[j].println(a);
									out[j].flush();
								}
								
								inputLine3 = br.readLine();// データを一行分読み込む
								
								if (inputLine3 != null)
								{ // データを受信したら
									System.out.println("クライアント" + TurnNum + "からメッセージ " + inputLine3 + " が届きました．他のクライアントへ転送します.");
									
									
									if(inputLine3.equals("GameSet"))
									{	//ゲーム終了のメッセージを受け取ったら
										System.out.println("クライアント" + TurnNum + "からメッセージGameSetが届きました．クライアントとの接続を切ります.");
										online[TurnNum - 1] = false;
										try2 = true;
										br.close();
										sisr.close();
										
										try2 = true;
									}
									
									if(inputLine3.equals("retire"))
									{	//ゲーム終了のメッセージを受け取ったら;
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											if(i !=Integer.parseInt(inputLine3) - 1)
											{
												out[i].println("retire");
												out[i].flush();
											}
										}
										online[TurnNum - 1] = false;
										try2 = true;
										br.close();
										sisr.close();
										
									}
									
									if(inputLine3.equals("shuffle"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										if(inputLine3.equals("shuffle2"))
										{
											int m, n, l;
											for(m = 0; m < 100; m++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].numin(m, Integer.parseInt(inputLine3));
													}
												}
											}
											for(n = 0; n < 64; n++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].cardorderin(n, Integer.parseInt(inputLine3));
													}
												}
											}
											for(l = 0; l < 64; l++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
												}
											}
										}
									}
									else if(inputLine3.equals("trade"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
									}
									else if(inputLine3.equals("noaction"))
									{
										System.out.println("noaction:ターン" + Plnum);
									}
									
								}
								Plnum = cpu[0].bringPlnum();
							
							}
							else if(cpunum == 2)
							{
								Thread.sleep(4000);
								
								int a = cpu[0].draw();
								System.out.println("ドロー結果" + a);
								cpu[0].cardopen(a);
								cpu[1].cardopen(a);
								for(int k = 0; k < connectnum; k++)
								{
									out[k].println("normal");
									out[k].flush();
								}
								for(int j = 0; j < connectnum; j++)
								{
									out[j].println(a);
									out[j].flush();
								}
								
								Thread.sleep(2200);
								
								a = cpu[0].draw();
								System.out.println("ドロー結果" + a);
								cpu[0].cardopen(a);
								cpu[1].cardopen(a);
								for(int k = 0; k < connectnum; k++)
								{
									out[k].println("normal");
									out[k].flush();
								}
								for(int j = 0; j < connectnum; j++)
								{
									out[j].println(a);
									out[j].flush();
								}
								
								inputLine3 = br.readLine();// データを一行分読み込む
								
								if (inputLine3 != null)
								{ // データを受信したら
									System.out.println("クライアント" + TurnNum + "からメッセージ " + inputLine3 + " が届きました．他のクライアントへ転送します.");
									
									
									if(inputLine3.equals("GameSet"))
									{	//ゲーム終了のメッセージを受け取ったら
										System.out.println("クライアント" + TurnNum + "からメッセージGameSetが届きました．クライアントとの接続を切ります.");
										online[TurnNum - 1] = false;
										try2 = true;
										br.close();
										sisr.close();
										
										try2 = true;
									}
									
									if(inputLine3.equals("retire"))
									{	//ゲーム終了のメッセージを受け取ったら;
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											if(i !=Integer.parseInt(inputLine3) - 1)
											{
												out[i].println("retire");
												out[i].flush();
											}
										}
										online[TurnNum - 1] = false;
										try2 = true;
										br.close();
										sisr.close();
										
									}
									
									if(inputLine3.equals("shuffle"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										if(inputLine3.equals("shuffle2"))
										{
											int m, n, l;
											for(m = 0; m < 100; m++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].numin(m, Integer.parseInt(inputLine3));
													}
												}
											}
											for(n = 0; n < 64; n++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].cardorderin(n, Integer.parseInt(inputLine3));
													}
												}
											}
											for(l = 0; l < 64; l++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
												}
											}
										}
									}
									else if(inputLine3.equals("trade"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
									}
									else if(inputLine3.equals("noaction"))
									{
										System.out.println("noaction:ターン" + Plnum);
									}
									
								}
								Plnum = cpu[0].bringPlnum();
							
							}
							else
							{

								inputLine3 = br.readLine();// データを一行分読み込む
								
								if (inputLine3 != null)
								{ // データを受信したら
									System.out.println("クライアント" + TurnNum + "からメッセージ " + inputLine3 + " が届きました．他のクライアントへ転送します.");
									
									
									if(inputLine3.equals("GameSet"))
									{	//ゲーム終了のメッセージを受け取ったら
										System.out.println("クライアント" + TurnNum + "からメッセージGameSetが届きました．クライアントとの接続を切ります.");
										online[TurnNum - 1] = false;
										try2 = true;
										br.close();
										sisr.close();
										
										try2 = true;
									}
									
									if(inputLine3.equals("retire"))
									{	//ゲーム終了のメッセージを受け取ったら;
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											if(i !=Integer.parseInt(inputLine3) - 1)
											{
												out[i].println("retire");
												out[i].flush();
											}
										}
										online[TurnNum - 1] = false;
										try2 = true;
										br.close();
										sisr.close();
										
									}
									
									if(inputLine3.equals("shuffle"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										if(inputLine3.equals("shuffle2"))
										{
											int m, n, l;
											for(m = 0; m < 100; m++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].numin(m, Integer.parseInt(inputLine3));
													}
												}
											}
											for(n = 0; n < 64; n++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].cardorderin(n, Integer.parseInt(inputLine3));
													}
												}
											}
											for(l = 0; l < 64; l++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
												}
											}
										}
									}
									else if(inputLine3.equals("trade"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
									}
									else if(inputLine3.equals("noaction"))
									{
										System.out.println("noaction:ターン" + Plnum);
									}
									
								}
								Plnum = cpu[0].bringPlnum();
							
							}
						}
						else if(Plnum == 4)
						{
							if(cpunum == 3)
							{
								Thread.sleep(4000);
								
								int a = cpu[2].draw();
								cpu[0].cardopen(a);
								cpu[1].cardopen(a);
								cpu[2].cardopen(a);
								for(int k = 0; k < connectnum; k++)
								{
									out[k].println("normal");
									out[k].flush();
								}
								for(int j = 0; j < connectnum; j++)
								{
									out[j].println(a);
									out[j].flush();
								}
								
								Thread.sleep(2200);
								
								a = cpu[2].draw();
								cpu[0].cardopen(a);
								cpu[1].cardopen(a);
								cpu[2].cardopen(a);
								for(int k = 0; k < connectnum; k++)
								{
									out[k].println("normal");
									out[k].flush();
								}
								for(int j = 0; j < connectnum; j++)
								{
									out[j].println(a);
									out[j].flush();
								}
								
								inputLine3 = br.readLine();// データを一行分読み込む
								
								if (inputLine3 != null)
								{ // データを受信したら
									System.out.println("クライアント" + TurnNum + "からメッセージ " + inputLine3 + " が届きました．他のクライアントへ転送します.");
									
									
									if(inputLine3.equals("GameSet"))
									{	//ゲーム終了のメッセージを受け取ったら
										System.out.println("クライアント" + TurnNum + "からメッセージGameSetが届きました．クライアントとの接続を切ります.");
										online[TurnNum - 1] = false;
										try2 = true;
										br.close();
										sisr.close();
										
										try2 = true;
									}
									
									if(inputLine3.equals("retire"))
									{	//ゲーム終了のメッセージを受け取ったら;
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											if(i !=Integer.parseInt(inputLine3) - 1)
											{
												out[i].println("retire");
												out[i].flush();
											}
										}
										online[TurnNum - 1] = false;
										try2 = true;
										br.close();
										sisr.close();
										
									}
									
									if(inputLine3.equals("shuffle"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										if(inputLine3.equals("shuffle2"))
										{
											int m, n, l;
											for(m = 0; m < 100; m++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].numin(m, Integer.parseInt(inputLine3));
													}
												}
											}
											for(n = 0; n < 64; n++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].cardorderin(n, Integer.parseInt(inputLine3));
													}
												}
											}
											for(l = 0; l < 64; l++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
												}
											}
										}
									}
									else if(inputLine3.equals("trade"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
									}
									else if(inputLine3.equals("noaction"))
									{
										System.out.println("noaction:ターン" + Plnum);
									}
									
								}
								Plnum = cpu[0].bringPlnum();
							
							
							}
							else if(cpunum == 2)
							{
								Thread.sleep(4000);
								
								int a = cpu[1].draw();
								System.out.println("ドロー結果" + a);
								cpu[0].cardopen(a);
								cpu[1].cardopen(a);
								for(int k = 0; k < connectnum; k++)
								{
									out[k].println("normal");
									out[k].flush();
								}
								for(int j = 0; j < connectnum; j++)
								{
									out[j].println(a);
									out[j].flush();
								}
								
								Thread.sleep(2200);
								
								a = cpu[1].draw();
								System.out.println("ドロー結果" + a);
								cpu[0].cardopen(a);
								cpu[1].cardopen(a);
								for(int k = 0; k < connectnum; k++)
								{
									out[k].println("normal");
									out[k].flush();
								}
								for(int j = 0; j < connectnum; j++)
								{
									out[j].println(a);
									out[j].flush();
								}
								
								inputLine3 = br.readLine();// データを一行分読み込む
								
								if (inputLine3 != null)
								{ // データを受信したら
									System.out.println("クライアント" + TurnNum + "からメッセージ " + inputLine3 + " が届きました．他のクライアントへ転送します.");
									
									
									if(inputLine3.equals("GameSet"))
									{	//ゲーム終了のメッセージを受け取ったら
										System.out.println("クライアント" + TurnNum + "からメッセージGameSetが届きました．クライアントとの接続を切ります.");
										online[TurnNum - 1] = false;
										try2 = true;
										br.close();
										sisr.close();
										
										try2 = true;
									}
									
									if(inputLine3.equals("retire"))
									{	//ゲーム終了のメッセージを受け取ったら;
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											if(i !=Integer.parseInt(inputLine3) - 1)
											{
												out[i].println("retire");
												out[i].flush();
											}
										}
										online[TurnNum - 1] = false;
										try2 = true;
										br.close();
										sisr.close();
										
									}
									
									if(inputLine3.equals("shuffle"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										if(inputLine3.equals("shuffle2"))
										{
											int m, n, l;
											for(m = 0; m < 100; m++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].numin(m, Integer.parseInt(inputLine3));
													}
												}
											}
											for(n = 0; n < 64; n++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].cardorderin(n, Integer.parseInt(inputLine3));
													}
												}
											}
											for(l = 0; l < 64; l++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
												}
											}
										}
									}
									else if(inputLine3.equals("trade"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
									}
									else if(inputLine3.equals("noaction"))
									{
										System.out.println("noaction:ターン" + Plnum);
									}
									
								}
								Plnum = cpu[0].bringPlnum();
							
							
							}
							else if(cpunum == 1)
							{
								Thread.sleep(4000);
								
								int a = cpu[0].draw();
								cpu[0].cardopen(a);
								for(int k = 0; k < connectnum; k++)
								{
									out[k].println("normal");
									out[k].flush();
								}
								for(int j = 0; j < connectnum; j++)
								{
									out[j].println(a);
									out[j].flush();
								}
								
								Thread.sleep(2200);
								
								a = cpu[0].draw();
								cpu[0].cardopen(a);
								for(int k = 0; k < connectnum; k++)
								{
									out[k].println("normal");
									out[k].flush();
								}
								for(int j = 0; j < connectnum; j++)
								{
									out[j].println(a);
									out[j].flush();
								}
								
								inputLine3 = br.readLine();// データを一行分読み込む
								
								if (inputLine3 != null)
								{ // データを受信したら
									System.out.println("クライアント" + TurnNum + "からメッセージ " + inputLine3 + " が届きました．他のクライアントへ転送します.");
									
									
									if(inputLine3.equals("GameSet"))
									{	//ゲーム終了のメッセージを受け取ったら
										System.out.println("クライアント" + TurnNum + "からメッセージGameSetが届きました．クライアントとの接続を切ります.");
										online[TurnNum - 1] = false;
										try2 = true;
										br.close();
										sisr.close();
										
										try2 = true;
									}
									
									if(inputLine3.equals("retire"))
									{	//ゲーム終了のメッセージを受け取ったら;
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											if(i !=Integer.parseInt(inputLine3) - 1)
											{
												out[i].println("retire");
												out[i].flush();
											}
										}
										online[TurnNum - 1] = false;
										try2 = true;
										br.close();
										sisr.close();
										
									}
									
									if(inputLine3.equals("shuffle"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										if(inputLine3.equals("shuffle2"))
										{
											int m, n, l;
											for(m = 0; m < 100; m++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].numin(m, Integer.parseInt(inputLine3));
													}
												}
											}
											for(n = 0; n < 64; n++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].cardorderin(n, Integer.parseInt(inputLine3));
													}
												}
											}
											for(l = 0; l < 64; l++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
												}
											}
										}
									}
									else if(inputLine3.equals("trade"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
									}
									else if(inputLine3.equals("noaction"))
									{
										System.out.println("noaction:ターン" + Plnum);
									}
									
								}
								Plnum = cpu[0].bringPlnum();
							
							
							}
							else
							{
								inputLine3 = br.readLine();// データを一行分読み込む
								
								if (inputLine3 != null)
								{ // データを受信したら
									System.out.println("クライアント" + TurnNum + "からメッセージ " + inputLine3 + " が届きました．他のクライアントへ転送します.");
									
									
									if(inputLine3.equals("GameSet"))
									{	//ゲーム終了のメッセージを受け取ったら
										System.out.println("クライアント" + TurnNum + "からメッセージGameSetが届きました．クライアントとの接続を切ります.");
										online[TurnNum - 1] = false;
										try2 = true;
										br.close();
										sisr.close();
										
										try2 = true;
									}
									
									if(inputLine3.equals("retire"))
									{	//ゲーム終了のメッセージを受け取ったら;
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											if(i !=Integer.parseInt(inputLine3) - 1)
											{
												out[i].println("retire");
												out[i].flush();
											}
										}
										online[TurnNum - 1] = false;
										try2 = true;
										br.close();
										sisr.close();
										
									}
									
									if(inputLine3.equals("shuffle"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										if(inputLine3.equals("shuffle2"))
										{
											int m, n, l;
											for(m = 0; m < 100; m++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].numin(m, Integer.parseInt(inputLine3));
													}
												}
											}
											for(n = 0; n < 64; n++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
													for(int j = 0; j < cpunum; j++)
													{
														cpu[j].cardorderin(n, Integer.parseInt(inputLine3));
													}
												}
											}
											for(l = 0; l < 64; l++)
											{
												inputLine3 = br.readLine();
												for(int i = 1; i < connectnum; i++)
												{
													out[i].println(Integer.parseInt(inputLine3)); // 受信データを対戦相手クライアントのバッファに書き出す
													out[i].flush(); // 受信データを対戦相手へ転送する
												}
											}
										}
									}
									else if(inputLine3.equals("trade"))
									{
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											out[i].println(inputLine3);
											out[i].flush();
										}
									}
									else if(inputLine3.equals("noaction"))
									{
										System.out.println("noaction:ターン" + Plnum);
									}
									
								}
								Plnum = cpu[0].bringPlnum();
							
							
							}
						}
						
					}
					else //ホストではないクライアントに対応
					{
						
						if(Plnum == TurnNum && TurnNum + cpunum <= 4)
						{
							
							System.out.println("クライアント" + TurnNum + "の動作");
							inputLine3 = br.readLine();
							if(inputLine3 != null)
							{					
								if(inputLine3.equals("normal"))
								{
									for(int a = 0; a < 2; a++)
									{
										System.out.println("normal" + (a + 1) + "回目");
										if(a == 1)
										{
											inputLine3 = br.readLine();
										}
										inputLine3 = br.readLine();
										for(int i = 0; i < connectnum; i++)
										{
											if(i + 1 != Plnum)
											{
												out[i].println("normal");
												out[i].flush();
											}
										}
										for(int j = 0; j < connectnum; j++)
										{
											if(j + 1 != Plnum)
											{
												out[j].println(Integer.parseInt(inputLine3));
												out[j].flush();
											}
										}
										
										if(cpunum == 1)
										{
											cpu[0].cardopen(Integer.parseInt(inputLine3));
										}
										else if(cpunum == 2)
										{
											cpu[0].cardopen(Integer.parseInt(inputLine3));
											cpu[1].cardopen(Integer.parseInt(inputLine3));
										}
										else if(cpunum == 3)
										{
											cpu[0].cardopen(Integer.parseInt(inputLine3));
											cpu[1].cardopen(Integer.parseInt(inputLine3));
											cpu[2].cardopen(Integer.parseInt(inputLine3));
										}
									}
								}
							}
							
						}
					}
				}
				
			}
			catch(InterruptedException e)
    		{
    			System.err.println("エラーが発生しました。" + e);
    		}
			catch (IOException e)
			{ // 接続が切れたとき
				System.err.println("クライアント" + TurnNum + "との接続が切れました．");
				online[TurnNum - 1] = false;
				// 対戦相手へ文字列 "disconnect" を転送する
				if(TurnNum == 1)
				{
					if(connectnum >= 2)
					{
						out[1].println("disconnect");
						out[1].flush();
					}
					if(connectnum >= 3)
					{
						out[2].println("disconnect");
						out[2].flush();
					}
					if(connectnum == 4)
					{
						out[3].println("disconnect");
						out[3].flush();
					}
				}
				else if(TurnNum == 2)
				{
					out[0].println("disconnect");
					out[0].flush();
					if(connectnum >= 3)
					{
						out[2].println("disconnect");
						out[2].flush();
					}
					if(connectnum == 4)
					{
						out[3].println("disconnect");
						out[3].flush();
					}
				}
				else if(TurnNum == 3)
				{
					out[0].println("disconnect");
					out[0].flush();
					out[1].println("disconnect");
					out[1].flush();
					if(connectnum == 4)
					{
						out[3].println("disconnect");
						out[3].flush();
					}
				}
				else if(TurnNum == 4)
				{
					out[0].println("disconnect");
					out[0].flush();
					out[1].println("disconnect");
					out[1].flush();
					out[2].println("disconnect");
					out[2].flush();
				}
				System.exit(1);
			}
		}
	}
	
	// 2つのクライアントの接続を行う(サーバを起動する)メソッド acceptClient
	public void acceptClient()
	{
		try
		{
			System.out.println("サーバが起動しました．");
			ServerSocket ss = new ServerSocket(port); // サーバソケットを用意
			Socket []socket = new Socket[4];
			
			socket[0] = ss.accept(); // クライアントから新規接続を受け付けるまでプログラムは停止
			System.out.println("クライアント" + 1 + "と接続しました．");
			out[0] = new PrintWriter(socket[0].getOutputStream(), true);// データ送信オブジェクトを用意
			receiver[0] = new Receiver(socket[0], 1);// データ受信オブジェクト(スレッド)を用意
			receiver[0].start();
			connectnum = 1;
			socket[1] = ss.accept();		
			System.out.println("クライアント" + 2 + "と接続しました．");
			out[1] = new PrintWriter(socket[1].getOutputStream(), true);
			receiver[1] = new Receiver(socket[1], 2);
			receiver[1].start();
			connectnum = 2;
			socket[2] = ss.accept(); 
			System.out.println("クライアント" + 3 + "と接続しました．");
			out[2] = new PrintWriter(socket[2].getOutputStream(), true);
			receiver[2] = new Receiver(socket[2], 3);
			receiver[2].start();
			connectnum = 3;
			socket[3] = ss.accept();
			System.out.println("クライアント" + 4 + "と接続しました．");
			out[3] = new PrintWriter(socket[3].getOutputStream(), true);
			receiver[3] = new Receiver(socket[3], 4);
			receiver[3].start();
			connectnum = 4;
			
			receiver[0].join();
			receiver[1].join();
			receiver[2].join();
			receiver[3].join();
			
			socket[0].close();
			socket[1].close();
			socket[2].close();
			socket[3].close();
			
			ss.close();

		}
		catch (Exception e)
		{
			System.err.println("ソケット作成時にエラーが発生しました: " + e);
		}
	}
	
	public static void main(String[] args)
	{
		
		server2 s2 = new server2(10001); // 待ち受けポート10000番でサーバオブジェクトを準備
		s2.acceptClient(); // クライアント受け入れを開始
	}

}