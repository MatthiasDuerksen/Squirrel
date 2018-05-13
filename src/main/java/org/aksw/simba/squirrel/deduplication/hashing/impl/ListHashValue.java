package org.aksw.simba.squirrel.deduplication.hashing.impl;

import org.aksw.simba.squirrel.deduplication.hashing.HashValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListHashValue implements HashValue {

    private List<Integer> hashValues;

    private static final String DELIMETER = ",";

    public ListHashValue() {
        this(Collections.emptyList());
    }

    public ListHashValue(List<Integer> hashValues) {
        this.hashValues = hashValues;
    }

    @Override
    public String encodeToString() {
        StringBuilder sb = new StringBuilder();
        for (int hashValue : hashValues) {
            sb.append(hashValue);
            sb.append(DELIMETER);
        }
        return sb.toString();
    }

    public HashValue decodeFromString(String s) {
        List<Integer> hashValues = new ArrayList<>();
        String[] array = s.split(DELIMETER);
        for (String part : array) {
            if (!part.equals("")) {
                hashValues.add(Integer.parseInt(part));
            }
        }
        return new ListHashValue(hashValues);
    }
}