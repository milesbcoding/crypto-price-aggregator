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
   cd ktor-sample
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

> Note: The symbol must match the Coinbase format, such as `BTC-USD`, `ETH-EUR`, etc. They can be configured from within the code. 

---

## 🧠 Key Design Decisions and Trade-offs

- **Ktor Framework**: Chosen for its asynchronous capabilities and Kotlin-native syntax, perfect for building small services quickly. This was chosen over other implementations such as SpringBoot or Micronaut such that I could learn the 'Kotlin Way' over the course of this challenge. The advantage is that allowed me to have more fine-grained control over the active processes. The disadvantage is that it demands a scalable design pattern + the lack of standardisation could lead to inefficiently structured code.
- **Co-Routines for Price Fetching**: One large benefit of Kotlin over Java is it's use of Co-Routines. Prices are fetched from within the coinbase service in parallel every interval. The advantage of this is a faster price update time, with prices reflecting the last call interval more closely. This disadvantage is that it introduces more complexity, Co-Routines must be managed efficiently to avoid memory leaks.
- **Dynamically Loaded Pairs**: The pairs for the API are modelled to be dynamically loaded instead of being hard coded into environment variables. This allows for flexible configuration of the API on demand. The disadvantage is that now a new dependency has been introduced which could reduce the reliability of the app.
- **Concurrent Hashmap**: Essential for ensuring thread safe updates, especially in my implementation that updates prices in parallel. Fortunately, there is modification required of the values stored in the Hashmap which helps avoid blocking. The disadvantage is that the concurrent hashmap comes with more overhead.
- **Exchange Interface**: It is important that the API is not tightly coupled to a single exchange. For this reason a simple exchange interface was added to model the requirements for fetching prices making the API exchange agnostic. The disadvantage is that exchanges require custom implementation and so this efficiency can be seen as more of a conceptual one.

---

## ⚙️ Scaling Considerations

To scale this service for production or add more features:

- **More Exchanges**: More exchanges could be added by providing the required configuration within the yaml file and implementing the interface as required. However how we scale the use of multiple exchanges would be dependent on use case. For example, if the API was being used by a small fund to track the price of a P of pairs over E exchanges. It might be better to try a lightweight solution, such as updating the same price map with price information using a composite key "BINANCE-BTC-USD". If the API was to be used an index for all pairs for example on a general search site like Google, response time may not be as important, and thus a less memory intensive solution such as relying upon database polls might be a better solution. With more context of the use case scalability options would differ. Hopefully I've provided some good examples :)
- **More Currency Pairs**: As currency pairs are dynamically loaded, the only consideration for scalability would be memory concerns. As the API stores the pairs in memory, multiple exchanges combined with a large catalogue of ticker pairs could lead to a large overhead. It must also be considered how often the API would poll for an update on the total list of pairs and how this change would flow through the rest of the application.
- **Load Balancing**: Run the app in containers and use a reverse proxy/load balancer (e.g., NGINX or Kubernetes) for high-availability.
- **Rate Limiting and Retry Logic**: Protect against hitting exchange API limits with rate limiting and exponential backoff strategies.

---

---

## ⭐️⭐️ Additional Notes

- **Similarities to other languages**: Whilst completing the challenge and learning Kotlin, I noticed similarities of structure to Swift and the simplicity afforded by a more declarative structure
- **Development Efficiencies**: To help me establish an understanding of how co routines work I reviewed sample projects I found online.
- **Sources**: To help with learning Kotlin I used websites like Baeldung, W3Schools, GeeksforGeeks & the Kotlin Documentation itself. For help with testing & dependency versioning I used sample projects, IntelliJ prompts & ChatGPT.