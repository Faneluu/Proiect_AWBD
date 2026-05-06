package com.example.proiect_awbd.dto;

public class StatisticsDTO<T> {
    private String metric;
    private T value;

    public StatisticsDTO(String metric, T value) {
        this.metric = metric;
        this.value = value;
    }


    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}


