package com.pao.laboratory07.exercise1;

import java.util.Stack;

public enum OrderCommand {
    PLASATA,
    PROCESATA,
    EXPEDIATA,
    LIVRATA,
    ANULATA;

    public OrderCommand next() {
        if (this == StareComanda.PLASATA) {
            return StareComanda.PROCESATA;
        } else if (this == StareComanda.PROCESATA) {
            return StareComanda.EXPEDIATA;
        } else if (this == StareComanda.EXPEDIATA) {
            return StareComanda.LIVRATA;
        }
        return this;
    }

    public StareComanda cancel() {
        return StareComanda.ANULATA;
    }

    public static Stack<StareComanda> undo(Stack<StareComanda> stivaStari) {
        if (stivaStari.size() > 1) {
            stivaStari.pop();
        }
        return stivaStari;
    }

    public boolean stareFinala() {
        return this == StareComanda.LIVRATA || this == StareComanda.ANULATA;
    }
    @Override
    public String toString() {
        return this.name();
    }
}