import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class ToyFTServer
{
    public static void main(String[] args) throws Exception
    {
    	System.setProperty("line.separator", "\r\n");
		ServerSocket serverSock = new ServerSocket(8000);
		while (true)
		{
			Socket sock = null;
			try 
			{
				sock = serverSock.accept();
				System.out.println("Connection accepted!");
				Runnable r = new Connection(sock);
				Thread t = new Thread(r);
				t.start();
			}
			catch(Exception e)
			{
				System.out.println(e);
				if (sock != null)
					sock.close();
			}
		}	
    }	
}

    
