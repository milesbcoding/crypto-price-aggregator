# Ktor Price Aggregator

A lightweight cryptocurrency price aggregator built using Kotlin and [Ktor](https://ktor.io/), fetching real-time data exclusively from **Coinbase**.

---

## 🚀 How to Run the Application Locally

### Prerequisites
- Java 17 or newer
- Kotlin (bundled via Gradle)
- Internet connection (to reach Coinbase API)

### Steps

1. **Clone the repository**:
   ```bash
   git clone https://github.com/milesbcoding/crypto-price-aggregator
   cd crypto-price-aggregator
   ```

2. **Run the application using the Gradle wrapper**:
   ```bash
   ./gradlew run
   ```

3. **Server will start at**:
   ```
   http://0.0.0.0:8080
   ```

---

## 🔍 Example API Usage

### GET `/prices/{symbol}`

Returns the current price for a cryptocurrency pair from Coinbase.

#### Example Request
```bash
curl http://0.0.0.0:8080/prices/BTC-USD
```

#### Example Response
```json
{
  "symbol": "BTC-USD",
  "price": "64235.67",
  "source": "Coinbase",
  "timestamp": "2025-05-08T14:52:30Z"
}
```

#### Available Symbols
```bash
  ["BTC-USD", "ETH-USD", "ETH-BTC"]
```
> Suggestion: I would suggest using postman for testing API calls


> Note: The symbol must match the Coinbase format, such as `BTC-USD`, `ETH-EUR`, etc. They can be configured from within the code. 

---
