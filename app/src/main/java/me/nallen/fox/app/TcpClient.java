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

    private static final TcpClient singleton = new TcpClient();

    private Socket fox_socket = null;
    private BufferedReader fox_in = null;
    private BufferedWriter fox_out = null;

    private Socket automation_socket = null;
    private BufferedReader automation_in = null;
    private BufferedWriter automation_out = null;

    private final LinkedList<DataListener> _listeners = new LinkedList<DataListener>();
    private boolean isConnected = false;

    public final BallType[][] goalOwnership = {
        {
                BallType.NONE, BallType.NONE, BallType.NONE
        },{
                BallType.NONE, BallType.NONE, BallType.NONE
        },{
                BallType.NONE, BallType.NONE, BallType.NONE
        }
    };

    public AutonWinner autonWinner = AutonWinner.NONE;

    public int redBalls = 0;

    public int blueBalls = 0;

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
        } catch (Exception ignored) { }
        fox_socket = null;
        fox_in = null;
        fox_out = null;

        try {
            automation_socket.close();
        } catch (Exception ignored) { }
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

            Thread foxListener = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            String str = fox_in.readLine();

                            if (str == null) {
                                throw new Exception("Connection Dropped");
                            }

                            String[] parts = str.split("" + ((char) 29), -1);
                            if (parts.length == 3) {
                                ScoreField field = ScoreField.fromInt(Integer.parseInt(parts[0]));
                                MessageType type = MessageType.fromInt(Integer.parseInt(parts[1]));
                                int num = Integer.parseInt(parts[2]);

                                if (field == ScoreField.GOAL_OWNERSHIP_0_0
                                        || field == ScoreField.GOAL_OWNERSHIP_0_1
                                        || field == ScoreField.GOAL_OWNERSHIP_0_2
                                        || field == ScoreField.GOAL_OWNERSHIP_1_0
                                        || field == ScoreField.GOAL_OWNERSHIP_1_1
                                        || field == ScoreField.GOAL_OWNERSHIP_1_2
                                        || field == ScoreField.GOAL_OWNERSHIP_2_0
                                        || field == ScoreField.GOAL_OWNERSHIP_2_1
                                        || field == ScoreField.GOAL_OWNERSHIP_2_2) {
                                    int x = 0, y = 0;
                                    switch (field) {
                                        case GOAL_OWNERSHIP_0_0:
                                            x = 0;
                                            y = 0;
                                            break;
                                        case GOAL_OWNERSHIP_0_1:
                                            x = 0;
                                            y = 1;
                                            break;
                                        case GOAL_OWNERSHIP_0_2:
                                            x = 0;
                                            y = 2;
                                            break;
                                        case GOAL_OWNERSHIP_1_0:
                                            x = 1;
                                            y = 0;
                                            break;
                                        case GOAL_OWNERSHIP_1_1:
                                            x = 1;
                                            y = 1;
                                            break;
                                        case GOAL_OWNERSHIP_1_2:
                                            x = 1;
                                            y = 2;
                                            break;
                                        case GOAL_OWNERSHIP_2_0:
                                            x = 2;
                                            y = 0;
                                            break;
                                        case GOAL_OWNERSHIP_2_1:
                                            x = 2;
                                            y = 1;
                                            break;
                                        case GOAL_OWNERSHIP_2_2:
                                            x = 2;
                                            y = 2;
                                            break;
                                        default:
                                            break;
                                    }

                                    goalOwnership[x][y] = BallType.fromInt(num);
                                } else if (field == ScoreField.AUTON) {
                                    autonWinner = AutonWinner.fromInt(num);
                                } else if (field == ScoreField.RED_BALLS) {
                                    if (type == MessageType.ADD) {
                                        num = redBalls + num;
                                    } else if (type == MessageType.SUBTRACT) {
                                        num = redBalls - num;
                                    }

                                    redBalls = num;
                                } else if (field == ScoreField.BLUE_BALLS) {
                                    if (type == MessageType.ADD) {
                                        num = blueBalls + num;
                                    } else if (type == MessageType.SUBTRACT) {
                                        num = blueBalls - num;
                                    }

                                    blueBalls = num;
                                } else if (field == ScoreField.CLEAR) {
                                    goalOwnership[0][0] = BallType.RED;
                                    goalOwnership[0][1] = BallType.BLUE;
                                    goalOwnership[0][2] = BallType.BLUE;
                                    goalOwnership[1][0] = BallType.RED;
                                    goalOwnership[1][1] = BallType.NONE;
                                    goalOwnership[1][2] = BallType.BLUE;
                                    goalOwnership[2][0] = BallType.RED;
                                    goalOwnership[2][1] = BallType.RED;
                                    goalOwnership[2][2] = BallType.BLUE;

                                    autonWinner = AutonWinner.NONE;

                                    redBalls = 9;

                                    blueBalls = 9;

                                    updateGUI();
                                }
                            }

                            Thread.sleep(10);
                        } catch (Exception e) {
                            Log.d("Fox", e.getMessage());
                            if (isConnected) {
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
                Thread automationListener = new Thread(new Runnable() {
                    public void run() {
                        while (true) {
                            try {
                                String str = automation_in.readLine();

                                if (str == null) {
                                    throw new Exception("Connection Dropped");
                                }

                                Thread.sleep(10);
                            } catch (Exception e) {
                                if (isConnected) {
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

    public void setHistoryMethod(HistoryMethod method) {
        sendFoxCommand(ScoreField.HISTORY_METHOD, MessageType.SET, method.getValue());
    }

    public void clearAllScores() {
        sendFoxCommand(ScoreField.CLEAR, MessageType.SET, 1);
    }

    public void setPaused(boolean isPaused) {
        sendFoxCommand(ScoreField.PAUSED, MessageType.SET, isPaused ? 1 : 0);
    }

    public void setGoalOwnership(int x, int y, BallType value) {
        ScoreField field = ScoreField.GOAL_OWNERSHIP_0_0;
        switch(x*3+y) {
            case 0: field = ScoreField.GOAL_OWNERSHIP_0_0; break;
            case 1: field = ScoreField.GOAL_OWNERSHIP_0_1; break;
            case 2: field = ScoreField.GOAL_OWNERSHIP_0_2; break;
            case 3: field = ScoreField.GOAL_OWNERSHIP_1_0; break;
            case 4: field = ScoreField.GOAL_OWNERSHIP_1_1; break;
            case 5: field = ScoreField.GOAL_OWNERSHIP_1_2; break;
            case 6: field = ScoreField.GOAL_OWNERSHIP_2_0; break;
            case 7: field = ScoreField.GOAL_OWNERSHIP_2_1; break;
            case 8: field = ScoreField.GOAL_OWNERSHIP_2_2; break;
        }
        sendFoxCommand(field, MessageType.SET, value.getValue());
        goalOwnership[x][y] = value;
    }

    public void setAutonWinner(AutonWinner value) {
        sendFoxCommand(ScoreField.AUTON, MessageType.SET, value.getValue());
        autonWinner = value;
    }

    public void setRedBalls(int value) {
        value = Math.max(value, 0);
        sendFoxCommand(ScoreField.RED_BALLS, MessageType.SET, value);
        redBalls = value;
    }
    public void addRedBall() {
        setRedBalls(redBalls + 1);
    }
    public void removeRedBall() {
        setRedBalls(redBalls - 1);
    }

    public void setBlueBalls(int value) {
        value = Math.max(value, 0);
        sendFoxCommand(ScoreField.BLUE_BALLS, MessageType.SET, value);
        blueBalls = value;
    }
    public void addBlueBall() {
        setBlueBalls(blueBalls + 1);
    }
    public void removeBlueBall() {
        setBlueBalls(blueBalls - 1);
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