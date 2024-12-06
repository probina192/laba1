package com.example.data_base;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MainController {

    public int getLastKey() {
        int lastKey = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\arina\\OneDrive\\Desktop\\данные\\example.txt"))) {
            String line;
            // Читаем файл построчно до конца
            while ((line = reader.readLine()) != null) {
                // Пропускаем первую строку (заголовок)
                if (line.startsWith("Key/")) {
                    continue;
                }
                // Разделяем строку по символу "/"
                String[] parts = line.split("/");
                // Получаем ключ (первая часть разделенной строки)
                lastKey = Integer.parseInt(parts[0]);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return lastKey;
    }
    int lastKey = getLastKey();

    @FXML
    private TableView<Main.MyRecord> tableView;

    private final ObservableList<Main.MyRecord> records = FXCollections.observableArrayList();

    //Функции для базы данных
    //Создание
    @FXML
    protected void createDatabase() {
        //путь к диску С
        String targetFolderPath = "C:\\Users\\arina\\OneDrive\\Desktop\\данные" + File.separator;
        //создаем файл на диске С
        File fileInFolder = new File(targetFolderPath, "example.txt");
        try {
            // Пытаемся создать файл
            if (fileInFolder.createNewFile()) {
                System.out.println("Файл успешно создан.");
            } else {
                System.out.println("Файл уже существует.");
            }
        } catch (IOException e) {
            // Обработка возможных ошибок ввода/вывода
            System.err.println("Не удалось создать файл: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // Обработка других исключений
            System.err.println("Произошла ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Открытие
    @FXML
    protected void loadDatabase() {
        String path = "C:\\Users\\arina\\OneDrive\\Desktop\\данные\\example.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            records.clear();

            //читаем первую строку
            String firstLine = br.readLine();
            String[] columnNames = firstLine.split("/");
            tableView.getColumns().clear();

            //создаем столбцы в таблице на основе названий
            for (int i = 0; i < columnNames.length; i++) {
                final int index = i;
                TableColumn<Main.MyRecord, String> column = new TableColumn<>(columnNames[i]);
                column.setCellValueFactory(data -> {
                    String cellValue = switch (index) {
                        case 0 -> String.valueOf(data.getValue().getKey());
                        case 1 -> data.getValue().getField1();
                        case 2 -> String.valueOf(data.getValue().getField2());
                        case 3 -> String.valueOf(data.getValue().getField3());
                        default -> "";
                    };
                    return new SimpleStringProperty(cellValue);
                });
                tableView.getColumns().add(column);
            }

            //читаем остальные строки
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("/");

                //создаем объект записи для каждой строки
                Main.MyRecord record = new Main.MyRecord();

                //заполняем запись данными
                for (int i = 0; i < data.length; i++) {
                    String columnName = columnNames[i];
                    switch (columnName) {
                        case "Key" -> record.setKey(Integer.parseInt(data[i]));
                        case "ФИО" -> record.setField1(data[i]);
                        case "Процент прохождения курса" -> record.setField2(Float.parseFloat(data[i]));
                        case "Средняя оценка (из 100)" -> record.setField3(Double.parseDouble(data[i]));
                    }
                }
                //добавляем запись в таблицу
                records.add(record);
            }
            //устанавливаем данные в таблицу после чтения всех записей
            tableView.setItems(records);

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            System.out.println("Ошибка формата данных.");
        }
    }

    //Удаление
    @FXML
    protected void deleteDatabase() {
        String targetFolderPath = "C:\\Users\\arina\\OneDrive\\Desktop\\данные" + File.separator;
        File fileInFolder = new File(targetFolderPath, "example.txt");

        if (fileInFolder.exists()) {
            if (fileInFolder.delete()) {
                ///окошко
                System.out.println("Файл успешно удален.");
            } else {
                ///окошко
                System.out.println("Не удалось удалить файл.");
            }
        } else {
            ///окошко
            System.out.println("Файл не существует.");
        }
    }

    //Очистка
    @FXML
    protected void cleanDatabase() {
        String targetFolderPath = "C:\\Users\\arina\\OneDrive\\Desktop\\данные" + File.separator;
        File fileInFolder = new File(targetFolderPath, "example.txt");

        if (fileInFolder.exists()) {
            try {
                FileWriter fileWriter = new FileWriter(fileInFolder);
                // Перезаписываем файл, очищая его содержимое
                fileWriter.write("");
                fileWriter.close();
                ///окошко
                System.out.println("Содержимое файла успешно очищено.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ///окошко
            System.out.println("Файл не существует.");
        }
    }

    //Сохранение
    @FXML
    protected void saveDatabase() {
        String targetFolderPath = "C:\\Users\\arina\\OneDrive\\Desktop\\данные" + File.separator;

        // Создаем файл на диске C
        File fileInFolder = new File(targetFolderPath, "example.txt");

        // Проверяем, существует ли файл
        if (fileInFolder.exists()) {
            // Обновляем метаданные файла
            fileInFolder.setLastModified(System.currentTimeMillis());

            System.out.println("Файл успешно сохранен.");
        } else {
            System.out.println("Файл не существует.");
        }
    }

    @FXML
    private TextField userInputField;


    //Функции для записей
    //Добавление новой записи в БД (с проверкой уникальности по ключевым полям)
    @FXML
    protected void addRecord() {
        long startTime = System.currentTimeMillis();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\arina\\OneDrive\\Desktop\\данные\\example.txt", true))) {
            // Получаем значение последнего ключа
            int newKey = lastKey + 1;
            long time = System.currentTimeMillis() - startTime;
            // Получаем введенные пользователем данные
            String userInput = openInputDialog();
            long startTime2 = System.currentTimeMillis();
            // Формируем строку для записи
            String newRecord = String.format("%d/%s", newKey, userInput);

            // Записываем новую запись в файл
            writer.write(newRecord);
            writer.newLine();
            lastKey++;

            System.out.println("Новая запись успешно добавлена.");
            long time2 = System.currentTimeMillis() - startTime2;
            System.out.println(time + time2);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private String openInputDialog() {
        // Создаем новое диалоговое окно
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Добавление новой записи");
        dialog.setHeaderText("Введите данные для новой записи");

        // Устанавливаем тип кнопок (OK и Cancel)
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        // Создаем элементы управления в окне
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField strField = new TextField();

        TextField floatField = new TextField();

        TextField doubleField = new TextField();

        gridPane.add(new Label("Текстовый столбец:"), 0, 1);
        gridPane.add(strField, 1, 1);
        gridPane.add(new Label("Флоат-столбец:"), 0, 2);
        gridPane.add(floatField, 1, 2);
        gridPane.add(new Label("Дабл-столбец:"), 0, 3);
        gridPane.add(doubleField, 1, 3);

        dialog.getDialogPane().setContent(gridPane);

        // Ожидаем закрытия окна и обрабатываем результат
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeOk) {
                // Возвращаем введенные пользователем данные
                return strField.getText() + "/" +
                        floatField.getText() + "/" + doubleField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    //удаление записи из БД по значению некоторого поля,
    @FXML
    protected void deleteRecord() throws IOException {
        String res = openDialog();
        String[] userResults = res.split("/");

        if (Integer.parseInt(userResults[0]) == 1) {
            long startTime = System.currentTimeMillis();
            String result = binarySearchByKey(Integer.parseInt(userResults[1]));
            if (result == "Запись не найдена") {
                System.out.println("Запись не найдена.");
            } else {
                deleteLine(Integer.parseInt(userResults[1])+1);
            }
            long time = System.currentTimeMillis() - startTime;
            System.out.println(time);
        }
        else if (Integer.parseInt(userResults[0]) == 2){
            List<String> result = binarySearchByNonKey(userResults[1]);
            List<Integer> resultList = new ArrayList<>();
            if (result.isEmpty()) {
                System.out.println("Запись не найдена.");
            } else {
                long startTime = System.currentTimeMillis();
                for (String item : result) {
                    String[] parts = item.split("/");
                    int firstValue = Integer.parseInt(parts[0]);
                    resultList.add(firstValue+1);
                }
                for (int i = resultList.size() - 1; i >= 0; i--) {
                    deleteLine(resultList.get(i));
                }
                long time = System.currentTimeMillis() - startTime;
                System.out.println(time);
                System.out.println("Запись(-и) по значению: " + result + " удалена(-ы).");
            }
        }
    }

    private void deleteLine(int lineNumber) throws IOException {
        String filePath = "C:\\Users\\arina\\OneDrive\\Desktop\\данные\\example.txt";
        Path path = Path.of(filePath);

        // Читаем все строки из файла
        List<String> lines = Files.readAllLines(path);

        // Проверяем, что lineNumber находится в допустимых пределах
        if (lineNumber > 0 && lineNumber <= lines.size()) {
            // Удаляем строку
            lineNumber--;
            lines.remove(lineNumber);
            lastKey--;

            // Перезаписываем файл
            Files.write(path, lines, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("Строка с номером " + lineNumber + " успешно удалена.");
        } else {
            lineNumber--;
            System.out.println("Строка с номером " + lineNumber + " не найдена.");
        }
    }

    //поиск по БД по значению некоторого поля (ключевого и не ключевого (в последнем случае найти нужно все записи, совпадающие по значению))
    //с выводом на экран результатов поиска
    @FXML
    protected void searchRecord() {
        String res = openDialog();
        String[] results = res.split("/");

        if (Integer.parseInt(results[0]) == 1) {
            long startTime = System.currentTimeMillis();
            String result = binarySearchByKey(Integer.parseInt(results[1]));
            long time = System.currentTimeMillis() - startTime;
            System.out.println(time);
            if (result.isEmpty()) {
                System.out.println("Запись не найдена.");
            } else {
                System.out.println("Результат поиска: " + result);
            }
        }
        else if (Integer.parseInt(results[0]) == 2){
            long startTime = System.currentTimeMillis();
            List<String> result = binarySearchByNonKey(results[1]);
            long time = System.currentTimeMillis() - startTime;
            System.out.println(time);
            if (result.isEmpty()) {
                System.out.println("Запись не найдена.");
            } else {
                System.out.println("Результат поиска: " + result);
            }
        }
    }

    private String openDialog() {
        // Создаем новое диалоговое окно
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Поиск записи");
        dialog.setHeaderText("Введите данные для поиска записи");

        // Устанавливаем тип кнопок (OK и Cancel)
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        // Создаем элементы управления в окне
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField choice = new TextField();
        TextField valueToFind = new TextField();

        gridPane.add(new Label("Поле для поиска (ключ - 1, текст - 2):"), 0, 1);
        gridPane.add(choice, 1, 1);
        gridPane.add(new Label("Искомое значение:"), 0, 2);
        gridPane.add(valueToFind, 1, 2);
        dialog.getDialogPane().setContent(gridPane);

        // Ожидаем закрытия окна и обрабатываем результат
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeOk) {
                // Возвращаем введенные пользователем данные
                return choice.getText() + "/" +
                        valueToFind.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private String binarySearchByKey(int keyToFind) {

        String Path = "C:\\Users\\arina\\OneDrive\\Desktop\\данные\\example.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(Path))) {
            int low = 1;
            getLastKey();
            int high = lastKey + 1;

            while (low <= high) {
                int mid = (low + high) / 2;

                int currentKey = getKeyByLineNumber(Path, mid);;

                if (currentKey == keyToFind) {
                    return readLineByLineNumber(Path, mid);
                } else if (currentKey < keyToFind) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Запись не найдена";
    }

    // Бинарный поиск по неключевому полю
    private  List<String>  binarySearchByNonKey( String nonKeyToFind) {
        List<String> results = new ArrayList<>();
        String Path = "C:\\Users\\arina\\OneDrive\\Desktop\\данные\\example.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(Path))) {
            int low = 1;
            int high = lastKey + 1;

            while (low <= high) {
                int mid = (low + high) / 2;
                String currentLine = readLineByLineNumber(Path, mid);
                String[] parts = currentLine.split("/");
                String currentNonKey = parts[1]; // Предполагаем, что неключевое поле находится на позиции 1

                int compareResult = currentNonKey.compareTo(nonKeyToFind);

                if (compareResult == 0) {
                    results.add(currentLine);
                    // Перемещаемся вправо (в сторону больших значений) для поиска дополнительных записей
                    low = mid + 1;
                } else if (compareResult < 0) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }

    private int getKeyByLineNumber(String filePath, int lineNumber) throws IOException {
        String line = readLineByLineNumber(filePath, lineNumber);
        String[] parts = line.split("/");
        return Integer.parseInt(parts[0]);
    }

    // Получение строки по номеру строки
    private static String readLineByLineNumber(String filePath, int lineNumber) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            for (int i = 1; i <= lineNumber; i++) {
                String line = reader.readLine();
                if (i == lineNumber) {
                    return line;
                }
            }
        }
        return null;
    }

    @FXML
    protected void editRecord() throws IOException {
        String res = openDialogToEdit();
        String[] userResults = res.split(" ");

        String result = binarySearchByKey(Integer.parseInt(userResults[0]));
        if (result == "Запись не найдена") {
            System.out.println("Запись не найдена.");
        } else {
            String[] recordWithoutKey = userResults[1].split("/");
            String newLineValue = userResults[0] + '/' + recordWithoutKey[0] + '/'+ recordWithoutKey[1] + '/'+ recordWithoutKey[2];
            EditLine(Integer.parseInt(userResults[0]), newLineValue);
        }
    }

    private void EditLine(int lineNumber, String newLineValue) throws IOException {
        String filePath = "C:\\Users\\arina\\OneDrive\\Desktop\\данные\\example.txt";
        Path path = Path.of(filePath);

        // Читаем все строки из файла
        List<String> lines = Files.readAllLines(path);

        // Проверяем, что lineNumber находится в допустимых пределах
        if (lineNumber > 0 && lineNumber <= lines.size()) {
            // Удаляем строку
            lines.remove(lineNumber );

            // Добавляем новую строку
            lines.add(lineNumber, newLineValue);

            // Перезаписываем файл
            Files.write(path, lines, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("Строка с номером " + lineNumber + " успешно отредактирована.");
        } else {
            System.out.println("Строка с номером " + lineNumber + " не найдена.");
        }
    }

    private String openDialogToEdit() {
        // Создаем новое диалоговое окно
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Редактирование записи");
        dialog.setHeaderText("Введите данные для редактирования записи");

        // Устанавливаем тип кнопок (OK и Cancel)
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        // Создаем элементы управления в окне
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField choiceKey = new TextField();
        TextField valueToEdit = new TextField();

        gridPane.add(new Label("Ключ записи"), 0, 1);
        gridPane.add(choiceKey, 1, 1);
        gridPane.add(new Label("Новая запись без ключа:"), 0, 2);
        gridPane.add(valueToEdit, 1, 2);
        dialog.getDialogPane().setContent(gridPane);

        // Ожидаем закрытия окна и обрабатываем результат
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeOk) {
                // Возвращаем введенные пользователем данные
                return choiceKey.getText() + " " +
                        valueToEdit.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }


    //Бэкап
    //создание backup-файла БД
    @FXML
    protected void createBackup() {
        String path = "C:\\Users\\arina\\OneDrive\\Desktop\\данные\\example.txt";

        //получение текущей даты и времени для имени backup
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());

        //имя backup
        String backupFileName = "example_backup_" + timestamp + ".txt";

        String backuppath = "C:\\Users\\arina\\OneDrive\\Desktop\\данные\\" + backupFileName;

        try {
            //копирование
            Files.copy(new File(path).toPath(), new File(backuppath).toPath());

            System.out.println("Создан бэкап файл: " + backupFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //восстановление из backup-файла БД
    @FXML
    protected void restoreBackup() {
        Stage stage = new Stage();
        File selectedFile = showFileChooser(stage);

        if (selectedFile != null) {
            // Восстановление из выбранного backup-файла
            String sourceFilePath = "C:\\Users\\arina\\OneDrive\\Desktop\\данные\\example.txt";

            try {
                Files.copy(selectedFile.toPath(), new File(sourceFilePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Файл восстановлен из: " + selectedFile.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Файл не выбран");
        }
    }

    private File showFileChooser(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Backup File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        return fileChooser.showOpenDialog(stage);
    }

    //импорт данных в Excel-файл
    @FXML
    protected void importData() {
        String path = "C:\\Users\\arina\\OneDrive\\Desktop\\данные\\example.txt";
        String path2 = "C:\\Users\\arina\\OneDrive\\Desktop\\данные\\example.xlsx";
        try (BufferedReader br = new BufferedReader(new FileReader(path));
             FileOutputStream fos = new FileOutputStream(path2);
             Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Data");

            String line;
            int rowNum = 0;

            while ((line = br.readLine()) != null) {
                String[] data = line.split("/");
                Row row = sheet.createRow(rowNum++);

                for (int i = 0; i < data.length; i++) {
                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(i);
                    cell.setCellValue(data[i]);
                }
            }

            workbook.write(fos);
            System.out.println("Данные успешно импортированы.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}