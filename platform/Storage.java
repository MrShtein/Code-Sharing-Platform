package platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class Storage {
    private int index;
    private HashMap<Integer, Code> elements;

    @Autowired
    public Storage() {
        elements = new HashMap<>();
        index = 1;
    }

    public HashMap<Integer, Code> getElements() {
        return elements;
    }

    public int getIndex() {
        return index;
    }

    public void increaseIndex() {
        index++;
    }
}
