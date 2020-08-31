package org.xtimms.kitsune.utils.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import timber.log.Timber;

public class SSLSocketFactoryExtended extends SSLSocketFactory
{
    private SSLContext mSSLContext;
    private String[] mCiphers;
    private String[] mProtocols;


    public SSLSocketFactoryExtended() throws NoSuchAlgorithmException, KeyManagementException
    {
        initSSLSocketFactoryEx();
    }

    public String[] getDefaultCipherSuites()
    {
        return mCiphers;
    }

    public String[] getSupportedCipherSuites()
    {
        return mCiphers;
    }

    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException
    {
        SSLSocketFactory factory = mSSLContext.getSocketFactory();
        SSLSocket ss = (SSLSocket)factory.createSocket(s, host, port, autoClose);

        ss.setEnabledProtocols(mProtocols);
        ss.setEnabledCipherSuites(mCiphers);

        return ss;
    }

    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException
    {
        SSLSocketFactory factory = mSSLContext.getSocketFactory();
        SSLSocket ss = (SSLSocket)factory.createSocket(address, port, localAddress, localPort);

        ss.setEnabledProtocols(mProtocols);
        ss.setEnabledCipherSuites(mCiphers);

        return ss;
    }

    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException
    {
        SSLSocketFactory factory = mSSLContext.getSocketFactory();
        SSLSocket ss = (SSLSocket)factory.createSocket(host, port, localHost, localPort);

        ss.setEnabledProtocols(mProtocols);
        ss.setEnabledCipherSuites(mCiphers);

        return ss;
    }

    public Socket createSocket(InetAddress host, int port) throws IOException
    {
        SSLSocketFactory factory = mSSLContext.getSocketFactory();
        SSLSocket ss = (SSLSocket)factory.createSocket(host, port);

        ss.setEnabledProtocols(mProtocols);
        ss.setEnabledCipherSuites(mCiphers);

        return ss;
    }

    public Socket createSocket(String host, int port) throws IOException
    {
        SSLSocketFactory factory = mSSLContext.getSocketFactory();
        SSLSocket ss = (SSLSocket)factory.createSocket(host, port);

        ss.setEnabledProtocols(mProtocols);
        ss.setEnabledCipherSuites(mCiphers);

        return ss;
    }

    private void initSSLSocketFactoryEx()
            throws NoSuchAlgorithmException, KeyManagementException
    {
        mSSLContext = SSLContext.getInstance("TLS");
        mSSLContext.init(null, null, null);

        mProtocols = GetProtocolList();
        mCiphers = GetCipherList();
    }

    protected String[] GetProtocolList()
    {
        String[] protocols = { "TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"};
        String[] availableProtocols = null;

        SSLSocket socket = null;

        try
        {
            SSLSocketFactory factory = mSSLContext.getSocketFactory();
            socket = (SSLSocket)factory.createSocket();

            availableProtocols = socket.getSupportedProtocols();
        }
        catch(Exception e)
        {
            return new String[]{ "TLSv1" };
        }
        finally
        {
            if(socket != null)
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
        }

        List<String> resultList = new ArrayList<String>();
        for(int i = 0; i < protocols.length; i++)
        {
            int idx = Arrays.binarySearch(availableProtocols, protocols[i]);
            if(idx >= 0)
                resultList.add(protocols[i]);
        }

        return resultList.toArray(new String[0]);
    }

    protected String[] GetCipherList()
    {
        List<String> resultList = new ArrayList<String>();
        SSLSocketFactory factory = mSSLContext.getSocketFactory();
        for(String s : factory.getSupportedCipherSuites()){
            Timber.tag("CipherSuite type = ").e(s);
            resultList.add(s);
        }
        return resultList.toArray(new String[resultList.size()]);
    }

}