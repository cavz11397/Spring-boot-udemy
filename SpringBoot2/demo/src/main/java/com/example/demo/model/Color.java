package com.example.demo.model;

public class Color {

    private Integer idColor;
    private String color;

    public Color(Integer idColor, String color) {
        this.idColor = idColor;
        this.color = color;
    }

    public Color() {
        super();
    }

    public Integer getIdColor() {
        return idColor;
    }

    public void setIdColor(Integer idColor) {
        this.idColor = idColor;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
