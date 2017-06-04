package penknife.segment.dictionary;

import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * 对标签-频次的封装
 *
 * @author hankcs
 */
public class EnumItem<E extends Enum<E>> {
    public Map<E, Integer> labelMap;

    public EnumItem() {
        labelMap = new TreeMap<>();
    }

    /**
     * 创建只有一个标签的条目
     *
     * @param label
     * @param frequency
     */
    public EnumItem(E label, Integer frequency) {
        this();
        labelMap.put(label, frequency);
    }

    /**
     * 创建一个条目，其标签频次都是1，各标签由参数指定
     *
     * @param labels
     */
    public EnumItem(E... labels) {
        this();
        for (E label : labels) {
            labelMap.put(label, 1);
        }
    }

    public void addLabel(E label) {
        Integer frequency = labelMap.get(label);
        if (frequency == null) {
            frequency = 1;
        } else {
            ++frequency;
        }

        labelMap.put(label, frequency);
    }

    public void addLabel(E label, Integer frequency) {
        Integer innerFrequency = labelMap.get(label);
        if (innerFrequency == null) {
            innerFrequency = frequency;
        } else {
            innerFrequency += frequency;
        }

        labelMap.put(label, innerFrequency);
    }

    public boolean containsLabel(E label) {
        return labelMap.containsKey(label);
    }

    public int getFrequency(E label) {
        Integer frequency = labelMap.get(label);
        if (frequency == null) return 0;
        return frequency;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        ArrayList<Map.Entry<E, Integer>> entries = new ArrayList<>(labelMap.entrySet());
        entries.sort((o1, o2) -> -o1.getValue().compareTo(o2.getValue()));
        for (Map.Entry<E, Integer> entry : entries) {
            sb.append(entry.getKey());
            sb.append(' ');
            sb.append(entry.getValue());
            sb.append(' ');
        }
        return sb.toString();
    }

    public static Map.Entry<String, Map.Entry<String, Integer>[]> create(String param) {
        if (param == null) return null;
        String[] array = param.split(" ");
        return create(array);
    }

    @SuppressWarnings("unchecked")
    public static Map.Entry<String, Map.Entry<String, Integer>[]> create(String param[]) {
        if (param.length % 2 == 0) return null;
        int natureCount = (param.length - 1) / 2;
        Map.Entry<String, Integer>[] entries = (Map.Entry<String, Integer>[]) Array.newInstance(Map.Entry.class, natureCount);
        for (int i = 0; i < natureCount; ++i) {
            entries[i] = new AbstractMap.SimpleEntry<>(param[1 + 2 * i], Integer.parseInt(param[2 + 2 * i]));
        }
        return new AbstractMap.SimpleEntry<>(param[0], entries);
    }
}
