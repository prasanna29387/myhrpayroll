package com.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

@Theme("valo")
@SpringUI
@SpringViewDisplay
public class MyUI extends UI implements ViewDisplay {


    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        setContent(root);



        Upload upload = new Upload("Upload it here", null);
        upload.setImmediateMode(false);
        upload.setButtonCaption("Upload Now");

        root.addComponent(upload);
       // root.setExpandRatio(springViewDisplay, 1.0f);
    }

    @Override
    public void showView(View view) {
        /*springViewDisplay.setContent((Component) view)*/;
    }
}
