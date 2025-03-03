package edu.prog2.model;

import org.json.JSONObject;

public interface Format {
    public boolean equals(Object obj);
    public String toCSV();
    public String toString();
    public JSONObject toJSONObject();
}
