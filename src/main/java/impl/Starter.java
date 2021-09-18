package impl;

import abstr.Agent;
import abstr.AgentSubscriber;
import abstr.Sorter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Starter {
    static boolean isStrings = true;
    static boolean isAscending = true;
    static List<String> inputFileNames = new ArrayList<>();
    static String encoding = "utf-8";
    static String outputFileName = "";

    public static void main(String[] args) {

        new ScannerCommandLine(args).parse();

        Agent agent = new AgentImpl();
        try (Sorter sorter = new SorterImpl(outputFileName, encoding);
             RetainerWorkers holder = new RetainerWorkers(sorter,agent)) {
            agent.subscribe((AgentSubscriber) sorter);
            holder.startWork();
        } catch (IOException e) {
            log.error("Проблема при закрытии файлов \'{}\'", e.getMessage());
        }
    }
}
