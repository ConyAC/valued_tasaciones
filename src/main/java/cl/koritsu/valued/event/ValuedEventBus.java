package cl.koritsu.valued.event;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

import cl.koritsu.valued.ValuedUI;

/**
 * A simple wrapper for Guava event bus. Defines static convenience methods for
 * relevant actions.
 */
public class ValuedEventBus implements SubscriberExceptionHandler {

    private final EventBus eventBus = new EventBus(this);

    public static void post(final Object event) {
        ValuedUI.getDashboardEventbus().eventBus.post(event);
    }

    public static void register(final Object object) {
        ValuedUI.getDashboardEventbus().eventBus.register(object);
    }

    public static void unregister(final Object object) {
        ValuedUI.getDashboardEventbus().eventBus.unregister(object);
    }

    @Override
    public final void handleException(final Throwable exception,
            final SubscriberExceptionContext context) {
        exception.printStackTrace();
    }
}
