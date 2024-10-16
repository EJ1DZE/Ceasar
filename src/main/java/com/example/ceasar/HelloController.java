package com.example.ceasar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;

import java.io.*;
import java.util.Scanner;

import java.net.HttpURLConnection;
import java.net.URL;

public class HelloController {
    @FXML
    private TextField inputForEncryption;

    @FXML
    private TextField inputFile;

    @FXML
    private ComboBox<String> choiseLanguage;

    @FXML
    private ComboBox<Byte> choiseKey;

    @FXML
    private TextField outputFile;

    @FXML
    private TextField output;

    @FXML
    public void initialize() {
        // Заполняем ComboBox элементами
        ObservableList<String> languages = FXCollections.observableArrayList("Английский", "Русский");
        choiseLanguage.setItems(languages);

        // Устанавливаем значение по умолчанию
        choiseLanguage.setValue("Русский");
        fillNumberComboBox((byte) 33);

        choiseLanguage.setOnAction(event -> {
            String selectedLanguage = choiseLanguage.getValue();

            if(selectedLanguage == "Английский"){
                fillNumberComboBox((byte) 28);
            }
            else if(selectedLanguage == "Русский"){
                fillNumberComboBox((byte) 33);
            }
        });
    }
    @FXML
    protected void onHelloButtonClick() {
        String vvod = inputForEncryption.getText().toUpperCase();
        String inputWay = inputFile.getText();
        String outputWay = outputFile.getText();
        byte key = choiseKey.getValue();
        String language = choiseLanguage.getValue();
        boolean flag = false;
        if(!inputWay.isEmpty() && !outputWay.isEmpty()){
            WorkWithFile(inputWay, outputWay, key, language, flag);
        }
        output.setText(ShifrCeasar(vvod, key, language, flag));
    }

    public void fillNumberComboBox(byte n){
        ObservableList<Byte> numbers = FXCollections.observableArrayList();
        for (byte i = 1; i <= n; i++){
            numbers.add(i);
        }
        choiseKey.setItems(numbers);
        choiseKey.setValue((byte) 1);
    }

    public String ShifrCeasar(String vvod, byte key, String language, boolean flag){
        String alphabet = "";
        byte keySize;
        String output = "";
        if(language == "Русский"){
            alphabet = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        }
        else if(language == "Английский"){
            alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        }
        keySize = (byte) alphabet.length();
        for(byte i = 0; i < vvod.length(); i++){
            char letter = vvod.charAt(i);
            if(alphabet.contains(String.valueOf(letter))) {byte indexLetter = (byte) alphabet.indexOf(letter);
                int indexNewLetter;
                if(flag){
                    indexNewLetter = indexLetter - key;
                    if(indexNewLetter < 0){
                        indexNewLetter = indexNewLetter + keySize;
                    }
                }
                else {
                    indexNewLetter = indexLetter + key;
                    if (indexNewLetter >= keySize) {
                        indexNewLetter = indexNewLetter - keySize;
                    }
                }
                letter = alphabet.charAt(indexNewLetter);
            }
            output = output + letter;
        }
        return output;
    }

    public void WorkWithFile(String inputWay, String outputWay, byte key, String language, boolean flag){
        File inputFile = new File(inputWay);
        if(inputFile.exists()){
            try{
                String content = Files.readString(Paths.get(inputWay));
                String outputContetnt = ShifrCeasar(content.toUpperCase(), key, language, flag);
                try (PrintWriter writer = new PrintWriter(new FileWriter(outputWay))) {
                    writer.println(outputContetnt);
                } catch (IOException e) {
                    System.out.println("Произошла ошибка при записи в файл: " + e.getMessage());
                }
            }
            catch(IOException ex){
                System.out.println(ex.getMessage());
            }
        }
        else{
            System.out.println("Введите нормальный путь");
        }
    }
    public static boolean isValidText(String text) {
        try {
            URL url = new URL("https://api.languagetool.org/v2/check");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            // Формируем данные запроса
            String data = "text=" + text + "&language=en-US";  // Используйте "ru" для русского языка

            try (OutputStream os = connection.getOutputStream()) {
                os.write(data.getBytes());
                os.flush();
            }

            // Читаем ответ
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            // Проверяем, есть ли ошибки
            return !response.toString().contains("\"matches\":[]");  // Если ошибок нет, массив "matches" будет пуст
        } catch (Exception e) {
            System.out.println("Ошибка при обращении к API: " + e.getMessage());
        }
        return false;
    }

    public void DecryptionButtonClick(ActionEvent actionEvent) {
        String vvod = inputForEncryption.getText().toUpperCase();
        byte key = choiseKey.getValue();
        String language = choiseLanguage.getValue();
        String inputWay = inputFile.getText();
        String outputWay = outputFile.getText();
        boolean flag = true;
        if(!inputWay.isEmpty() && !outputWay.isEmpty()){
            WorkWithFile(inputWay, outputWay, key, language, flag);
        }
        output.setText(ShifrCeasar(vvod, key, language, flag));
    }
}
