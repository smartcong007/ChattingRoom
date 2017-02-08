package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
public class Server extends ServerSocket{
	private static final int PORT = 2017;
	private List<String> users = new ArrayList<String>();
	private List<ServerThread> thread_list = new ArrayList<ServerThread>();
	private LinkedList<MSG> msgs = new LinkedList<MSG>();
	public Server() throws IOException{
		super(PORT);
		new PushThread();
		try{
			while(true){
				Socket socket = this.accept();
			    new ServerThread(socket);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			close();
		}
	}
	
	public class MSG{
		private String publisher;
		private String message;
	    public MSG(String publisher,String message){
	    	this.publisher = publisher;
	    	this.message = message;
	    }
		public String getPublisher() {
			return publisher;
		}
		public void setPublisher(String publisher) {
			this.publisher = publisher;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		
	}
	
	public class PushThread extends Thread{
		public PushThread(){
			start();
		}
		
		@Override
		public void run(){
			while(true){
				synchronized (msgs) {
					if(msgs.size()>0){
						MSG msg = msgs.pollFirst();
						for(ServerThread t:thread_list){
							if(!msg.getPublisher().equals(t.getName()))
							t.sendmsg(msg.getMessage());
						}
					}
				}
			}
		}
	}
	
	public class ServerThread extends Thread{
		private Socket client;
		private PrintWriter pw;
		private BufferedReader br;
		private String name;
		
		public ServerThread(Socket s) throws IOException{
			client = s;
			pw = new PrintWriter(client.getOutputStream(),true);
			br = new BufferedReader(new InputStreamReader(client.getInputStream()));
			pw.println("成功进入聊天室,请输入你的名字: ");
			start();
		}
		
		@Override
		public void run(){
			try{
			int flag = 0;
			String line;
			while(!"exit".equals(line=br.readLine())){
	        	if(flag++==0){
	        		name = line;
	        		users.add(name);
	        		thread_list.add(this);
	        		pw.println("你好,可以开始聊天了...");
	        	    this.pushmsg("clinet<"+name+">进入了聊天室...");
	        	}else{
	        		this.pushmsg("clinet<"+name+">:"+line);
	        	}
			}
			pw.println("shutdown");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				thread_list.remove(this);
				users.remove(name);
				this.pushmsg("clinet<"+name+">退出了聊天室!");
			}
		}
		 
		 public void sendmsg(String msg){
			 pw.println(msg);
		 }
		 
		 public void send(String msg){
			 System.out.println("clinet<"+name+">推送出了消息!");
			 synchronized (thread_list) {
			 for(ServerThread t:thread_list){
				 if(t!=this){
					 t.sendmsg(msg);
				 }
			 }
			 }
		 }
		 
		 public void pushmsg(String msg){
			 msgs.push(new MSG(this.getName(), msg));
		 }
	}
	
	
	 public static void main(String[] args)throws IOException {
	        new Server();//启动服务端
	    }
}