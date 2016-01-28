package me.nallen.fox.app;

public class TcpClient {
    private static TcpClient singleton = new TcpClient();

    private boolean isConnected = false;

    private TcpClient() {
    }

    public static TcpClient getInstance() {
        return singleton;
    }

    public boolean connect(String fox_IP, ScorerLocation location, String automation_ip) {
        return false;
    }

    public boolean isConnected() {
        return this.isConnected;
    }
}