package abstr;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;

public interface Sorter extends Closeable {
    void startSort(List<BlockingDeque<String>> deques);
}
