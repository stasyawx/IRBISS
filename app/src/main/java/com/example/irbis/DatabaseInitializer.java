package com.example.irbis;

public class DatabaseInitializer {
    public static void initializeFuels(FuelService fuelService) {
        fuelService.addFuel(new Fuel("", "АИ-95 XTRim", 57.99, "Премиальный бензин с улучшенными характеристиками", true));
        fuelService.addFuel(new Fuel("", "АИ-92", 53.49, "Стандартный неэтилированный бензин", true));
        fuelService.addFuel(new Fuel("", "АИ-92 XTRim", 54.49, "Улучшенный неэтилированный бензин", true));
        fuelService.addFuel(new Fuel("", "Дизель", 65.99, "Стандартное дизельное топливо", true));
    }
}