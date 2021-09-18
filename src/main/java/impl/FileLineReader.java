package impl;

import abstr.Agent;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;


@Slf4j
public class FileLineReader implements Runnable, Closeable {

    private FileInputStream fileInputStream;
    private String fileName;
    private BlockingDeque<String> blockingDeque;
    private Scanner scanner;
    private boolean failed;
    private Agent agent;
    private ExecutorService executorService;

    public FileLineReader(String fileName, String encoding, Agent agent) {
        this.agent = agent;
        this.fileName = fileName;
        try {
            fileInputStream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            log.error("Входной файл \'{}\' не открыт по причине \'{}\'. В сортировке не участвует.", fileName, e.getMessage());
            failed = true;
            return;
        }

        scanner = new Scanner(fileInputStream, encoding);
        blockingDeque = new LinkedBlockingDeque<>(2);
        executorService = Executors.newSingleThreadExecutor();
    }

    public boolean isFailed() {
        return failed;
    }

    public BlockingDeque startAsyncReading() {
        if (!failed) {
            executorService.submit(this);
            return blockingDeque;
        } else {
            return null;
        }
    }

    @Override
    public void run() {
        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line == null) continue;
                String value = line.replaceAll("\\s+", "");
                try {
                    blockingDeque.putLast(value);
                } catch (InterruptedException e) {
                    log.warn("Прервано чтение файла \'{}\'", fileName);
                }
            }
            if (scanner.ioException() != null) // note that Scanner suppresses exceptions
                log.warn("Сбой при чтении файла \'{}\' по причине \'{}\'", fileName, scanner.ioException().getMessage());
        } finally {
            close();
        }
    }

    @Override
    public void close() {
        if (scanner != null) scanner.close();
        if (fileInputStream != null) {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                // hide on closing
            }
        }
        agent.signal(blockingDeque);
        executorService.shutdownNow();
    }
}
