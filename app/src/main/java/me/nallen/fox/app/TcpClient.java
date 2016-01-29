package me.nallen.fox.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

public class TcpClient {
    public static final int CONNECT_OK = 0;
    public static final int CONNECT_FOX_IP_ISSUE = 1;
    public static final int CONNECT_AUTOMATION_IP_ISSUE = 2;
    public static final int CONNECT_ALREADY_CONNECTED = 3;

    public static final int SOCKET_TIMEOUT_MS = 1000;
    public static final int FOX_PORT = 5005;
    public static final int AUTOMATION_PORT = 5006;

    private static TcpClient singleton = new TcpClient();

    private Socket fox_socket = null;
    private BufferedReader fox_in = null;
    private BufferedWriter fox_out = null;

    private Socket automation_socket = null;
    private BufferedReader automation_in = null;
    private BufferedWriter automation_out = null;

    private LinkedList<DataListener> _listeners = new LinkedList<DataListener>();
    private boolean isConnected = false;

    private TcpClient() {
    }

    public static TcpClient getInstance() {
        return singleton;
    }

    public synchronized void addDataListener(DataListener listener)  {
        _listeners.add(listener);
    }
    public synchronized void removeDataListener(DataListener listener)   {
        _listeners.remove(listener);
    }

    private synchronized void connectionDropped() {
        Iterator<DataListener> i = _listeners.iterator();
        while(i.hasNext())  {
            ((DataListener) i.next()).connectionDropped();
        }
    }

    public void cleanUp() {
        isConnected = false;
        try {
            fox_socket.close();
        } catch (Exception e) { }
        fox_socket = null;
        fox_in = null;
        fox_out = null;

        try {
            automation_socket.close();
        } catch (Exception e) { }
        automation_socket = null;
        automation_in = null;
        automation_out = null;
    }

    public void logout() {
        isConnected = false;

        cleanUp();
    }

    private boolean sendFoxMessage(String paramString) {
        if (fox_out != null) {
            try {
                fox_out.write(paramString + '\n');
                fox_out.flush();
                return true;
            } catch (Exception e) {}
        }
        return false;
    }

    private boolean sendFoxCommand(ScoreField field, MessageType type, int value) {
        return sendFoxMessage("" + field.getValue() + ((char)29) + type.getValue() + ((char)29) + value);
    }

    private void listener() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        String str = fox_in.readLine();

                        if(str == null) {
                            throw new Exception("Connection Dropped");
                        }

                        Thread.sleep(10);
                    }
                    catch (Exception e) {
                        logout();
                        connectionDropped();
                        break;
                    }
                }
            }
        }).start();
    }

    public int connect(String fox_ip, ScorerLocation location, String automation_ip) {
        if(!isConnected()) {
            try {
                fox_socket = new Socket();
                fox_socket.connect(new InetSocketAddress(fox_ip, FOX_PORT), SOCKET_TIMEOUT_MS);
                fox_out = new BufferedWriter(new OutputStreamWriter(fox_socket.getOutputStream()));
                fox_in = new BufferedReader(new InputStreamReader(fox_socket.getInputStream()));
            }
            catch(Exception e) {
                cleanUp();
                return CONNECT_FOX_IP_ISSUE;
            }

            if(location == ScorerLocation.COMMENTATOR_AUTOMATION) {
                try {
                    automation_socket = new Socket();
                    automation_socket.connect(new InetSocketAddress(fox_ip, AUTOMATION_PORT), SOCKET_TIMEOUT_MS);
                    automation_out = new BufferedWriter(new OutputStreamWriter(automation_socket.getOutputStream()));
                    automation_in = new BufferedReader(new InputStreamReader(automation_socket.getInputStream()));
                }
                catch(Exception e) {
                    cleanUp();
                    return CONNECT_AUTOMATION_IP_ISSUE;
                }
            }

            listener();

            return CONNECT_OK;
        }

        return CONNECT_ALREADY_CONNECTED;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setLargeHistory(boolean isLarge) {
        sendFoxCommand(ScoreField.LARGE_HISTORY, MessageType.SET, isLarge ? 1 : 0);
    }

    public void setHistoryVisible(boolean isVisible) {
        sendFoxCommand(ScoreField.HISTORY, MessageType.SET, isVisible ? 1 : 0);
    }

    public void clearAllScores() {
        sendFoxCommand(ScoreField.CLEAR, MessageType.SET, 1);
    }

    public void setPaused(boolean isPaused) {
        sendFoxCommand(ScoreField.PAUSED, MessageType.SET, isPaused ? 1 : 0);
    }

    public void setRedHighBalls(int value) {
        sendFoxCommand(ScoreField.RED_HIGH_BALLS, MessageType.SET, value);
    }
    public void addRedHighBall() {
        sendFoxCommand(ScoreField.RED_HIGH_BALLS, MessageType.ADD, 1);
    }

    public void setRedLowBalls(int value) {
        sendFoxCommand(ScoreField.RED_LOW_BALLS, MessageType.SET, value);
    }
    public void addRedLowBall() {
        sendFoxCommand(ScoreField.RED_LOW_BALLS, MessageType.ADD, 1);
    }

    public void setBlueHighBalls(int value) {
        sendFoxCommand(ScoreField.BLUE_HIGH_BALLS, MessageType.SET, value);
    }
    public void addBlueHighBall() {
        sendFoxCommand(ScoreField.BLUE_HIGH_BALLS, MessageType.ADD, 1);
    }

    public void setBlueLowBalls(int value) {
        sendFoxCommand(ScoreField.BLUE_LOW_BALLS, MessageType.SET, value);
    }
    public void addBlueLowBall() {
        sendFoxCommand(ScoreField.BLUE_LOW_BALLS, MessageType.ADD, 1);
    }

    public void setRedElevatedState(ElevatedState state) {
        sendFoxCommand(ScoreField.RED_ELEVATION, MessageType.SET, state.getValue());
    }

    public void setBlueElevatedState(ElevatedState state) {
        sendFoxCommand(ScoreField.BLUE_ELEVATION, MessageType.SET, state.getValue());
    }

    public void setRedAuton(boolean auton) {
        sendFoxCommand(ScoreField.RED_AUTON, MessageType.SET, auton ? 1 : 0);
    }

    public void setBlueAuton(boolean auton) {
        sendFoxCommand(ScoreField.BLUE_AUTON, MessageType.SET, auton ? 1 : 0);
    }
}