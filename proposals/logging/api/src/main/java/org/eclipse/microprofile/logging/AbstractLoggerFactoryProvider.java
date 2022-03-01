package org.eclipse.microprofile.logging;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Centralised storage of Loggers based on name and type of LogEvent generated.
 */
public abstract class AbstractLoggerFactoryProvider implements LoggerFactoryProvider {

  private static final int STACK_TRACE_INDEX = 5;

  private final LogEventSupplier defaultSupplier = new LogEventSupplier();

  private final Map<LoggerKey, Logger> loggers = new ConcurrentHashMap<>();

  /**
   * Get a Logger that uses standard LogEvent instances, without specifying a name.
   *
   * <p>
   * The name of the Logger will be the name of the class using the logger,
   * inferred from the Stack
   * </p>
   *
   * @return The Logger instance
   */
  @Override
  public Logger<LogEvent> getLogger() {
    return getLogger(defaultSupplier);
  }

  /**
   * Get a Logger that uses standard LogEvent instances, by name.
   *
   * @param name The name of the Logger
   *
   * @return The Logger for the given name that uses standard LogEvent instances.
   */
  @Override
  public Logger<LogEvent> getLogger(String name) {
    return getLogger(name, defaultSupplier);
  }

  /**
   * Get a Logger that uses specialised LogEvents, without specifying a name.
   * 
   * <p>
   * The name of the Logger will be the name of the class using the logger,
   * inferred from the Stack
   * </p>
   * 
   * @param <T> The LogEvent Type generated by the Logger
   * 
   * @param supplier The supplier used to generate LogEvents of the appropriate type.
   * 
   * @return The Logger for the given name and LogEvent type combination.
   */
  @Override
  public <T extends LogEvent> Logger<T> getLogger(Supplier<T> supplier) {
    final String name = getLoggerName();
    return getLogger(name, supplier);
  }

  /**
   * Get a Logger that uses specialised LogEvents, using its name and the LogEvent type it generates.
   * 
   * @param <T> The LogEvent Type generated by the Logger
   * 
   * @param name The name of the Logger
   * @param supplier The supplier used to generate LogEvents of the appropriate type.
   * 
   * @return The Logger for the given name and LogEvent type combination.
   */
  @Override
  public <T extends LogEvent> Logger<T> getLogger(String name, Supplier<T> supplier) {
    final LoggerKey<T> key = new LoggerKey<>(name, supplier);

    // The Map contains Loggers that may differ in the type of LogEvent they expose.
    // This method is used to return a Logger of a specific type; however, the map
    // after type erasure can't expose the logger with a specific LogEvent type.
    // As the only way Logger instances can get in to the map with specific LogEvent types,
    // the unchecked cast should be ok.
    @SuppressWarnings("unchecked")
    final Logger<T> logger = loggers.computeIfAbsent(key, k -> getLoggerBuilder().apply(k));
    return logger;
  }

  public abstract <T extends LogEvent> Function<LoggerKey<T>, Logger<T>> getLoggerBuilder();

  /**
   * Get the Logger's name from the Stack.
   * 
   * @return The logger name
   */
  private String getLoggerName() {
    String name = "unknown";
    final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

    if (stackTrace.length >= STACK_TRACE_INDEX) {
      final StackTraceElement callingClass = stackTrace[STACK_TRACE_INDEX];
      name = callingClass.getClassName();
    }

    return name;
  }
  
  /**
   * Key used to identify Logger instances by both their name and
   * the type of LogEvent they generate.
   * 
   * A logger with the name "MyLogger" that generates a LogEvent 
   * is different to a logger with the same name but generates CustomLogEvents.
   *
   * @param <T> The type of LogEvent generated
   */
  public class LoggerKey<T extends LogEvent> {
    private final String name;
    private final Supplier<T> supplier;
    
    public LoggerKey(String name, Supplier<T> supplier) {
      this.name = name;
      this.supplier = supplier;
    }

    public String getName() {
      return name;
    }

    public Supplier<T> getSupplier() {
      return supplier;
    }

    @Override
    public int hashCode() {
      int hash = 3;
      hash = 29 * hash + Objects.hashCode(this.name);
      hash = 29 * hash + Objects.hashCode(this.supplier);
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final LoggerKey other = (LoggerKey) obj;
      if (!Objects.equals(this.name, other.name)) {
        return false;
      }
      return Objects.equals(this.supplier.getClass(), other.supplier.getClass());
    }
  }
}