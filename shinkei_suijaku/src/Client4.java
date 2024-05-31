import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client4 extends JFrame implements MouseListener
{
	private Player player;
	private static LoginInterface login;
	private Result result;
	private int numnow = 1;
	private int needcpunum = 3;
	private int cpuinfo[] = new int[3];
	{
		cpuinfo[0] = 0;
		cpuinfo[1] = 0;
		cpuinfo[2] = 0;
	}
	private int[] num = new int[100];
	private int[] cardorder = new int[64];
	{
		for(int i = 0; i < 64; i++)
		{
			cardorder[i] = 0;
		}
		
	}
	private int[] B = new int[64];
	private int[] pt = new int[4];
	{
		pt[0] = 0;
		pt[1] = 0;
		pt[2] = 0;
		pt[3] = 0;
	}
	private String []playername = new String[4];
	private PrintWriter out;// データ送信用オブジェクト
	private Receiver receiver; // データ受信用オブジェクト
	
	//コンストラクタ
	Client4(Player player)
	{
		this.player = player;
		
		// ウィンドウ設定
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// ウィンドウを閉じる場合の処理
		setTitle("神経衰弱ゲーム");// ウィンドウのタイトル（暫定）
		setSize(800, 600);// ウィンドウのサイズを設定（暫定）
		
	}
	
	public class DException extends Exception
	{
		DException()
		{
			System.out.println("相手の接続が切れました。");
		}
	}
	
	// サーバに接続するメソッド
	public boolean connectServer(String ipAddress, int port)
	{
		Socket socket = null;
		try
		{
			socket = new Socket(ipAddress, port); // サーバ(ipAddress, port)に接続
			out = new PrintWriter(socket.getOutputStream(), true); // データ送信用オブジェクトの用意
			// 受信用オブジェクトを用意
			// ここで、ハンデレベルと先手後手情報を受け取る
			receiver = new Receiver(socket);

			// 受信用オブジェクトのスレッドを起動する
			receiver.start();
			
			return true;
		}
		catch(UnknownHostException e)
		{
			System.err.println("ホストのIPアドレスが判定できません: " + e);
			return false;
		}
		catch(IOException e)
		{
			System.err.println("サーバ接続時にエラーが発生しました: " + e);
			return false;
		}
	}
	
	//情報の送受信を行う内部クラス
	class Receiver extends Thread
	{
		private InputStreamReader sisr; // 受信データ用文字ストリーム
		private BufferedReader br; // 文字ストリーム用のバッファ
		
			//コンストラクタ
			Receiver(Socket socket)
			{
				try
				{
					sisr = new InputStreamReader(socket.getInputStream()); // 受信したバイトデータを文字ストリームに
					br = new BufferedReader(sisr);// 文字ストリームをバッファリングする
					
					out.println(player.getName());
					out.flush();
				}
				catch (IOException e)
				{
					System.err.println("データ受信時にエラーが発生しました: " + e);
				}
			}

			// 内部クラス Receiverのrunメソッド
			public void run()
			{

				// 最初は手番を受け取るための無限ループを行う
				try
				{
					// 手番情報を受け取るための無限ループ
					String myturninfo;
					int myturn;
					boolean try1 = false;
					while (try1 == false)
					{
						myturninfo = br.readLine();// 受信データを一行分読み込む
						
						if (myturninfo != null)
						{// データを受信したら
							myturn = Integer.parseInt(myturninfo);
							System.out.println("クライアントは自分の手番が " + myturn + " 番目であると受信しました");
							player.setTurnNum(myturn);
							try1 = true; // 無限ループを抜ける
							System.out.println("try1 == true");
						}

					}
				}
				catch (IOException e)
				{
					System.err.println("データ受信時にエラーが発生しました: " + e);
				}
				
				// 待ちの無限ループを行う
				try
				{
					//無限ループ
					String numinfo;
					System.out.println("現在の人数:" + numnow);
					boolean try2 = false;
					while (try2 == false)
					{
						// 受信データを一行分読み込む
						numinfo = br.readLine();
						if(numinfo.equals("disconnect"))
						{
							throw new DException();
						}
						System.out.println("numinfo:" + numinfo);
						
						if(numinfo != null && Integer.parseInt(numinfo) != 0)
						{// データを受信したら
							numnow = Integer.parseInt(numinfo);
							needcpunum = 4 - numnow;
							System.out.println("Reciever:" + numnow);
						}
						else if(numinfo != null && Integer.parseInt(numinfo) == 0)
						{// データを受信したら
							needcpunum = 4 - numnow;
							try2 = true;
							System.out.println("try2 == true");
							if(player.getTurnNum() != 1)
							{
								System.out.println("hostdecided を送信");
								out.println("hostdecided");
								out.flush();
							}
						}
						else
						{
							System.out.println("null");
						}

					}
				}
				catch (IOException e)
				{
					System.err.println("データ受信時にエラーが発生しました: " + e);
				}
				catch (DException e)
				{
					System.err.println(e.getMessage());
					login.wait.setVisible(false);
					login.connectfail.setVisible(true);
				}
				
				
				
				try
				{
					//無限ループ
					String finallyinfo;
					int cpucount = 0;
					while (true)
					{
						if(needcpunum == 0)
						{
							finallyinfo = br.readLine();
							for(int i = 0; i < 4; i++)
							{
								finallyinfo = br.readLine();
								playername[i] = finallyinfo;
							}
							if(player.getTurnNum() == 1)
							{
								login.wait.setVisible(false);
								login.game = new Game();
			    				login.game.setVisible(true);
							}
							
							break;
						}
						finallyinfo = br.readLine();// 受信データを一行分読み込む
						
						if(finallyinfo.equals("disconnect"))
						{
							throw new DException();
						}
						
						if(finallyinfo != null && Integer.parseInt(finallyinfo) != -3)
						{// データを受信したら
							System.out.println(finallyinfo);
							cpuinfo[cpucount] = Integer.parseInt(finallyinfo);
							cpucount++;
						}
						else if(finallyinfo != null && Integer.parseInt(finallyinfo) == -3)
						{
							for(int i = 0; i < 4; i++)
							{
								finallyinfo = br.readLine();
								playername[i] = finallyinfo;
							}
							
							if(player.getTurnNum() == 1)
							{
								login.choice.setVisible(false);
								login.game = new Game();
			    				login.game.setVisible(true);
							}
							
							break;
						}

					}
				}
				catch (IOException e)
				{
					System.err.println("データ受信時にエラーが発生しました: " + e);
				}
				catch (DException e)
				{
					System.err.println(e.getMessage());
					login.choice.setVisible(false);
					login.connectfail.setVisible(true);
				}

				try
				{
					String inputLine;
					while (true)
					{// データを受信し続ける
						inputLine = br.readLine();// 受信データを一行分読み込む
						//System.out.println(inputLine + "を無限ループ頭に受信しました。");
						if (inputLine != null)
						{// データを受信したら
							
							if(inputLine.equals("disconnect"))
							{
								throw new DException();
							}
							
							if(inputLine.equals("start"))
							{
								System.out.println("startを受信しました。");
								if(player.getTurnNum() != 1)
								{
									int m, n, l;
									for(m = 0; m < 100; m++)
									{
										inputLine = br.readLine();
										num[m] = Integer.parseInt(inputLine);								
									}
									for(n = 0; n < 64; n++)
									{
										inputLine = br.readLine();
										cardorder[n] = Integer.parseInt(inputLine);
									}
									for(l = 0; l < 64; l++)
									{
										inputLine = br.readLine();
										B[l] = Integer.parseInt(inputLine);
									}
									login.wait.setVisible(false);
									login.game = new Game();
				    				login.game.setVisible(true);
								}
							}
							else if(inputLine.equals("shuffle"))
							{
								if(player.getTurnNum() == 1)
								{
									login.game.reshuffle();
								}
								else if(player.getTurnNum() != 1)
								{
									int m, n, l;
									for(m = 0; m < 100; m++)
									{
										inputLine = br.readLine();
										num[m] = Integer.parseInt(inputLine);								
									}
									for(n = 0; n < 64; n++)
									{
										inputLine = br.readLine();
										cardorder[n] = Integer.parseInt(inputLine);
									}
									for(l = 0; l < 64; l++)
									{
										inputLine = br.readLine();
										B[l] = Integer.parseInt(inputLine);
									}
									login.game.reshuffle();
								}
							}
							else if(inputLine.equals("normal"))
							{
								int i;
								inputLine = br.readLine();
								i = Integer.parseInt(inputLine);
								System.out.println("---------------------------------------------------------------------");
								System.out.println("normalを受信しました。");
								System.out.println("i = " + Integer.parseInt(inputLine) + "を受信しました。");
								login.game.panel5.anotheraction(i);
							}
							else if(inputLine.equals("retire"))
							{
								result = new Result("Result");
								login.game.setVisible(false);
								result.setVisible(true);
								br.close();
								sisr.close();
								break;
							}
						}
					}
				}
				catch (IOException e)
				{
					System.err.println("データ受信時にエラーが発生しました: " + e);
				}
				catch (DException e)
				{
					System.err.println(e.getMessage());
					login.game.setVisible(false);
					login.connectfail.setVisible(true);
				}
			}
	}
	
	// 内部クラスLogin
	class LoginInterface extends JFrame
	{
		
		Match match;
		Rules rules;
		Rulecheck rulecheck;
		Wait wait;
		Choice choice;
		Connectfail connectfail;
		Game game;

		String serverIP;
		int port;

		// 内部クラスLoginのコンストラクタ
		LoginInterface(String serverIP, int port)
		{
			// 引数を与える
			this.serverIP = serverIP;
			this.port = port;
			
			match = new Match("Match");
			rules = new Rules("Rules");
			connectfail = new Connectfail("Connectfail");
		}
		
		class Match extends JFrame
		{
			
		    JLabel label;
		    JTextField tf = new JTextField(16);
		    JLabel label2 = new JLabel("");
		    JButton b2 = new JButton("ログイン");
		    JButton b1 = new JButton("遊び方");

		    Match(String title)
		    {
		            super(title);
		            JPanel c = (JPanel)getContentPane();
		            
		            c.setLayout(null);
		            
		            c.setBackground(new Color(34,139,34));
		            
		            label = new JLabel("神経衰弱");
		            label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 60)));
		            label.setBounds(250,100,300,60);
		            label.setHorizontalAlignment(JLabel.CENTER);
		            c.add(label);
		            
		            label = new JLabel("名前を入力してください");
		            label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 15)));
		            label.setBounds(200,240,400,40);
		            label.setHorizontalAlignment(JLabel.CENTER);
		            c.add(label);
		            
		            
		            tf.setBounds(230,300,300,30);
		            tf.setHorizontalAlignment(JTextField.CENTER);
		            c.add(tf);
		            
		            
		            b1.setBounds(340,400,80,30);
		            b1.addActionListener(new myListener1());
		            c.add(b1);
		            
		            
		            b2.setBounds(330,450,100,30);
		            b2.addActionListener(new myListener2());
		            c.add(b2);
		            
		            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		            setVisible(true);
		            setSize(800, 600);
		    }
		    
		    public class myListener1 implements ActionListener
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		            setVisible(false);
		            rules.setVisible(true);
		        }
		    }
		    
		    public class myListener2 implements ActionListener
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		String cmd = tf.getText(); // テキストフィールドに入力されている文字列を取得

					// テキストフィールドに何も入力されていない場合
					if (cmd.equals("") == true)
					{
						// 専用のメッセージを表示
						label.setForeground(Color.red);
						label.setText("ログイン名が入力されていません");
					}
					// テキストフィールドに名前が入力されている場合
					else
					{
						player.setName(cmd);
						boolean cs = connectServer(serverIP, port);
						// サーバに繋がらなかった場合
						if(cs == false)
						{
							label.setForeground(Color.red);
							label.setText("接続に失敗しました。もう一度ログインしてください。");
						}
						else
						{
							//myturn = player.getTurnNum();
							rulecheck = new Rulecheck("Rulecheck");
							setVisible(false);
							rulecheck.setVisible(true);
						}
					}
		        }
		    }
		}
		
		class Rules extends JFrame
		{
			
		    JLabel label;
		    JButton b1 = new JButton("ログイン画面に戻る");

		    Rules(String title)
		    {
		            super(title);
		            JPanel c = (JPanel)getContentPane();
		            
		            c.setLayout(null);
		            
		            c.setBackground(new Color(34,139,34));
		            
		            label = new JLabel("遊び方（ルール説明）");
		            label.setBounds(150,40,550,100);
		            label.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 50));
		            label.setHorizontalAlignment(JLabel.CENTER);
		            label.setForeground(Color.white);
		            c.add(label);
		            
		            label = new JLabel("<html>ここに特殊カードの説明を入れる。<br>"
		            		+ "2枚揃うと機能が発動する。");
		            label.setBounds(200,100,500,200);
		            label.setFont(new Font("ＭＳ ゴシック", Font.PLAIN, 20));
		            label.setForeground(Color.white);
		            c.add(label);
		            
		            
		            label = new JLabel("<html>・シャッフルカード – カードの位置を入れ替える<br>"
		            		+ "・スペシャルカード – 得点が〇倍になる<br>"
		            		+ "・リバースカード – 対戦の順序が逆回りになる<br>"
		            		+ "・スキップカード – 次の人を飛ばして対戦が進む<br>"
		            		+ "・持ちカード交換 – 取得カードを交換する<br>"
		            		+ "");
		            label.setBounds(200,250,400,200);
		            label.setFont(new Font("ＭＳ ゴシック", Font.PLAIN, 17));
		            label.setHorizontalAlignment(JLabel.CENTER);
		            label.setForeground(Color.white);
		            c.add(label);
		            
		            
		            b1.setBounds(250,450,300,50);
		            b1.setFont(new Font("ＭＳ ゴシック", Font.PLAIN, 20));
		            b1.addActionListener(new myListener1());
		            c.add(b1);
		            
		            
		            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		            setSize(800, 600);
		    }
		    
		    public class myListener1 implements ActionListener
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		setVisible(false);
		            match.setVisible(true);
		        }
		    }
		    
		}
		
		class Rulecheck extends JFrame
		{
			
		    JLabel label;
		    JButton b1 = new JButton("次へ");

		    Rulecheck(String title)
		    {
		            super(title);
		            JPanel c = (JPanel)getContentPane();
		            
		            c.setLayout(null);
		            
		            c.setBackground(new Color(34,139,34));
		            
		            label = new JLabel("遊び方（ルール説明）");
		            label.setBounds(150,40,550,100);
		            label.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 50));
		            label.setHorizontalAlignment(JLabel.CENTER);
		            label.setForeground(Color.white);
		            c.add(label);
		            
		            label = new JLabel("<html>ここに特殊カードの説明を入れる。<br>"
		            		+ "2枚揃うと機能が発動する。");
		            label.setBounds(200,100,500,200);
		            label.setFont(new Font("ＭＳ ゴシック", Font.PLAIN, 20));
		            label.setForeground(Color.white);
		            c.add(label);
		            
		            
		            label = new JLabel("<html>・シャッフルカード – カードの位置を入れ替える<br>"
		            		+ "・スペシャルカード – 得点が〇倍になる<br>"
		            		+ "・リバースカード – 対戦の順序が逆回りになる<br>"
		            		+ "・スキップカード – 次の人を飛ばして対戦が進む<br>"
		            		+ "・持ちカード交換 – 取得カードを交換する<br>"
		            		+ "");
		            label.setBounds(200,250,400,200);
		            label.setFont(new Font("ＭＳ ゴシック", Font.PLAIN, 17));
		            label.setHorizontalAlignment(JLabel.CENTER);
		            label.setForeground(Color.white);
		            c.add(label);
		            
		            
		            b1.setBounds(250,450,300,50);
		            b1.setFont(new Font("ＭＳ ゴシック", Font.PLAIN, 20));
		            b1.addActionListener(new myListener1());
		            c.add(b1);
		            
		            
		            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		            setSize(800, 600);
		    }
		    
		    public class myListener1 implements ActionListener
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		wait = new Wait("Wait");
		    		setVisible(false);
		            wait.setVisible(true);
		        }
		    }
		    
		}

		public class Wait extends JFrame
		{
			
			JLabel label;
			JLabel labelnum;
			
			JButton b1 = new JButton("始める");
			JButton b2 = new JButton("再読込");
			
			public Wait(String title)
			{
				super(title);
				
				JPanel c = (JPanel)getContentPane();
				
				c.setLayout(null);
				
				c.setBackground(new Color (34, 139, 34));
				
				label = new JLabel("神経衰弱");
				label.setBounds(270, 50, 260, 150);
				label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 60)));
				c.add(label);
				
				labelnum = new JLabel("現在の接続人数:"+ numnow +"人");
				labelnum.setBounds(250, 180, 300, 100);
				labelnum.setHorizontalAlignment(JLabel.CENTER);
				labelnum.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 30)));
				c.add(labelnum);
				labelnum.setVisible(true);
				
				label = new JLabel("対戦相手を探しています");
				label.setBounds(200, 270, 400, 100);
				label.setHorizontalAlignment(JLabel.CENTER);
				label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 30)));
				c.add(label);
				
				
				
				System.out.println("yourNum = " + player.getTurnNum());
				if(player.getTurnNum() == 1)
				{
					label = new JLabel("あなたはホストです。このまま追加メンバーを待ちますか？");
					label.setBounds(100, 320, 600, 100);
					label.setHorizontalAlignment(JLabel.CENTER);
					label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 20)));
					c.add(label);
					
					b1.setBounds(350, 430, 100, 50);
					b1.addActionListener(new myListener1());
					c.add(b1);
				}
				
				b2.setBounds(360, 500, 80, 30);
				b2.addActionListener(new myListener2());
				c.add(b2);
				
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				setSize(800, 600);
				
				
			}
			
			public void reload()
			{
				labelnum.setVisible(false);
				labelnum = new JLabel("現在の接続人数:"+ numnow +"人");
				labelnum.setBounds(250, 180, 300, 100);
				labelnum.setHorizontalAlignment(JLabel.CENTER);
				labelnum.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 30)));
				add(labelnum);
				labelnum.setVisible(true);
				System.out.println(numnow);
			}
			
			public class myListener1 implements ActionListener
			{
				public void actionPerformed(ActionEvent e)
				{
					out.println(0);
					out.flush();
					if(needcpunum != 0)
					{
						choice = new Choice("Choice");
						setVisible(false);
						choice.setVisible(true);
					}
		    		
		    		
				}
			}
			
			public class myListener2 implements ActionListener
			{
				public void actionPerformed(ActionEvent e)
				{
					out.println(-2);
					out.flush();
					reload();
				}
			}

		}
		
		class Choice extends JFrame
		{
			boolean []bflag = new boolean[9];
		    JLabel label;
		    JLabel label2 = new JLabel("");
		    JButton []b1 = new JButton[3];
		    {
		    	b1[0] = new JButton("弱い");
		    	b1[1] = new JButton("弱い");
		    	b1[2] = new JButton("弱い");
		    }
		    JButton []b2 = new JButton[3];
		    {
			    b2[0] = new JButton("ふつう?");
				b2[1] = new JButton("ふつう?");
				b2[2] = new JButton("ふつう?");
		    }
		    JButton []b3 = new JButton[3];
		    {
			    b3[0] = new JButton("強い");
				b3[1] = new JButton("強い");
				b3[2] = new JButton("強い");
		    }
		    JButton b4 = new JButton("決定");

		    Choice(String title)
		    {
		            super(title);
		            JPanel c = (JPanel)getContentPane();
		            
		            c.setLayout(null);
		            c.setBackground(new Color(34,139,34));
		            
		            label = new JLabel("CPUのレベルを決めてください");
		            label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 40))); 
		            label.setBounds(120,80,600,40);
		            c.add(label);
		            
		            bflag[0] = true;
		            bflag[1] = false;
		            bflag[2] = false;
		            bflag[3] = true;
		            bflag[4] = false;
		            bflag[5] = false;
		            bflag[6] = true;
		            bflag[7] = false;
		            bflag[8] = false;
		            
		            int i;
		            if(needcpunum >= 1)
		            {
		            	for(i = 0; i < needcpunum; i++)
			            {
				            label = new JLabel("CPU" + (i + 1));
				            label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 40))); 
				            label.setBounds(130,200+(i)*80,300,40);
				            c.add(label);
				            
				            b1[i].setBounds(260,200+(i)*80,80,40);
				            b1[i].setForeground(Color.black);
				            if(i == 0)
				            {
				            	b1[i].addActionListener(new myListener0());
				            }
				            else if(i == 1)
				            {
				            	b1[i].addActionListener(new myListener3());
				            }
				            else if(i == 2)
				            {
				            	b1[i].addActionListener(new myListener6());
				            }
				            c.add(b1[i]);
				            
				            b2[i].setBounds(390,200+(i)*80,80,40);
				            b2[i].setForeground(Color.gray);
				            if(i == 0)
				            {
				            	b2[i].addActionListener(new myListener1());
				            }
				            else if(i == 1)
				            {
				            	b2[i].addActionListener(new myListener4());
				            }
				            else if(i == 2)
				            {
				            	b2[i].addActionListener(new myListener7());
				            }
				            c.add(b2[i]);
				            
				            b3[i].setBounds(520,200+(i)*80,80,40);
				            b3[i].setForeground(Color.gray);
				            if(i == 0)
				            {
				            	b3[i].addActionListener(new myListener2());
				            }
				            else if(i == 1)
				            {
				            	b3[i].addActionListener(new myListener5());
				            }
				            else if(i == 2)
				            {
				            	b3[i].addActionListener(new myListener8());
				            }
				            c.add(b3[i]);
			            }
		            }
		            
		            b4.setBounds(350,480,100,40);
		            b4.addActionListener(new myListener());
		            c.add(b4);
		            
		            
		            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		            setSize(800, 600);
		    }

		    public class myListener implements ActionListener
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		if(needcpunum == 1)
		    		{
		    			if(bflag[0] == true && bflag[1] == false && bflag[2] == false)//CPU1：弱い
		    			{
		    				out.println(101);
		    				out.flush();
		    			}
		    			else if(bflag[0] == false && bflag[1] == true && bflag[2] == false)//CPU1：ふつう
		    			{
		    				out.println(102);
		    				out.flush();
		    			}
		    			else if(bflag[0] == false && bflag[1] == false && bflag[2] == true)//CPU1：強い
		    			{
		    				out.println(103);
		    				out.flush();
		    			}
	    				
		    		}
		    		else if(needcpunum == 2)
		    		{
		    			if(bflag[0] == true && bflag[1] == false && bflag[2] == false)//CPU1：弱い
		    			{
		    				out.println(101);
		    				out.flush();
		    			}
		    			else if(bflag[0] == false && bflag[1] == true && bflag[2] == false)//CPU1：ふつう
		    			{
		    				out.println(102);
		    				out.flush();
		    			}
		    			else if(bflag[0] == false && bflag[1] == false && bflag[2] == true)//CPU1：強い
		    			{
		    				out.println(103);
		    				out.flush();
		    			}
		    			
		    			if(bflag[3] == true && bflag[4] == false && bflag[5] == false)//CPU2：弱い
		    			{
		    				out.println(201);
		    				out.flush();
		    			}
		    			else if(bflag[3] == false && bflag[4] == true && bflag[5] == false)//CPU2：ふつう
		    			{
		    				out.println(202);
		    				out.flush();
		    			}
		    			else if(bflag[3] == false && bflag[4] == false && bflag[5] == true)//CPU2：強い
		    			{
		    				out.println(203);
		    				out.flush();
		    			}
		    		}
		    		else if(needcpunum == 3)
		    		{
		    			if(bflag[0] == true && bflag[1] == false && bflag[2] == false)//CPU1：弱い
		    			{
		    				out.println(101);
		    				out.flush();
		    			}
		    			else if(bflag[0] == false && bflag[1] == true && bflag[2] == false)//CPU1：ふつう
		    			{
		    				out.println(102);
		    				out.flush();
		    			}
		    			else if(bflag[0] == false && bflag[1] == false && bflag[2] == true)//CPU1：強い
		    			{
		    				out.println(103);
		    				out.flush();
		    			}
		    			
		    			if(bflag[3] == true && bflag[4] == false && bflag[5] == false)//CPU2：弱い
		    			{
		    				out.println(201);
		    				out.flush();
		    			}
		    			else if(bflag[3] == false && bflag[4] == true && bflag[5] == false)//CPU2：ふつう
		    			{
		    				out.println(202);
		    				out.flush();
		    			}
		    			else if(bflag[3] == false && bflag[4] == false && bflag[5] == true)//CPU2：強い
		    			{
		    				out.println(203);
		    				out.flush();
		    			}
		    			
		    			if(bflag[6] == true && bflag[7] == false && bflag[8] == false)//CPU3：弱い
		    			{
		    				out.println(301);
		    				out.flush();
		    			}
		    			else if(bflag[6] == false && bflag[7] == true && bflag[8] == false)//CPU3：ふつう
		    			{
		    				out.println(302);
		    				out.flush();
		    			}
		    			else if(bflag[6] == false && bflag[7] == false && bflag[8] == true)//CPU3：強い
		    			{
		    				out.println(303);
		    				out.flush();
		    			}
		    		}
		        }
		    }
		    
		    public class myListener0 implements ActionListener
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		b1[0].setForeground(Color.black);
		    		b2[0].setForeground(Color.gray);
		    		b3[0].setForeground(Color.gray);
		    		
		    		bflag[0] = true;
		    		bflag[1] = false;
		    		bflag[2] = false;
		        }
		    }
		    
		    public class myListener1 implements ActionListener
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		b1[0].setForeground(Color.gray);
		    		b2[0].setForeground(Color.black);
		    		b3[0].setForeground(Color.gray);
		    		
		    		bflag[0] = false;
		    		bflag[1] = true;
		    		bflag[2] = false;
		        }
		    }
		    
		    public class myListener2 implements ActionListener
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		b1[0].setForeground(Color.gray);
		    		b2[0].setForeground(Color.gray);
		    		b3[0].setForeground(Color.black);
		    		
		    		bflag[0] = false;
		    		bflag[1] = false;
		    		bflag[2] = true;
		        }
		    }
		    
		    public class myListener3 implements ActionListener
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		b1[1].setForeground(Color.black);
		    		b2[1].setForeground(Color.gray);
		    		b3[1].setForeground(Color.gray);
		    		
		    		bflag[3] = true;
		    		bflag[4] = false;
		    		bflag[5] = false;
		        }
		    }
		    
		    public class myListener4 implements ActionListener
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		b1[1].setForeground(Color.gray);
		    		b2[1].setForeground(Color.black);
		    		b3[1].setForeground(Color.gray);
		    		
		    		bflag[3] = false;
		    		bflag[4] = true;
		    		bflag[5] = false;
		        }
		    }
		    
		    public class myListener5 implements ActionListener
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		b1[1].setForeground(Color.gray);
		    		b2[1].setForeground(Color.gray);
		    		b3[1].setForeground(Color.black);
		    		
		    		bflag[3] = false;
		    		bflag[4] = false;
		    		bflag[5] = true;
		        }
		    }
		    
		    public class myListener6 implements ActionListener
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		b1[2].setForeground(Color.black);
		    		b2[2].setForeground(Color.gray);
		    		b3[2].setForeground(Color.gray);
		    		
		    		bflag[6] = true;
		    		bflag[7] = false;
		    		bflag[8] = false;
		        }
		    }
		    
		    public class myListener7 implements ActionListener
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		b1[2].setForeground(Color.gray);
		    		b2[2].setForeground(Color.black);
		    		b3[2].setForeground(Color.gray);
		    		
		    		bflag[6] = false;
		    		bflag[7] = true;
		    		bflag[8] = false;
		        }
		    }
		
		    public class myListener8 implements ActionListener
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		b1[2].setForeground(Color.gray);
		    		b2[2].setForeground(Color.gray);
		    		b3[2].setForeground(Color.black);
		    		
		    		bflag[6] = false;
		    		bflag[7] = false;
		    		bflag[8] = true;
		        }
		    }
		    
		}
		
		class Connectfail extends JFrame
		{
		    JLabel label;
		    JButton b = new JButton("OK");

		    
		    Connectfail(String title)
		    {
		        super(title);
		        JPanel c = (JPanel)getContentPane();
		        
		        c.setLayout(null);
		        
		        c.setBackground(new Color(34,139,34));
		        
		        label = new JLabel("神経衰弱");
		        label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 60)));
	            label.setBounds(250,100,300,60);
	            label.setHorizontalAlignment(JLabel.CENTER);
		        c.add(label);
		        
		        label = new JLabel("接続が切断されました。");
		        label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 20)));
	            label.setBounds(250,200,300,60);
	            label.setHorizontalAlignment(JLabel.CENTER);
		        c.add(label);
		        
		        label = new JLabel("ログイン画面に戻ります。");
		        label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 20)));
	            label.setBounds(250,260,300,60);
	            label.setHorizontalAlignment(JLabel.CENTER);
		        c.add(label);
		        
		        b.setBounds(350,400,100,50);
		        b.addActionListener(new myListener1());
		        c.add(b);
		        
		        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		        setSize(800, 600);
		    }
		
		    
		    public class myListener1 implements ActionListener
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		setVisible(false);
		    		match.setVisible(true);
		        }
		    }
		}
		
		
	}
		
	class Result extends JFrame
	{
		JLabel label;
	    JButton b1 = new JButton("終了");

	    
	    Result(String title)
	    {
	        super(title);
	        JPanel c = (JPanel)getContentPane();
            
	        int i = 0;
	        boolean rankflag = false;
	        int []rank = new int[4];
	        
	        rank[0] = pt[0];
	        
	        if(pt[1] > rank[0])
	        {
	        	rank[1] = rank[0];
	        	rank[0] = pt[1];
	        }
	        else
	        {
	        	rank[1] = pt[1];
	        }
	        
	        if(pt[2] > rank[0])
	        {
	        	rank[2] = rank[1];
	        	rank[1] = rank[0];
	        	rank[0] = pt[2];
	        }
	        else if(pt[2] <= rank[0] && pt[2] > rank[1])
	        {
	        	rank[2] = rank[1];
	        	rank[1] = pt[2];
	        }
	        else
	        {
	        	rank[2] = pt[2];
	        }
	        
	        if(pt[3] > rank[0])
	        {
	        	rank[3] = rank[2];
	        	rank[2] = rank[1];
	        	rank[1] = rank[0];
	        	rank[0] = pt[3];
	        }
	        else if(pt[3] <= rank[0] && pt[3] > rank[1])
	        {
	        	rank[3] = rank[2];
	        	rank[2] = rank[1];
	        	rank[1] = pt[3];
	        }
	        else if(pt[3] <= rank[1] && pt[3] > rank[2])
	        {
	        	rank[3] = rank[2];
	        	rank[2] = pt[3];
	        }
	        else
	        {
	        	rank[3] = pt[3];
	        }
	        
            c.setLayout(null);
            c.setBackground(new Color(34,139,34));
            
            label = new JLabel(playername[player.getTurnNum() - 1]);
            label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 40)));
            label.setBounds(200,40,400,40);
            label.setHorizontalAlignment(JLabel.CENTER);
            c.add(label);
            
            while(rankflag == false)
            {
            	if(rank[i] == pt[player.getTurnNum() - 1])
            	{
            		label = new JLabel("あなたは"+ (i + 1) +"位です!");
    	            label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 40)));
    	            label.setBounds(200,90,400,40);
    	            label.setHorizontalAlignment(JLabel.CENTER);
    	            c.add(label);
    	            rankflag = true;
            	}
            	i++;
            }
            
            label = new JLabel(playername[0] + "：" + pt[0] + "pt");
            label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 30)));
            label.setBounds(200,150,400,40);
            c.add(label);
            
            label = new JLabel(playername[1] + "：" + pt[1] + "pt");
            label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 30)));
            label.setBounds(200,200,400,40);
            c.add(label);
            
            label = new JLabel(playername[2] + "：" + pt[2] + "pt");
            label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 30)));
            label.setBounds(200,250,400,40);
            c.add(label);
            
            label = new JLabel(playername[3] + "：" + pt[3] + "pt");
            label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 30)));
            label.setBounds(200,300,400,40);
            c.add(label);
            
            b1.setBounds(360,480,80,40);
            b1.addActionListener(new myListener1());
            c.add(b1);
            
            
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(800, 600);
	    }
	
	    
	    public class myListener1 implements ActionListener
	    {
	    	public void actionPerformed(ActionEvent e)
	    	{
	    		setVisible(false);
	    		login.match.setVisible(true);
	        }
	    }
	}	  			
	
	
	class Game extends JFrame 		//ゲーム画面全体の管理
	{	
		GameUI panel1;
		RuleUI panel2;
		PlayerUI player1;
		PlayerUI player2;
		PlayerUI player3;
		PlayerUI player4;
		JPanel panel4;
		Board panel5;
		
		Container container;
		
		public Game(){
			
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setTitle("Game");
			//JPanel panel = new Game("a");//ボタンを配置したパネルを生成
			container = this.getContentPane();// Frameに入れる入れ物取得
			this.setLayout(null);
			
			panel1 = new GameUI(30);		//ゲーム情報(引数残り時間*残してあるだけ)
			panel1.setBounds(820,160,150,350);
			panel1.setOpaque(false);
			container.add(panel1);
			
			panel2 = new RuleUI();				//遊び方,ゲームルール
			panel2.setBounds(815,530,150,120);
			panel2.setOpaque(false);
			container.add(panel2);
			
			
			player1 = new PlayerUI(0,1);		//プレイヤー1情報
			player1.setBounds(50,540,150,100);
			//player1.setBackground(new Color(210,87,87));
			player1.setOpaque(false);
			container.add(player1);
			
			player2 = new PlayerUI(0,2);		//プレイヤー2情報
			player2.setBounds(250,540,150,100);
			//player2.setBackground(new Color(87,180,210));
			player2.setOpaque(false);
			container.add(player2);
			
			player3 = new PlayerUI(0,3);		//プレイヤー3情報
			player3.setBounds(450,540,150,100);
			//player3.setBackground(new Color(134,87,210));
			player3.setOpaque(false);
			container.add(player3);
			
			player4 = new PlayerUI(0,4);		//プレイヤー4情報
			player4.setBounds(650,540,150,100);
			//player4.setBackground(new Color(215,217,80));
			player4.setOpaque(false);
			container.add(player4);
		    
		    ImageIcon background3=new ImageIcon("tex6.png");	//プレイヤー１
		    Image img3=background3.getImage();
		    Image temp3=img3.getScaledInstance(200,120,Image.SCALE_SMOOTH);
		    background3=new ImageIcon(temp3);
		    JLabel back3=new JLabel(background3);
		    back3.setLayout(null);
		    back3.setBounds(0,535,200,120);
		    container.add(back3);
		    
		    ImageIcon background4=new ImageIcon("tex7.png");	//プレイヤー２
		    Image img4=background4.getImage();
		    Image temp4=img4.getScaledInstance(200,120,Image.SCALE_SMOOTH);
		    background4=new ImageIcon(temp4);
		    JLabel back4=new JLabel(background4);
		    back4.setLayout(null);
		    back4.setBounds(200,535,200,120);
		    container.add(back4);
		    
		    ImageIcon background5=new ImageIcon("tex8.png");	//プレイヤー３
		    Image img5=background5.getImage();
		    Image temp5=img5.getScaledInstance(200,120,Image.SCALE_SMOOTH);
		    background5=new ImageIcon(temp5);
		    JLabel back5=new JLabel(background5);
		    back5.setLayout(null);
		    back5.setBounds(400,535,200,120);
		    container.add(back5);
		    
		    ImageIcon background6=new ImageIcon("tex9.png");	//プレイヤー４
		    Image img6=background6.getImage();
		    Image temp6=img6.getScaledInstance(200,120,Image.SCALE_SMOOTH);
		    background6=new ImageIcon(temp6);
		    JLabel back6=new JLabel(background6);
		    back6.setLayout(null);
		    back6.setBounds(600,535,200,120);
		    container.add(back6);
		    
		    
		    ImageIcon background7=new ImageIcon("tex10.png");	//ゲーム情報拝啓
		    Image img7=background7.getImage();
		    Image temp7=img7.getScaledInstance(150,350,Image.SCALE_SMOOTH);
		    background7=new ImageIcon(temp7);
		    JLabel back7=new JLabel(background7);
		    back7.setLayout(null);
		    back7.setBounds(815,160,150,350);
		    container.add(back7);
		    
		    /*
		    ImageIcon background2=new ImageIcon("tex13.jpg");	//全体背景
		    Image img2=background2.getImage();
		    Image temp2=img2.getScaledInstance(1000,700,Image.SCALE_SMOOTH);
		    background2=new ImageIcon(temp2);
		    JLabel back2=new JLabel(background2);
		    back2.setLayout(null);
		    back2.setBounds(0,0,1000,700);
		    container.add(back2);
			*/
			
			if(player.getTurnNum() == 1)
	    	{
				generata(num,cardorder);
				
				out.println("start");
		    	out.flush();
				
		    	for(int i=0;i<100;i++)
		    	{
		    		out.println(num[i]);
			    	out.flush();
		    	}
		    	
		    	for(int i=0;i<64;i++)
		    	{
		    		out.println(cardorder[i]);
			    	out.flush();
		    	} 
		    	
		    	for(int i = 0; i < 64; i++)
		    	{
		    		Random rand = new Random();
		    		B[i] = rand.nextInt(25);
		    		out.println(B[i]);
			    	out.flush();
		    	}
		    	
		    	panel5= new Board();	//盤面の描画
				panel5.setBackground(new Color(102,153,51));
				panel5.setBounds(0,5,800,530);
				container.add(panel5);
	    	}
			else
			{
				panel5= new Board();	//盤面の描画
				panel5.setBackground(new Color(102,153,51));
				panel5.setBounds(0,5,800,530);
				container.add(panel5);
			}
			
			this.setBounds(0, 0, 1000, 700);
		}
		
		public void generata(int a[],int b[]) {
			int n=0;
			while(n<64) {
		    	Random rand = new Random();
		        int A = rand.nextInt(100);
		        if(a[A]==0) {	
		        	a[A] = 1;
		        	n++;
		        }
	    	}
	    	
	    	for(n=0;n<64;n++) 
	    	{
	    		b[n] = n+1;
	    	}
	    	shuffle(b);
		}
		
		public void shuffle(int[] array) {
	        // 配列が空か１要素ならシャッフルしようがないのので、そのままreturn
	        if (array.length <= 1) {
	            return;
	        }

	        // Fisher–Yates shuffle
	        Random rnd = ThreadLocalRandom.current();
	        for (int i = array.length - 1; i > 0; i--) {
	            int index = rnd.nextInt(i + 1);
	            // 要素入れ替え(swap)
	            int tmp = array[index];
	            array[index] = array[i];
	            array[i] = tmp;
	        }
	    }
		
		public void reshuffle()
		{
			if(player.getTurnNum() == 1)
	    	{	
				shuffle(num);
		    	shuffle(cardorder);
				
				out.println("shuffle2");
		    	out.flush();
				
		    	for(int i=0;i<100;i++)
		    	{
		    		out.println(num[i]);
			    	out.flush();
		    	}
		    	
		    	for(int i=0;i<64;i++)
		    	{
		    		out.println(cardorder[i]);
			    	out.flush();
		    	} 
		    	
		    	for(int i = 0; i < 64; i++)
		    	{
		    		Random rand = new Random();
		    		B[i] = rand.nextInt(25);
		    		out.println(B[i]);
			    	out.flush();
		    	}
		    	
		    	panel5.alldelete();
		    	panel5.relocate();
	    	}
			else
			{
				panel5.alldelete();
		    	panel5.relocate();
			}
		}
		
		
		
		class Board extends JPanel implements ActionListener{
			
		    int count = 0;
		    Image img = Toolkit.getDefaultToolkit().getImage("back.png");
		    JButton []labels = new JButton[64];
			int turn=1;
			int Plnum=1;
			int card=64;
			boolean Switch=true;	//ターンの流れが正順ならtrue、逆順ならfalse
		    
		    Board() {	//画像の配置と、トランプ裏面の描画
		    	
		    	this.setLayout(null);  
		    	
		    	int f = 0;
		    	
		    	for(int n=1;n<=10;n++)
		    	{
		    		for(int m=1;m<=10;m++)
		    		{
				        if(num[f]==1)
				        {    
				        	labels[count] = new JButton();
				        	labels[count].setIcon(new ImageIcon(img));
				        	
			            	labels[count].setBounds((m-1)*80+B[count],(n-1)*50+B[count],39,53);
				            this.add(labels[count]);
				            labels[count].addActionListener(this);
				            count++;	//ボタン番号
				        }
				        f++;	//100配列探索用
		    		}
		        }
		    	//repaint();
		    	
		        this.setVisible(true);
		    }
		    
		    public void actionPerformed(ActionEvent e){		//各ボタンのイベント処理
		    	if(player.getTurnNum() == Plnum)
		    	{
		    		Object obj=e.getSource();
		    		for(int i= 0; i<64 ;i++){	
			    		if(obj == labels[i]){//i個目のボタンに関する処理;
			    			out.println("normal");
			    			out.flush();
			    			System.out.println("normal を送信");
			    			out.println(i);
			    			out.flush();
			    			trycard(i);
			    			check(addressnum(i));
			    			System.out.println("カード位置" + i + "、カード番号" + cardorder[i]);
			    			if(cardorder[i] <= 52)
			    			{
			    				System.out.println("カード" + (cardorder[i] % 13) + "が押されました。");
			    			}
			    			else
			    			{
			    				System.out.println("特殊カードが押されました。");
			    			}
			    			
			    		}
			    	}
		    	}
		    }
		    
		    public void anotheraction(int i)
		    {
		    	if(player.getTurnNum() != Plnum)
		    	{
		    		System.out.println("anotheraction" + Plnum);
	    			trycard(i);
	    			check(addressnum(i));
		    	}
		    	
		    }
		    
		    public int addressnum(int i) { //i番目のボタンがnumの何番目に調べる
		    	int k=0,l=-1;
		    	while(k<100) {
		    		if(num[k]!=0) {
		    			l++;		
		    		}
		    		if(l==i) {
		    			break;
		    		}
		    		k++;	
		    	}
		    	return k;
		    }
		    
		    public int addresscardorder(int n) {	//n番目のnumに何番目のボタンがあるか調べる
		    	int k=0,l=-1;
		    	while(k<=n) {
		    		if(num[k]!=0) {
		    			l++;
		    		}
		    		k++;
		    	}
		    	return l;
		    	
		    }
		    
		    public int judge(int k,int n) {		//numのk番目とn番目がどれで同じカードか判定
		    	int card1 = cardorder[addresscardorder(k)];
		    	int card2 = cardorder[addresscardorder(n)];
		    	System.out.println("プレイヤー" + player.getTurnNum() + "のjudge1:" + cardorder[addresscardorder(k)]);
		    	System.out.println("プレイヤー" + player.getTurnNum() + "のjudge2:" + cardorder[addresscardorder(n)]);
		    	
		    	if(card1 <= 52 && card2 <= 52){
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
		    				Sleep2 sleep2 = new Sleep2(addresscardorder(k),addresscardorder(n));
		    				sleep2.start();
		    				pointadd(2);
		    				System.out.println("ノーマルカード");
		    				if(player.getTurnNum() == 1)
		    				{
		    					out.println("noaction");
			    				out.flush();
			    				if(player.getTurnNum() == 1 && numnow == 4)
			        			{
			        				out.println(Plnum);
			        				out.flush();
			        			}
		    				}
		    				cardleft();
		    				return 1;
		    			}else if(J==2){	//シャッフル
		    				pickcard(addresscardorder(k),addresscardorder(n));
		    				pointadd(2);
		    				System.out.println("シャッフル");
		    				if(player.getTurnNum() == 1)
		    				{
		    					out.println("shuffle");
			    				out.flush();
		    				}
		    				cardleft();
		    				return 1;
		    			}else if(J==3){	//リバース
		    				Sleep2 sleep2 = new Sleep2(addresscardorder(k),addresscardorder(n));
		    				sleep2.start();
		    				pointadd(2);
		    				System.out.println("リバース");
		    				if(player.getTurnNum() == 1)
		    				{
		    					out.println("noaction");
			    				out.flush();
			    				if(player.getTurnNum() == 1 && numnow == 4)
			        			{
			        				out.println(Plnum);
			        				out.flush();
			        			}
		    				}
		    				turnswitch();	//手順を反転
		    				cardleft();
		    				return 1;
		    			}else if(J==4){	//手札交換
		    				Sleep2 sleep2 = new Sleep2(addresscardorder(k),addresscardorder(n));
		    				sleep2.start();
		    				pchange();
		    				pointadd(2);
		    				if(player.getTurnNum() == 1)
		    				{
		    					out.println("noaction");
			    				out.flush();
			    				if(player.getTurnNum() == 1 && numnow == 4)
			        			{
			        				out.println(Plnum);
			        				out.flush();
			        			}
		    				}
		    				System.out.println("手札交換");
		    				cardleft();
		    				return 1;
		    			}else if(J==5){	//スキップ
		    				Sleep2 sleep2 = new Sleep2(addresscardorder(k),addresscardorder(n));
		    				sleep2.start();
		    				pointadd(2);
		    				System.out.println("スキップ");
		    				turnadd();
		    				Playeroder(-1);	//手順をスキップ
		    				if(player.getTurnNum() == 1)
		    				{
		    					out.println("noaction");
			    				out.flush();
			    				if(player.getTurnNum() == 1 && numnow == 4)
			        			{
			        				out.println(Plnum);
			        				out.flush();
			        			}
		    				}
		    				cardleft();
		    				return 1;
		    				
		    			}else if(J==6){	//スペシャルカード
		    				Sleep2 sleep2 = new Sleep2(addresscardorder(k),addresscardorder(n));
		    				sleep2.start();
		    				pointadd(4);	//4ptゲット
		    				System.out.println("スペシャルカード");
		    				if(player.getTurnNum() == 1)
		    				{
		    					out.println("noaction");
			    				out.flush();
			    				if(player.getTurnNum() == 1 && numnow == 4)
			        			{
			        				out.println(Plnum);
			        				out.flush();
			        			}
		    				}
		    				cardleft();
		    				return 1;
		    			}else {
		    				Sleep sleepk = new Sleep(addresscardorder(k));
		    				Sleep sleepn = new Sleep(addresscardorder(n));
		    				sleepk.start();
		    				sleepn.start();
		    				Sleepturnadd sleepturnadd = new Sleepturnadd();
		    				sleepturnadd.start();
		    				Playeroder(0);
		    				if(player.getTurnNum() == 1)
		    				{
		    					out.println("noaction");
			    				out.flush();
			    				if(player.getTurnNum() == 1 && numnow == 4)
			        			{
			        				out.println(Plnum);
			        				out.flush();
			        			}
		    				}
		    				return 1;
		    			}
		    			
		    		}
		    		k++;
		    	}
		    	System.out.println("check complete");
		    	return 0;
		    }
		    
		    public void turnadd()	//ターンを経過させる
		    {
		    	turn++;
		    	panel1.turnUpdate(turn);
		    	System.out.println("turnadd complete");
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
		    	
				panel1.PlnumUpdata(Plnum);
		    }
		    
		    public void cardleft()		//取ったカードを引く
		    {
		    	card = card - 2;
				panel1.cardUpdata(card);
				if(card == 0)
				{
					out.println("GameSet");
					out.flush();
					result = new Result("Result");
					login.game.setVisible(false);
					result.setVisible(true);
					
				}
		    }
		    
		    public void pointadd(int n) //引数nは獲得ポイント
		    {
		    	if(Plnum==1) {
		    		pt[0] = pt[0] + n;
		    		player1.ptUpdata(pt[0]);
		    	}else if(Plnum==2) {
		    		pt[1] = pt[1] + n;
		    		player2.ptUpdata(pt[1]);
		    	}else if(Plnum==3) {
		    		pt[2] = pt[2] + n;
		    		player3.ptUpdata(pt[2]);
		    	}else if(Plnum==4) {
		    		pt[3] = pt[3] + n;
		    		player4.ptUpdata(pt[3]);
		    	}
		    	System.out.println("ポイント増やした");
		    }
		    
		    public void turnswitch() 
		    {
		    	if(Switch==true) {
		    		Switch = false;
		    	}else if(Switch==false) {
		    		Switch = true;
		    	}
		    }
		    
		    public void trycard(int i)	//i個目のボタンのカードを裏返す
		    {	
		    	num[addressnum(i)] = 2;	//i個目のボタンの位置nを２にする
		    	String image_name = "torannpu-illust" + String.valueOf(cardorder[i]) + ".png";
				ImageIcon icon = new ImageIcon(image_name);
				labels[i].setIcon(icon);
		    }
		    
		    public class Sleep extends Thread
		    {
		    	int i;
		    	
		    	Sleep(int i)
		    	{
		    		this.i = i; 
		    	}
		    	
		    	public void run()
		    	{
		    		try
		    		{
		    			Thread.sleep(2000);
		    			offcard(i);
		    		}
		    		catch(InterruptedException e)
		    		{
		    			System.err.println("エラーが発生しました。" + e);
		    		}
		    	}
		    }
		    
		    public class Sleep2 extends Thread
		    {
		    	int i, j;
		    	
		    	Sleep2(int i, int j)
		    	{
		    		this.i = i;
		    		this.j = j;
		    	}
		    	
		    	public void run()
		    	{
		    		try
		    		{
		    			Thread.sleep(2000);
		    			pickcard(i, j);
		    		}
		    		catch(InterruptedException e)
		    		{
		    			System.err.println("エラーが発生しました。" + e);
		    		}
		    	}
		    }
		    
		    public class Sleepturnadd extends Thread
		    {
		    	
		    	Sleepturnadd()
		    	{
		    		
		    	}
		    	
		    	public void run()
		    	{
		    		try
		    		{
		    			Thread.sleep(2000);
		    			turnadd();
		    		}
		    		catch(InterruptedException e)
		    		{
		    			System.err.println("エラーが発生しました。" + e);
		    		}
		    	}
		    }
		    
		    public void offcard(int i)	//	i個目のボタンのカードを元に戻す
		    {	
		    	labels[i].setIcon(new ImageIcon(img));
		    	num[addressnum(i)] = 1;
		    	//System.out.println("アイコンを戻した");
		    	//System.out.println(num[addressnum(i)]);
		    } 
		    
		    public void pickcard(int i1,int i2)	//そろった２枚のカードをとる
		    {	
		    	SwingUtilities.invokeLater(new Runnable() {
		    	    @Override
		    	    public void run() {
		    	        labels[i1].setIcon(new ImageIcon(img));
		    	        labels[i2].setIcon(new ImageIcon(img));
		    	        repaint();
		    	    }
		    	});
		    	this.remove(labels[i1]);
		    	this.remove(labels[i2]);
		    	cardorder[i1] = -1;
		    	cardorder[i2] = -1;
		    	num[addressnum(i1)] = 3;
		    	num[addressnum(i2)] = 3;
		    	System.out.println("アイコンを消した");
		    }
		    
		    public void delete(int i)	//i番目のボタンを消す
		    {
		    	/*SwingUtilities.invokeLater(new Runnable() {
		    	    @Override
		    	    public void run() {
		    	        labels[i].setIcon(new ImageIcon(img));
		    	        repaint();
		    	    }
		    	});*/
		    	this.remove(labels[i]);
		    }
		    
		    public void alldelete()	//ボタンの全削除
		    {
		    	for(int i=0;i<64;i++)
		    	{
		    		delete(i);
		    	}
		    }
		    
		    public void relocate()	//ボタンの再配置
		    {
		    	count = 0;
		    	int f=0;
		    	
		    	for(int n=1;n<=10;n++)
		    	{
		    		for(int m=1;m<=10;m++)
		    		{
		    			if(num[f]==1)	//１のある所に再配置
				        {   
				        	while(cardorder[count]==-1) {	//-1は取ったカード
				        		count++;
				        	}
		    				
		    				if(cardorder[count]!=-1)	//撮ったカードでないなら
				        	{
		    					labels[count] = new JButton();
					        	String image_name = "torannpu-illust" + String.valueOf(cardorder[count]) + ".png";
								ImageIcon icon = new ImageIcon(image_name);
								labels[count].setIcon(icon);
					        	
				            	labels[count].setBounds((m-1) * 80 + B[count], (n-1) * 50 + B[count], 39, 53);
					            this.add(labels[count]);
					            labels[count].addActionListener(new myListener());
					            repaint();
					            count++;
				        	}
				        }
				        f++;	//100配列探索用
		    		}
		    		
		    		
		        }
		    }
		    
		    public void pchange()
		    {
		    	int minute = Calendar.getInstance().get(Calendar.MINUTE);//分
		    	int pc = minute % 4;
		    	int n;
		    	
		    	System.out.println("pc = " + pc);
		    	
		    	if((pc + 1) == Plnum) //一致しているならば。ptが1つ後の人と交換
		    	{
		    		if(Plnum == 4)
		    		{
		    			n = pt[0];
			    		pt[0] = pt[Plnum - 1];
			    		pt[Plnum - 1] = n;
		    		}
		    		else
		    		{
		    			n = pt[Plnum];
			    		pt[Plnum] = pt[Plnum - 1];
			    		pt[Plnum - 1] = n;
		    		}
		    	}
		    	else
		    	{
		    		n = pt[pc];
		    		pt[pc] = pt[Plnum - 1];
		    		pt[Plnum - 1] = n;
		    	}
		    	
		    }
		    
		    public class myListener implements ActionListener
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{		//各ボタンのイベント処理
			    	if(player.getTurnNum() == Plnum)
			    	{
			    		Object obj=e.getSource();
			    		for(int i= 0; i<64 ;i++){	
				    		if(obj == labels[i]){//i個目のボタンに関する処理;
				    			out.println("normal");
				    			out.flush();
				    			System.out.println("normal を送信");
				    			out.println(i);
				    			out.flush();
				    			trycard(i);
				    			check(addressnum(i));
				    			System.out.println("カード位置" + i + "、カード番号" + cardorder[i]);
				    			if(cardorder[i] <= 52)
				    			{
				    				System.out.println("カード" + (cardorder[i] % 13) + "が押されました。");
				    			}
				    			else
				    			{
				    				System.out.println("特殊カードが押されました。");
				    			}
				    			
				    		}
				    	}
			    	}
			    }
		    }
		    
		}
		
		
		class GameUI extends JPanel		//ゲーム情報のUI
		{
			JLabel labelturn;
			JLabel labelPlnum;
			JLabel labelcard;
			
			public GameUI(int leftsec) {
				
				GridLayout layout = new GridLayout(3,1);
				this.setLayout(layout);
				
				layout.setVgap(5);//垂直方向間隔
				layout.setVgap(20);//水平方向の間隔
		        
		        String turn1 = String.valueOf(1);
		        labelturn = new JLabel("ターン"+turn1);
		        this.add(labelturn);
		        
		        labelPlnum = new JLabel(playername[0] + "の番です");
		        this.add(labelPlnum);
		        
		        
		        String card = String.valueOf(64);
		        labelcard = new JLabel("カード残り"+card+"枚です");
		        this.add(labelcard);
		        
			}
			
			public void turnUpdate(int turn)
			{
				labelturn.setText("ターン"+turn);
			}
			
			public void PlnumUpdata(int Planum)
			{
				labelPlnum.setText(playername[Planum - 1] + "の番です");
			}
			
			public void cardUpdata(int card)
			{
				labelcard.setText("カード残り"+card+"枚です");
			}
			
		}

		class PlayerUI extends JPanel		//プレイヤー情報のUI
		{
			JLabel labelpt;
			
			public PlayerUI(int pt,int order) {
				
				GridLayout layout = new GridLayout(3,1);
				this.setLayout(layout);
				
				layout.setVgap(5);//垂直方向間隔
				layout.setVgap(20);//水平方向の間
				
				String Playernum = String.valueOf(order);
		        JLabel labelPlanum = new JLabel("Player"+Playernum);
		        this.add(labelPlanum);
		        
		        JLabel labelname = new JLabel(playername[order - 1]);
		        this.add(labelname);
		        
		        String pt1 = String.valueOf(pt);
		        labelpt = new JLabel(pt1+"pt");
		        this.add(labelpt);
		        
				
			}
			
			public void ptUpdata(int pt)
			{
				labelpt.setText(pt+"pt");
			}
		}
				
				
				

		class RuleUI extends JPanel			//ルール情報のUI
		{
			JButton btn0 = new JButton("遊び方");
			JButton btn1 = new JButton("途中終了");
		    
			Rulecheck2 rulecheck2 = new Rulecheck2("Rulecheck2");
			Retire retire = new Retire("Retire");
			
		    public RuleUI(){
		    	
		    	GridLayout layout = new GridLayout(2,1);
				this.setLayout(layout);
				
				layout.setVgap(5);//垂直方向間隔
				layout.setVgap(20);//水平方向の間隔
		    	
		    	this.add(btn0);
		        btn0.addActionListener(new myListener1());
		        
		        this.add(btn1);
		        btn1.addActionListener(new myListener2());
		    }
		    
		    public class myListener1 implements ActionListener
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		rulecheck2.setVisible(true);
		    	}
		    }

		    public class myListener2 implements ActionListener
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		retire.setVisible(true);
		    	}
		    }
		}
		
	}


	class Rulecheck2 extends JFrame
	{
		
	    JLabel label;
	    JButton b1 = new JButton("OK");

	    Rulecheck2(String title)
	    {
	            super(title);
	            JPanel c = (JPanel)getContentPane();
	            
	            c.setLayout(null);
	            
	            c.setBackground(new Color(34,139,34));
	            
	            label = new JLabel("遊び方（ルール説明）");
	            label.setBounds(150,40,550,100);
	            label.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 50));
	            label.setHorizontalAlignment(JLabel.CENTER);
	            label.setForeground(Color.white);
	            c.add(label);
	            
	            label = new JLabel("<html>ここに特殊カードの説明を入れる。<br>"
	            		+ "2枚揃うと機能が発動する。");
	            label.setBounds(200,100,500,200);
	            label.setFont(new Font("ＭＳ ゴシック", Font.PLAIN, 20));
	            label.setForeground(Color.white);
	            c.add(label);
	            
	            
	            label = new JLabel("<html>・シャッフルカード – カードの位置を入れ替える<br>"
	            		+ "・スペシャルカード – 得点が〇倍になる<br>"
	            		+ "・リバースカード – 対戦の順序が逆回りになる<br>"
	            		+ "・スキップカード – 次の人を飛ばして対戦が進む<br>"
	            		+ "・持ちカード交換 – 取得カードを交換する<br>"
	            		+ "");
	            label.setBounds(200,250,400,200);
	            label.setFont(new Font("ＭＳ ゴシック", Font.PLAIN, 17));
	            label.setHorizontalAlignment(JLabel.CENTER);
	            label.setForeground(Color.white);
	            c.add(label);
	            
	            
	            b1.setBounds(250,450,300,50);
	            b1.setFont(new Font("ＭＳ ゴシック", Font.PLAIN, 20));
	            b1.addActionListener(new myListener1());
	            c.add(b1);
	            
	            
	            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	            setSize(800, 600);
	    }
	    
	    public class myListener1 implements ActionListener
	    {
	    	public void actionPerformed(ActionEvent e)
	    	{
	    		setVisible(false);
	        }
	    }
	    
	}	    
		    
	class Retire extends JFrame
	{
	    JLabel label;
	    JButton b1 = new JButton("はい");
	    JButton b2 = new JButton("いいえ");
	    
	    Retire(String title)
	    {
	        super(title);
	        JPanel c = (JPanel)getContentPane();
	        
	        c.setLayout(null);
	        
	        c.setBackground(new Color(34,139,34));
	        
	        label = new JLabel("神経衰弱");
	        label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 60)));
            label.setBounds(250,100,300,60);
            label.setHorizontalAlignment(JLabel.CENTER);
	        c.add(label);
	        
	        label = new JLabel("途中終了して結果画面を");
	        label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 20)));
            label.setBounds(250,200,300,60);
            label.setHorizontalAlignment(JLabel.CENTER);
	        c.add(label);
	        
	        label = new JLabel("表示しますか？");
	        label.setFont((new Font(Font.DIALOG_INPUT, Font.BOLD, 20)));
            label.setBounds(250,260,300,60);
            label.setHorizontalAlignment(JLabel.CENTER);
	        c.add(label);
	        
	        b1.setBounds(250,400,100,50);
	        b1.addActionListener(new myListener1());
	        c.add(b1);
	        
	        b2.setBounds(450,400,100,50);
	        b2.addActionListener(new myListener2());
	        c.add(b2);
	        
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setSize(800, 600);
	    }
	
	    
	    public class myListener1 implements ActionListener
	    {
	    	public void actionPerformed(ActionEvent e)
	    	{
	    		out.println("retire");
				out.flush();
				out.println(player.getTurnNum());
				out.flush();
	    		result = new Result("Result");
				login.game.setVisible(false);
				setVisible(false);
				result.setVisible(true);
	        }
	    }
	    
	    public class myListener2 implements ActionListener
	    {
	    	public void actionPerformed(ActionEvent e)
	    	{
	    		setVisible(false);
	        }
	    }
	}
	
	// マウスクリック時の処理
	public void mouseClicked(MouseEvent e)
	{
	}
	
	public void mouseEntered(MouseEvent e)
	{
		
	}// マウスがオブジェクトに入ったときの処理

	public void mouseExited(MouseEvent e)
	{
		
	}// マウスがオブジェクトから出たときの処理

	public void mousePressed(MouseEvent e)
	{
		
	}// マウスでオブジェクトを押したときの処理

	public void mouseReleased(MouseEvent e)
	{
		
	}// マウスで押していたオブジェクトを離したときの処理
	
	

	public static void main(String[] args)
	{
		// プレイヤオブジェクトの用意
		Player player = new Player();

		// クライアントオブジェクトの用意
		Client4 oclient = new Client4(player); // 引数としてオセロオブジェクト、プレイヤオブジェクトを渡す

		// Login オブジェクトの用意
		login = oclient.new LoginInterface(args[0], 10001);// サーバのIPアドレス、ポート番号を与える
	}

}
