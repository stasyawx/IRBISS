package com.example.irbis;

public class DatabaseInitializer {
    public static void initializeAllData(FuelService fuelService, GasStationService gasStationService, PumpService pumpService) {
        // Инициализация топлива
        fuelService.addFuel(new Fuel("", "АИ-95 XTRim", 57.99, "Премиальный бензин с улучшенными характеристиками", true));
        fuelService.addFuel(new Fuel("", "АИ-92", 53.49, "Стандартный неэтилированный бензин", true));
        fuelService.addFuel(new Fuel("", "АИ-92 XTRim", 54.49, "Улучшенный неэтилированный бензин", true));
        fuelService.addFuel(new Fuel("", "Дизель", 65.99, "Стандартное дизельное топливо", true));

        // Инициализация АЗС
        gasStationService.addGasStation(new GasStation("", "Техническая", "Техническая ул., 27, Казань", true));
        gasStationService.addGasStation(new GasStation("", "Адоратского", "ул. Адоратского, 50В, Казань", true));
        gasStationService.addGasStation(new GasStation("", "Аделя Кутуя", "ул. Аделя Кутуя, 160Б, Казань", true));

        // Инициализация колонок для каждой АЗС
        // (В реальном приложении нужно сначала получить ID АЗС после их создания)
        // Здесь предполагается, что мы знаем ID АЗС
        pumpService.addPump(new Pump("", "id_азс_1", "1", true));
        pumpService.addPump(new Pump("", "id_азс_1", "2", true));
        pumpService.addPump(new Pump("", "id_азс_2", "1", true));
        pumpService.addPump(new Pump("", "id_азс_2", "2", true));
        pumpService.addPump(new Pump("", "id_азс_3", "1", true));
        pumpService.addPump(new Pump("", "id_азс_3", "2", true));
    }
}