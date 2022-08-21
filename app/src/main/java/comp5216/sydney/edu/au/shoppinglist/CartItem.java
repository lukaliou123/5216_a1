package comp5216.sydney.edu.au.shoppinglist;


import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Entity(tableName = "cartItems")
public class CartItem {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "itemID")
    public int itemID;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "amount")
    public int amount;

    @ColumnInfo(name = "purchased")
    public int purchased;

    @ColumnInfo(name = "dueTime")
    public String dueTime;

    public CartItem(String name, int amount, String dueTime, int purchased) {
        this.name = name;
        this.amount = amount;
        this.dueTime = dueTime;
        this.purchased = purchased;
    }

    public int getitemID() {
        return itemID;
    }

    public void setitemID(int toDoItemID) {
        this.itemID = toDoItemID;
    }

    public void setName(String name){
        this.name= name;
    }

    public String getName(){
        return name;
    }

    public void setAmount(int amount){
        this.amount = amount;
    }

    public int getAmount(){
        return amount;
    }

    public void setDueTime(String dueTime){
        this.dueTime = dueTime;
    }

    public String getDueTime(){
        return dueTime;
    }

    public void setPurchased(int purchased){
        this.purchased = purchased;
    }

    public int getPurchased(){
        return purchased;
    }

    public String getInfo() throws ParseException {
        String info = getName()+" Amount: "+getAmount()+" Remain: "+remainTime();
        return info;
    }

    public String remainTime() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dueTime = simpleDateFormat.parse(getDueTime());
        Date date = new Date(System.currentTimeMillis());
        long diff = dueTime.getTime() - date.getTime();
        if(diff<=0){
            return "Overdue";
        }else{
            long day=diff/(24*60*60*1000);
            long hour=(diff/(60*60*1000)-day*24);
            String ans = day+"days "+hour+"hours";
            Log.i("time diff is :",ans);
            return ans;
        }
    }

}
