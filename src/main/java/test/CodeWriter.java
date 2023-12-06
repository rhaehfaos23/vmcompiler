package test;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

class CodeWriter implements Closeable {
    private BufferedWriter bufferedWriter;
    private String filename;
    private int lineNumber = 0;
    private String currFunctionName = "";
    private int returnCount = 0;
    private Path filePath = null;

    public CodeWriter(String filename) throws IOException {
        this.filename = filename.substring(0, filename.lastIndexOf('.'));

        bufferedWriter = new BufferedWriter(new FileWriter(filename));
        writeCommand("@256");
        writeCommand("D=A");
        writeCommand("@SP");
        writeCommand("M=D");

        gotoFunction("Sys.init");
    }

    public void setFilename(Path path) throws IOException {
        filePath = path;
        filename = path.getFileName().toString();
        filename = filename.substring(0, filename.lastIndexOf('.'));
    }

    public void process() throws Exception {
        Parser parser = new Parser(filePath.toAbsolutePath().toString());

        while (parser.hasMoreLine()) {
            parser.advance();
            switch (parser.getCommandType()) {
                case C_ARITHMETIC:
                    writeArithmetic(parser.arg1());
                    break;
                case C_PUSH:
                    writePushPop(CommandType.C_PUSH, parser.arg1(), Integer.parseInt(parser.arg2()));
                    break;
                case C_POP:
                    writePushPop(CommandType.C_POP, parser.arg1(), Integer.parseInt(parser.arg2()));
                    break;
                case C_IF:
                    writeIfGo(parser.arg1());
                    break;
                case C_GOTO:
                    writeGoto(parser.arg1());
                    break;
                case C_LABEL:
                    writeLabel(parser.arg1());
                    break;
                case C_FUNCTION:
                    writeFunction(parser.arg1(), Integer.parseInt(parser.arg2()));
                    break;
                case C_CALL:
                    writeCall(parser.arg1(), Integer.parseInt(parser.arg2()));
                    break;
                case C_RETURN:
                    writeReturn();
                    break;
                default:
                    break;
            }
        }
    }

    public void writeArithmetic(String command) throws IOException {
        StringBuilder sb = new StringBuilder();
        switch (command) {
            case "add":
                sb.append("// add").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M-1").append("\n");
                sb.append("A=M").append("\n");
                sb.append("D=M").append("\n");
                sb.append("@13").append("\n");
                sb.append("M=D").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M-1").append("\n");
                sb.append("A=M").append("\n");
                sb.append("D=M").append("\n");
                sb.append("@R13").append("\n");
                sb.append("D=D+M").append("\n");
                sb.append("@SP").append("\n");
                sb.append("A=M").append("\n");
                sb.append("M=D").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M+1").append("\n");
                lineNumber += 17;
                break;
            case "sub":
                sb.append("// sub").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M-1").append("\n");
                sb.append("A=M").append("\n");
                sb.append("D=M").append("\n");
                sb.append("@13").append("\n");
                sb.append("M=D").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M-1").append("\n");
                sb.append("A=M").append("\n");
                sb.append("D=M").append("\n");
                sb.append("@R13").append("\n");
                sb.append("D=D-M").append("\n");
                sb.append("@SP").append("\n");
                sb.append("A=M").append("\n");
                sb.append("M=D").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M+1").append("\n");
                lineNumber += 17;
                break;
            case "neg":
                sb.append("// neg").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M-1").append("\n");
                sb.append("A=M").append("\n");
                sb.append("D=-M").append("\n");
                sb.append("@SP").append("\n");
                sb.append("A=M").append("\n");
                sb.append("M=D").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M+1").append("\n");
                lineNumber += 9;
                break;
            case "eq":
                sb.append("// eq").append("\n");
                writeCommand("@SP");
                writeCommand("M=M-1");
                writeCommand("A=M");
                writeCommand("D=M");
                writeCommand("@R13");
                writeCommand("M=D");
                writeCommand("@SP");
                writeCommand("M=M-1");
                writeCommand("A=M");
                writeCommand("D=M");
                writeCommand("@R13");
                writeCommand("D=D-M");
                writeCommand("@" + (lineNumber + 11));
                writeCommand("D;JNE");
                writeCommand("@1");
                writeCommand("D=-A");
                writeCommand("@SP");
                writeCommand("A=M");
                writeCommand("M=D");
                writeCommand("@SP");
                writeCommand("M=M+1");
                writeCommand("@" + (lineNumber + 9));
                writeCommand("0;JMP");
                writeCommand("@0");
                writeCommand("D=A");
                writeCommand("@SP");
                writeCommand("A=M");
                writeCommand("M=D");
                writeCommand("@SP");
                writeCommand("M=M+1");
                break;
            case "gt":
                sb.append("// gt").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M-1").append("\n");
                sb.append("A=M").append("\n");
                sb.append("D=M").append("\n");
                sb.append("@R13").append("\n");
                sb.append("M=D").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M-1").append("\n");
                sb.append("A=M").append("\n");
                sb.append("D=M").append("\n");
                sb.append("@R13").append("\n");
                sb.append("D=D-M").append("\n");
                lineNumber += 12;
                sb.append("@" + (lineNumber + 11)).append("\n");
                sb.append("D;JLE").append("\n");
                sb.append("@1").append("\n");
                sb.append("D=-A").append("\n");
                sb.append("@SP").append("\n");
                sb.append("A=M").append("\n");
                sb.append("M=D").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M+1").append("\n");
                lineNumber += 9;
                sb.append("@" + (lineNumber + 9)).append("\n");
                sb.append("0;JMP").append("\n");
                sb.append("@0").append("\n");
                sb.append("D=A").append("\n");
                sb.append("@SP").append("\n");
                sb.append("A=M").append("\n");
                sb.append("M=D").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M+1").append("\n");
                lineNumber += 9;
                break;
            case "lt":
                sb.append("// lt").append("\n");
                writeCommand("@SP");
                writeCommand("M=M-1");
                writeCommand("A=M");
                writeCommand("D=M");
                writeCommand("@R13");
                writeCommand("M=D");
                writeCommand("@SP");
                writeCommand("M=M-1");
                writeCommand("A=M");
                writeCommand("D=M");
                writeCommand("@R13");
                writeCommand("D=D-M");
                writeCommand("@" + (lineNumber + 11));
                writeCommand("D;JGE");
                writeCommand("@1");
                writeCommand("D=-A");
                writeCommand("@SP");
                writeCommand("A=M");
                writeCommand("M=D");
                writeCommand("@SP");
                writeCommand("M=M+1");
                writeCommand("@" + (lineNumber + 9));
                writeCommand("0;JMP");
                writeCommand("@0");
                writeCommand("D=A");
                writeCommand("@SP");
                writeCommand("A=M");
                writeCommand("M=D");
                writeCommand("@SP");
                writeCommand("M=M+1");
                break;
            case "and":
                sb.append("// and").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M-1").append("\n");
                sb.append("A=M").append("\n");
                sb.append("D=M").append("\n");
                sb.append("@13").append("\n");
                sb.append("M=D").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M-1").append("\n");
                sb.append("A=M").append("\n");
                sb.append("D=M").append("\n");
                sb.append("@R13").append("\n");
                sb.append("D=D&M").append("\n");
                sb.append("@SP").append("\n");
                sb.append("A=M").append("\n");
                sb.append("M=D").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M+1").append("\n");
                lineNumber += 17;
                break;
            case "or":
                sb.append("// or").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M-1").append("\n");
                sb.append("A=M").append("\n");
                sb.append("D=M").append("\n");
                sb.append("@13").append("\n");
                sb.append("M=D").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M-1").append("\n");
                sb.append("A=M").append("\n");
                sb.append("D=M").append("\n");
                sb.append("@R13").append("\n");
                sb.append("D=D|M").append("\n");
                sb.append("@SP").append("\n");
                sb.append("A=M").append("\n");
                sb.append("M=D").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M+1").append("\n");
                lineNumber += 17;
                break;
            case "not":
                sb.append("// not").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M-1").append("\n");
                sb.append("A=M").append("\n");
                sb.append("D=!M").append("\n");
                sb.append("@SP").append("\n");
                sb.append("A=M").append("\n");
                sb.append("M=D").append("\n");
                sb.append("@SP").append("\n");
                sb.append("M=M+1").append("\n");
                lineNumber += 9;
                break;
            default:
                break;
        }

        bufferedWriter.write(sb.toString());
        bufferedWriter.flush();
    }

    public void writePushPop(CommandType type, String segment, int index) {
        switch (type) {
            case C_PUSH:
                pushHelper(segment, index);
                break;
            case C_POP:
                popHelper(segment, index);
                break;
            default:
                break;
        }
        try {
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLabel(String label) {
        writeComment("label " + label);

        // create label
        writeLabelLine(getFunctionLabel(label));

        flush();
    }

    public void writeGoto(String label) {
        writeComment("goto " + label);

        // goto label
        writeCommand("@" + getFunctionLabel(label));
        writeCommand("0;JMP");

        flush();
    }

    public void writeIfGo(String label) {
        writeComment("if-goto " + label);

        // pop
        writeCommand("@SP");
        writeCommand("M=M-1");
        writeCommand("A=M");
        writeCommand("D=M");

        // if 0 goto label
        writeCommand("@" + getFunctionLabel(label));
        writeCommand("D;JNE");

        flush();
    }

    public void writeFunction(String functionName, int nVar) {
        // set currunt function
        currFunctionName = functionName;
        // reset return count
        returnCount = 0;

        writeComment("function " + functionName + " " + nVar);

        // create function label
        writeLabelLine(functionName);

        // create function local variable
        for (int i = 0; i < nVar; ++i) {
            pushHelper("constant", 0);
        }

        flush();
    }

    public void writeCall(String functionName, int nVar) {
        writeComment("call " + functionName + " " + nVar);

        // push return address
        writeCommand("@" + getFunctionReturnLabel());
        writeCommand("D=A");
        push();

        // push LCL
        writeCommand("@LCL");
        writeCommand("D=M");
        push();

        // push ARG
        writeCommand("@ARG");
        writeCommand("D=M");
        push();

        // push THIS
        writeCommand("@THIS");
        writeCommand("D=M");
        push();

        // push THAT
        writeCommand("@THAT");
        writeCommand("D=M");
        push();

        // ARG = SP - 5 - nVar
        writeCommand("@SP");
        writeCommand("D=M");
        writeCommand("@5");
        writeCommand("D=D-A");
        writeCommand("@" + nVar);
        writeCommand("D=D-A");
        writeCommand("@ARG");
        writeCommand("M=D");

        // LCL = SP
        writeCommand("@SP");
        writeCommand("D=M");
        writeCommand("@LCL");
        writeCommand("M=D");

        gotoFunction(functionName);
        // create function return label
        writeLabelLine(getFunctionReturnLabel());
        // increase return count
        ++returnCount;

        flush();
    }

    public void writeReturn() {
        // comment return
        writeComment("return");

        writeComment("frame = LCL");
        writeCommand("@LCL");
        writeCommand("D=M");
        writeCommand("@R13");
        writeCommand("M=D");

        writeComment("retAddr = *(frame - 5)");
        writeCommand("@R13");
        writeCommand("D=M");
        writeCommand("@5");
        writeCommand("A=D-A");
        writeCommand("D=M");
        writeCommand("@R14");
        writeCommand("M=D");

        writeComment("*ARG = pop()");
        writeCommand("@SP");
        writeCommand("M=M-1");
        writeCommand("A=M");
        writeCommand("D=M");
        writeCommand("@ARG");
        writeCommand("A=M");
        writeCommand("M=D");

        writeComment("SP = ARG + 1");
        writeCommand("@ARG");
        writeCommand("D=M+1");
        writeCommand("@SP");
        writeCommand("M=D");

        writeComment("THAT = *(frame - 1)");
        writeCommand("@R13");
        writeCommand("D=M");
        writeCommand("@1");
        writeCommand("A=D-A");
        writeCommand("D=M");
        writeCommand("@THAT");
        writeCommand("M=D");

        writeComment("THIS = *(frame - 2)");
        writeCommand("@R13");
        writeCommand("D=M");
        writeCommand("@2");
        writeCommand("A=D-A");
        writeCommand("D=M");
        writeCommand("@THIS");
        writeCommand("M=D");

        writeComment("ARG = *(frame - 3)");
        writeCommand("@R13");
        writeCommand("D=M");
        writeCommand("@3");
        writeCommand("A=D-A");
        writeCommand("D=M");
        writeCommand("@ARG");
        writeCommand("M=D");

        writeComment("LCL = *(frame - 4)");
        writeCommand("@R13");
        writeCommand("D=M");
        writeCommand("@4");
        writeCommand("A=D-A");
        writeCommand("D=M");
        writeCommand("@LCL");
        writeCommand("M=D");

        writeComment("goto retAddr");
        writeCommand("@R14");
        writeCommand("A=M");
        writeCommand("0;JMP");

        flush();
    }

    private void pushHelper(String segment, int index) {
        switch (segment) {
            case "local":
            case "argument":
            case "this":
            case "that":
                memorySegmentPush(segment, index);
                break;
            case "pointer":
                pointerPush(index);
                break;
            case "temp":
                tempPush(index);
                break;
            case "constant":
                constantPush(index);
                break;
            case "static":
                staticPush(index);
                break;
            default:
                break;
        }
    }

    private void popHelper(String segment, int index) {
        switch (segment) {
            case "local":
            case "argument":
            case "this":
            case "that":
                memorySegmentPop(segment, index);
                break;
            case "pointer":
                pointerPop(index);
                break;
            case "temp":
                tempPop(index);
                break;
            case "static":
                staticPop(index);
                break;
            default:
                break;
        }
    }

    private String convertSegment2Register(String segment) {
        switch (segment) {
            case "local":
                return "LCL";
            case "argument":
                return "ARG";
            case "this":
                return "THIS";
            case "that":
                return "THAT";
            default:
                return "";
        }
    }

    private void memorySegmentPush(String segment, int index) {
        String register = convertSegment2Register(segment);
        writeComment("push " + segment + "(" + register + ") " + index);
        writeCommand("@" + index);
        writeCommand("D=A");
        writeCommand("@" + register);
        writeCommand("A=D+M");
        writeCommand("D=M");
        writeCommand("@SP");
        writeCommand("A=M");
        writeCommand("M=D");
        writeCommand("@SP");
        writeCommand("M=M+1");
    }

    private void memorySegmentPop(String segment, int index) {
        String register = convertSegment2Register(segment);

        writeComment("pop " + segment + "(" + register + ") " + index);
        writeCommand("@" + index);
        writeCommand("D=A");
        writeCommand("@" + register);
        writeCommand("D=M+D");
        writeCommand("@R13");
        writeCommand("M=D");
        writeCommand("@SP");
        writeCommand("M=M-1");
        writeCommand("A=M");
        writeCommand("D=M");
        writeCommand("@R13");
        writeCommand("A=M");
        writeCommand("M=D");
    }

    private void pointerPush(int index) {
        String register = index == 0 ? "THIS" : "THAT";

        writeComment("push pointer " + index);
        writeCommand("@" + register);
        writeCommand("D=M");
        writeCommand("@SP");
        writeCommand("A=M");
        writeCommand("M=D");
        writeCommand("@SP");
        writeCommand("M=M+1");
    }

    private void pointerPop(int index) {
        String register = index == 0 ? "THIS" : "THAT";

        writeComment("pop pointer " + index);
        writeCommand("@SP");
        writeCommand("M=M-1");
        writeCommand("A=M");
        writeCommand("D=M");
        writeCommand("@" + register);
        writeCommand("M=D");
    }

    private void tempPush(int index) {
        String register = "R" + (index + 5);

        writeComment("push temp " + index);
        writeCommand("@" + register);
        writeCommand("D=M");
        writeCommand("@SP");
        writeCommand("A=M");
        writeCommand("M=D");
        writeCommand("@SP");
        writeCommand("M=M+1");
    }

    private void tempPop(int index) {
        String register = "R" + (index + 5);

        writeComment("pop temp " + index);
        writeCommand("@SP");
        writeCommand("M=M-1");
        writeCommand("A=M");
        writeCommand("D=M");
        writeCommand("@" + register);
        writeCommand("M=D");
    }

    private void constantPush(int index) {
        try {
            bufferedWriter.append("// push constant " + index).append('\n');
            bufferedWriter.append("@" + index).append('\n');
            bufferedWriter.append("D=A").append('\n');
            bufferedWriter.append("@SP").append('\n');
            bufferedWriter.append("A=M").append('\n');
            bufferedWriter.append("M=D").append('\n');
            bufferedWriter.append("@SP").append('\n');
            bufferedWriter.append("M=M+1").append('\n');
            lineNumber += 7;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void staticPush(int index) {
        try {
            bufferedWriter.append("// push static " + index).append('\n');
            bufferedWriter.append("@" + filename + "." + index).append('\n');
            bufferedWriter.append("D=M").append('\n');
            bufferedWriter.append("@SP").append('\n');
            bufferedWriter.append("A=M").append('\n');
            bufferedWriter.append("M=D").append('\n');
            bufferedWriter.append("@SP").append('\n');
            bufferedWriter.append("M=M+1").append('\n');
            lineNumber += 7;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void staticPop(int index) {
        try {
            bufferedWriter.append("// pop static " + index).append('\n');
            bufferedWriter.append("@SP").append('\n');
            bufferedWriter.append("M=M-1").append('\n');
            bufferedWriter.append("A=M").append('\n');
            bufferedWriter.append("D=M").append('\n');
            bufferedWriter.append("@" + filename + "." + index).append('\n');
            bufferedWriter.append("M=D").append('\n');
            lineNumber += 6;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLabelLine(String lable) {
        writeLine("(" + lable + ")");
    }

    private void writeComment(String comment) {
        writeLine("// " + comment);
    }

    private void writeCommand(String command) {
        writeLine(command + "// lineNumber: " + lineNumber);
        ++lineNumber;
    }

    private void writeLine(String line) {
        try {
            bufferedWriter.append(line).append('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void flush() {
        try {
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFunctionLabel(String label) {
        return currFunctionName + "$" + label;
    }

    private String getFunctionReturnLabel() {
        return currFunctionName + "$ret." + returnCount;
    }

    /**
     * PUSH ( value of D register)
     */
    private void push() {
        writeCommand("@SP");
        writeCommand("A=M");
        writeCommand("M=D");
        writeCommand("@SP");
        writeCommand("M=M+1");
    }

    private void gotoFunction(String functionName) {
        writeCommand("@" + functionName);
        writeCommand("0;JMP");
    }

    @Override
    public void close() throws IOException {
        writeLabelLine("END");
        writeCommand("@END");
        writeCommand("0;JMP");
        flush();
        if (bufferedWriter != null) {
            bufferedWriter.close();
        }
    }

}
