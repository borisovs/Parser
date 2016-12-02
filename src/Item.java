
/**
 * Created by sborisov on 02.12.16.
 */
public class Item implements Comparable<Item> {

    public Item(String first, String second) {
        this.first = first;
        this.second = second;
    }

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    @Override
    public int compareTo(Item item) {
        return second.compareTo(item.second);
    }

    private String first;
    private String second;


}
