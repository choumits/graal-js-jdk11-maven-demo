package com.mycompany.app;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class ResourceLoader {
    public InputStreamReader loadReader(String file) {
        try {
            return new InputStreamReader(this.getClass().getResourceAsStream(file), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
