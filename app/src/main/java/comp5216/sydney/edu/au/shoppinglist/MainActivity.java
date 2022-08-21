package comp5216.sydney.edu.au.shoppinglist;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TwoLineListItem;


import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    List<CartItem> items;
    ArrayList<String> infos;
    ArrayAdapter<String> itemsAdapter;
    EditText addItemEditText;

    CartItemDB db;
    CartItemDao cartItemDao;
    long mLastClickTime;


    // Register a request to start an activity for result and register the result callback
    ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Extract name value from result extras
                    String itemName = result.getData().getExtras().getString("item");
                    //int position = result.getData().getIntExtra("position", -1);
                    //items.set(position, editedItem);
                    //Log.i("Updated item in list ", editedItem + ", position: " + position);
                    // Make a standard toast that just contains text
                    //Toast.makeText(getApplicationContext(), "Updated: " + editedItem, Toast.LENGTH_SHORT).show();
                    //toast是一种临时弹窗
                    readItemsFromDatabase();
                    try {
                        sortByChecked();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice  ,infos);
                    // Connect the listView and the adapter
                    listView.setAdapter(itemsAdapter);
                    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    setCheckBox();
                    itemsAdapter.notifyDataSetChanged();

                    //saveItemsToDatabase();;
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use "activity_main.xml" as the layout
        setContentView(R.layout.activity_main);
        db = CartItemDB.getDatabase(this.getApplication().getApplicationContext());
        cartItemDao = db.cartItemDao();
        // Reference the "listView" variable to the id "lstView" in the layout

        listView = (ListView) findViewById(R.id.lstView);

        //save current click time
        mLastClickTime = System.currentTimeMillis();
        readItemsFromDatabase();
        // Create an adapter for the list view using Android's built-in item layout

        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice  ,infos);
        // Connect the listView and the adapter

        listView.setAdapter(itemsAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //order is very important!
        setCheckBox();

        try {
            sortByChecked();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice  ,infos);
        // Connect the listView and the adapter
        listView.setAdapter(itemsAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        setCheckBox();
        itemsAdapter.notifyDataSetChanged();

        //long clik
        itemsAdapter.notifyDataSetChanged();
        setupListViewListener();

    }


    //click this button then jump to edit page
    public void onAddItemClick(View view) {
        Intent intent = new Intent(MainActivity.this, EditToDoItemActivity.class);
        if (intent != null) {
            // brings up the second activity
            mLauncher.launch(intent);
            //itemsAdapter.notifyDataSetChanged();
        }
    }

    public void saveCheck(View view) throws ParseException {
        int len = listView.getCount();
        for(int i =0;i<len;i++){
            if(listView.isItemChecked(i)){
                String name = infos.get(i).split(" Amount")[0];
                CartItem cartItem = cartItemDao.findByName(name);
                cartItem.setPurchased(1);
                cartItemDao.updateItem(cartItem);
            }else{
                String name = infos.get(i).split(" Amount")[0];
                CartItem cartItem = cartItemDao.findByName(name);
                cartItem.setPurchased(0);
                cartItemDao.updateItem(cartItem);
            }
        }
        sortByChecked();
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice  ,infos);
        // Connect the listView and the adapter
        listView.setAdapter(itemsAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        setCheckBox();
        itemsAdapter.notifyDataSetChanged();
    }

    public void setCheckBox(){
        int len = listView.getCount();
        for(int i =0;i<len;i++){
            String name = infos.get(i).split(" Amount")[0];
            CartItem cartItem = cartItemDao.findByName(name);
            if(cartItem.getPurchased()==0){
                listView.setItemChecked(i,false);
                //Log.i("item: ",name+" has not be purchased");
            }else{
                listView.setItemChecked(i,true);
                //Log.i("item: ",name+" has be purchased");
            }
        }
    }

    public void sortByChecked() throws ParseException {
        List<CartItem> checked = new ArrayList<>();
        List<CartItem> unchecked = new ArrayList<>();
        List<CartItem> total = new ArrayList<>();
        int len = listView.getCount();
        for(int i =0;i<len;i++){
            System.out.println(infos.get(i));
            String name = infos.get(i).split(" Amount")[0];
            CartItem cartItem = cartItemDao.findByName(name);
            if(cartItem.getPurchased()==0){
                unchecked.add(cartItem);
                Log.i("sort item: ",name+" has not be purchased");
            }else{
                checked.add(cartItem);
                Log.i("sort item: ",name+" has be purchased");
            }
        }
        Collections.sort(unchecked,new DueSort());
        Collections.sort(checked, new DueSort());
        Collections.reverse(unchecked);
        Collections.reverse(checked);
        total.addAll(unchecked);
        total.addAll(checked);
        infos.clear();
        for(CartItem item:total){
            infos.add(item.getInfo());
        }

    }

    private void setupListViewListener() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long rowId) {
                Log.i("MainActivity", "Long Clicked item " + position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.dialog_delete_title)
                        .setMessage(R.string.dialog_delete_msg)
                        .setPositiveButton(R.string.delete, new
                                DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String line = infos.remove(position); // Remove item from the ArrayList
                                        //find name and delete from database
                                        String name = line.split(" Amount")[0];
                                        Log.i("find name is: ",name);
//                                        for(int cnt =0;cnt<items.size();i++){
//                                            if(items.get(cnt).getName().equals(name)){
//                                                items.remove(cnt);
//                                                break;
//                                            }
//                                        }
                                        CartItem deleteItem = cartItemDao.findByName(name);
                                        cartItemDao.delete(deleteItem);
                                        itemsAdapter.notifyDataSetChanged(); // Notify listView adapter
                                        saveItemsToDatabase();
                                    }
                                })
                        .setNegativeButton(R.string.cancel, new
                                DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // User cancelled the dialog
                                        // Nothing happens
                                    }
                                });
                builder.create().show();
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long currTime = System.currentTimeMillis();
                if (currTime - mLastClickTime < ViewConfiguration.getDoubleTapTimeout()) {
                    onItemDoubleClick(parent, view, position, id);
                }
                mLastClickTime = currTime;

            }
            public void onItemDoubleClick(AdapterView<?> parent, View view, int position, long id) {
                String updateItem = (String) itemsAdapter.getItem(position);
                String name = updateItem.split(" Amount")[0];
                Log.i("MainActivity", "Clicked item " + position + ": " + updateItem);
                Intent intent = new Intent(MainActivity.this, EditToDoItemActivity.class);
                if (intent != null) {
                    // put "extras" into the bundle for access in the edit activity
                    intent.putExtra("item", name);
                    intent.putExtra("position", position);
                    // brings up the second activity
                    mLauncher.launch(intent);
                    itemsAdapter.notifyDataSetChanged();
                }
            }
        });
    }



    private void readItemsFromDatabase()
    {
        //Use asynchronous task to run query on the background and wait for result
        try {
            // Run a task specified by a Runnable Object asynchronously.
            CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                //read items from database
                    //CartItem cartItem = new CartItem("pear",2,"202208182200",0);
                    items = cartItemDao.listAll();
                    infos = new ArrayList<String>();
                    if (items != null & items.size() > 0) {
                        for (CartItem item : items) {
                            try {
                                infos.add(item.getInfo());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Log.i("SQLite read item", "ID: " + item.getitemID() + " Name: " +
                                    item.getName());
                        }
                    }
                    System.out.println("I'll run in a separate thread than the main thread.");
                }
            });
            // Block and wait for the future to complete
            future.get();
        }
        catch(Exception ex) {
            Log.e("readItemsFromDatabase", ex.getStackTrace().toString());
        }
    }
    private void saveItemsToDatabase()
    {
        //Use asynchronous task to run query on the background to avoid locking UI
        try {
            // Run a task specified by a Runnable Object asynchronously.
            CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    //delete all items and re-insert
                    items.clear();
                    items=cartItemDao.listAll();
                    System.out.println("I'll run in a separate thread than the main thread.");
                }
            });
            // Block and wait for the future to complete
            future.get();
        }
        catch(Exception ex) {
            Log.e("saveItemsToDatabase", ex.getStackTrace().toString());
        }
    }
}