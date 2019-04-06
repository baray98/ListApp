package com.bryan.todolist;

import com.bryan.todolist.datamodel.ToDoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {

    @FXML
    private TextField ShortDescField;
    @FXML
    private TextArea DetailsField;
    @FXML
    private DatePicker DueDate;
    public ToDoItem getToDoItem(){

        String shorDesription = ShortDescField.getText();
        String details = DetailsField.getText().replaceAll("\n","");
        LocalDate date = DueDate.getValue();
        return new ToDoItem(shorDesription,details,date);
    }

    public void setEditItem(ToDoItem item) {

        ShortDescField.setText(item.getShortDescription());
        DetailsField.setText(item.getDetails());
        DueDate.setValue(item.getDeadLine());
    }

}
