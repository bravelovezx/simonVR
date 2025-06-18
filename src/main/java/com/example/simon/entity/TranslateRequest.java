package com.example.simon.entity;

import lombok.Data;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Data
public class TranslateRequest {
    String q;
    String from;
    String to;

    @Setter
    String vocabId;

    public Map<String, String[]> ToMap(){
        Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[]{q});
        if (from != null) {
            params.put("from", new String[]{from});
        }
        if (to != null) {
            params.put("to", new String[]{to});
        }
        if (vocabId != null) {
            params.put("vocabId", new String[]{vocabId});
        }
        return params;
    }
}
