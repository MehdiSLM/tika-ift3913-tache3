package org.apache.tika.tach3Securite;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class UnsafeTLS implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}