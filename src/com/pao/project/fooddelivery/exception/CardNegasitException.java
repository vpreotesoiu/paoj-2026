package com.pao.project.fooddelivery.exception;

import com.pao.project.fooddelivery.model.CardBancar;

public class CardNegasitException extends RuntimeException {
    public CardNegasitException(CardBancar card) {
        super("Cardul nu a fost gasit: " + card);
    }
}
