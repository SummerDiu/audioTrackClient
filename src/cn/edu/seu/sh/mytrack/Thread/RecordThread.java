package cn.edu.seu.sh.mytrack.Thread;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import cn.edu.seu.sh.mytrack.Config.CommonConfig;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

public class RecordThread  extends Thread {
    private boolean keepRunning = true;
    private AudioRecord audioRecord = null;
    private int minSize = 0;
    private DatagramSocket socket = null;
    int n = 0;

    @Override
    public void run() {
        initSocket();
        initAudio();
        while(keepRunning){
            recodeAndSend();
        }
        release();
    }

    private void initSocket(){
        try {
            socket = new DatagramSocket();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initAudio(){
        minSize = AudioTrack.getMinBufferSize(CommonConfig.freq, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,CommonConfig.freq, AudioFormat.CHANNEL_IN_STEREO,AudioFormat.ENCODING_PCM_16BIT, minSize);
        audioRecord.startRecording();
    }

    private void recodeAndSend(){
        byte [] buffer = new byte[CommonConfig.bufferSize];
        audioRecord.read(buffer,0,buffer.length);

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try{
            InetAddress ip = InetAddress.getByName(CommonConfig.SERVER_IP_ADDRESS.trim());//;;CLIENT_A_IP_ADDRESS
            packet.setAddress(ip);
            packet.setPort(CommonConfig.AUDIO_SERVER_UP_PORT);//;CLIENT_A_PORT
            socket.send(packet);
            if(n++<10){
            	System.out.println("SendPacket:--->"+n+"length:"+packet.getLength()+
            			"content:"+buffer.toString());
            	System.out.println("RecordThread-->Send Audio Packet to "+ip.toString());
            }
            
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stopThread(){
        keepRunning = false;
    }

    private void release(){
        if(socket!=null){
            socket.close();
            socket = null;
        }
        if(audioRecord!=null){
            audioRecord.release();
            audioRecord = null;
        }
    }
}
