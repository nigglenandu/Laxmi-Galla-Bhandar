package com.laxmi.galla.customer.delete;

public final class ActionReasonUtils {
    private ActionReasonUtils() {}

    public static String buildReason(
            String reason,
            String action,
            String performedBy
    ){
        return (reason != null && !reason.isBlank())
                ? reason.trim()
                : action + " by " + performedBy;
    }
}
