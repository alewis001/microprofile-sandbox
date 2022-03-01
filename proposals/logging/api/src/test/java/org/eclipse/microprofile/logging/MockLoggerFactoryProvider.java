package org.eclipse.microprofile.logging;

import java.util.function.Function;

public class MockLoggerFactoryProvider extends AbstractLoggerFactoryProvider {
  
  public <T extends LogEvent> Function<LoggerKey<T>, Logger<T>> getLoggerBuilder() {
    return loggerKey -> new MockLogger<T>(loggerKey.getName(), loggerKey.getSupplier());
  }
}
