package flight.fare;

import flight.data.ExchangeRateProvider;
import flight.model.FlightRequest;

/**
 * BEFORE refactoring: all pricing variants live in one class and are chosen by switch.
 * Adding a new variant means editing this class (violates the Open/Closed Principle).
 */
public class FareCalculator {

    private FareType type;
    private final ExchangeRateProvider rates;

    public FareCalculator(FareType type, ExchangeRateProvider rates) {
        this.type = type;
        this.rates = rates;
    }

    public void setFareType(FareType type) {
        this.type = type;
    }

    public double calculate(FlightRequest request) {
        double base = request.distanceKm() * Prices.BASE_RATE_PER_KM;
        double price;

        switch (type) {
            case DISTANCE: {
                price = base + Prices.TAX;
                break;
            }
            case DEMAND: {
                double factor;
                if (request.occupancyPercent() >= 80) {
                    factor = 1.8;
                } else if (request.occupancyPercent() >= 50) {
                    factor = 1.3;
                } else {
                    factor = 1.0;
                }
                price = base * factor;
                break;
            }
            case LAST_MINUTE: {
                double factor;
                int days = request.daysBeforeDeparture();
                if (days <= 2) {
                    factor = 1.9;
                } else if (days <= 6) {
                    factor = 1.4;
                } else if (days <= 14) {
                    factor = 1.15;
                } else {
                    factor = 1.0;
                }
                price = base * factor;
                break;
            }
            case INTERNATIONAL: {
                double usd = request.distanceKm() * 0.06 + 25.0;
                price = usd * rates.getRate("USD", "KZT");
                break;
            }
            default:
                throw new IllegalStateException("Unknown fare type: " + type);
        }
        return Prices.round2(price);
    }
}
