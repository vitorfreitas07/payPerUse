package com.company;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//http://localhost:8080
//http://192.168.90.182:8080

public class Main {


    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8004),0);
        server.createContext("/login", new loginHandler());
        server.createContext("/register", new registerHandler());

        //addClient("joao", "lolada", "1234567");
        //System.out.println("joao" + " has registered in the system!");


        String result = "";
        //listProducts();

        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server up and running... port8004");


        //System.out.println(server.getAddress());
    }



    static class loginHandler implements HttpHandler {
        // Handle incoming calls in threads

        @Override

        public void handle(HttpExchange t) throws IOException {

            Thread thread = new loginThread(t);

            thread.start();

        }

    }

    static class registerHandler implements HttpHandler {
        // Handle incoming calls in threads

        @Override

        public void handle(HttpExchange t) throws IOException {

            Thread thread = new registerThread(t);

            thread.start();

        }

    }



    static class loginThread extends Thread
    {

        private String username, password;
        private HttpExchange t;
        private String query;

        public loginThread(HttpExchange t)
        {
            //System.out.println("teste1");
            this.t = t;
            query = t.getRequestURI().getQuery();
            try
            {
                query = URLDecoder.decode(query, "UTF-8");
            }
            catch(UnsupportedEncodingException e)
            {
                throw new IllegalStateException("Unsupported Encoding exception: ", e);
            }


            if(query != null)
            {
                //System.out.println(query);
                String[] parts = query.split("&");
                username = parts[0];
                password = parts[1];
            }
        }

        @Override
        public void run()
        {
            //System.out.println("Foi ao run");
            String response;
            if(query != null){
                try {
                    if(verifyClient(username,password)>0) //
                    {
                        response = "1";
                        System.out.println(username + " has logged in in the system!");
                        response = URLEncoder.encode(response, "UTF-8");
                    }
                    else
                    {
                        System.out.println(username + " has failed to login in the system!");
                        response = "0";
                        response = URLEncoder.encode(response, "UTF-8");
                    }
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }




    static class registerThread extends Thread

    {

        private String username, password, nif;
        private HttpExchange t;
        private String query;

        public registerThread(HttpExchange t)
        {
            //System.out.println("teste1");
            this.t = t;
            query = t.getRequestURI().getQuery();
            try
            {
                query = URLDecoder.decode(query, "UTF-8");
            }
            catch(UnsupportedEncodingException e)
            {
                throw new IllegalStateException("Unsupported Encoding exception: ", e);
            }


            if(query != null)
            {
                //System.out.println(query);
                String[] parts = query.split("&");
                if(parts.length == 2) {
                    username = parts[0];
                    password = parts[1];
                }
                else{
                    if(parts.length == 3){
                        username = parts[0];
                        password = parts[1];
                        nif = parts[2];
                    }
                }
            }
        }

        @Override
        public void run()
        {
            //System.out.println("Foi ao run");
            String response;
            if(query != null){
                try {
                    if(verifyClient(username,password)>0) // O cliente já existe!
                    {
                        response = "0";
                        System.out.println(username + " is already registered in the system!");
                        response = URLEncoder.encode(response, "UTF-8");
                    }
                    else
                    {
                        addClient(username, password, nif);
                        System.out.println(username + " has registered in the system!");
                        response = "1";
                        response = URLEncoder.encode(response, "UTF-8");
                    }
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }




    public static int verifyClient(String username, String password)
    {
        int result = 0;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/academydb", "root", "");

            String query = "SELECT COUNT(*) as num FROM `clientes` WHERE name = '"+username+"' AND password ='"+password+"'";

            // create the java statement
            Statement st = connection.createStatement();

            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query);

            // Verify if client exists
            while(rs.next()){
                result = rs.getInt("num");
                //System.out.println(result);
            }

            st.close();
            connection.close();

            if(result > 0)
            {
                return 1;
            }
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Database exception: ", e);
        }
        return 0;
    }

    // Check if the client exists and if the username and password pair matches

    public static void addClient(String username, String password, String nif)
    {
        int result = 0;
        try
        {
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/academydb", "root", "");
            // Verify if user already exists
            String query = "INSERT INTO `clientes`(`name`, `password`, `nif`) VALUES ('"+username+"','"+password+"','"+nif+"')";


            // create the java statement
            Statement st = connection.createStatement();

            // execute the query, and get a java resultset
            st.executeUpdate(query);

            st.close();
            connection.close();
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Database exception: ", e);
        }
    }


    public static int listProducts()
    {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> prices=new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/academydb", "root", "");

            String query = "SELECT Name, Price from produtos";

            // create the java statement
            Statement st = connection.createStatement();

            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query);


            // Verify if client exists
            while(rs.next()){
                names.add(rs.getString("name"));
                prices.add(rs.getString("price"));
                //System.out.println(result);
            }

            st.close();
            connection.close();

            if(!names.isEmpty())
            {
                for(int i = 0; i < names.size();i++){
                    //System.out.println(names.get(i)+" "+prices.get(i));
                }
                return 1;
            }
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Database exception: ", e);
        }
        return 0;
    }



    public static int listarHistorico()
    {
        ArrayList<String> names = new ArrayList<>();
        //ArrayList<String> prices=new ArrayList<>();
        ArrayList<String> prices=new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/academydb", "root", "");

            String query = "SELECT Name, Price from produtos"; //historico..

            // create the java statement
            Statement st = connection.createStatement();

            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query);

            // Verify if client exists
            while(rs.next()){
                names.add(rs.getString("name"));
                prices.add(rs.getString("price"));
                //System.out.println(result);
            }

            st.close();
            connection.close();

            if(!names.isEmpty())
            {
                for(String name: names ){
                    //System.out.println(name);
                }
                return 1;
            }
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Database exception: ", e);
        }
        return 0;
    }



}



