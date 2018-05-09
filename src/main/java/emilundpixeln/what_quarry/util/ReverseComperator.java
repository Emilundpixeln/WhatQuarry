package emilundpixeln.what_quarry.util;



import java.util.Comparator;

public class ReverseComperator<V extends Comparable<? super V>> implements Comparator<V> {

    @Override
    public int compare(V b1, V b2) {

        return b2.compareTo(b1);
    }
}