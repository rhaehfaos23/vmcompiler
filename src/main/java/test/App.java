package test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class App {
    public static void main(String[] args) throws Exception {
        String filename = args[0];
        String outFilename = "";
        Path filePath = Paths.get(args[0]);

        if (Files.isDirectory(filePath)) {
            System.out.println("Input Directory!!!!!!!!!!!!!!");
            System.out.println("Parent Path: " + filePath.getParent());
            System.out.println("filename: " + filePath.getFileName());

            try (Stream<Path> pathList = Files.walk(filePath)) {
                outFilename = filePath.toString() + File.separator + filePath.getFileName() + ".asm";
                System.out.println("outFilename: " + outFilename);

                try (CodeWriter codeWriter = new CodeWriter(outFilename)) {
                    pathList.filter(file -> !Files.isDirectory(file))
                            .filter(file -> file.toString().endsWith(".vm"))
                            .forEach(file -> {
                                try {
                                    System.out.println("process file name: " + file.getFileName());
                                    codeWriter.setFilename(file);
                                    codeWriter.process();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        int dotIndex = filename.lastIndexOf('.');
        String name = filename.substring(0, dotIndex);
        String extension = filename.substring(dotIndex + 1);

        System.out.println("input filename: " + name);
        System.out.println("input extension: " + extension);

        try (CodeWriter codeWriter = new CodeWriter(name + ".asm")) {
            codeWriter.setFilename(Paths.get(filename));
            codeWriter.process();
        }
    }
}
