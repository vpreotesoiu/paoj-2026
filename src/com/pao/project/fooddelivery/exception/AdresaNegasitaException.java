package com.pao.project.fooddelivery.exception;

import com.pao.project.fooddelivery.model.Adresa;

public class AdresaNegasitaException extends RuntimeException {
    public AdresaNegasitaException(Adresa adresa) {
        super("Adresa nu a fost gasita: " + adresa);
    }
}
