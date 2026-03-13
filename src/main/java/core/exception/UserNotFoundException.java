// 5. UserNotFoundException.java (optional custom wrapper)
package core.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Domain-friendly wrapper around UsernameNotFoundException.
 * Allows consistent error codes/messages in ApiResult without leaking security internals.
 */
public class UserNotFoundException extends UsernameNotFoundException {

    public UserNotFoundException(String msg) {
        super(msg);
    }

    public UserNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public String getErrorCode() {
        return "AUTH_USER_NOT_FOUND";
    }
}