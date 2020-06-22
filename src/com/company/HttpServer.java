package com.company;

import javax.sound.sampled.Port;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.function.DoubleToIntFunction;

public class HttpServer implements Runnable{
    private static String WEB_ROOT = ".";
    private static String DEFAULT_FILE = "/index.html";
    private String CURRENT_PAGE = "/index.html";
    private static int PORT = 8080;
    private Socket socket;

    public HttpServer(Socket socket){
        this.socket = socket;
    }

    public static void main(String[] args) {
	    try {
            ServerSocket serverConnection = new ServerSocket(PORT);
            System.out.println("Server started.\nListening to port "+ PORT);
            while (true){
                HttpServer server = new HttpServer(serverConnection.accept());
                System.out.println("Connection opened on "+new Date());
                Thread thread = new Thread(server);
                thread.start();
            }
        }catch (Exception ex){
            System.err.println("Server Connection error : " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader =new BufferedReader(new InputStreamReader(input));

            String string = reader.readLine();
         //   BufferedInputStream reader2 = new BufferedInputStream(input);
            StringTokenizer parse = new StringTokenizer(string);
            String method = parse.nextToken();
            String page = parse.nextToken();
            page = (getContentType(page) == "text/html") ? page : DEFAULT_FILE;
            System.out.println(string);
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            //binary output to client
            BufferedOutputStream dataOut = new BufferedOutputStream(socket.getOutputStream());
            createHTML(randomizer(), page);
            File file = new File(WEB_ROOT, page);
            int fileLength = (int) file.length();
            byte[] fileData = readFileDate(file, fileLength);
      //      String content = getContentType(fileRequested);
            out.println("HTTP/1.1 200 OK");
            out.println("Server: Java HTTP Server from SSaurel : 1.0");
            out.println("Date: " + new Date());
            out.println("Content-type: " + "text/html");
            out.println("Content-length: " + fileLength);
            out.println(); // blank line between headers and content, very important !
         //   out.println(readFileDataString(file));
            out.flush(); // flush character output stream buffer
            dataOut.write(fileData, 0, fileLength);
            dataOut.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] readFileDate(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] data = new byte[fileLength];
        try{
            fileIn = new FileInputStream(file);
            fileIn.read(data);
        }finally {
            fileIn.close();
        }
        return data;
    }

    private String readFileDataString(File file){
        StringBuilder stringBuilder = new StringBuilder();;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
//            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String content = stringBuilder.toString();
        return content;
    }

    private String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm")  ||  fileRequested.endsWith(".html"))
            return "text/html";
        else
            return "text/plain";
    }

    private boolean createHTML(int numberToShow, String fileName) throws IOException {
        String start = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1 style=\"text-align: center;\">";

        String end = "</h1>\n" +
                "</body>\n" +
                "</html>";
        start+=numberToShow;
        start+=end;

        File file = new File(WEB_ROOT, fileName);
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(fileWriter);
        writer.write(start);
        writer.close();
        return true;
    }

    private int randomizer(){
        Random random = new Random();
        return random.nextInt();
    }


}
