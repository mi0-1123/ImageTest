package com.example.tajimasachiko.imagetest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.sql.BatchUpdateException;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements Runnable, View.OnClickListener {

    private ImageView itemImage;

    //ここからコピペ
    /* tag */
    private static final String TAG = "BluetoothSample";

    /* Bluetooth Adapter */
    private BluetoothAdapter mAdapter;

    /* Bluetoothデバイス */
    private BluetoothDevice mDevice;

    /* Bluetooth UUID */
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /* デバイス名 */
    private final String DEVICE_NAME = "RNBT-205F";

    /* Soket */
    private BluetoothSocket mSocket;

    /* Thread */
    private Thread mThread;

    /* Threadの状態を表す */
    private boolean isRunning;

    /** 接続ボタン. */
    private Button connectButton;

    /** ステータス. */
    private TextView mStatusTextView;

    /** Bluetoothから受信した値. */
    private TextView mInputTextView;

    /** Action(ステータス表示). */
    private static final int VIEW_STATUS = 0;

    /** Action(取得文字列). */
    private static final int VIEW_INPUT = 1;

    /** BluetoothのOutputStream. */
    OutputStream mmOutputStream = null;

    /** Connect状態確認用フラグ. */
    private boolean connectFlg = false;

    //ここまで

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectButton = (Button)findViewById(R.id.connectBotton);
        itemImage = (ImageView)findViewById(R.id.items);

        // TextViewの設定(Layoutにて設定したものを関連付け)
        mStatusTextView = (TextView)findViewById(R.id.statuMsg);


        // Buttonのイベント設定
        connectButton.setOnClickListener(this);

        // Bluetoothのデバイス名を取得
        // デバイス名は、RNBT-XXXXになるため、
        // DVICE_NAMEでデバイス名を定義
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mStatusTextView.setText("SearchDevice");
        Set< BluetoothDevice > devices = mAdapter.getBondedDevices();
        for ( BluetoothDevice device : devices){

            if(device.getName().equals(DEVICE_NAME)){
                mStatusTextView.setText("find: " + device.getName());
                mDevice = device;
            }
        }

    }
    // 別のアクティビティが起動した場合の処理
    @Override
    protected void onPause(){
        super.onPause();

        isRunning = false;
        connectFlg = false;

        try{
            mSocket.close();
        }
        catch(Exception e){}
    }

    // スレッド処理(connectボタン押下後に実行)
    @Override
    public void run() {
        InputStream mmInStream = null;

        Message valueMsg = new Message();
        valueMsg.what = VIEW_STATUS;
        valueMsg.obj = "connecting...";
        mHandler.sendMessage(valueMsg);

        try{
            // 取得したデバイス名を使ってBluetoothでSocket接続
            mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
            mSocket.connect();
            mmInStream = mSocket.getInputStream();
            mmOutputStream = mSocket.getOutputStream();

            // InputStreamのバッファを格納
            byte[] buffer = new byte[1024];
            ByteBuffer buf = ByteBuffer.allocate(1024);

            // 取得したバッファのサイズを格納
            int bytes;
            int size = 0;
            byte lastByte = 0x00;

            valueMsg = new Message();
            valueMsg.what = VIEW_STATUS;
            valueMsg.obj = "connected.";
            mHandler.sendMessage(valueMsg);

            connectFlg = true;

            while(isRunning){
                if (mmInStream.available() > 0) {
                    // InputStreamの読み込み
                    bytes = mmInStream.read(buffer);
                    Log.i(TAG, "bytes=" + bytes);

                    // 受信データが存在する場合
                    if (bytes != 0) {
                        for (byte oneBuf : buffer) {
                            // 改行コードを受信、または1024バイト取得するまでバッファにためる
                            if ((oneBuf == (byte) 0x0a && lastByte == 0x0d) || (size >= 1023)) {
                                try {
                                    byte tmp[] = new byte[size];
                                    buf.position(0);
                                    buf.get(tmp);
//                                    String readMsg = new String(tmp, "UTF-8");
                                    int intread = -1;
                                    intread = ByteBuffer.wrap(tmp).getInt();
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    // null以外なら表示
                                    if (intread != -1 ) {
                                        valueMsg = new Message();
                                        valueMsg.what = VIEW_INPUT;
                                        valueMsg.obj = intread;
                                        mHandler.sendMessage(valueMsg);
                                    }

                                    lastByte = 0x00;
                                    size = 0;
                                    buf = ByteBuffer.allocate(1024);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (oneBuf != 0) {
                                    buf.put(oneBuf);
                                    lastByte = oneBuf;
                                    size++;
                                }
                            }
                        }
                    }
                } else {
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        // エラー処理
        catch(Exception e){

            valueMsg = new Message();
            valueMsg.what = VIEW_STATUS;
            valueMsg.obj = "Error1:" + e;
            mHandler.sendMessage(valueMsg);

            try{
                mSocket.close();
            }catch(Exception ee){}
            isRunning = false;
            connectFlg = false;
        }
    }

    //connection botton
    public void onClick(View view){
        // Connectボタン
        if(view.equals(connectButton)) {
            if(!connectFlg) {

                mStatusTextView.setText("try connect");

                mThread = new Thread(this);
                // Threadを起動し、Bluetooth接続
                isRunning = true;
                mThread.start();
            }
        }
    }
    /**
     * 描画処理はHandlerでおこなう
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int action = msg.what;
            int msgNum = (int)msg.obj;

            if(action == VIEW_INPUT){
                switch (msgNum){
                    case 0:
                        itemImage.setImageResource(R.drawable.coin);
                        break;
                    case 1:
                        itemImage.setImageResource(R.drawable.star);
                        break;
                    case 2:
                        itemImage.setImageResource(R.drawable.mash);
                        break;

                    default:
                        itemImage.setImageResource(R.drawable.ic_launcher_foreground);
                        break;
                }
            }
            else if(action == VIEW_STATUS){
                itemImage.setImageResource(R.drawable.ic_launcher_foreground);
            }
        }
    };


}
