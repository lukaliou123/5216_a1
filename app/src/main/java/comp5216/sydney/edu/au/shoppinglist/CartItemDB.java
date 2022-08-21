package comp5216.sydney.edu.au.shoppinglist;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
@Database(entities = {CartItem.class}, version = 1, exportSchema = false)
public abstract class CartItemDB extends RoomDatabase {
    private static final String DATABASE_NAME = "cartitems_db";
    private static CartItemDB DBINSTANCE;
    public abstract CartItemDao cartItemDao();
    public static CartItemDB getDatabase(Context context) {
        if (DBINSTANCE == null) {
            synchronized (CartItemDB.class) {
                DBINSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        CartItemDB.class, DATABASE_NAME).allowMainThreadQueries().build();
            }
        }
        return DBINSTANCE;
    }
    public static void destroyInstance() {
        DBINSTANCE = null;
    }
}
