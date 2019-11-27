package org.vaadin.sami.javaday;

import org.vaadin.viritin.components.DisclosurePanel;
import org.vaadin.viritin.label.RichText;


public class About extends DisclosurePanel {

    public About() {
        setCaption("Не знаешь как пользоваться программой? Читай дальше »");
        setContent(new RichText().withMarkDownResource("/about.md"));
    }

}
