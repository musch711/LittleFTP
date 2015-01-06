import java.net.Socket;
import java.io.*;

public class ToyFTClient
{
    public static void main(String[] args)
    {
    	try
    	{
		    Socket sock = new Socket("localhost", 8000);

		    DataOutputStream socketOut = new DataOutputStream(sock.getOutputStream());
			DataInputStream socketIn = new DataInputStream(sock.getInputStream());
		    BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));

			boolean run = true;

		    while(run)
		    {
		    	System.out.print("Please enter a command: ");
				String option = consoleIn.readLine();
				if (option.charAt(0) == '*')
				{
					socketOut.writeUTF(option);
					socketOut.flush();
					int numFiles = socketIn.readInt();
					for (int i = 0; i < numFiles; i++)
						System.out.println(socketIn.readUTF());
				}
				else if (option.charAt(0) == '!')
				{
					socketOut.writeUTF(option);
					socketOut.flush();
					run = false;
				}
				else
				{
					String line;
					long fileLength;
					long maxBuffer = 4096;
					int iMaxBuffer = 4096;
					int result;

				
					socketOut.writeUTF(option);
					socketOut.flush();

					String valid = socketIn.readUTF();
					if (valid.compareTo("good") == 0)
					{
						DataOutputStream fileOut = new DataOutputStream(new FileOutputStream(new File(option)));
						fileLength = socketIn.readLong();
						//System.out.println(fileLength);
						byte[] data = new byte[4096];
				

						if (fileLength <= maxBuffer)
						{
							int fLength = (int) fileLength;
							result = socketIn.read(data, 0, fLength);
							fileOut.write(data, 0, fLength);
						}
						else
						{	
							while (fileLength > maxBuffer)
							{
								result = socketIn.read(data, 0, iMaxBuffer);
								fileOut.write(data, 0, iMaxBuffer);
								fileLength = fileLength - maxBuffer;
							}
							int end = (int) fileLength;
							result = socketIn.read(data, 0, end);
							fileOut.write(data, 0, end);
						}
						System.out.println("Receiving: " + option);
						fileOut.close();
						socketOut.flush();
					}
					else
					{
						System.err.println(option + " is a invalid file name!  Please try again.");
					}
				}
		    }

			socketOut.close();
		    socketIn.close();
        }
        catch (IOException e)
        {
			System.err.println(e);
        }
    }
}

    
