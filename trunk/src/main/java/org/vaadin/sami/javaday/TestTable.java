package org.vaadin.sami.javaday;

import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestTable extends TwinColSelect {


    TwinColSelect<String> sample;

    public TestTable() {
//        List<String> data2 = new ArrayList<>();
//        List<String> data = IntStream.range(0, 6).mapToObj(i -> "Option " + i).collect(Collectors.toList());
//
//
//        data2.add("123");
//        data2.add("123");
//        data2.add("123");
//        data2.add("123");
//        data2.add("123");
//
//        sample = new TwinColSelect<>("Select Targets");
//
//        sample = new TwinColSelect<>(null, data);
//        sample.setRows(6);
//        sample.setLeftColumnCaption("Available options");
//        sample.setRightColumnCaption("Selected options");
//
//        sample.addValueChangeListener(event -> Notification.show("Value changed:", String.valueOf(event.getValue()),
//                Notification.Type.TRAY_NOTIFICATION));

//        TwinColSelect<String> select = new TwinColSelect<>("Select Targets");
//
//        // Put some items in the select
//        select.setItems("Mercury", "Venus", "Earth", "Mars",
//                "Jupiter", "Saturn", "Uranus", "Neptune");
//
//        // Few items, so we can set rows to match item count
//        select.setRows(select.size());
//
//        // Preselect a few items
//        select.select("Venus", "Earth", "Mars");
//
//        // Handle value changes
//        select.addSelectionListener(event ->
//                layout.addComponent(
//                        new Label("Selected: " + event.getNewSelection())));
    }
}
