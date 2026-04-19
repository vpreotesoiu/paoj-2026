package com.pao.project.fooddelivery.model;

import com.pao.project.fooddelivery.exception.TranzitieInvalidaException;

public enum StatusComanda {
    NEPRELUATA {
        @Override
        public StatusComanda avanseaza() {
            return IN_LIVRARE;
        }
    },
    IN_LIVRARE {
        @Override
        public StatusComanda avanseaza() {
            return FINALIZATA;
        }
    },
    FINALIZATA {
        @Override
        public StatusComanda avanseaza() {
            throw new TranzitieInvalidaException(this, FINALIZATA);
        }
    };
    public abstract StatusComanda avanseaza();
}
