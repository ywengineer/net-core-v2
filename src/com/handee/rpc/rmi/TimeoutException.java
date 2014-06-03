
package com.handee.rpc.rmi;

/**
 * Thrown when a method with a return value is invoked on a remote object and the response is not received with the
 * {@link com.handee.rpc.rmi.RemoteObject#setResponseTimeout(int) response timeout}.
 *
 * @author Nathan Sweet <misc@n4te.com>
 * @see com.handee.rpc.rmi.ObjectSpace#getRemoteObject(com.handee.rpc.Connection, int, Class...)
 */
@SuppressWarnings("serial")
public class TimeoutException extends RuntimeException {
    public TimeoutException() {
        super();
    }

    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException(Throwable cause) {
        super(cause);
    }
}
