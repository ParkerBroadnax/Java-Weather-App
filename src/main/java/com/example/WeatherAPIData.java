package com.example;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WeatherAPIData{
    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in)){
            //Close memory leak
            String city;

            do{
                //Get user input
                System.out.println("---------------");
                System.out.print("Enter city name (Type no to quit)");
                city = scanner.nextLine();

                if(city.equalsIgnoreCase("No")) break;

                //Get location data
                JSONObject cityLocationData = (JSONObject) getLocationData(city);
                double latitude = (double) cityLocationData.get("latitude");
                double longitude = (double) cityLocationData.get("longitude");

                displayWeatherData(latitude, longitude); 

            }while (!city.equalsIgnoreCase("No"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private static JSONObject getLocationData(String city){
        city = city.replaceAll(" ", "+");

        //Add Api key here:
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=&count=1&language=en&format=json";
            city = "&count=1&language=en&format=json";

        try{
            //Fetch the API response based on API Link
            HttpURLConnection apiConnection = fetchApiResponse(urlString);

            //check for response status 
            //200 - means that the connection was a success

            if(apiConnection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }

            //2. Read the response and convert store String type
            String jsonResponse = readApiResponse(apiConnection);

            //3. Parse the string into a JSON Object
            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(jsonResponse);

            //4. Retrieve Location Data 
            //What's key about?
            JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
            return (JSONObject) locationData.get(0);
        }

        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private static void displayWeatherData(double latitude, double longitude){
        try{
            //1. Fetch the API response based on API Link
            //Make sure to add API Link
            String url = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&hourly=temperature_2m&current=temperature_2m,relative_humidity_2m,wind_speed_10m" + latitude + "&longitude=" + longitude + "&current=temperature_2m,relative_humidity_2m,wind_speed_10m";
            HttpURLConnection apiConnection = fetchApiResponse(url);

            //check for response status
            //200 - means that the connection worked 
            if(apiConnection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return;
            }

            //2. Read the response and convert store String type 
            String jsonResponse = readApiResponse(apiConnection);

            //3. Parse the string into a JSON Object
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
            JSONObject currentWeatherJson = (JSONObject) jsonObject.get("current");

            //4. Store the data into their corresponding data type
            String time = (String) currentWeatherJson.get("time");
            System.out.println("Current Time: " + time);

            double temperature = (double) currentWeatherJson.get("temperature_2m");
            System.out.println("Current Temperature (C): " + temperature);

            double relativeHumidity = (long) currentWeatherJson.get("relative_humidity_2m");
            System.out.println("Relative Humidity (C): " + relativeHumidity);

            double windSpeed = (double) currentWeatherJson.get("wind_speed_10m");
            System.out.println("Weather Description: " + windSpeed);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private static String readApiResponse(HttpURLConnection apiConnection){
        try{
            //Create a string builder to store the resulting JSON data
            //Why use Stringbuilder over string
            StringBuilder resultJson = new StringBuilder();

            //Create a Scanner to read from the InputStream of the HttpURLConnection
            Scanner scanner = new Scanner(apiConnection.getInputStream());

            //Loop through each line in the response and append it to the StringBuilder
            while(scanner.hasNext()){
                //Read and append the current line to the StringBuilder
                resultJson.append(scanner.nextLine());
            }

            //Close scanner to release resources associated with it
            scanner.close();

            //Return the JSON data as a String
            return resultJson.toString();
        }

        catch(IOException e){
            //Print the exception details in case of an IOException
            e.printStackTrace();
        }

        return null;

    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            //attemp to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //Set request method to get
            conn.setRequestMethod("GET");

            return conn;
        }

        catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }
}