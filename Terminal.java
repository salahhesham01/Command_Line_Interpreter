import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class Terminal {
    private final Parser parser;
    Path currentDirectory = Paths.get(""); //   Initializing path
    private final List<String> commandHistory; //   List of commands history

    //  Constructor to construct a parser for the terminal and a list of command history
    public Terminal() {
        parser = new Parser();
        commandHistory = new ArrayList<>();
    }

    //  A method that returns whatever string sent to it
    public void echo(String[] args) {
        if (args.length > 0) {
            System.out.println(String.join(" ", args)); //  Returns a string joined on whitespace
        } else {
            System.out.println("Please provide text to echo.");
        }
    }

    //  A method that returns current directory using Path class
    public void pwd() {
        System.out.println("Current directory: " + currentDirectory.toAbsolutePath());
    }

    //  A method that returns current history of commands executed by user
    public void history() {
        for (int i = 0; i < commandHistory.size(); i++) {
            System.out.println((i + 1) + " " + commandHistory.get(i));
        }
    }

    //  A method that returns both a list of current directory files in order and in reverse order based on input
    //  ls => in order | ls -r in reverse order
    public void ls(boolean reverseOrder) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(currentDirectory)) {
            List<Path> entries = new ArrayList<>();
            for (Path entry : directoryStream) {
                entries.add(entry);
            }

            if (reverseOrder) {
                Collections.reverse(entries);
            }

            for (Path entry : entries) {
                System.out.println(entry.getFileName());
            }
        } catch (IOException e) {
            System.out.println("Error listing directory: " + e.getMessage());
        }
    }

    //function to make a directory
    public void mkdir(String[] args) {
        if(args.length <1){
            System.out.println("Please provide 1 argument");
        }
        else {
            for (String arg : args) {
                Path newDirectory = currentDirectory.resolve(arg);
                try {
                    Files.createDirectory(newDirectory);
                } catch (IOException e) {
                    System.out.println("Error creating directory: " + e.getMessage());
                }
            }
        }
    }

    //function to make a file
    public void touch(String[] args) {
        if(args.length <1){
            System.out.println("Please provide 1 argument");
        }
        else {
            for (String arg : args) {
                Path newFile = currentDirectory.resolve(arg);
                try {
                    Files.createFile(newFile);
                } catch (IOException e) {
                    System.out.println("Error creating file: " + e.getMessage());
                }
            }
        }
    }

    //function to copy a file
    public void cp(String[] args) {
        if(args.length != 2){
            System.out.println("Please provide 2 arguments");
            return;
        }
        Path source = currentDirectory.resolve(args[0]);
        Path destination = currentDirectory.resolve(args[1]);
        try {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error copying file: " + e.getMessage());
        }
    }
    //check if folder is empty
    public boolean isEmpty(Path path) throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            return !directoryStream.iterator().hasNext();
        }
    }

    //get all empty directories in the current directory
    public List<Path> getEmptyDirectories() throws IOException {
        List<Path> emptyDirectories = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(currentDirectory)) {
            for (Path entry : directoryStream) {
                if (Files.isDirectory(entry) && isEmpty(entry)) {
                    emptyDirectories.add(entry);
                }
            }
        }
        return emptyDirectories;
    }

    //function to remove a directory
    public void rmdir(String[] args){
        if(args.length < 1){
            System.out.println("Please provide 1 argument");
        } else if (args[0].equals("*")) {
            try {
                List<Path> emptyDirectories = getEmptyDirectories();
                for (Path emptyDirectory : emptyDirectories) {
                    Files.delete(emptyDirectory);
                }
            } catch (IOException e) {
                System.out.println("Error deleting directory: " + e.getMessage());
            }
        }
        else {
            for(String arg : args){
                Path directory = currentDirectory.resolve(arg);
                try {
                    Files.delete(directory);
                } catch (IOException e) {
                    System.out.println("Error deleting directory: " + e.getMessage());
                }
            }
        }
    }

    //  A method to delete a file
    public static void rm(String file) {
        File f = new File(file);
        if (f.exists()) {
            if (f.delete()) {
                System.out.println("File deleted successfully.");
            } else {
                System.out.println("Error: Failed to delete the file.");
            }
        } else {
            System.out.println("Error: The specified file does not exist.");
        }
    }

    //  A method that prints the content of file
    public static void cat(String file) {
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Error: Failed to read the file.");
            e.printStackTrace();
        }
    }

    //  A method that prints the content of two files after concatenating them
    public static void cat(String file1, String file2) {
        try {
            StringBuilder concatenatedFile = new StringBuilder();

            BufferedReader reader1 = new BufferedReader(new FileReader(file1));
            String line;
            while ((line = reader1.readLine()) != null) {
                concatenatedFile.append(line).append('\n');
            }
            reader1.close();

            BufferedReader reader2 = new BufferedReader(new FileReader(file2));
            while ((line = reader2.readLine()) != null) {
                concatenatedFile.append(line).append('\n');
            }
            reader2.close();

            System.out.println(concatenatedFile);
        } catch (IOException e) {
            System.out.println("Error: Failed to read the file.");
            e.printStackTrace();
        }
    }

    //  A method that counts lines, words, characters in a file
    public void wc(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: wc <file>");
            return;
        }

        Path filePath = currentDirectory.resolve(args[0]);

        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            long lines = 0;
            long words = 0;
            long characters = 0;

            try (Stream<String> linesStream = Files.lines(filePath)) {
                lines = linesStream.count();
            } catch (IOException e) {
                System.out.println("Error counting lines: " + e.getMessage());
            }

            try (Stream<String> fileContentStream = Files.lines(filePath)) {
                words = fileContentStream
                        .flatMap(line -> Stream.of(line.split("\\s+")))
                        .filter(word -> !word.isEmpty())
                        .count();
            } catch (IOException e) {
                System.out.println("Error counting words: " + e.getMessage());
            }

            try (Stream<String> fileContentStream = Files.lines(filePath)) {
                characters = fileContentStream
                        .flatMapToInt(CharSequence::chars)
                        .count();
            } catch (IOException e) {
                System.out.println("Error counting characters: " + e.getMessage());
            }

            System.out.println(lines + " " + words + " " + characters + " " + filePath.getFileName());
        } else {
            System.out.println("File does not exist.");
        }
    }

    //  A method that copied a directory into another directory
    public void cpr(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: cp -r <source_directory> <destination_directory>");
            return;
        }

        Path sourceDirectory = currentDirectory.resolve(args[0]);
        Path destinationDirectory = currentDirectory.resolve(args[1]);

        if (!Files.isDirectory(sourceDirectory) || !Files.isDirectory(destinationDirectory)) {
            System.out.println("Both source and destination must be directories.");
            return;
        }

        try (DirectoryStream<Path> sourceStream = Files.newDirectoryStream(sourceDirectory)) {
            for (Path sourceFile : sourceStream) {
                Path destinationFile = destinationDirectory.resolve(sourceFile.getFileName());
                Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("Files copied successfully.");
        } catch (IOException e) {
            System.out.println("Error copying files: " + e.getMessage());
        }
    }

    //  A method that deals with the command entered by user and calls appropriate method for the command to be executed
    public void chooseCommandAction() {
        String commandName = parser.getCommandName();
        String[] args = parser.getArgs();

        commandHistory.add(commandName + " " + String.join(" ", args)); //  Add the command to history

        switch (commandName) {
            case "echo":
                echo(args);
                break;
            case "pwd":
                pwd();
                break;
            case "history":
                history();
                break;
            case "ls":
                ls(false);
                break;
            case "ls -r":
                ls(true);
                break;
            case "mkdir":
                mkdir(args);
                break;
            case "touch":
                touch(args);
                break;
            case "cp":
                cp(args);
                break;
            case "rmdir":
                rmdir(args);
                break;
            case "rm":
                if (args.length == 1) {
                    rm(args[0]); // Call the rm method with the file to be deleted
                } else {
                    System.out.println("Usage: rm <file>");
                }
                break;
            case "cat":
                if (args.length == 1) {
                    cat(args[0]); // Call the cat method to print the content of a single file
                } else if (args.length == 2) {
                    cat(args[0], args[1]); // Call the cat method to concatenate and print two files
                } else {
                    System.out.println("Usage: cat <file1> [file2]");
                }
                break;
            case "cp -r":
                cpr(args);
                break;
            case "wc":
                wc(args);
                break;
            case "exit":
                System.out.println("Exiting the program.");
                System.exit(0);
                break;
            default:
                System.out.println("Command not recognized.");
        }
    }

    //  Driver code to start the program
    public static void main(String[] args) {
        Terminal terminal = new Terminal(); //  Initialize a terminal that will use parser to parse input and deal with commands
        Scanner scanner = new Scanner(System.in);   //  Get Input from user

        //  An infinite loop that exits on user entering "exit"
        while (true){
            System.out.println("Enter command: ");
            String input = scanner.nextLine();
            if (terminal.parser.parse(input)){
                terminal.chooseCommandAction(); //  Call appropriate method to deal with the command
            }else {
                System.out.println("Invalid input, please enter a valid command");  //  Tell user to enter a valid command
            }
        }
    }
}