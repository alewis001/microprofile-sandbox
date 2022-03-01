package org.eclipse.microprofile.logging;

import java.util.function.Function;

/**
 * Factory to build {@link NoOpLogger} instances.
 */
class NoOpLoggerProvider extends AbstractLoggerFactoryProvider {
  
  public <T extends LogEvent> Function<LoggerKey<T>, Logger<T>> getLoggerBuilder() {
    return loggerKey -> new NoOpLogger<T>(loggerKey.getName(), loggerKey.getSupplier());
  }
}
