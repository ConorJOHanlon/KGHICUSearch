package com.example.conor.myapplication;
/**
 * Created by conor on 26/01/17.
 */

public class items {
        public String title;
        public String link;

    public items(String s){
        String[] splitString = s.split(":::");
        title = splitString[0];
        link = splitString[1];

    }
        @Override
        public String toString() {
            return title + ":::" + link;
    }
}
