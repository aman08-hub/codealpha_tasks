import java.util.*;

class Stock {
    String symbol;
    String name;
    double price;

    Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    void updatePrice() {
        double changePercent = (Math.random() - 0.5) * 0.1;
        price += price * changePercent;
        if (price < 1) price = 1; 
    }

    @Override
    public String toString() {
        return symbol + " (" + name + "): $" + String.format("%.2f", price);
    }
}

class Portfolio {
    double cash;
    Map<String, Integer> holdings;

    Portfolio(double initialCash) {
        this.cash = initialCash;
        this.holdings = new HashMap<>();
    }

    void buy(String symbol, int shares, double price) {
        double cost = shares * price;
        if (cost > cash) {
            System.out.println("Insufficient funds.");
            return;
        }
        cash -= cost;
        holdings.put(symbol, holdings.getOrDefault(symbol, 0) + shares);
        System.out.println("Bought " + shares + " shares of " + symbol + " at $" + String.format("%.2f", price));
    }

    void sell(String symbol, int shares, double price) {
        int owned = holdings.getOrDefault(symbol, 0);
        if (shares > owned) {
            System.out.println("Not enough shares to sell.");
            return;
        }
        cash += shares * price;
        holdings.put(symbol, owned - shares);
        if (holdings.get(symbol) == 0) holdings.remove(symbol);
        System.out.println("Sold " + shares + " shares of " + symbol + " at $" + String.format("%.2f", price));
    }

    double getPortfolioValue(Map<String, Stock> market) {
        double value = cash;
        for (String symbol : holdings.keySet()) {
            Stock stock = market.get(symbol);
            if (stock != null) {
                value += holdings.get(symbol) * stock.price;
            }
        }
        return value;
    }

    void printPortfolio(Map<String, Stock> market) {
        System.out.println("Cash balance: $" + String.format("%.2f", cash));
        System.out.println("Holdings:");
        if (holdings.isEmpty()) {
            System.out.println("  None");
        } else {
            for (String symbol : holdings.keySet()) {
                int shares = holdings.get(symbol);
                Stock stock = market.get(symbol);
                double price = (stock != null) ? stock.price : 0.0;
                System.out.println("  " + symbol + ": " + shares + " shares (Current price: $" + String.format("%.2f", price) + ")");
            }
        }
        System.out.println("Total portfolio value: $" + String.format("%.2f", getPortfolioValue(market)));
    }
}

public class Stock_Trading {
    static Scanner scanner = new Scanner(System.in);
    static Map<String, Stock> market = new HashMap<>();
    static Portfolio portfolio = new Portfolio(10000.0);

    public static void main(String[] args) {
        // Initialize market with some stocks
        market.put("AAPL", new Stock("AAPL", "Apple Inc.", 170.00));
        market.put("GOOG", new Stock("GOOG", "Alphabet Inc.", 2800.00));
        market.put("TSLA", new Stock("TSLA", "Tesla Inc.", 700.00));
        market.put("AMZN", new Stock("AMZN", "Amazon.com Inc.", 3300.00));
        market.put("NFLX", new Stock("NFLX", "Netflix Inc.", 350.00));

        while (true) {
            updateMarketPrices();
            System.out.println("\n--- Stock Trading Platform ---");
            System.out.println("1. View Market Data");
            System.out.println("2. Buy Stock");
            System.out.println("3. Sell Stock");
            System.out.println("4. View Portfolio");
            System.out.println("5. Exit");
            System.out.print("Select option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1: viewMarketData(); break;
                case 2: buyStock(); break;
                case 3: sellStock(); break;
                case 4: portfolio.printPortfolio(market); break;
                case 5: System.exit(0);
                default: System.out.println("Invalid option.");
            }
        }
    }

    static void updateMarketPrices() {
        for (Stock stock : market.values()) {
            stock.updatePrice();
        }
    }

    static void viewMarketData() {
        System.out.println("Market Data:");
        for (Stock stock : market.values()) {
            System.out.println(stock);
        }
    }

    static void buyStock() {
        System.out.print("Enter stock symbol to buy: ");
        String symbol = scanner.nextLine().toUpperCase();
        Stock stock = market.get(symbol);
        if (stock == null) {
            System.out.println("Stock not found.");
            return;
        }
        System.out.print("Enter number of shares to buy: ");
        int shares = scanner.nextInt();
        scanner.nextLine();
        if (shares <= 0) {
            System.out.println("Invalid number of shares.");
            return;
        }
        portfolio.buy(symbol, shares, stock.price);
    }

    static void sellStock() {
        System.out.print("Enter stock symbol to sell: ");
        String symbol = scanner.nextLine().toUpperCase();
        Stock stock = market.get(symbol);
        if (stock == null) {
            System.out.println("Stock not found.");
            return;
        }
        System.out.print("Enter number of shares to sell: ");
        int shares = scanner.nextInt();
        scanner.nextLine();
        if (shares <= 0) {
            System.out.println("Invalid number of shares.");
            return;
        }
        portfolio.sell(symbol, shares, stock.price);
    }
}


