package com.example.dungeonmelody.data;

public class TabPart {

    public TabPart(String tabs)
    {
        Tabs = tabs;
    }

    public final String Tabs;
    public Integer ProgressStart = null;
    public Integer ProgressEnd = null;

    public void ClearProgresses(){
        ProgressStart = null;
        ProgressEnd = null;
    }
}
