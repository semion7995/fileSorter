package impl;

import abstr.Agent;
import abstr.Sorter;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class RetainerWorkers implements Closeable {
    private List<FileLineReader> fileLineReaders = new ArrayList<>();
    private List<BlockingDeque<String>> blockingDeques = new CopyOnWriteArrayList<>();
    private Sorter sorter;
    private Agent agent;


    public RetainerWorkers(Sorter sorter, Agent agent) {
        this.sorter = sorter;
        this.agent = agent;
    }

    public void startWork() {
        for (String file : Starter.inputFileNames) {
            FileLineReader worker = new FileLineReader(file, Starter.encoding, agent);
            if (!worker.isFailed()) fileLineReaders.add(worker);
        }
        if (fileLineReaders.size() == 0) {
            log.error("Нет доступных для обработки входных файлов.");
            System.exit(20);
        } else {
            for (FileLineReader reader : fileLineReaders) {
                BlockingDeque<String> deque = reader.startAsyncReading();
                if (deque != null) blockingDeques.add(deque);
            }
        }
        sorter.startSort(blockingDeques);
    }

    @Override
    public void close() {
        fileLineReaders.forEach(FileLineReader::close);
    }
}
