package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Путь к файлу: ");
        TicketList ticketList = new TicketList();

        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();

        Gson gson = new Gson();

        try (FileReader reader = new FileReader(path)) {
            // Преобразование JSON-файла в объект TicketList
            ticketList = gson.fromJson(reader, TicketList.class);

        } catch (JsonSyntaxException | JsonIOException | IOException e) {
            e.printStackTrace();
        }


        task1(ticketList);
        task2(ticketList);
    }

    public static void task1(TicketList ticketList) {
        System.out.println("""
                Задание:
                Минимальное время полета между городами Владивосток и Тель-Авив для каждого авиаперевозчика
                """);

        HashMap<String, Long> map = calcMinimalTime(ticketList.getTickets());
        for (var key : map.keySet()) {
            long value = map.get(key);
            System.out.println("Минимальное время для " + key + " = " + value + " минут");
        }
    }

    public static void task2(TicketList list) {
        System.out.println("""
                Задание:
                Разница между средней ценой и медианой для полета между городами Владивосток и Тель-Авив
                """);
        int dif = diffBetweenMedianAndAverage(list.getTickets(), "TLV", "VVO");
        System.out.println("Разница: " + dif);
    }

    public static long calcTime(String date_dep, String date_arrive) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy H:mm");

        LocalDateTime departure = LocalDateTime.parse(date_dep, formatter);
        LocalDateTime arrival = LocalDateTime.parse(date_arrive, formatter);

        return departure.until(arrival, ChronoUnit.MINUTES);
    }

    public static HashMap<String, Long> calcMinimalTime(List<Ticket> ticketsList) {
        HashMap<String, Long> map = new HashMap<>();
        for (Ticket tk : ticketsList) {
            if (tk.origin.equals("VVO") && tk.destination.equals("TLV")) {
                long time = calcTime(tk.departure_date + " " + tk.departure_time,
                        tk.arrival_date + " " + tk.arrival_time);

                if (map.containsKey(tk.carrier)) {
                    if (map.get(tk.carrier) > time) {
                        map.put(tk.carrier, time);
                    }
                } else {
                    map.put(tk.carrier, time);
                }
            }
        }

        return map;
    }

    public static int diffBetweenMedianAndAverage(List<Ticket> list, String dest, String src) {
        List<Ticket> ticketList = new ArrayList<>();

        for (var item : list) {
            if (item.origin.equals(src) && item.destination.equals(dest))
                ticketList.add(item);
        }

        int[] prices = ticketList.stream().mapToInt(item -> item.price).toArray();
        int average = Arrays.stream(prices).sum() / prices.length;
        Arrays.sort(prices);

        int pos = prices.length / 2;
        if (prices.length % 2 == 0) {
            return Math.abs(average - (prices[pos] + prices[pos - 1]) / 2);
        } else {
            return Math.abs(average - prices[pos]);
        }
    }
}

