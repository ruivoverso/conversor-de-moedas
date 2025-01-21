import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.HashMap;
import java.util.InputMismatchException;

public class conversor {

    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";
    private static final String API_KEY = "0c00c00804e0f74c6913f623"; // chave de API

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String baseCurrency = "USD";
        Map<Integer, String> menuOptions = new HashMap<>();

        menuOptions.put(1, "USD para EUR");
        menuOptions.put(2, "USD para BRL");
        menuOptions.put(3, "EUR para USD");
        menuOptions.put(4, "BRL para USD");
        menuOptions.put(5, "BRL para EUR");
        menuOptions.put(6, "EUR para BRL");

        while (true) {
            System.out.println("=== Conversor de Moedas ===");
            menuOptions.forEach((key, value) -> System.out.println(key + ". " + value));
            System.out.println("7. Sair");

            try {
                System.out.print("Escolha uma opcao: ");
                int option = scanner.nextInt();

                if (option == 7) {
                    System.out.println("Encerrando o programa...");
                    break;
                }

                if (!menuOptions.containsKey(option)) {
                    System.out.println("Opcao invalida. Tente novamente.");
                    continue;
                }

                System.out.print("Digite o valor a ser convertido: ");
                double amount = scanner.nextDouble();

                String[] currencies = menuOptions.get(option).split(" para ");
                String fromCurrency = currencies[0];
                String toCurrency = currencies[1];

                double rate = getExchangeRate(fromCurrency, toCurrency);
                if (rate != -1) {
                    double convertedAmount = amount * rate;
                    System.out.printf("%.2f %s equivale a %.2f %s\n", amount, fromCurrency, convertedAmount, toCurrency);
                } else {
                    System.out.println("Nao foi possivel obter a taxa de conversao. Tente novamente mais tarde.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Entrada invalida. Por favor, insira um numero.");
                scanner.next(); // Limpa a entrada invalida
            }
        }

        scanner.close();
    }

    private static double getExchangeRate(String fromCurrency, String toCurrency) {
        try {
            URL url = new URL(API_URL + fromCurrency + "?apikey=" + API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Erro ao conectar-se à API: " + responseCode);
                return -1;
            }

            Scanner scanner = new Scanner(url.openStream());
            StringBuilder inline = new StringBuilder();
            while (scanner.hasNext()) {
                inline.append(scanner.nextLine());
            }
            scanner.close();

            // parse json usando gson
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(inline.toString(), JsonObject.class);
            JsonObject rates = data.getAsJsonObject("rates");

            if (rates.has(toCurrency)) {
                return rates.get(toCurrency).getAsDouble();
            } else {
                System.out.println("Moeda destino nao encontrada.");
                return -1;
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            return -1;
        }
    }
}
