/**
 * Copyright (c) 2014 Shan Ji.
 *
 * @author Shan Ji
 * @version 5/4/2014
 **/
package edu.asu.individualapp.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends Activity {

    private Socket socket;

    private static final int SERVERPORT = 8090;
    private static String SERVER_IP;
    private static BufferedReader is = null;
    private static TextView dialogue = null;
    private static String responseLine ="";
    private static Handler handler = new Handler();;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClick(View view) {
        try {
            new Thread(new ClientThread()).start();
            Thread.sleep(2000);
            EditText Name = (EditText) findViewById(R.id.editText);
            String NickName = Name.getText().toString();
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);

            out.println(NickName);
            if(socket.isConnected()){
                Context context = getApplicationContext();
                CharSequence text = "You are successfully connected to the private chatroom! Welcome!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            else{
                Context context = getApplicationContext();
                CharSequence text = "Chatroom not available right now, Please try again later";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            setContentView(R.layout.chatroom);
            dialogue= (TextView) findViewById(R.id.textView);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick2(View view) {
        try {
            EditText message = (EditText) findViewById(R.id.editText);
            String str = message.getText().toString();
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);

            out.println(str);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ClientThread implements Runnable {

        @Override


        public void run() {

            try {
                EditText ip = (EditText) findViewById(R.id.ipAddress);
                SERVER_IP = ip.getText().toString();
                System.out.println(SERVER_IP);
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);
                is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while ((responseLine = is.readLine()) != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("ShanJi", responseLine);
                            if(dialogue!=null){
                                dialogue.append(responseLine+"\n");
                            }
                        }
                    });

                    if (responseLine.indexOf("*** Bye") != -1)
                        break;
                }

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }


    public void onDestroy(){
        try {
            socket.close();
            is.close();
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }
}