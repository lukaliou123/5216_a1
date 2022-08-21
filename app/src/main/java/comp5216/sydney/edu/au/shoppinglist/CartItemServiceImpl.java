package comp5216.sydney.edu.au.shoppinglist;

public class CartItemServiceImpl implements CartItemService {
    @Override
    public void setPurchased(CartItem cartItem, int purchased) {
        cartItem.setPurchased(purchased);
    }

    @Override
    public void setAmount(CartItem cartItem, int amount) {
        cartItem.setAmount(amount);
    }

    @Override
    public void setDueDate(CartItem cartItem, String date) {

    }

    @Override
    public String getInfo(CartItem cartItem) {
        return null;
    }
}
