package peertopeer;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PeerToPeer {
    Main obj;
    ObjectOutputStream os=null;
    ObjectInputStream is=null;
    Socket s=null;
    Thread t1;
    private boolean isConnected = false;
    String sourceFilePath ;
    private FileEvent fileEvent = null;
    private String destinationPath = "./";
    private File dstFile = null;
    private FileOutputStream fileOutputStream = null;String showMessage;

    void function(Main obj){
        this.obj=obj;
        try {
           
            s = new Socket(Main.ip, 5056);//making connection with server
            Client();//if get connected with server then this will work as client otherwise will go on catch part ans call for server.
        }
        catch (Exception ex) {
            
            
            Server();//work as server if no server of that ip is there..
            
            
        }
    }
    void Server(){
        ServerSocket ss; 
        try {
            ss = new ServerSocket(5056); // making server to work at port 5056
            s=ss.accept();//waiting for client
           
                   
            is = new ObjectInputStream(s.getInputStream()); // making object input stream for accpeting messages and files
            os = new ObjectOutputStream(s.getOutputStream());// making object output stream for sending messages and files
          
            
            receive();//calling function that will recieve messqages and files   
        } catch (Exception ex) {
            
            Logger.getLogger(PeerToPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void Client(){
        
        try {
           
           
           os = new ObjectOutputStream(s.getOutputStream()); // making object output stream for sending messages and files
          
            is = new ObjectInputStream(s.getInputStream());// making object input stream for accpeting messages and files      
            receive(); // receiving messages from client side 
        } catch (Exception ex) {
              
               Logger.getLogger(PeerToPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
      
        
    }
    void receive(){
        // thread is for handling sending and receiving of messages any time
        t1=new Thread(){
                public void run(){
                    while(true){
                        try {
                           
                            Object oob = is.readObject();
                            
                            if(oob instanceof Message){ //checking for receiving object  as of message or for file
                                Message m=(Message)oob;
                          
                                obj.showMessage(m.str);
                            }
                            else{
                                downloadFile(oob);  // if file type then for saving it we call downloadfile
                           
                                obj.showMessage(showMessage);
                            }
                         
                            
                            
                        } catch (Exception ex) {
                            try {
                                is.close();os.close();s.close(); //closing all streams
                                break;
                            } catch (IOException ex1) {
                                Logger.getLogger(PeerToPeer.class.getName()).log(Level.SEVERE, null, ex1);
                            }

                            
                           
                            Logger.getLogger(PeerToPeer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                }
            };
        t1.start();// starting the thread
    }
    
    void send(String send){
        try {
                Message message = new Message(send);// creating message type object 
                os.writeObject(message);//sending ou message
                os.flush();
          
            
        } catch (Exception ex) {
            System.out.println("Send");
            Logger.getLogger(PeerToPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //for sending file  
    public void sendFile() {
        fileEvent = new FileEvent();//making object for sending file
        String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());// extracting the file name
        String path = sourceFilePath.substring(0, sourceFilePath.lastIndexOf("/") + 1); // setting path name
        fileEvent.setDestinationDirectory(destinationPath);
        fileEvent.setFilename(fileName);
        fileEvent.setSourceDirectory(sourceFilePath);
        fileEvent.userName=Main.name;
        File file = new File(sourceFilePath);//taking the selected file
        if (file.isFile()) {
        try {
        DataInputStream diStream = new DataInputStream(new FileInputStream(file));
        long len = (int) file.length();
        byte[] fileBytes = new byte[(int) len];//saving bytes by bytes
        int read = 0;
        int numRead = 0;
        while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {   //
        read = read + numRead;
        }
        fileEvent.setFileSize(len);
        fileEvent.setFileData(fileBytes);//setting the bytes array 
        fileEvent.setStatus("Success");//for successful saving of all content for sending file
        } catch (Exception e) {
        e.printStackTrace();
        fileEvent.setStatus("Error");
        }
        } else {
        System.out.println("path specified is not pointing to a file");
        fileEvent.setStatus("Error");
        }
        //Now writing the FileEvent object to socket
        try {
        os.writeObject(fileEvent);// sending file object
        System.out.println("Done...Going to exit");
        Thread.sleep(3000);

        } catch (IOException e) {
        e.printStackTrace();
        } catch (InterruptedException e) {
        e.printStackTrace();
        }

    }
    // for saving the received file  
    public void downloadFile(Object oob) {
        try {
        FileEvent fileEvent=(FileEvent)oob; // converting received object type to file object type
        if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
            System.out.println("Error occurred ..So exiting");
        
        }
        showMessage= fileEvent.userName+": "+fileEvent.getDestinationDirectory()+fileEvent.getFilename();
        String outputFile = fileEvent.getDestinationDirectory() + fileEvent.getFilename();
        if (!new File(fileEvent.getDestinationDirectory()).exists()) { //checking this file already exits or not 
        new File(fileEvent.getDestinationDirectory()).mkdirs();
        }
        dstFile = new File(outputFile);
        fileOutputStream = new FileOutputStream(dstFile);
        fileOutputStream.write(fileEvent.getFileData());
        fileOutputStream.flush();
        Thread.sleep(3000);// giving time to download file


        } catch (IOException e) {
        e.printStackTrace();
        } catch (InterruptedException e) {
        e.printStackTrace();
        }
    }
    // closing all streams
    void close(){
        try {
            System.out.println("Socket Closed");
            os.close();
            is.close();
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(PeerToPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}

