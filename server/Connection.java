import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class Connection implements Runnable
{
	Socket sock;
	DataInputStream socketIn;
	DataOutputStream socketOut;

	public Connection(Socket s) throws Exception
	{
		sock = s;
		socketIn = new DataInputStream(sock.getInputStream());
		socketOut = new DataOutputStream(sock.getOutputStream());
	}

	public void run()
	{
		try
    	{
    		boolean loop = true;
		    while(loop) 
		    {
		        String name;
				long fileLength;
				long maxBuffer = 4096;
				int iMaxBuffer = 4096;
				int fLength;
				int result;

		        name = socketIn.readUTF();
		        if (name.charAt(0) == '*')
		        {
		        	String dir = System.getProperty("user.dir");
		        	File inFile = new File(dir);
		        	String[] files = inFile.list();
		        	socketOut.writeInt(files.length);
		        	socketOut.flush();
		        	for (int i = 0; i < files.length; i++)
		        	{
		        		socketOut.writeUTF(files[i]);
		        		socketOut.flush();
		        	}
		        }
		        else if (name.charAt(0) == '!')
		        {
		        	loop = false;
		        }
		        else
		        {
		        	try
		        	{
						File inFile = new File(name);
						DataInputStream fileIn = new DataInputStream(new FileInputStream(inFile));
						fileLength = inFile.length();
						socketOut.writeUTF("good");
						socketOut.flush();
						socketOut.writeLong(fileLength);
						socketOut.flush();

						byte[] data = new byte[4096];

						if (fileLength <= maxBuffer)
						{
							fLength = (int) fileLength;
							result = fileIn.read(data, 0, fLength);
							socketOut.write(data, 0, fLength);	
						}
						else
						{
							while (fileLength > maxBuffer)
							{
								result = fileIn.read(data, 0, iMaxBuffer);
								socketOut.write(data, 0, iMaxBuffer);
								fileLength = fileLength - maxBuffer;
							}
							int end = (int) fileLength;
							result = fileIn.read(data, 0, end);
							socketOut.write(data, 0, end);

						}
						fileIn.close();
			
						System.out.println("Sending: " + name);
						socketOut.flush();
					}
					catch(FileNotFoundException e)
					{
						socketOut.writeUTF("bad");
						System.err.println(e);
					}
				}
		    }
        }
        catch(IOException e)
        {
			System.out.println(e);
        }
        finally
        {
        	System.out.println("Closing connection.");
        	try
        	{
		    	socketOut.close();
		    	socketIn.close();
		    	sock.close();
		    }
		    catch (IOException e)
		    {
				System.out.println(e);
		    }
        }
	}
}
