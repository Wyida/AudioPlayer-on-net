import java.io.File;
import java.net.InetAddress;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioReceiveTest implements Runnable
{
	private AudioInputStream audioInputStream;
	private AudioFormat audioFormat;
	private SourceDataLine sourceDataLine;
	private DataLine.Info dataLineInfo;
	private ASoNProtocol NetReceive;

	public void run()//{{{
	{
		try{
			int temp = -1;
			while(true)
			{
				ASoNPacket packet = NetReceive.getData();
				byte[] data = packet.getData();
				int serial = packet.getHeader_serial();
				if(serial != temp + 1)
				{
					System.out.println("====");
					System.out.println(serial);
					System.out.println(serial-temp);
				}
				temp = serial;
				sourceDataLine.write(data, 0, data.length);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}//}}}
	public AudioReceiveTest(int tPort, int Port, String tAddress)//{{{
	{
		try{
			NetReceive = new ASoNProtocol(tPort, Port, InetAddress.getByName(tAddress));
			File file = new File("/liangcheng.mp3");
			audioInputStream = AudioSystem.getAudioInputStream(file);
			audioFormat = audioInputStream.getFormat();
			if(audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED)
			{
				audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,audioFormat.getSampleRate(),
						16,audioFormat.getChannels(),audioFormat.getChannels()*2,audioFormat.getSampleRate(),false);
				audioInputStream = AudioSystem.getAudioInputStream(audioFormat, audioInputStream);
			}
			dataLineInfo = new DataLine.Info(
				SourceDataLine.class, audioFormat,
				AudioSystem.NOT_SPECIFIED);
			sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();
			NetReceive.startWorking();
		}catch(Exception e){ e.printStackTrace(); }
	}//}}}
	public static void main(String args[])//{{{
	{
		AudioReceiveTest ART = new AudioReceiveTest(10010, 10011, "192.168.0.2");
		Thread thread = new Thread(ART);
		thread.start();
	}//}}}
}
