package com.sequenceiq.cloudbreak.polling;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PollingService<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PollingService.class);

    /**
     * Executes a {@link StatusCheckerService} until it signals success, or the
     * maximum attempts are reached. A {@link StatusCheckerService} has no
     * restrictions about what kind of tasks it should do, it just needs to
     * return if the task succeeded or not. If maxAttempts is lower than 0,
     * there will be no timeout.
     *
     * @param interval    sleeps this many milliseconds between status checking attempts
     * @param maxAttempts signals how many times will the status check be executed before timeout
     */
    public Pair<PollingResult, Exception> pollWithTimeout(StatusCheckerService<T> statusCheckerService, T t, long interval, int maxAttempts, int maxFailure) {
        return pollWithTimeout(statusCheckerService, t, interval, new AttemptBasedTimeoutChecker(maxAttempts), maxFailure);
    }

    public Pair<PollingResult, Exception> pollWithAbsolutTimeout(StatusCheckerService<T> statusCheckerService, T t, long interval, long waitSec, int maxFailure) {
        return pollWithTimeout(statusCheckerService, t, interval, new AbsolutTimeBasedTimeoutChecker(waitSec), maxFailure);
    }

    public Pair<PollingResult, Exception> pollWithTimeout(StatusCheckerService<T> statusCheckerService, T t, long interval, TimeoutChecker timeoutChecker,
            int maxFailure) {
        boolean success = false;
        boolean timeout = false;
        int attempts = 0;
        int failures = 0;
        Exception actual = null;
        boolean exit = statusCheckerService.exitPolling(t);
        while (!timeout && !exit) {
            LOGGER.debug("Polling attempt {}.", attempts);
            try {
                success = statusCheckerService.checkStatus(t);
            } catch (Exception ex) {
                LOGGER.debug("Exception occurred in the polling: {}", ex.getMessage(), ex);
                failures++;
                actual = ex;
            }
            if (failures >= maxFailure) {
                LOGGER.debug("Polling failure reached the limit which was {}, poller will drop the last exception.", maxFailure);
                statusCheckerService.handleException(actual);
                return new ImmutablePair<>(PollingResult.FAILURE, actual);
            } else if (success) {
                LOGGER.debug(statusCheckerService.successMessage(t));
                return new ImmutablePair<>(PollingResult.SUCCESS, actual);
            }
            sleep(interval);
            attempts++;
            timeout = timeoutChecker.checkTimeout();
            exit = statusCheckerService.exitPolling(t);
        }
        if (timeout) {
            LOGGER.debug("Poller timeout.");
            statusCheckerService.handleTimeout(t);
            return new ImmutablePair<>(PollingResult.TIMEOUT, actual);
        }
        LOGGER.debug("Poller exiting.");
        return new ImmutablePair<>(PollingResult.EXIT, actual);
    }

    public PollingResult pollWithTimeoutSingleFailure(StatusCheckerService<T> statusCheckerService, T t, int interval, int maxAttempts) {
        return pollWithTimeout(statusCheckerService, t, interval, maxAttempts, 1).getLeft();
    }

    private void sleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted exception occurred during polling.", e);
            Thread.currentThread().interrupt();
        }
    }
}