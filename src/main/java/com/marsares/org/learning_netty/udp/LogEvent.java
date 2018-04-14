package com.marsares.org.learning_netty.udp;

import java.net.InetSocketAddress;

/**
 * Created by marsares on 18/4/14.
 */
public class LogEvent {
    public static final byte SEPARATOR=(byte)'|';
    private InetSocketAddress source;
    private String logfile;
    private String msg;
    private long received;

    public LogEvent(String logfile,String msg){
        this(null,-1,logfile,msg);
    }

    public LogEvent(InetSocketAddress source, long received, String logfile, String msg) {
        this.source = source;
        this.logfile = logfile;
        this.msg = msg;
        this.received = received;
    }

    public InetSocketAddress getSource() {
        return source;
    }

    public void setSource(InetSocketAddress source) {
        this.source = source;
    }

    public String getLogfile() {
        return logfile;
    }

    public void setLogfile(String logfile) {
        this.logfile = logfile;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getReceived() {
        return received;
    }

    public void setReceived(long received) {
        this.received = received;
    }
}
