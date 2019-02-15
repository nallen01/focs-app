package me.nallen.fox.app;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
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

    public ToggleState[][] highFlags = {
            {ToggleState.NONE, ToggleState.NONE, ToggleState.NONE},
            {ToggleState.NONE, ToggleState.NONE, ToggleState.NONE}
    };
    public ToggleState[] lowFlags = {ToggleState.NONE, ToggleState.NONE, ToggleState.NONE};

    public int redHighCaps = 0;
    public int redLowCaps = 0;
    public ParkingState[] redParking = {ParkingState.NONE, ParkingState.NONE};
    public boolean redAuton = false;

    public int blueHighCaps = 0;
    public int blueLowCaps = 0;
    public ParkingState[] blueParking = {ParkingState.NONE, ParkingState.NONE};
    public boolean blueAuton = false;

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
            i.next().connectionDropped();
        }
    }

    private synchronized void updateGUI() {
        Iterator<DataListener> i = _listeners.iterator();
        while(i.hasNext())  {
            i.next().updateUI();
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
        cleanUp();
    }

    private void sendFoxMessage(final String paramString) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (fox_out != null) {
                    try {
                        fox_out.write(paramString + '\n');
                        fox_out.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void sendFoxCommand(ScoreField field, MessageType type, int value) {
        sendFoxMessage("" + field.getValue() + ((char)29) + type.getValue() + ((char)29) + value);
    }

    private void sendAutomationMessage(final String paramString) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (automation_out != null) {
                    try {
                        automation_out.write(paramString + '\n');
                        automation_out.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void sendAutomationCommand(int value) {
        sendAutomationMessage("" + value);
    }

    private Thread foxListener;

    private Thread automationListener;

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
                    automation_socket.connect(new InetSocketAddress(automation_ip, AUTOMATION_PORT), SOCKET_TIMEOUT_MS);
                    automation_out = new BufferedWriter(new OutputStreamWriter(automation_socket.getOutputStream()));
                    automation_in = new BufferedReader(new InputStreamReader(automation_socket.getInputStream()));
                }
                catch(Exception e) {
                    cleanUp();
                    return CONNECT_AUTOMATION_IP_ISSUE;
                }
            }

            foxListener = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            String str = fox_in.readLine();

                            if(str == null) {
                                throw new Exception("Connection Dropped");
                            }

                            String[] parts = str.split("" + ((char)29), -1);
                            if(parts.length == 3) {
                                ScoreField field = ScoreField.fromInt(Integer.parseInt(parts[0]));
                                MessageType type = MessageType.fromInt(Integer.parseInt(parts[1]));
                                int num = Integer.parseInt(parts[2]);

                                if(field == ScoreField.HIGH_FLAG_1_1
                                        || field == ScoreField.HIGH_FLAG_1_2
                                        || field == ScoreField.HIGH_FLAG_1_3
                                        || field == ScoreField.HIGH_FLAG_2_1
                                        || field == ScoreField.HIGH_FLAG_2_2
                                        || field == ScoreField.HIGH_FLAG_2_3) {
                                    int row = 0;
                                    int col = 0;
                                    switch(field) {
                                        case HIGH_FLAG_1_1: row = 0; col = 0; break;
                                        case HIGH_FLAG_1_2: row = 0; col = 1; break;
                                        case HIGH_FLAG_1_3: row = 0; col = 2; break;
                                        case HIGH_FLAG_2_1: row = 1; col = 0; break;
                                        case HIGH_FLAG_2_2: row = 1; col = 1; break;
                                        case HIGH_FLAG_2_3: row = 1; col = 2; break;
                                        default: break;
                                    }

                                    highFlags[row][col] = ToggleState.fromInt(num);
                                }
                                else if(field == ScoreField.LOW_FLAG_1
                                        || field == ScoreField.LOW_FLAG_2
                                        || field == ScoreField.LOW_FLAG_3) {
                                    int col = 0;
                                    switch(field) {
                                        case LOW_FLAG_1: col = 0; break;
                                        case LOW_FLAG_2: col = 1; break;
                                        case LOW_FLAG_3: col = 2; break;
                                        default: break;
                                    }

                                    lowFlags[col] = ToggleState.fromInt(num);
                                }
                                else if(field == ScoreField.RED_HIGH_CAPS) {
                                    if(type == MessageType.ADD) {
                                        num = redHighCaps + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = redHighCaps - num;
                                    }

                                    redHighCaps = num;
                                }
                                else if(field == ScoreField.RED_LOW_CAPS) {
                                    if(type == MessageType.ADD) {
                                        num = redLowCaps + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = redLowCaps - num;
                                    }

                                    redLowCaps = num;
                                }
                                else if(field == ScoreField.RED_PARKING_1) {
                                    redParking[0] = ParkingState.fromInt(num);
                                }
                                else if(field == ScoreField.RED_PARKING_2) {
                                    redParking[1] = ParkingState.fromInt(num);
                                }
                                else if(field == ScoreField.RED_AUTON) {
                                    redAuton = num > 0;
                                }
                                else if(field == ScoreField.BLUE_HIGH_CAPS) {
                                    if(type == MessageType.ADD) {
                                        num = blueHighCaps + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = blueHighCaps - num;
                                    }

                                    blueHighCaps = num;
                                }
                                else if(field == ScoreField.BLUE_LOW_CAPS) {
                                    if(type == MessageType.ADD) {
                                        num = blueLowCaps + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = blueLowCaps - num;
                                    }

                                    blueLowCaps = num;
                                }
                                else if(field == ScoreField.BLUE_PARKING_1) {
                                    blueParking[0] =ParkingState.fromInt(num);
                                }
                                else if(field == ScoreField.BLUE_PARKING_2) {
                                    blueParking[1] = ParkingState.fromInt(num);
                                }
                                else if(field == ScoreField.BLUE_AUTON) {
                                    blueAuton = num > 0;
                                }
                                else if(field == ScoreField.CLEAR) {
                                    for (ToggleState[] highFlag : highFlags)
                                        Arrays.fill(highFlag, ToggleState.NONE);
                                    Arrays.fill(lowFlags, ToggleState.NONE);

                                    highFlags[0][0] = ToggleState.BLUE;
                                    highFlags[0][2] = ToggleState.RED;
                                    highFlags[1][0] = ToggleState.BLUE;
                                    highFlags[1][2] = ToggleState.RED;
                                    lowFlags[0] = ToggleState.BLUE;
                                    lowFlags[2] = ToggleState.RED;

                                    redHighCaps = 0;
                                    redLowCaps = 4;
                                    Arrays.fill(redParking, ParkingState.NONE);
                                    redAuton = false;


                                    blueHighCaps = 0;
                                    blueLowCaps = 4;
                                    Arrays.fill(blueParking, ParkingState.NONE);
                                    blueAuton = false;
                                    
                                    updateGUI();
                                }
                            }

                            Thread.sleep(10);
                        }
                        catch (Exception e) {
                            Log.d("Fox", e.getMessage());
                            if(isConnected) {
                                logout();
                                connectionDropped();
                            }
                            return;
                        }
                    }
                }
            });
            foxListener.start();

            if(location == ScorerLocation.COMMENTATOR_AUTOMATION) {
                automationListener = new Thread(new Runnable() {
                    public void run() {
                        while (true) {
                            try {
                                String str = automation_in.readLine();

                                if(str == null) {
                                    throw new Exception("Connection Dropped");
                                }

                                Thread.sleep(10);
                            }
                            catch (Exception e) {
                                if(isConnected) {
                                    logout();
                                    connectionDropped();
                                }
                                return;
                            }
                        }
                    }
                });
                automationListener.start();
            }

            isConnected = true;

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

    public void setHighFlag(int row, int column, ToggleState value) {
        ScoreField field = ScoreField.HIGH_FLAG_1_1;
        switch(3*row + column) {
            case 0: field = ScoreField.HIGH_FLAG_1_1; break;
            case 1: field = ScoreField.HIGH_FLAG_1_2; break;
            case 2: field = ScoreField.HIGH_FLAG_1_3; break;
            case 3: field = ScoreField.HIGH_FLAG_2_1; break;
            case 4: field = ScoreField.HIGH_FLAG_2_2; break;
            case 5: field = ScoreField.HIGH_FLAG_2_3; break;
        }
        sendFoxCommand(field, MessageType.SET, value.getValue());
        highFlags[row][column] = value;
    }

    public void setLowFlag(int column, ToggleState value) {
        ScoreField field = ScoreField.LOW_FLAG_1;
        switch(column) {
            case 0: field = ScoreField.LOW_FLAG_1; break;
            case 1: field = ScoreField.LOW_FLAG_2; break;
            case 2: field = ScoreField.LOW_FLAG_3; break;
        }
        sendFoxCommand(field, MessageType.SET, value.getValue());
        lowFlags[column] = value;
    }

    public void setRedHighCaps(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.RED_HIGH_CAPS, MessageType.SET, value);
        redHighCaps = value;
    }
    public void addRedHighFlag() {
        setRedHighCaps(redHighCaps + 1);
    }
    public void removeRedHighFlag() {
        setRedHighCaps(redHighCaps - 1);
    }

    public void setRedLowCaps(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.RED_LOW_CAPS, MessageType.SET, value);
        redHighCaps = value;
    }
    public void addRedLowCap() {
        setRedHighCaps(redLowCaps + 1);
    }
    public void removeRedLowCap() {
        setRedHighCaps(redLowCaps - 1);
    }

    public void setRedParking(int robot, ParkingState value) {
        ScoreField field = ScoreField.RED_PARKING_1;
        switch(robot) {
            case 0: field = ScoreField.RED_PARKING_1; break;
            case 1: field = ScoreField.RED_PARKING_2; break;
        }
        sendFoxCommand(field, MessageType.SET, value.getValue());
        redParking[robot] = value;
    }

    public void setBlueHighCaps(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.BLUE_HIGH_CAPS, MessageType.SET, value);
        blueHighCaps = value;
    }
    public void addBlueHighFlag() {
        setRedHighCaps(blueHighCaps + 1);
    }
    public void removeBlueHighFlag() {
        setRedHighCaps(blueHighCaps - 1);
    }

    public void setBlueLowCaps(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.BLUE_LOW_CAPS, MessageType.SET, value);
        blueHighCaps = value;
    }
    public void addBlueLowCap() {
        setRedHighCaps(blueLowCaps + 1);
    }
    public void removeRlueLowCap() {
        setRedHighCaps(blueLowCaps - 1);
    }

    public void setBlueParking(int robot, ParkingState value) {
        ScoreField field = ScoreField.BLUE_PARKING_1;
        switch(robot) {
            case 0: field = ScoreField.BLUE_PARKING_1; break;
            case 1: field = ScoreField.BLUE_PARKING_2; break;
        }
        sendFoxCommand(field, MessageType.SET, value.getValue());
        blueParking[robot] = value;
    }

    public void setRedAuton(boolean auton) {
        sendFoxCommand(ScoreField.RED_AUTON, MessageType.SET, auton ? 1 : 0);
        redAuton = auton;

        if(auton && blueAuton) {
            setBlueAuton(false);
        }
    }

    public void setBlueAuton(boolean auton) {
        sendFoxCommand(ScoreField.BLUE_AUTON, MessageType.SET, auton ? 1 : 0);
        blueAuton = auton;

        if(auton && redAuton) {
            setRedAuton(false);
        }
    }

    public void setHideFox(boolean hide) {
        sendFoxCommand(ScoreField.HIDE, MessageType.SET, hide ? 1 : 0);
    }

    public void setThreeTeam(boolean threeTeam) {
        sendFoxCommand(ScoreField.THREE_TEAM, MessageType.SET, threeTeam ? 1 : 0);
    }

    public void setFoxDisplay(FoxDisplay display) {
        sendAutomationCommand(display.getValue());
    }
}