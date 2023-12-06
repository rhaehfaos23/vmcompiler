package test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

class Parser {
    private List<String> lines = new ArrayList<>();
    private Integer currLine = -1;
    private Integer maxLine = Integer.MAX_VALUE;
    private String[] currCommand = null;
    private CommandType currCommandType = CommandType.C_NOTDEFINED;

    private static final Integer COMMAND = 0;
    private static final Integer ARGUMENTS1 = 1;
    private static final Integer ARGUMENTS2 = 2;

    public Parser(String filename) throws IOException {
        try (Stream<String> fileLines = Files.lines(Paths.get(filename), StandardCharsets.UTF_8)) {
            lines.addAll(fileLines.map((line) -> line.strip()).filter((line) -> !line.isBlank()).toList());
            maxLine = lines.size();
        }
    }

    public boolean hasMoreLine() {
        return currLine + 1 < maxLine;
    }

    public void test() {
        try {
            while (hasMoreLine()) {
                advance();
                System.out.println(
                        currCommandType + " ----- " + Arrays.toString(currCommand));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void advance() {
        if (!hasMoreLine()) {
            return;
        }

        ++currLine;
        String nextLine = lines.get(currLine);
        while (hasMoreLine() && nextLine.startsWith("//")) {
            nextLine = lines.get(++currLine);
        }

        currCommand = nextLine.split("\\s+");
        currCommandType = determinCommandType();
    }

    private CommandType determinCommandType() {
        String command = currCommand[0];
        if (currCommand == null || currCommand.length == 0) {
            return CommandType.C_NOTDEFINED;
        } else if (command.startsWith("push")) {
            return CommandType.C_PUSH;
        } else if (command.startsWith("pop")) {
            return CommandType.C_POP;
        } else if (command.startsWith("add") ||
                command.startsWith("sub") ||
                command.startsWith("neg") ||
                command.startsWith("eq") ||
                command.startsWith("gt") ||
                command.startsWith("lt") ||
                command.startsWith("and") ||
                command.startsWith("or") ||
                command.startsWith("not")) {
            return CommandType.C_ARITHMETIC;
        } else if (command.startsWith("function")) {
            return CommandType.C_FUNCTION;
        } else if (command.startsWith("call")) {
            return CommandType.C_CALL;
        } else if (command.startsWith("return")) {
            return CommandType.C_RETURN;
        } else if (command.startsWith("goto")) {
            return CommandType.C_GOTO;
        } else if (command.startsWith("label")) {
            return CommandType.C_LABEL;
        } else if (command.startsWith("if-goto")) {
            return CommandType.C_IF;
        } else {
            return CommandType.C_NOTDEFINED;
        }
    }

    public CommandType getCommandType() {
        return currCommandType;
    }

    public String arg1() throws Exception {
        if (currCommand == null || currCommand.length == 0) {
            throw new Exception("currCommand is null or empty");
        }

        if (CommandType.C_ARITHMETIC == currCommandType || CommandType.C_RETURN == currCommandType) {
            return currCommand[COMMAND];
        }

        return currCommand[ARGUMENTS1];
    }

    public String arg2() throws Exception {
        if (currCommand == null || currCommand.length == 0) {
            throw new Exception("currCommand is null or empty");
        }

        if (currCommand.length < 3) {
            throw new Exception("Illegal command - not enough size(" + currCommand.length + "), curruntCommand: " + Arrays.toString(currCommand));
        }

        if (currCommandType != CommandType.C_PUSH &&
                currCommandType != CommandType.C_POP &&
                currCommandType != CommandType.C_FUNCTION &&
                currCommandType != CommandType.C_CALL) {
            throw new Exception("current command type is not C_PUSH, C_POP, C_FUNCTION, C_CALL");
        }

        return currCommand[ARGUMENTS2];
    }
}
