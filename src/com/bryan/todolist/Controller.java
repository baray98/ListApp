package com.bryan.todolist;

import com.bryan.todolist.datamodel.ToDoData;
import com.bryan.todolist.datamodel.ToDoItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {

    private List<ToDoItem> toDoItemList;

    @FXML
    private ListView<ToDoItem> TodoItemSD;
    @FXML
    private TextArea ItemDetails;
    @FXML
    private Label DueDate;
    @FXML
    private Label DueDateLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private ToggleButton filterToggleButton;

    private FilteredList filteredList;

    public void initialize(){
        listContextMenu = new ContextMenu();

        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ToDoItem item = TodoItemSD.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });

        MenuItem editMenuItem = new MenuItem("Edit");
        editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ToDoItem item = TodoItemSD.getSelectionModel().getSelectedItem();
                editItem(item);
            }
        });

        listContextMenu.getItems().addAll(editMenuItem);
        listContextMenu.getItems().addAll(deleteMenuItem);

        TodoItemSD.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoItem>() {
            @Override
            public void changed(ObservableValue<? extends ToDoItem> observableValue, ToDoItem toDoItem, ToDoItem t1) {
                if (t1 != toDoItem && t1 != null)
                {
                    ItemDetails.setText(t1.getDetails());
                    if (t1.getDeadLine().isBefore(LocalDate.now())){
                        DueDateLabel.setStyle("-fx-background-color:red");
                    }
                    else if (t1.getDeadLine().isEqual(LocalDate.now())){
                        DueDateLabel.setStyle("-fx-background-color:green");
                    }
                    else{
                        DueDateLabel.setStyle("-fx-background-color:white");
                    }

                    DueDate.setText(t1.getDeadLine().toString());
                }
            }
        });
         filteredList = new FilteredList<ToDoItem>(ToDoData.getInstance().getToDoItems(), new Predicate<ToDoItem>() {
             @Override
             public boolean test(ToDoItem toDoItem) {
                 return true;
             }
         });

        SortedList<ToDoItem> sortedList = new SortedList<ToDoItem>(filteredList,
                new Comparator<ToDoItem>() {
                    @Override
                    public int compare(ToDoItem o1, ToDoItem o2) {
                        return o1.getDeadLine().compareTo(o2.getDeadLine());
                    }
                });

        TodoItemSD.setItems(sortedList);
        TodoItemSD.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TodoItemSD.getSelectionModel().selectFirst();

        TodoItemSD.setCellFactory(new Callback<ListView<ToDoItem>, ListCell<ToDoItem>>() {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> toDoItemListView) {
                ListCell<ToDoItem> cell = new ListCell<ToDoItem>(){
                    @Override
                    protected void updateItem(ToDoItem toDoItem, boolean b) {
                        super.updateItem(toDoItem, b);

                        if (b) {
                            setText(null);
                        }else{
                            setText(toDoItem.getShortDescription());
                            if (toDoItem.getDeadLine().isBefore(LocalDate.now().plusDays(1))){
                                setTextFill(Color.RED);
                            }
                            else if (toDoItem.getDeadLine().isEqual(LocalDate.now().plusDays(1))){
                                setTextFill(Color.GREEN);
                            }
                        }

                    }
                };

                cell.emptyProperty().addListener(
                        (obs,wasEmpty,isNowEmpty) ->{
                            if(isNowEmpty){
                                cell.setContextMenu(null);
                            }
                            else{
                                cell.setContextMenu(listContextMenu);
                            }
                }
                );

                return cell;

            }
        });
    }

    private void editItem(ToDoItem item) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Editing " + item.getShortDescription());
        dialog.setHeaderText("Press OK to save");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoitemdialog.fxml"));

        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }
        catch (IOException e)
        {
            System.out.println("Can not load dialog");
            e.printStackTrace();
            return;
        }
        DialogController dialogController = fxmlLoader.getController();
        dialogController.setEditItem(item);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional <ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            ToDoData.getInstance().edit(item,dialogController.getToDoItem());
        }
    }

    private void deleteItem(ToDoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete To Do Item");
        alert.setHeaderText("Do you want to delete - " + item.getShortDescription());
        alert.setContentText("Are you sure ? Press OK to confirm");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK)
        {
            ToDoData.getInstance().deleteTodoItem(item);
        }
    }

    @FXML
    public void showNewDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New Item");
        dialog.setHeaderText("This a header text in Dialog");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoitemdialog.fxml"));

        try{
               dialog.getDialogPane().setContent(fxmlLoader.load());
        }
        catch (IOException e)
        {
            System.out.println("Can not load dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional <ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            DialogController dialogController = fxmlLoader.getController();

            ToDoData.getInstance().getToDoItems().add(dialogController.getToDoItem());
            TodoItemSD.getSelectionModel().selectLast();

            System.out.println("OK Button was pressed");
        }
        else{
            System.out.println("Cancel Button was pressed");
        }

    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        ToDoItem selectedItem = TodoItemSD.getSelectionModel().getSelectedItem();
        if (selectedItem == null)
            return;

        if (keyEvent.getCode() == KeyCode.DELETE)
            deleteItem(selectedItem);
    }


    public void handelFilter(ActionEvent actionEvent) {
        if (filterToggleButton.isSelected()) {
            filteredList.setPredicate(new Predicate<ToDoItem>() {
                @Override
                public boolean test(ToDoItem toDoItem) {
                    return toDoItem.getDeadLine().equals(LocalDate.now());
                }

            });

        }
        else{
            filteredList.setPredicate(new Predicate<ToDoItem>() {
                @Override
                public boolean test(ToDoItem toDoItem) {
                    return true;
                }

            });
        }
    }

    public void close ( ActionEvent actionEvent ) {
        Platform.exit ();
        System.exit ( 0 );
    }

    public void about ( ActionEvent actionEvent ) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("about");
        dialog.setHeaderText("Application : To do List ");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("about.fxml"));

        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }
        catch (IOException e)
        {
            System.out.println("Can not load dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait ();
    }
}
