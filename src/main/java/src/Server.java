package src;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.sqlite.*;

import com.sun.xml.internal.messaging.saaj.util.Base64;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import src.impl.ServerSocketImpl;

import src.utils.GenericSocket.GenericSocketException;

public class Server implements Runnable {

  @SuppressWarnings("unchecked")
@Override
	public void run() {
        // Ejecución del servidor
        ServerSocketImpl serverSocket = null;
        Main.log.debug("Inicio de servidor");
        try {
            serverSocket = new ServerSocketImpl(8080);
            
            while(true) {
                serverSocket.awaitConnection();
                JSONObject message = serverSocket.readLineFromSocket();
                // Poner aqui restricciones necesarias
                
                JSONArray data = (JSONArray) message.get("data");
                Signature signature = (Signature) message.get("signature");
                String publicKeyEnc = (String) message.get("publicKey");
                
                System.out.println(data);
                System.out.println(signature);
                System.out.println(publicKeyEnc);
                
                Signature sigVer = Signature.getInstance("SHA256withRSA","SC");
                String publicKeyString = Base64.base64Decode(publicKeyEnc);
                PublicKey publicKey = PublicKey.class.cast(publicKeyString);
                sigVer.initVerify(publicKey);
                Boolean flag1 = sigVer.verify(signature.toString().getBytes());
                
                //Comprobamos los datos introducidos
                Boolean mayorQueCero = false;
                Boolean valido = true;
                for(int ind=0;ind<=data.size();ind++){
                	
                	//Tomamos el valor que haya en el campo y miramos si es distinto de cero
                	int valor = (int) data.get(ind);
                	
                	System.out.println("El valor de valor es "+valor);
                	
                	if(valor<0 || valor > 300){
                		
                		valido = false;
                		System.out.println("Valido es: "+valido);
                		break;
                		
                	}
                	
                	if(!mayorQueCero){
                		
                		System.out.println("mayorQueCero es: "+mayorQueCero);
                		mayorQueCero = valor>0;
                		
                	}
                		
                }
                
                
                
                // ...
                // y guardado en BD
                insertData((int) data.get(0),(int) data.get(1),(int) data.get(2),(int) data.get(3),(int) data.get(4));
                // ...

//                // Si todo sale bien, enviar esto:
//                JSONObject result = new JSONObject();
//
//                //Si hay algun valor mayor que cero, si todos los valores estan entre 0 y 300 y si la firma es
//                //correcta, entonces se guarda un success. Si no, se guarda un Incorrect, o lo que sea
//
//                if(flag1 && valido && mayorQueCero){
//                	result.put("status", "Success");
//                	connect();
//                	result.put("s?banas", (int) data.get(0));
//                	result.put("camas", (int) data.get(1));
//                	result.put("mesas", (int) data.get(2));
//                	result.put("sillas", (int) data.get(3));
//                	result.put("minibar", (int) data.get(4));
//                	String saveResult = result.toString();
//                	insertData(saveResult);
//
//                }else{
//
//                	result.put("status", "Failure");
//                }
//                System.out.println(result.toJSONString());
//                serverSocket.writeLineToSocket(result.toJSONString());
//                serverSocket.closeClientSocket();
            }
        } catch(GenericSocketException gse) {
            gse.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
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
                if(serverSocket != null) {
                    serverSocket.closeSocket();
                }
            } catch(GenericSocketException gse) {
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
  
  public void insertData(int sabanas, int camas, int mesas,int sillas, int minibar ) {
      String sql ="INSERT INTO COMPANY (sabanas,camas,mesas,sillas,minibar)\n" +
                "VALUES ("+String.valueOf(sabanas)+","+String.valueOf(camas)+","+String.valueOf(mesas)+","
              +String.valueOf(sillas)+","+String.valueOf(minibar)+")";
      try (Connection conn = Server.connect();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {

          pstmt.executeUpdate();
      } catch (SQLException e) {
          System.out.println(e.getMessage());
      }
  }
  
}
