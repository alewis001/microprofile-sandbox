package org.eclipse.microprofile.logging;

import org.eclipse.microprofile.logging.specialized.SpecializedLogEvent;
import org.eclipse.microprofile.logging.specialized.SpecializedLogEventSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Test the utility class that maps Loggers to a combination of their names and
 * suppliers.
 */
public class LoggerRegistryTest {

    /**
     * Test that accessing a logger using the same name returns the same instance of
     * that logger.
     *
     * @param info Test information.
     */
    @Test
    public void testSameLoggerReturnedUsingSameName(TestInfo info) {
        final String loggerName = info.getDisplayName();

        final Logger<LogEvent> loggerOne = LoggerFactory.getLogger(loggerName);
        final Logger<LogEvent> loggerTwo = LoggerFactory.getLogger(loggerName);

        assertEquals(System.identityHashCode(loggerOne), System.identityHashCode(loggerTwo));
    }

    /**
     * Test that accessing a Logger by the same name but different LogEvent
     * Types does not fail the application.
     *
     * @param info Test information.
     */
    @Test
    public void testDifferentLoggerSuppliersSameNameExplicit(TestInfo info) {
        final String loggerName = info.getDisplayName();

        final Logger<LogEvent> logEventLogger = LoggerFactory.getLogger(loggerName);
        logEventLogger.debug(e -> String.format("Logger %s for LogEvents", loggerName));

        final Logger<SpecializedLogEvent> specializedLogEventLogger
                = LoggerFactory.getLogger(loggerName, new SpecializedLogEventSupplier());

        assertNotEquals(System.identityHashCode(logEventLogger), System.identityHashCode(specializedLogEventLogger));

        specializedLogEventLogger.debug(e -> {
            e.name = "Special";
            e.version = 2;
            return String.format("Logger %s for SpecializedLogEvents", loggerName);
        });

        final Logger originalLogger = LoggerFactory.getLogger(loggerName);
        assertTrue(logEventLogger == originalLogger);
    }

    /**
     * Test that accessing a Logger by the same name but different LogEvent
     * Types does not fail the application.
     */
    @Test
    public void testDifferentLoggerSuppliersSameNameImplicit() {
        final Logger<LogEvent> logEventLogger = LoggerFactory.getLogger();
        logEventLogger.debug(e -> String.format("Logger %s ", logEventLogger.getName()));

        final Logger<SpecializedLogEvent> specializedLogEventLogger
                = LoggerFactory.getLogger(new SpecializedLogEventSupplier());

        assertNotEquals(System.identityHashCode(logEventLogger), System.identityHashCode(specializedLogEventLogger));

        specializedLogEventLogger.debug(e -> {
            e.name = "Special";
            e.version = 2;
            return String.format("Logger %s for SpecializedLogEvents", specializedLogEventLogger.getName());
        });

        final Logger originalLogger = LoggerFactory.getLogger();
        assertTrue(logEventLogger == originalLogger);
    }
}
