package me.nallen.fox.app;

public class TcpClient {
    public static final int CONNECT_OK = 0;
    public static final int CONNECT_FOX_IP_ISSUE = 1;
    public static final int CONNECT_AUTOMATION_IP_ISSUE = 2;

    private static TcpClient singleton = new TcpClient();

    private boolean isConnected = false;

    private TcpClient() {
    }

    public static TcpClient getInstance() {
        return singleton;
    }

    public void logout() {

    }

    public int connect(String fox_ip, ScorerLocation location, String automation_ip) {
        return CONNECT_OK;
    }

    public boolean isConnected() {
        return this.isConnected;
    }
}