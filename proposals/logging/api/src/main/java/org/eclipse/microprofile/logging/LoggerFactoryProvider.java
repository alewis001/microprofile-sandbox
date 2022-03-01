package org.eclipse.microprofile.logging;

import java.util.function.Supplier;

/**
 * Interface for Logger implementations.
 *
 * This interface acts similarly to the SPI mechanism in core Java I.e. This is the "Provider"
 * interface
 */
public interface LoggerFactoryProvider {
  
  Logger<LogEvent> getLogger();
  
  Logger<LogEvent> getLogger(String name);

  <T extends LogEvent> Logger<T> getLogger(Supplier<T> supplier);
  
  <T extends LogEvent> Logger<T> getLogger(String name, Supplier<T> supplier);
}
