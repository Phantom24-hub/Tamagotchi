package com.ynov.tamagochi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.ynov.time.Time;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Menu {
    Tamagotchi tamagotchi;
    Clean clean;
    Heal heal;
    Meal meal;
    public Time time;

    Label tamgotchiLabel;
    Label statAge;
    Label statHunger;
    Label statClean;
    Label statHappiness;
    Label statSick;
    Stage stage ;

    public Menu(Tamagotchi tamagotchi) {
        this.tamagotchi = tamagotchi;
        this.heal = new Heal(this.tamagotchi);
        this.meal = new Meal(tamagotchi);
        this.time = new Time(tamagotchi, meal, clean, heal);
        this.tamgotchiLabel = new Label(tamagotchi.name);
        this.statAge  = new Label("Age : "+tamagotchi.stageOfLife);
        this.statHunger  = new Label("has Eaten : "+tamagotchi.hasEaten);
        this.statClean  = new Label("is Clean : "+tamagotchi.isClean);
        this.statHappiness  = new Label("Happiness : "+tamagotchi.happiness);
        this.statSick  = new Label("is Sick: "+tamagotchi.sick);
        this.stage= new Stage();

    }

    public int printMenu() {
        System.out.println("what do you want to do ?");
        System.out.println("1 : play");
        System.out.println("2 : eat");
        System.out.println("3 : clean");
        System.out.println("4 : heal");
        System.out.println("5 : view the status");
        System.out.println("0 : quitter");
        return promptRangeNbr("choose : ", 0, 5);
    }

    public String prompt(String question) {
        System.out.print(question + " ");
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader buffer = new BufferedReader(reader);
        try {
            String answer = buffer.readLine();
            if (answer.equals("")) {
                System.err.println("retry ...");
                return prompt(question);
            }
            return answer;
        } catch (IOException e) {
            System.err.println("retry ...");
            return prompt(question);
        }
    }

    public int promptNbr(String question) {
        String answer = prompt(question);
        try {
            return Integer.parseInt(answer);
        } catch (NumberFormatException e) {
            System.err.println("retry with number ...");
            return promptNbr(question);
        }
    }

    public int promptRangeNbr(String question, int min, int max) {
        int answer = promptNbr(question);
        if (answer < min || answer > max) {
            System.out.println("between " + min + " and " + max);
            return promptRangeNbr(question, min, max);
        }
        return answer;
    }

    public void tameScene(){
        
        // Label tamgotchiLabel = new Label(tamagotchi.name);
        // Label statAge  = new Label("Age : "+tamagotchi.stageOfLife);
        // Label statHunger  = new Label("hasEaten : "+tamagotchi.hasEaten);
        // Label statClean  = new Label("isClean : "+tamagotchi.isClean);
        // Label statHappiness  = new Label("Happiness : "+tamagotchi.happiness);

        Button feedButton = new Button("Feed Me");
        Button cleanButton = new Button("Clean Me");
        Button playButton = new Button("Play with Me");
        Button healButton = new Button("Heal me");

        VBox statBox = new VBox(20,this.tamgotchiLabel,this.statAge,this.statHunger,this.statClean,this.statHappiness,this.statSick);
        statBox.setAlignment(Pos.CENTER);
        statBox.setStyle("-fx-padding: 10;" + "-fx-border-width: 2;"+"-fx-border-color: blue;");
        HBox hbox = new HBox(20,statBox,feedButton,cleanButton,playButton,healButton);
        hbox.setAlignment(Pos.CENTER);
        hbox.setStyle("-fx-padding: 10;" + "-fx-border-width: 2;"+"-fx-border-color: red;");
        VBox vbox = new VBox(20,hbox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-padding: 10;" + "-fx-border-width: 2;"+"-fx-border-color: yellow;");
        Scene scene = new Scene(vbox, 640, 480);

        feedButton.setOnMouseClicked(e->{
            meal.eat();
            updateStat();
        });
        cleanButton.setOnMouseClicked(e->{
            Clean.CleanRoom(this.tamagotchi);
            updateStat();
        });
        playButton.setOnMouseClicked(e->{
            Play.PlayTamagotchi(this.tamagotchi);
            updateStat();
        });
        healButton.setOnMouseClicked(e->{
            heal.HealTamagotchi();
            updateStat();
        });
        stage.setScene(scene);
       
        
        stage.show();
    }

    private void updateStat(){
        this.statAge.setText("Age : "+tamagotchi.stageOfLife);
        this.statHunger.setText("hasEaten : "+tamagotchi.hasEaten);
        this.statClean.setText("isClean : "+tamagotchi.isClean);
        this.statHappiness.setText("Happiness : "+tamagotchi.happiness);
    }

    public Scene deadScene(){
        Label deadLabel = new Label("dead");
        VBox deadBox = new VBox(deadLabel);
        deadBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(deadBox, 640, 480);
        return scene;
    }

    public void timeThread(){
        new Thread(() -> {
            while(!this.tamagotchi.stageOfLife.equals("dead")){
                if (time.elapsedTime()==1) {
                    this.time.newDay();
                    Platform.runLater(() -> {
                        updateStat();
                     });
                     if (this.tamagotchi.stageOfLife.equals("dead")) {
                    Platform.runLater(() -> {
                        stage.setScene(deadScene());
                        stage.show();
                    });
                    }
                } 
            }
        }).start();
    }

    public void test() {
        System.out.println(tamagotchi.sick);
        int choice = -1;
        do {
            time.newDay();
            choice = printMenu();
            switch (choice) {
                case 1:
                    Play.PlayTamagotchi(this.tamagotchi);
                    break;
                case 2:
                    meal.eat();
                    break;
                case 3:
                    Clean.CleanRoom(this.tamagotchi);
                    break;
                case 4:
                    heal.HealTamagotchi();
                    break;
                case 5:
                    TamagotchiStatus();
                    break;
            }

            if (this.tamagotchi.stageOfLife.equals("dead")) {
                choice = 0;
            }

        } while (choice != 0);
        System.out.println("bye bye");
    }

    public void TamagotchiStatus() {
        System.out.println();
        System.out.println("Tamagotchi : " + tamagotchi.name);
        System.out.println();
        System.out.println("status : " + tamagotchi.stageOfLife);
        if (tamagotchi.hasEaten) {
            System.out.println(tamagotchi.name + " has eaten");
        } else {
            System.out.println(tamagotchi.name + " is hungry");
        }
        System.out.println("hapiness : " + tamagotchi.happiness);
        if (tamagotchi.isClean) {
            System.out.println(tamagotchi.name + " is clean");
        } else {
            System.out.println(tamagotchi.name + " is dirty");
        }
        if (tamagotchi.sick) {
            System.out.println(tamagotchi.name + " is sick");
        } else {
            System.out.println(tamagotchi.name + " is healthy");
        }

        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader buffer = new BufferedReader(reader);
        try {
            buffer.readLine();
        } catch (IOException e) {

        }
    }
}
