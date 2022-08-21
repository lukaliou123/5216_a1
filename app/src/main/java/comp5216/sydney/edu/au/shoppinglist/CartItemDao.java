package comp5216.sydney.edu.au.shoppinglist;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.List;

@Dao
public interface CartItemDao {
    @Query("SELECT * FROM cartItems")
    List<CartItem> listAll();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUsers(CartItem... crtItem);

    @Insert
    void insertAll(CartItem... cartItems);

    @Query("DELETE FROM cartItems")
    void deleteAll();

    @Query("SELECT * FROM cartItems WHERE name LIKE :name LIMIT 1")
    CartItem findByName(String name);

    @Query("DELETE FROM cartItems WHERE name LIKE :name")
    void deleteByName(String name);

    @Update
    public void updateItem(CartItem cartItem);

    @Insert
    void insert(CartItem item);

    @Delete
    void delete(CartItem item);
}
