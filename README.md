<div align="center"><img src="https://i.imgur.com/2WyiLMU.png" alt="mMetric Logo" width="200"/></div>
<div align="center"><h1>mMetric</h1></div>
<div align="center">An open-source solution for monitoring your metrics.</div>

### About

mMetric is a powerful, flexible tool designed to help you track and analyze various metrics in your projects. Whether you're monitoring performance, user engagement, or system health, mMetric provides the framework you need to collect, process, and visualize your data effectively.

### Features

- Coming soon!

### Examples

```java
public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

public static void main(String[] args) {
  MetricAuth auth = new MetricAuth(
      1, //Project id
      "https://metrics.example.com/api/v1/metric", //Endpoint
      "505aaYsoSXVDj7xPsQKAMyyL6DxirX8h" //Integration key
  );

  scheduler.scheduleAtFixedRate(() -> {
    new MetricsPublisher(auth)
        .addMetrics("version", "1.2.3")
        .addMetrics("users", 150)
        .addMetrics("session-time", 4.51)
        .addMetrics("chrome-browser", true)
        .publish();
  }, 1, 2, TimeUnit.MINUTES);
}
```

### Installation

Docker-Compose
```yaml
# Coming soon!
```

### Documentation

Coming soon!

### Contributing

Contributions are welcome! If you'd like to contribute, please follow these steps:

1. Fork the repository
2. Create a new branch for your feature or bug fix
3. Make your changes
4. Submit a pull request