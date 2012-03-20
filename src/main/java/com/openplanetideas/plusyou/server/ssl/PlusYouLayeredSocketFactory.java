/*
 * Copyright (c) 2012, Sony Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the Sony Corporation.
 */

package com.openplanetideas.plusyou.server.ssl;

import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PlusYouLayeredSocketFactory implements LayeredSocketFactory {

    private SSLContext sslContext;

    public PlusYouLayeredSocketFactory() throws IOException {
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new PlusYouX509TrustManager(null)}, null);
        }
        catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(PlusYouLayeredSocketFactory.class);
    }

    @Override
    public int hashCode() {
        return PlusYouLayeredSocketFactory.class.hashCode();
    }

    @Override
    public Socket connectSocket(Socket socket, String host, int port, InetAddress localAddress, int localPort, HttpParams params) throws IOException {
        SSLSocket sslSocket = getSSLSocket(socket);
        if (localAddress != null || localPort > 0) {
            if (localPort < 0) {
                localPort = 0;
            }

            InetSocketAddress isa = new InetSocketAddress(localAddress, localPort);
            sslSocket.bind(isa);
        }

        InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
        int connectionTimeout = HttpConnectionParams.getConnectionTimeout(params);
        int socketTimeout = HttpConnectionParams.getSoTimeout(params);

        sslSocket.connect(remoteAddress, connectionTimeout);
        sslSocket.setSoTimeout(socketTimeout);
        return sslSocket;
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public boolean isSecure(Socket socket) {
        return true;
    }

    private SSLSocket getSSLSocket(Socket socket) throws IOException {
        if (socket == null) {
            return (SSLSocket) createSocket();
        }
        else {
            return (SSLSocket) socket;
        }
    }
}