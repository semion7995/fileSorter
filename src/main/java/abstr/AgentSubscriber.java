package abstr;

import java.util.concurrent.BlockingDeque;

public interface AgentSubscriber {
    void signal(BlockingDeque<String> deque);
}
