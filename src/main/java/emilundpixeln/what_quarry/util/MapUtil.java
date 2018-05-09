package emilundpixeln.what_quarry.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapUtil {
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean reverse) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        if(reverse)
            list.sort(Map.Entry.comparingByValue(new ReverseComperator<V>()));
        else
            list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}