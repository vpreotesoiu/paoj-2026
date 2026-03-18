package com.pao.laboratory03.bonus.exception;

import com.pao.laboratory03.bonus.model.Status;

public class InvalidTransitionException extends RuntimeException {
    private Status fromStatus, toStatus;
    public InvalidTransitionException(Status fromStatus, Status toStatus) {
        super("Nu se poate trece din " + fromStatus + " in " + toStatus);
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }
    public String getMessage() {
        return "Nu se poate trece din " + fromStatus + " in " + toStatus;
    }
}
