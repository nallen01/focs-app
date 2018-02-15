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

    public int[] redBaseCones = {0, 0, 0, 0};
    public ScoringZone[] redBaseZones = {ScoringZone.NONE, ScoringZone.NONE, ScoringZone.NONE, ScoringZone.NONE};
    public int redStationaryCones = 0;
    public int redParking = 0;
    public boolean redAuton = false;

    public int[] blueBaseCones = {0, 0, 0, 0};
    public ScoringZone[] blueBaseZones = {ScoringZone.NONE, ScoringZone.NONE, ScoringZone.NONE, ScoringZone.NONE};
    public int blueStationaryCones = 0;
    public int blueParking = 0;
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

                                if(field == ScoreField.RED_BASE_ONE_CONES
                                        || field == ScoreField.RED_BASE_TWO_CONES
                                        || field == ScoreField.RED_BASE_THREE_CONES
                                        || field == ScoreField.RED_BASE_FOUR_CONES) {
                                    int index = 0;
                                    switch(field) {
                                        case RED_BASE_ONE_CONES: index = 0; break;
                                        case RED_BASE_TWO_CONES: index = 1; break;
                                        case RED_BASE_THREE_CONES: index = 2; break;
                                        case RED_BASE_FOUR_CONES: index = 3; break;
                                        default: break;
                                    }

                                    if(type == MessageType.ADD) {
                                        num = redBaseCones[index] + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = redBaseCones[index] - num;
                                    }

                                    redBaseCones[index] = num;
                                }
                                if(field == ScoreField.RED_BASE_ONE_ZONE
                                        || field == ScoreField.RED_BASE_TWO_ZONE
                                        || field == ScoreField.RED_BASE_THREE_ZONE
                                        || field == ScoreField.RED_BASE_FOUR_ZONE) {
                                    int index = 0;
                                    switch(field) {
                                        case RED_BASE_ONE_ZONE: index = 0; break;
                                        case RED_BASE_TWO_ZONE: index = 1; break;
                                        case RED_BASE_THREE_ZONE: index = 2; break;
                                        case RED_BASE_FOUR_ZONE: index = 3; break;
                                        default: break;
                                    }

                                    redBaseZones[index] = ScoringZone.fromInt(num);
                                }
                                else if(field == ScoreField.RED_STATIONARY_CONES) {
                                    if(type == MessageType.ADD) {
                                        num = redStationaryCones + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = redStationaryCones - num;
                                    }

                                    redStationaryCones = num;
                                }
                                else if(field == ScoreField.RED_PARKING) {
                                    if(type == MessageType.ADD) {
                                        num = redParking + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = redParking - num;
                                    }

                                    redParking = num;
                                }
                                else if(field == ScoreField.RED_AUTON) {
                                    redAuton = num > 0;
                                }
                                else if(field == ScoreField.BLUE_BASE_ONE_CONES
                                        || field == ScoreField.BLUE_BASE_TWO_CONES
                                        || field == ScoreField.BLUE_BASE_THREE_CONES
                                        || field == ScoreField.BLUE_BASE_FOUR_CONES) {
                                    int index = 0;
                                    switch(field) {
                                        case BLUE_BASE_ONE_CONES: index = 0; break;
                                        case BLUE_BASE_TWO_CONES: index = 1; break;
                                        case BLUE_BASE_THREE_CONES: index = 2; break;
                                        case BLUE_BASE_FOUR_CONES: index = 3; break;
                                        default: break;
                                    }

                                    if(type == MessageType.ADD) {
                                        num = blueBaseCones[index] + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = blueBaseCones[index] - num;
                                    }

                                    blueBaseCones[index] = num;
                                }
                                if(field == ScoreField.BLUE_BASE_ONE_ZONE
                                        || field == ScoreField.BLUE_BASE_TWO_ZONE
                                        || field == ScoreField.BLUE_BASE_THREE_ZONE
                                        || field == ScoreField.BLUE_BASE_FOUR_ZONE) {
                                    int index = 0;
                                    switch(field) {
                                        case BLUE_BASE_ONE_ZONE: index = 0; break;
                                        case BLUE_BASE_TWO_ZONE: index = 1; break;
                                        case BLUE_BASE_THREE_ZONE: index = 2; break;
                                        case BLUE_BASE_FOUR_ZONE: index = 3; break;
                                        default: break;
                                    }

                                    blueBaseZones[index] = ScoringZone.fromInt(num);
                                }
                                else if(field == ScoreField.BLUE_STATIONARY_CONES) {
                                    if(type == MessageType.ADD) {
                                        num = blueStationaryCones + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = blueStationaryCones - num;
                                    }

                                    blueStationaryCones = num;
                                }
                                else if(field == ScoreField.BLUE_PARKING) {
                                    if(type == MessageType.ADD) {
                                        num = blueParking + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = blueParking - num;
                                    }

                                    blueParking = num;
                                }
                                else if(field == ScoreField.BLUE_AUTON) {
                                    blueAuton = num > 0;
                                }
                                else if(field == ScoreField.CLEAR) {
                                    Arrays.fill(redBaseCones, 0);
                                    Arrays.fill(redBaseZones, ScoringZone.NONE);
                                    redStationaryCones = 0;
                                    redAuton = false;


                                    Arrays.fill(blueBaseCones, 0);
                                    Arrays.fill(blueBaseZones, ScoringZone.NONE);
                                    blueStationaryCones = 0;
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

    public void setRedBaseCones(int index, int value) {
        value = value < 0 ? 0 : value;
        if(index >= 0 && index < 4) {
            ScoreField field = ScoreField.RED_BASE_ONE_CONES;
            switch(index) {
                case 0: field = ScoreField.RED_BASE_ONE_CONES; break;
                case 1: field = ScoreField.RED_BASE_TWO_CONES; break;
                case 2: field = ScoreField.RED_BASE_THREE_CONES; break;
                case 3: field = ScoreField.RED_BASE_FOUR_CONES; break;
            }
            sendFoxCommand(field, MessageType.SET, value);
            redBaseCones[index] = value;
        }
    }
    public void addRedBaseCone(int index) {
        if(index >= 0 && index < 4)
            setRedBaseCones(index, redBaseCones[index] + 1);
    }
    public void removeRedBaseCone(int index) {
        if(index >= 0 && index < 4)
            setRedBaseCones(index, redBaseCones[index] - 1);
    }

    public void setRedBaseZone(int index, ScoringZone value) {
        if(index >= 0 && index < 4) {
            ScoreField field = ScoreField.RED_BASE_ONE_ZONE;
            switch(index) {
                case 0: field = ScoreField.RED_BASE_ONE_ZONE; break;
                case 1: field = ScoreField.RED_BASE_TWO_ZONE; break;
                case 2: field = ScoreField.RED_BASE_THREE_ZONE; break;
                case 3: field = ScoreField.RED_BASE_FOUR_ZONE; break;
            }
            sendFoxCommand(field, MessageType.SET, value.getValue());
            redBaseZones[index] = value;
        }
    }

    public void setRedStationaryCones(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.RED_STATIONARY_CONES, MessageType.SET, value);
        redStationaryCones = value;
    }
    public void addRedStationaryCone() {
        setRedStationaryCones(redStationaryCones + 1);
    }
    public void removeRedStationaryCone() {
        setRedStationaryCones(redStationaryCones - 1);
    }

    public void setRedParking(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.RED_PARKING, MessageType.SET, value);
        redParking = value;
    }
    public void addRedParking() {
        setRedParking(redParking + 1);
    }
    public void removeRedParking() {
        setRedParking(redParking - 1);
    }

    public void setBlueBaseCones(int index, int value) {
        value = value < 0 ? 0 : value;
        if(index >= 0 && index < 4) {
            ScoreField field = ScoreField.BLUE_BASE_ONE_CONES;
            switch(index) {
                case 0: field = ScoreField.BLUE_BASE_ONE_CONES; break;
                case 1: field = ScoreField.BLUE_BASE_TWO_CONES; break;
                case 2: field = ScoreField.BLUE_BASE_THREE_CONES; break;
                case 3: field = ScoreField.BLUE_BASE_FOUR_CONES; break;
            }
            sendFoxCommand(field, MessageType.SET, value);
            blueBaseCones[index] = value;
        }
    }
    public void addBlueBaseCone(int index) {
        if(index >= 0 && index < 4)
            setBlueBaseCones(index, blueBaseCones[index] + 1);
    }
    public void removeBlueBaseCone(int index) {
        if(index >= 0 && index < 4)
            setBlueBaseCones(index, blueBaseCones[index] - 1);
    }

    public void setBlueBaseZone(int index, ScoringZone value) {
        if(index >= 0 && index < 4) {
            ScoreField field = ScoreField.BLUE_BASE_ONE_ZONE;
            switch(index) {
                case 0: field = ScoreField.BLUE_BASE_ONE_ZONE; break;
                case 1: field = ScoreField.BLUE_BASE_TWO_ZONE; break;
                case 2: field = ScoreField.BLUE_BASE_THREE_ZONE; break;
                case 3: field = ScoreField.BLUE_BASE_FOUR_ZONE; break;
            }
            sendFoxCommand(field, MessageType.SET, value.getValue());
            blueBaseZones[index] = value;
        }
    }

    public void setBlueStationaryCones(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.BLUE_STATIONARY_CONES, MessageType.SET, value);
        blueStationaryCones = value;
    }
    public void addBlueStationaryCone() {
        setRedStationaryCones(blueStationaryCones + 1);
    }
    public void removeBlueStationaryCone() {
        setRedStationaryCones(blueStationaryCones - 1);
    }

    public void setBlueParking(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.BLUE_PARKING, MessageType.SET, value);
        blueParking = value;
    }
    public void addBlueParking() {
        setRedParking(blueParking + 1);
    }
    public void removeBlueParking() {
        setRedParking(blueParking - 1);
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