package pl.minecodes.metric.backend.metric;

import lombok.Getter;

@Getter
public enum MetricValueType {

  INTEGER {
    @Override
    public Object parseValue(String value) {
      return Integer.parseInt(value);
    }
  },
  DOUBLE {
    @Override
    public Object parseValue(String value) {
      return Double.parseDouble(value);
    }
  },
  STRING {
    @Override
    public Object parseValue(String value) {
      return value;
    }
  },
  BOOLEAN {
    @Override
    public Object parseValue(String value) {
      if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
        return Boolean.parseBoolean(value);
      } else {
        throw new IllegalArgumentException("Invalid boolean value: " + value);
      }
    }
  };

  public abstract Object parseValue(String value);
}

