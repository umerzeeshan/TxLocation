package com.example.getgpslocation;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class Server extends Activity {

    public static final String TAG2 = "SERVERCHECK";
    public static boolean newLocationAvailable = false;
    private ServerSocket serverSocket;

    Handler updateConversationHandler;

    Thread serverThread = null;

    public TextView text;



    public static final int SERVERPORT = 6000;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent sIntent = getIntent();
        setContentView(R.layout.server);

        text = (TextView) findViewById(R.id.Server_view);

        updateConversationHandler = new Handler();

        Log.e(TAG2, "Before starting ServerThread");
        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();
        Log.e(TAG2, "After starting ServerThread");

    }


    @Override
    protected void onStop() {
        super.onStop();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ServerThread implements Runnable {

        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SERVERPORT);
                Log.e(TAG2, "Server port is open");
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {

                try {

                    socket = serverSocket.accept();

                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class CommunicationThread implements Runnable {

        private Socket clientSocket;

        private BufferedReader input;
        private BufferedWriter output;
        //GPSTracker gpsTracker;

        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;


            try {
                this.output = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
                //this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                if (newLocationAvailable == true)
                {
                    newLocationAvailable = false;
                    output.write(String.valueOf(GPSTracker.getLatitude()));
                    output.newLine();
                    output.write(String.valueOf(GPSTracker.getLongitude()));
                    output.flush();
                    //this.output = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {


            while (!Thread.currentThread().isInterrupted()) {

                try {

                    String read = input.readLine();

                    updateConversationHandler.post(new updateUIThread(read));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    class updateUIThread implements Runnable {
        private String msg;

        public updateUIThread(String str) {
            this.msg = str;
        }

        @Override
        public void run() {
            text.setText(text.getText().toString()+"Client Says: "+ msg + "\n");
        }

    }

}