package me.nallen.fox.app;

public class TcpClient {
    private static TcpClient singleton = new TcpClient();

    private TcpClient() {
    }

    public static TcpClient getInstance() {
        return singleton;
    }

}