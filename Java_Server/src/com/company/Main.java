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
        server.createContext("/categorias", new categoriasHandler());
        server.createContext("/search", new searchHandler());
        server.createContext("/catalogo", new catalogoHandler());
        server.createContext("/historico", new historicoHandler());
        //getHistorico();


        //addClient("joao", "lolada", "1234567");
        //System.out.println("joao" + " has registered in the system!");


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

    static class categoriasHandler implements HttpHandler {
        // Handle incoming calls in threads

        @Override

        public void handle(HttpExchange t) throws IOException {

            Thread thread = new categoriasThread(t);

            thread.start();

        }

    }

    static class searchHandler implements HttpHandler {
        // Handle incoming calls in threads

        @Override

        public void handle(HttpExchange t) throws IOException {

            Thread thread = new searchThread(t);

            thread.start();

        }

    }


    static class catalogoHandler implements HttpHandler {
        // Handle incoming calls in threads

        @Override

        public void handle(HttpExchange t) throws IOException {

            Thread thread = new catalogoThread(t);
            thread.start();

        }

    }

    static class historicoHandler implements HttpHandler {
        // Handle incoming calls in threads

        @Override
        public void handle(HttpExchange t) throws IOException {

            Thread thread = new historicoThread(t);
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

    static class categoriasThread extends Thread
    {

        private String username, password;
        private HttpExchange t;
        private String query;

        public categoriasThread(HttpExchange t)
        {
            //System.out.println("teste1");
            this.t = t;
            query = t.getRequestURI().getQuery();
            /*try
            {
                query = URLDecoder.decode(query, "UTF-8");
            }
            catch(UnsupportedEncodingException e)
            {
                throw new IllegalStateException("Unsupported Encoding exception: ", e);
            }*/
        }

        @Override
        public void run()
        {
            String response = "";
            if(query != null){
                try {
                    if(!getCategorias().equals("")){
                        response = getCategorias();
                        response = URLEncoder.encode(response, "UTF-8");
                    }else{
                        response = "0";
                    }

                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    static class searchThread extends Thread
    {

        private String username, password;
        private HttpExchange t;
        private String query;

        public searchThread(HttpExchange t)
        {
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
        }

        @Override
        public void run()
        {
            String response = "";
            if(query != null){
                try {
                    response = searchProduct(query);
                    if(response.equals("")){
                        response = URLEncoder.encode(response, "UTF-8");
                    }else{
                        response = "0";
                    }

                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }



    static class catalogoThread extends Thread
    {

        private String username, password;
        private HttpExchange t;
        private String query;

        public catalogoThread(HttpExchange t)
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
        }

        @Override
        public void run()
        {
            String response = "";
            if(query != null){
                try {
                    if(!getCatalogo().equals("")){
                        response = getCatalogo();
                        response = URLEncoder.encode(response, "UTF-8");
                    }else{
                        response = "0";
                    }

                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    static class historicoThread extends Thread
    {

        private String username, password;
        private HttpExchange t;
        private String query;

        public historicoThread(HttpExchange t)
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
        }

        @Override
        public void run()
        {
            String response = "";
            if(query != null){
                try {
                    if(!getHistorico().equals("")){
                        response = getHistorico();
                        response = URLEncoder.encode(response, "UTF-8");
                    }else{
                        response = "0";
                    }

                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
                catch (IOException e)
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

            String query = "SELECT COUNT(*) as num FROM `clientes` WHERE email = '"+username+"' AND password ='"+password+"'";

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
        String query = "";
        try
        {
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/academydb", "root", "");


                if(nif == null){
                    query = "INSERT INTO clientes(email, password) VALUES ('"+username+"','"+password+"')";
                }else{
                    query = "INSERT INTO clientes(email, password, nif) VALUES ('"+username+"','"+password+"','"+nif+"')";
                }


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

    public static String getCategorias()
    {
        int result = 0;
        String response = "";
        try
        {
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/academydb", "root", "");
            // Verify if user already exists
            String query = "";//"INSERT INTO `clientes`(`name`, `password`, `nif`) VALUES ('"+username+"','"+password+"','"+nif+"')";


            // create the java statement
            Statement st = connection.createStatement();

            // execute the query, and get a java resultset
            st.executeQuery(query);

            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query);

            // Verify if client exists
            while(rs.next()){
                response = response + rs.getString("type");
                //System.out.println(result);
            }

            st.close();
            connection.close();
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Database exception: ", e);
        }

        return response;
    }





    public static String getCatalogo()
    {
        //criar listas para os varios atributos do produto procurado
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> prices = new ArrayList<>();
        ArrayList<String> type = new ArrayList<>();
        ArrayList<String> aisle = new ArrayList<>();
        ArrayList<String> shelf = new ArrayList<>();

        String response = "";
        try
        {
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/academydb", "root", "");
            // Verify if user already exists
            String query = "Select Name, Price,Type,Aisle,Shelf from produtos left join stocks on stocks.product_id=produtos.product_id order by Name asc";


            // create the java statement
            Statement st = connection.createStatement();

            // execute the query, and get a java resultset
            st.executeQuery(query);

            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query);

            // Verify if client exists
            while(rs.next()){
                names.add(rs.getString("name"));
                type.add(rs.getString("type"));
                aisle.add(rs.getString("aisle"));
                shelf.add(rs.getString("shelf"));
                //System.out.println(result);
            }


            for(int i = 0; i < names.size();i++){
                response = response + names.get(i)+ "," + type.get(i)+ "," + aisle.get(i) + "," + shelf.get(i)+"&";
            }

            if (response != null && response.length() > 0 && response.charAt(response.length() - 1) == '&') {
                response = response.substring(0, response.length() - 1);
            }
            System.out.println(response);


            //response = response +;
                    st.close();
            connection.close();
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Database exception: ", e);
        }

        return response;
    }


    public static String getHistorico()
    {
        //criar listas para os varios atributos do produto procurado
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> quantities = new ArrayList<>();
        ArrayList<String> total = new ArrayList<>();

        String response = "";
        try
        {
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/academydb", "root", "");
            // Verify if user already exists
            String query = "Select Date,Name,Quantity,round(Quantity*Price*(1-Discount),2) as Total from produtos LEFT JOIN historico ON historico.product_id = produtos.product_id WHERE Date is not null ORDER BY date desc";


            // create the java statement
            Statement st = connection.createStatement();

            // execute the query, and get a java resultset
            st.executeQuery(query);

            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query);

            // Verify if client exists
            while(rs.next()){
                dates.add(rs.getString("date"));
                names.add(rs.getString("name"));
                quantities.add(rs.getString("quantity"));
                total.add(rs.getString("total"));
                //System.out.println(result);
            }


            for(int i = 0; i < names.size();i++){
                response = response + dates.get(i)+ "," + names.get(i) + "," + quantities.get(i)+ "," + total.get(i) + "&";
            }

            if (response != null && response.length() > 0 && response.charAt(response.length() - 1) == '&') {
                response = response.substring(0, response.length() - 1);
            }
            System.out.println(response);


            //response = response +;
            st.close();
            connection.close();
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Database exception: ", e);
        }

        return response;
    }


    public static String searchProduct(String toBeSearched)
    {
        //criar listas para os varios atributos do produto procurado
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> prices = new ArrayList<>();
        ArrayList<String> brand = new ArrayList<>();

        String response = "";
        try
        {
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/academydb", "root", "");
            // Verify if user already exists
            String query = "select name, price, brand from produtos where name like '%"+ toBeSearched +"%'";


            // create the java statement
            Statement st = connection.createStatement();

            // execute the query, and get a java resultset
            st.executeQuery(query);

            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query);

            // Verify if client exists
            while(rs.next()){

                names.add(rs.getString("name"));
                prices.add(rs.getString("price"));
                brand.add(rs.getString("brand"));
                //System.out.println(result);
            }


            for(int i = 0; i < names.size();i++){
                response = response + names.get(i)+ "," + prices.get(i) + "," + brand.get(i) + "&";
            }

            if (response != null && response.length() > 0 && response.charAt(response.length() - 1) == '&') {
                response = response.substring(0, response.length() - 1);
            }
            System.out.println(response);


            //response = response +;
            st.close();
            connection.close();
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Database exception: ", e);
        }

        return response;
    }



}



