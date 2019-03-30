package com.example.panicapp;

public class CustomMessage {

    private String text;

    public CustomMessage(){}

    public CustomMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
