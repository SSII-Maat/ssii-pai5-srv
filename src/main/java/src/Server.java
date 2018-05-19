package src;

import java.io.ByteArrayInputStream;

import java.nio.charset.StandardCharsets;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.xml.bind.DatatypeConverter;

import com.sun.xml.messaging.saaj.util.Base64;

import org.sqlite.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import src.impl.ServerSocketImpl;

import src.utils.GenericSocket.GenericSocketException;

public class Server implements Runnable {

    private static final String certificate = "-----BEGIN CERTIFICATE-----\n"
            + "MIICeTCCAeKgAwIBAgIJANPLhubuNn8LMA0GCSqGSIb3DQEBCwUAMFQxCzAJBgNV\n"
            + "BAYTAkVTMRAwDgYDVQQIDAdTZXZpbGxlMRAwDgYDVQQHDAdTZXZpbGxlMSEwHwYD\n"
            + "VQQKDBhJbnRlcm5ldCBXaWRnaXRzIFB0eSBMdGQwHhcNMTgwNTE4MTk0NjI4WhcN\n"
            + "MTkwNTE4MTk0NjI4WjBUMQswCQYDVQQGEwJFUzEQMA4GA1UECAwHU2V2aWxsZTEQ\n"
            + "MA4GA1UEBwwHU2V2aWxsZTEhMB8GA1UECgwYSW50ZXJuZXQgV2lkZ2l0cyBQdHkg\n"
            + "THRkMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCtFAZRQ+VOW4btawS42C6Q\n"
            + "FP3BNu4om6/IBrP0i5TFmaBKzlDKE2+JuJWbP0PQur/+4tG71GUDfy1Ot+n3M4Hl\n"
            + "cHvQ0/1wmtBU3F29kf6ObcJdk3cOghXzbP5eHvMoIbFNqHzpugjT6Ym7gp9E4WmY\n"
            + "clP8wh2OXe7Ebc8PqOziawIDAQABo1MwUTAdBgNVHQ4EFgQUyLUTCVsvomMS/LVL\n"
            + "nfSK8nXTRNMwHwYDVR0jBBgwFoAUyLUTCVsvomMS/LVLnfSK8nXTRNMwDwYDVR0T\n"
            + "AQH/BAUwAwEB/zANBgkqhkiG9w0BAQsFAAOBgQCZqshStHWxenz30S655ANn5Scd\n"
            + "TfFR/tDRnpXkKSvZn1ys0iLgMZJRncN1o0Za2O9GeyYX5G4OVlP/4Zt2l4OkKNNs\n"
            + "l8gh2cW7r0zx1TynHTHuSWkEjQXJYTO1UKG4lFPIcPsKshtX4Pmgd22MVtAMxwHm\n" + "dwBeo2gqq4zCrFcnRA==\n"
            + "-----END CERTIFICATE-----";
    private static PublicKey publicKey;

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        // Ejecuci�n del servidor
        ServerSocketImpl serverSocket = null;
        Main.log.debug("Inicio de servidor");
        try {
            CertificateFactory certFact = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFact
                    .generateCertificate(new ByteArrayInputStream(certificate.getBytes(StandardCharsets.UTF_8)));
            publicKey = cert.getPublicKey();
        } catch (CertificateException ce) {
            ce.printStackTrace();
        }
        try {
            serverSocket = new ServerSocketImpl(4430);

            while (true) {
                serverSocket.awaitConnection();
                // Generamos nonce
                Long nonce = new SecureRandom().nextLong();

                // Lo enviamos al cliente para que pueda calcular el proceso
                serverSocket.writeLineToSocket(nonce.toString() + '\n');

                JSONObject message = serverSocket.readLineFromSocket();
                // Poner aqui restricciones necesarias

                JSONArray data = (JSONArray) message.get("data");
                String signatureString = (String) message.get("signature");
                byte[] signature = DatatypeConverter.parseHexBinary(signatureString); // Es necesario hacer el replace
                                                                                      // porque se introducen caracteres
                                                                                      // extraños

                Signature sigVer = Signature.getInstance("SHA256withRSA");
                sigVer.initVerify(publicKey);
                sigVer.update(data.toString().getBytes());
                Boolean flag1 = sigVer.verify(signature);

                // Comprobamos los datos introducidos
                Boolean mayorQueCero = false;
                Boolean valido = true;
                // Este bucle es hasta menos 1 ya que el nonce ocupará la última posición
                for (int ind = 0; ind < data.size() - 2; ind++) {
                    // Tomamos el valor que haya en el campo y miramos si es distinto de cero
                    long valor = (long) data.get(ind);

                    System.out.println("El valor de valor es " + valor);

                    if (valor < 0 || valor > 300) {
                        valido = false;
                        System.out.println("Valido es: " + valido);
                        break;
                    }

                    if (!mayorQueCero) {
                        System.out.println("mayorQueCero es: " + mayorQueCero);
                        mayorQueCero = valor > 0;
                    }
                }

                // Si todo sale bien, enviar esto:
                JSONObject result = new JSONObject();

                // Si hay algun valor mayor que cero, si todos los valores estan entre 0 y 300 y
                // si la firma es
                // correcta, entonces se guarda un success. Si no, se guarda un Incorrect, o lo
                // que sea

                if (flag1 && valido && mayorQueCero && (long) data.get(data.size()-1) == nonce) {
                    result.put("status", "Success"); 
                    connect();
                    result.put("sabanas", (long) data.get(0));
                    result.put("camas", (long) data.get(1));
                    result.put("mesas", (long) data.get(2));
                    result.put("sillas", (long) data.get(3));
                    result.put("minibar", (long) data.get(4));
                    insertData((long) data.get(0), (long) data.get(1), (long) data.get(2), (long) data.get(3), (long) data.get(4));
                } else {
                    result.put("status", "Failure");
                }

                System.out.println(result.toJSONString());
                serverSocket.writeLineToSocket(result.toJSONString());
                serverSocket.closeClientSocket();
            }
        } catch (GenericSocketException gse) {
            gse.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SignatureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.closeSocket();
                }
            } catch (GenericSocketException gse) {
                // Algo muy malo debe estar pasando, emitimos en pantalla el stack
                Main.log.warn(gse.getMessage());
            }
        }
    }

    private static Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:database.sqlite";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void insertData(long sabanas, long camas, long mesas, long sillas, long minibar) {
        String sql = "INSERT INTO DATA (sabanas,camas,mesas,sillas,minibar)\n" + "VALUES (" + String.valueOf(sabanas)
                + "," + String.valueOf(camas) + "," + String.valueOf(mesas) + "," + String.valueOf(sillas) + ","
                + String.valueOf(minibar) + ")";
        try (Connection conn = Server.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
