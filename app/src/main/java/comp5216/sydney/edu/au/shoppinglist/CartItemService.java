package comp5216.sydney.edu.au.shoppinglist;

public interface CartItemService {

    void setPurchased(CartItem cartItem,int purchased);

    void setAmount(CartItem cartItem,int amount);

    void setDueDate(CartItem cartItem, String date);

    String getInfo(CartItem cartItem);

}
