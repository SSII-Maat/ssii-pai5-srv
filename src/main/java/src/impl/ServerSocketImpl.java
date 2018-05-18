package src.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import src.Main;

public class ServerSocketImpl extends GenericSocketImpl {
    private ServerSocket socket;
    private Socket clientSocket;
    private BufferedWriter outputStream;
    private BufferedReader inputStream;

    public ServerSocketImpl(int portNumber) throws GenericSocketException {
        super();

        try {
            this.socket = ServerSocketFactory.getDefault().createServerSocket(portNumber);

            Main.log.info("S - Socket creado");
            Main.log.info("S - Conexión establecida con cliente");
            Main.log.info("S - Realizado el handshake de SSL");           
        } catch (Exception e) {
            throw new GenericSocketException("S - "+e.getMessage());
        }
    }

    public void awaitConnection() throws GenericSocketException {
        try {
            this.clientSocket = this.socket.accept();
            this.outputStream = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
            this.inputStream = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        } catch(IOException ioe) {
            throw new GenericSocketException("Failed to establish connection");
        }
    }

    public JSONObject readLineFromSocket() throws GenericSocketException {
        JSONObject result = new JSONObject();

        // Leemos la petición entera
        String message = new String();
        BufferedReader br = this.inputStream;
        try {
            message = new String();
            while(true) {
                String line = br.readLine();
                if(line != null) {
                    message = message.concat(line);
                    if(line.contains("}")) {
                        break;
                    }
                } else {
                    break;
                }
            }
            System.out.println(message);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            throw new GenericSocketException("Failed to receive request");
        }
        
        // Quitamos el header
        String body = message.substring(message.indexOf("{"), message.lastIndexOf("}") + 1);
        // Obteniendo el mensaje y lo adaptamos a JSON
        try {
            result = (JSONObject) new JSONParser().parse(body);
        } catch(ParseException pe) {
            System.out.println("Failed to parse incoming JSON Object\nCause of exception: "+pe.getMessage());
        }

        return result;
    }

	public void writeLineToSocket(String line) throws GenericSocketException {
        BufferedWriter bw = this.outputStream;
        try {
            System.out.println("Output ready");
            bw.write(line);
            System.out.println("Written data");
            bw.flush();
        } catch (Exception ioe) {
            throw new GenericSocketException("Failed to write to client socket\nException: "+ioe.getMessage());
        }
    }

    public void closeClientSocket() throws GenericSocketException {
        try {
            this.clientSocket.close();
        } catch(IOException ioe) {
            throw new GenericSocketException("Failed to close client socket");
        }
    }

    public void closeSocket() throws GenericSocketException {
        try {
            //this.bufferedWriter.close();
            //this.bufferedReader.close();
            this.clientSocket.close();
            this.socket.close();
        } catch(IOException ioe) {
            throw new GenericSocketException(ioe.getMessage());
        }
    }
}
