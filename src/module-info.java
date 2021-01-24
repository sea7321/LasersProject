module LasersGUI {
    requires transitive javafx.controls;
    requires javafx.media;
    exports lasers;
    exports lasers.gui;
    exports lasers.model;
    exports lasers.model.components;
}