package me.nallen.fox.app;

import android.util.Log;

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

    public int redFarStars = 7;
    public int redNearStars = 0;
    public int redFarCubes = 1;
    public int redNearCubes = 0;
    public boolean redAuton = false;
    public ElevatedState redElevation = ElevatedState.NONE;
    public int blueFarStars = 7;
    public int blueNearStars = 0;
    public int blueFarCubes = 1;
    public int blueNearCubes = 0;
    public boolean blueAuton = false;
    public ElevatedState blueElevation = ElevatedState.NONE;

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

    private boolean sendAutomationMessage(String paramString) {
        if (automation_out != null) {
            try {
                automation_out.write(paramString + '\n');
                automation_out.flush();
                return true;
            } catch (Exception e) {}
        }
        return false;
    }

    private boolean sendAutomationCommand(int value) {
        return sendAutomationMessage("" + value);
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

                                if(field == ScoreField.RED_FAR_CUBES) {
                                    if(type == MessageType.ADD) {
                                        num = redFarCubes + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = redFarCubes - num;
                                    }

                                    redFarCubes = num;
                                    updateGUI();
                                }
                                else if(field == ScoreField.RED_FAR_STARS) {
                                    if(type == MessageType.ADD) {
                                        num = redFarStars + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = redFarStars - num;
                                    }

                                    redFarStars = num;
                                    updateGUI();
                                }
                                else if(field == ScoreField.RED_NEAR_CUBES) {
                                    if(type == MessageType.ADD) {
                                        num = redNearCubes + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = redNearCubes - num;
                                    }

                                    redNearCubes = num;
                                    updateGUI();
                                }
                                else if(field == ScoreField.RED_NEAR_STARS) {
                                    if(type == MessageType.ADD) {
                                        num = redNearStars + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = redNearStars - num;
                                    }

                                    redNearStars = num;
                                    updateGUI();
                                }
                                else if(field == ScoreField.RED_ELEVATION) {
                                    ElevatedState state = ElevatedState.fromInt(num);
                                    redElevation = state;
                                    updateGUI();
                                }
                                else if(field == ScoreField.RED_AUTON) {
                                    redAuton = (num > 0);
                                    updateGUI();
                                }
                                else if(field == ScoreField.BLUE_FAR_CUBES) {
                                    if(type == MessageType.ADD) {
                                        num = blueFarCubes + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = blueFarCubes - num;
                                    }

                                    blueFarCubes = num;
                                    updateGUI();
                                }
                                else if(field == ScoreField.BLUE_FAR_STARS) {
                                    if(type == MessageType.ADD) {
                                        num = blueFarStars + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = blueFarStars - num;
                                    }

                                    blueFarStars = num;
                                    updateGUI();
                                }
                                else if(field == ScoreField.BLUE_NEAR_CUBES) {
                                    if(type == MessageType.ADD) {
                                        num = blueNearCubes + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = blueNearCubes - num;
                                    }

                                    blueNearCubes = num;
                                    updateGUI();
                                }
                                else if(field == ScoreField.BLUE_NEAR_STARS) {
                                    if(type == MessageType.ADD) {
                                        num = blueNearStars + num;
                                    }
                                    else if(type == MessageType.SUBTRACT) {
                                        num = blueNearStars - num;
                                    }

                                    blueNearStars = num;
                                    updateGUI();
                                }
                                else if(field == ScoreField.BLUE_ELEVATION) {
                                    ElevatedState state = ElevatedState.fromInt(num);
                                    blueElevation = state;
                                    updateGUI();
                                }
                                else if(field == ScoreField.BLUE_AUTON) {
                                    blueAuton = (num > 0);
                                    updateGUI();
                                }
                                else if(field == ScoreField.CLEAR) {
                                    redFarCubes = 1;
                                    redFarStars = 7;
                                    redNearCubes = 0;
                                    redNearStars = 0;
                                    redAuton = false;
                                    redElevation = ElevatedState.NONE;

                                    blueFarCubes = 1;
                                    blueFarStars = 7;
                                    blueNearCubes = 0;
                                    blueNearStars = 0;
                                    blueAuton = false;
                                    blueElevation = ElevatedState.NONE;
                                    
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

    public void setRedFarStars(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.RED_FAR_STARS, MessageType.SET, value);
        redFarStars = value;
    }
    public void addRedFarStar() {
        setRedFarStars(redFarStars + 1);
    }
    public void removeRedFarStar() {
        setRedFarStars(redFarStars - 1);
    }

    public void setRedFarCubes(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.RED_FAR_CUBES, MessageType.SET, value);
        redFarCubes = value;
    }
    public void addRedFarCube() {
        setRedFarCubes(redFarCubes + 1);
    }
    public void removeRedFarCube() {
        setRedFarCubes(redFarCubes - 1);
    }

    public void setRedNearStars(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.RED_NEAR_STARS, MessageType.SET, value);
        redNearStars = value;
    }
    public void addRedNearStar() {
        setRedNearStars(redNearStars + 1);
    }
    public void removeRedNearStar() {
        setRedNearStars(redNearStars - 1);
    }

    public void setRedNearCubes(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.RED_NEAR_CUBES, MessageType.SET, value);
        redNearCubes = value;
    }
    public void addRedNearCube() {
        setRedNearCubes(redNearCubes + 1);
    }
    public void removeRedNearCube() {
        setRedNearCubes(redNearCubes - 1);
    }

    public void setBlueFarStars(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.BLUE_FAR_STARS, MessageType.SET, value);
        blueFarStars = value;
    }
    public void addBlueFarStar() {
        setBlueFarStars(blueFarStars + 1);
    }
    public void removeBlueFarStar() {
        setBlueFarStars(blueFarStars - 1);
    }

    public void setBlueFarCubes(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.BLUE_FAR_CUBES, MessageType.SET, value);
        blueFarCubes = value;
    }
    public void addBlueFarCube() {
        setBlueFarCubes(blueFarCubes + 1);
    }
    public void removeBlueFarCube() {
        setBlueFarCubes(blueFarCubes - 1);
    }

    public void setBlueNearStars(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.BLUE_NEAR_STARS, MessageType.SET, value);
        blueNearStars = value;
    }
    public void addBlueNearStar() {
        setBlueNearStars(blueNearStars + 1);
    }
    public void removeBlueNearStar() {
        setBlueNearStars(blueNearStars - 1);
    }

    public void setBlueNearCubes(int value) {
        value = value < 0 ? 0 : value;
        sendFoxCommand(ScoreField.BLUE_NEAR_CUBES, MessageType.SET, value);
        blueNearCubes = value;
    }
    public void addBlueNearCube() {
        setBlueNearCubes(blueNearCubes + 1);
    }
    public void removeBlueNearCube() {
        setBlueNearCubes(blueNearCubes - 1);
    }

    public void setRedElevatedState(ElevatedState state) {
        sendFoxCommand(ScoreField.RED_ELEVATION, MessageType.SET, state.getValue());
        redElevation = state;
    }

    public void setBlueElevatedState(ElevatedState state) {
        sendFoxCommand(ScoreField.BLUE_ELEVATION, MessageType.SET, state.getValue());
        blueElevation = state;
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

    public void setFoxDisplay(FoxDisplay display) {
        sendAutomationCommand(display.getValue());
    }
}