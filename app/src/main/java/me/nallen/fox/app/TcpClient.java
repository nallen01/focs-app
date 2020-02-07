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

    private CubeType[] towerCubes = {
            CubeType.NONE, CubeType.NONE, CubeType.NONE, CubeType.NONE, CubeType.NONE, CubeType.NONE, CubeType.NONE
    };

    private AutonWinner autonWinner = AutonWinner.NONE;

    private int redOrangeCubes = 0;
    private int redGreenCubes = 0;
    private int redPurpleCubes = 0;

    private int blueOrangeCubes = 0;
    private int blueGreenCubes = 0;
    private int bluePurpleCubes = 0;

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

                                if(field == ScoreField.TOWER_CUBE_1
                                        || field == ScoreField.TOWER_CUBE_2
                                        || field == ScoreField.TOWER_CUBE_3
                                        || field == ScoreField.TOWER_CUBE_4
                                        || field == ScoreField.TOWER_CUBE_5
                                        || field == ScoreField.TOWER_CUBE_6
                                        || field == ScoreField.TOWER_CUBE_7) {
                                    int pos = 0;
                                    switch(field) {
                                        case TOWER_CUBE_1: pos = 0; break;
                                        case TOWER_CUBE_2: pos = 1; break;
                                        case TOWER_CUBE_3: pos = 2; break;
                                        case TOWER_CUBE_4: pos = 3; break;
                                        case TOWER_CUBE_5: pos = 4; break;
                                        case TOWER_CUBE_6: pos = 5; break;
                                        case TOWER_CUBE_7: pos = 6; break;
                                        default: break;
                                    }

                                    towerCubes[pos] = CubeType.fromInt(num);
                                }
                                else if(field == ScoreField.AUTON) {
                                    autonWinner = AutonWinner.fromInt(num);
                                }
                                else if(field == ScoreField.RED_ORANGE_CUBES) {
                                    if(type == MessageType.ADD) {
                                        num = redOrangeCubes + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = redOrangeCubes - num;
                                    }

                                    redOrangeCubes = num;
                                }
                                else if(field == ScoreField.RED_GREEN_CUBES) {
                                    if(type == MessageType.ADD) {
                                        num = redGreenCubes + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = redGreenCubes - num;
                                    }

                                    redGreenCubes = num;
                                }
                                else if(field == ScoreField.RED_PURPLE_CUBES) {
                                    if(type == MessageType.ADD) {
                                        num = redPurpleCubes + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = redPurpleCubes - num;
                                    }

                                    redPurpleCubes = num;
                                }
                                else if(field == ScoreField.BLUE_ORANGE_CUBES) {
                                    if(type == MessageType.ADD) {
                                        num = blueOrangeCubes + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = blueOrangeCubes - num;
                                    }

                                    blueOrangeCubes = num;
                                }
                                else if(field == ScoreField.BLUE_GREEN_CUBES) {
                                    if(type == MessageType.ADD) {
                                        num = blueGreenCubes + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = blueGreenCubes - num;
                                    }

                                    blueGreenCubes = num;
                                }
                                else if(field == ScoreField.BLUE_PURPLE_CUBES) {
                                    if(type == MessageType.ADD) {
                                        num = bluePurpleCubes + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = bluePurpleCubes - num;
                                    }

                                    bluePurpleCubes = num;
                                }
                                else if(field == ScoreField.CLEAR) {
                                    Arrays.fill(towerCubes, CubeType.NONE);

                                    autonWinner = AutonWinner.NONE;

                                    redOrangeCubes = 0;
                                    redGreenCubes = 0;
                                    redPurpleCubes = 0;

                                    blueOrangeCubes = 0;
                                    blueGreenCubes = 0;
                                    bluePurpleCubes = 0;

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

    public void setTowerCube(int pos, CubeType value) {
        ScoreField field = ScoreField.TOWER_CUBE_1;
        switch(pos) {
            case 0: field = ScoreField.TOWER_CUBE_1; break;
            case 1: field = ScoreField.TOWER_CUBE_2; break;
            case 2: field = ScoreField.TOWER_CUBE_3; break;
            case 3: field = ScoreField.TOWER_CUBE_4; break;
            case 4: field = ScoreField.TOWER_CUBE_5; break;
            case 5: field = ScoreField.TOWER_CUBE_6; break;
            case 6: field = ScoreField.TOWER_CUBE_7; break;
        }
        sendFoxCommand(field, MessageType.SET, value.getValue());
        towerCubes[pos] = value;
    }

    public void setAutonWinner(AutonWinner value) {
        sendFoxCommand(ScoreField.AUTON, MessageType.SET, value.getValue());
        autonWinner = value;
    }

    public void setRedOrangeCubes(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.RED_ORANGE_CUBES, MessageType.SET, value);
        redOrangeCubes = value;
    }
    public void addRedOrangeCube() {
        setRedOrangeCubes(redOrangeCubes + 1);
    }
    public void removeRedOrangeCube() {
        setRedOrangeCubes(redOrangeCubes - 1);
    }

    public void setRedGreenCubes(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.RED_GREEN_CUBES, MessageType.SET, value);
        redGreenCubes = value;
    }
    public void addRedGreenCube() {
        setRedGreenCubes(redGreenCubes + 1);
    }
    public void removeRedGreenCube() {
        setRedGreenCubes(redGreenCubes - 1);
    }

    public void setRedPurpleCubes(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.RED_PURPLE_CUBES, MessageType.SET, value);
        redPurpleCubes = value;
    }
    public void addRedPurpleCube() {
        setRedPurpleCubes(redPurpleCubes + 1);
    }
    public void removeRedPurpleCube() {
        setRedPurpleCubes(redPurpleCubes - 1);
    }

    public void setBlueOrangeCubes(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.RED_ORANGE_CUBES, MessageType.SET, value);
        blueOrangeCubes = value;
    }
    public void addBlueOrangeCube() {
        setBlueOrangeCubes(blueOrangeCubes + 1);
    }
    public void removeBlueOrangeCube() {
        setBlueOrangeCubes(blueOrangeCubes - 1);
    }

    public void setBlueGreenCubes(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.RED_GREEN_CUBES, MessageType.SET, value);
        blueGreenCubes = value;
    }
    public void addBlueGreenCube() {
        setBlueGreenCubes(blueGreenCubes + 1);
    }
    public void removeBlueGreenCube() {
        setBlueGreenCubes(blueGreenCubes - 1);
    }

    public void setBluePurpleCubes(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.RED_PURPLE_CUBES, MessageType.SET, value);
        bluePurpleCubes = value;
    }
    public void addBluePurpleCube() {
        setBluePurpleCubes(bluePurpleCubes + 1);
    }
    public void removeBluePurpleCube() {
        setBluePurpleCubes(bluePurpleCubes - 1);
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