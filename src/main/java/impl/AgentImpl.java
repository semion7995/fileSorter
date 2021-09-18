package impl;

import abstr.Agent;
import abstr.AgentSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;

public class AgentImpl implements Agent {

    private List<AgentSubscriber> allSignalingReceiver = new ArrayList<>();

    @Override
    public synchronized void signal(BlockingDeque<String> deque) {
        for (AgentSubscriber subscriber : allSignalingReceiver){
            subscriber.signal(deque);
        }
    }

    public void subscribe(AgentSubscriber subscriber){
        allSignalingReceiver.add(subscriber);
    }
}
