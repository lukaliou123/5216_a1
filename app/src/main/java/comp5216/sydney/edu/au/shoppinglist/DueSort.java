package comp5216.sydney.edu.au.shoppinglist;

import java.util.Comparator;

public class DueSort implements Comparator<CartItem> {
    @Override
    public int compare(CartItem t1, CartItem t2) {
        return -(t1.getDueTime().compareTo(t2.getDueTime()));
    }
}
