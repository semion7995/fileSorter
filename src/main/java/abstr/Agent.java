package abstr;

import java.util.concurrent.BlockingDeque;

public interface Agent {
    void signal(BlockingDeque<String> deque);
    void subscribe(AgentSubscriber subscriber);
}
