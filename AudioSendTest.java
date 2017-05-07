import java.io.File;
import java.net.InetAddress;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class AudioSendTest implements Runnable
{
	private ASoNProtocol NetSend; 
	private ACoNProtocol NetControl;
	private AudioInputStream audioInputStream;
	private AudioFormat audioFormat;

	public void run()//{{{
	{
		try{
			NetControl.sendCMD_AudioFormat(audioFormat);
			while(true)
			{
				//byte[] readbuf = new byte[2000];//this for long range transmitting
				byte[] readbuf = new byte[320];// this for short range transmitting
				if (audioInputStream.read(readbuf, 0, readbuf.length) == -1)
				{
					System.out.print("Reading Over");
					break;
				}
				NetSend.sendData(readbuf);	
				Thread.sleep(0,10);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}//}}}
	public AudioSendTest(int sPort, int cPort, String tAddress)//{{{
	{
		try{
			NetSend = new ASoNProtocol(sPort, sPort, InetAddress.getByName(tAddress));
			NetControl = new ACoNProtocol(cPort, cPort, InetAddress.getByName(tAddress), null);

			File file = new File("/liangcheng.mp3");
			audioInputStream = AudioSystem.getAudioInputStream(file);
			audioFormat = audioInputStream.getFormat(); 
			if(audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED)
			{
				audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,audioFormat.getSampleRate(),
						16,audioFormat.getChannels(),audioFormat.getChannels()*2,audioFormat.getSampleRate(),false);
				audioInputStream = AudioSystem.getAudioInputStream(audioFormat, audioInputStream);
			}
			NetSend.startWorking();
			NetControl.startWorking();
		}catch(Exception e) { e.printStackTrace(); }
	}//}}}
	public static void main(String args[])//{{{
	{
		//AudioSendTest AST = new AudioSendTest(10010, 10011, "59.71.142.58");
		AudioSendTest AST = new AudioSendTest(10010, 10011, "127.0.0.1");
		Thread thread = new Thread(AST);
		thread.start();	
	}//}}}
}
