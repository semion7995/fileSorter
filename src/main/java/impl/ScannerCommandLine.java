package impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.util.List;

@Slf4j
class ScannerCommandLine {
    private String[] arguments;

    ScannerCommandLine(String... args) {
        this.arguments = args;
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar filesorter.jar [OPTIONS] output.file input.files...\n" +
                "output.file  Обязательное имя файла с результатом сортировки.\n" +
                "input.files  Один, или больше входных файлов.\n", options);
    }

    private static void performanceShutdownAndPrint(Options options, int status) {
        printHelp(options);
        System.exit(status);
    }

    void parse() {
        Options options = new Options();
        options.addOption("s", false, "Файлы содержат строки. Обязательна, взаимоисключительна с -i.");
        options.addOption("i", false, "Файлы содержат целые числа. Обязательна, взаимоисключительна с -s.");
        options.addOption("a", false, "Сортировка по возрастанию. Применяется по умолчанию при отсутствии -a или -d.");
        options.addOption("d", false, "Сортировка по убыванию. Опция не обязательна как и -a.");
        options.addOption("w", false, "Файлы ожидаются в кодировке CP1251. Опция не обязательна. По умолчанию используется UTF8 кодировка файлов.");
        options.addOption("h", "help", false, "Отобразить справку.");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, arguments);
        } catch (UnrecognizedOptionException e) {
            log.warn("Неизвестная опция \'{}\'", e.getOption());
            performanceShutdownAndPrint(options, 1);
        } catch (ParseException e) {
            log.error("Сбой разбора командной строки \'{}\'", e.getMessage());
            performanceShutdownAndPrint(options, 2);
        }
        if (cmd == null) {
            log.error("Сбой разбора командной строки по неведомой причине");
            performanceShutdownAndPrint(options, 3);
        } else {
            if (cmd.hasOption('h') || arguments.length == 0) {
                performanceShutdownAndPrint(options, 0);
            }
            if (!(cmd.hasOption('i') || cmd.hasOption('s'))) {
                log.warn("Отсутствует обязательная опция -s или -i");
                performanceShutdownAndPrint(options, 4);
            }
            if (cmd.hasOption('i') && cmd.hasOption('s')) {
                log.warn("Должна быть только одна опция или -s или -i");
                performanceShutdownAndPrint(options, 5);
            }
            if (cmd.hasOption('a') && cmd.hasOption('d')) {
                log.warn("Должна быть только одна опция или -a или -d");
                performanceShutdownAndPrint(options, 6);
            }

            List<String> files = cmd.getArgList();
            if (files.size() < 2) {
                log.warn("Отсутствует имя файла для результата, или хотя бы одно имя входного файла.");
                performanceShutdownAndPrint(options, 7);
            }

            if (cmd.hasOption('d')) Starter.isAscending = false;
            if (cmd.hasOption('i')) Starter.isStrings = false;
            if (cmd.hasOption('w')) Starter.encoding = "cp1251";
            Starter.outputFileName = files.get(0);
            files.remove(0);
            Starter.inputFileNames = files;

        }
    }
}