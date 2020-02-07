/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peertopeer;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PeerToPeer {
    Main obj;
    DataOutputStream dos;
    DataInputStream dis;
    ObjectOutputStream os=null;
    ObjectInputStream is=null;
    Socket s=null;
    Thread t1;
    void function(Main obj){
        this.obj=obj;
        try {
            System.out.println(Main.ip);
            s = new Socket(Main.ip, 5056);
            Client();
        }
        catch (Exception ex) {
            System.out.println("ex");
            
            Server();
            System.out.println("Function");
            
        }
    }
    void Server(){
        ServerSocket ss; 
        try {
            ss = new ServerSocket(5056);
            s=ss.accept();
            System.out.println("A new client is connected : " + s); 
                  
                // obtaining input and out streams 
            //dos = new DataOutputStream(s.getOutputStream());
            //dis = new DataInputStream(s.getInputStream()); 
            is = new ObjectInputStream(s.getInputStream());
            os = new ObjectOutputStream(s.getOutputStream());
          
            //os.flush();
            System.out.println("input Oject stream established");
             //is = new ObjectInputStream(s.getInputStream());
            System.out.println("Oject stream established");
            receive();   
        } catch (Exception ex) {
            System.out.println("Server");
            Logger.getLogger(PeerToPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void Client(){
        
        try {
            //s = new Socket(Main.ip, 5056);
            System.out.println("Client socket established");
                // obtaining input and out streams 
            //dos = new DataOutputStream(s.getOutputStream());
            //dis = new DataInputStream(s.getInputStream());
           os = new ObjectOutputStream(s.getOutputStream()); 
          
//            
           System.out.println("Oject output stream established");
            is = new ObjectInputStream(s.getInputStream());
          
          System.out.println(" Oject input stream established ");
           
           
            
            receive();
        } catch (Exception ex) {
            System.out.println("Client");    
               Logger.getLogger(PeerToPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
      
        
    }
    void receive(){
        t1=new Thread(){
                public void run(){
                    while(true){
                        try {
                            System.out.println("receive while(true)");
                            //String received = dis.readUTF();
                            Message m = (Message) is.readObject();
                            System.out.println("after readUTF "+m.str);
                            //System.out.println("after readUTF "+received);
                            
                            obj.showMessage(m.str);
                        } catch (Exception ex) {
                            System.out.println("receive");
                            Logger.getLogger(PeerToPeer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                }
            };
        t1.start();
    }
    
    void send(String send){
        try {
            
                System.out.println("in send");
                //dos.writeUTF(send);
                //dos.flush();
                Message message = new Message(send);
                os.writeObject(message);
                os.flush();
                System.out.println("after flush"+send);
            
        } catch (Exception ex) {
            System.out.println("Send");
            Logger.getLogger(PeerToPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

