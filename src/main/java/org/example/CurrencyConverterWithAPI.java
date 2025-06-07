package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

public class CurrencyConverterWithAPI {

    private static final Logger logger = Logger.getLogger(CurrencyConverterWithAPI.class.getName());
    private static final String API_KEY = "82923cbe80ec28745229b18b";

    public static double getExchangeRate(String base, String target) {
        logger.info("Запрашиваем курс из " + base + " в " + target);

        try {
            String urlStr = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/" + base;
            logger.info("URL запроса: " + urlStr);

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            logger.info("HTTP код ответа: " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            logger.fine("Ответ от API (коротко): " + response.substring(0, Math.min(100, response.length())) + "...");

            JSONObject json = new JSONObject(response.toString());

            if (!json.getString("result").equals("success")) {
                logger.warning("Ошибка от API: " + json.getString("error-type"));
                return -1;
            }

            JSONObject rates = json.getJSONObject("conversion_rates");
            double rate = rates.getDouble(target);
            logger.info("Полученный курс: 1 " + base + " = " + rate + " " + target);

            return rate;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при запросе курса", e);
            return -1;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        logger.info("Программа запущена");
        logger.setLevel(Level.ALL);

        System.out.print("Введите сумму: ");
        double amount = scanner.nextDouble();

        System.out.print("Из какой валюты (например, USD): ");
        String fromCurrency = scanner.next().toUpperCase();

        System.out.print("В какую валюту (например, EUR): ");
        String toCurrency = scanner.next().toUpperCase();

        logger.info("Пользователь хочет конвертировать " + amount + " " + fromCurrency + " в " + toCurrency);

        double rate = getExchangeRate(fromCurrency, toCurrency);

        if (rate != -1) {
            double result = amount * rate;
            System.out.printf("%.2f %s = %.2f %s\n", amount, fromCurrency, result, toCurrency);
            logger.info("Результат конвертации: " + result);
        } else {
            System.out.println("Не удалось получить курс валют.");
            logger.warning("Конвертация не выполнена из-за ошибки");
        }

        scanner.close();
        logger.info("Программа завершена");
    }
}
