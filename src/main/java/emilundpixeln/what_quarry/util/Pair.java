package emilundpixeln.what_quarry.util;

import java.util.Objects;


/*
* Simple Pair that holds 2 Objects
* */
public class Pair<F, S> {
    private F first;
    private S second;


    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }


    public F getKey()
    {
        return first;
    }

    public S getValue()
    {
        return second;
    }

    public void setKey(F key) {
        first = key;
    }

    public void setValue(S value) {
        second = value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair<?, ?> p = (Pair<?, ?>) o;
        return Objects.equals(p.first, first) && Objects.equals(p.second, second);
    }

    @Override
    public int hashCode() {
        return (first == null ? 0 : first.hashCode()) ^ (second == null ? 0 : second.hashCode());
    }

}