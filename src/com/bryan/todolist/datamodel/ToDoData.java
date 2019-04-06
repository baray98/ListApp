package com.bryan.todolist.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class ToDoData {
    private static ToDoData instance = new ToDoData();
    private static String fileName = "TodoListItems.txt";

    private ObservableList<ToDoItem> toDoItems;
    private DateTimeFormatter formatter;

    public static ToDoData getInstance() {
        return instance;
    }

    private ToDoData()
    {
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public ObservableList<ToDoItem> getToDoItems() {
        return toDoItems;
    }


    public void loadTodoData() throws IOException{
        toDoItems = FXCollections.observableArrayList();
        Path path = Paths.get(fileName);
        BufferedReader reader = Files.newBufferedReader(path);

        String input;
        try{
            while ((input = reader.readLine())!= null)
            {
                String[] items = input.split("\t");
                String shortDescription = items[0];
                String details = items[1];
                String deadLineString = items[2];

                LocalDate date = LocalDate.parse(deadLineString,formatter);

                ToDoItem doItem = new ToDoItem(shortDescription,details,date);
                toDoItems.add(doItem);
            }
        }

        finally {
            if (reader != null)
            {
                reader.close();
            }
        }
    }

    public void saveTodoItems() throws IOException{
        Path path = Paths.get(fileName);
        BufferedWriter writer = Files.newBufferedWriter(path);
        try{
            Iterator<ToDoItem> iterator = toDoItems.iterator();
            while (iterator.hasNext()){
                ToDoItem item = iterator.next();
                writer.write(String.format("%s\t%s\t%s",
                        item.getShortDescription(),
                        item.getDetails(),
                        item.getDeadLine().format(formatter)));
                writer.newLine();

            }

        }
        finally {
            if (writer != null)
                writer.close();
        }
    }

    public void deleteTodoItem(ToDoItem item) {
        toDoItems.remove(item);
    }

    public void edit(ToDoItem original, ToDoItem newItem) {
        int index = toDoItems.indexOf(original);
        if (index == -1)
            return;

        toDoItems.set(index,newItem);
    }
}
