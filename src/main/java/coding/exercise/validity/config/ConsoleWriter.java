package coding.exercise.validity.config;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class ConsoleWriter<T> implements ItemWriter<T> {
    @Override
    public void write(List<? extends T> items) throws Exception {
        for (T item : items) {
            System.out.println(item);
        }
    }
}
