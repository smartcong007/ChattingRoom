package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 * 聊天室应用的客户端
 * @author 聪
 * @version v1.0
 */
public class SocketClinet extends Socket{
	private static final String SERVER_IP = "127.0.0.1";
	private static final int SERVER_PORT = 2017;
	
	private PrintWriter pw;
	private BufferedReader br;
	private Socket clinet;
	
	public SocketClinet() throws UnknownHostException, IOException{
		super(SERVER_IP,SERVER_PORT);
		pw = new PrintWriter(this.getOutputStream(),true);
		br = new BufferedReader(new InputStreamReader(this.getInputStream()));
		clinet = this;
		new readThread();
		
		while(true){
			BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
			pw.println(br1.readLine());
		}
	}
	
	/*
	 * 负责从服务端接受消息的线程
	 */
	class readThread extends Thread{
		public readThread(){
				start();
		}
		
		@Override 
		public void run(){
			try{
			while(true){
				String result = br.readLine();
				if("shutdown".equals(result)){
					System.out.println("您已退出了聊天室,再见!");
					break;
				}else {
					System.out.println(result);
				}
			}
			pw.close();
			br.close();
			clinet.close();
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		}
	}
	
	 @SuppressWarnings("resource")
	public static void main(String[] args) {
	        try {
	            new SocketClinet();//新建一个客户端socket
	        }catch (Exception e) {
	        }
	    }
}