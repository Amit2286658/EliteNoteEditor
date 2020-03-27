package com.me.android.noteeditor.customListener;

public class genericListener<T> {

    static private genericEventListener mGenericEventListener;

    public void triggerEventListener(T genericData) throws DifferentDataTypes {
        mGenericEventListener.eventDispatcher(genericData);
    }

    public static void setEventListener(genericEventListener genericEventListener) {
        mGenericEventListener = genericEventListener;
    }

    public interface genericEventListener<T> {
        void eventDispatcher(T genericData) throws DifferentDataTypes;
    }
}

class DifferentDataTypes extends Exception {
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public DifferentDataTypes(String message) {
        super(message);
    }

    /**
     * Returns a short description of this throwable.
     * The result is the concatenation of:
     * <ul>
     * <li> the {@linkplain Class#getName() name} of the class of this object
     * <li> ": " (a colon and a space)
     * <li> the result of invoking this object's {@link #getLocalizedMessage}
     * method
     * </ul>
     * If {@code getLocalizedMessage} returns {@code null}, then just
     * the class name is returned.
     *
     * @return a string representation of this throwable.
     */
    @Override
    public String toString() {
        return "There's a problem in dispatching the event, The data types do not match";
    }
}
