package comp5216.sydney.edu.au.shoppinglist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EditToDoItemActivity extends Activity implements View.OnClickListener {
    public int position=0;
    public int posAmount=1;
    public int posDue=2;
    EditText etItem;
    EditText etAmount;
    EditText etDueDate;

    Button btnDatePicker, btnTimePicker;
    EditText txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;

    CartItemDB db;
    CartItemDao cartItemDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Populate the screen using the layout
        setContentView(R.layout.activity_edit_item);
        db = CartItemDB.getDatabase(this.getApplication().getApplicationContext());
        cartItemDao = db.cartItemDao();
        // Get the data from the main activity screen
        String editItem = getIntent().getStringExtra("item");
        position = getIntent().getIntExtra("position",-1);
        // Show original content in the text field
        etItem = (EditText)findViewById(R.id.etEditItem);
        etItem.setText(editItem);
        Log.i("Updated item in db", editItem + ", position: " + position);

        //find amount num
        String editAmount = getIntent().getStringExtra("amount");
        posAmount = getIntent().getIntExtra("posAmo",-1);
        etAmount = findViewById(R.id.etEditAmount);
        etAmount.setText(editAmount);
        Log.i("Updated amount in db", editAmount + ", position: " + posAmount);
        //find due date
//        String editDueDate = getIntent().getStringExtra("dueDate");
//        posDue = getIntent().getIntExtra("posDue",-1);
//        etDueDate = findViewById(R.id.etEditDue);
//        etDueDate.setText(editDueDate);
//        Log.i("Updated due date in db", editDueDate + ", position: " + posDue);

        btnDatePicker=(Button)findViewById(R.id.btn_date);
        btnTimePicker=(Button)findViewById(R.id.btn_time);
        txtDate=(EditText)findViewById(R.id.in_date);
        txtTime=(EditText)findViewById(R.id.in_time);

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);

    }
    public void onSubmit(View v) {
        etItem = (EditText) findViewById(R.id.etEditItem);
        etAmount = findViewById(R.id.etEditAmount);
        // Prepare data intent for sending it back
        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra("item", etItem.getText().toString());
        data.putExtra("position", position);
        data.putExtra("amount", etAmount.getText().toString());
        data.putExtra("posAmo", posAmount);
        String total = etItem.getText().toString()+" " +etAmount.getText().toString();
        String dateAndTime = txtDate.getText().toString()+" "+txtTime.getText().toString();
        data.putExtra("total", total);
        data.putExtra("DT",dateAndTime);
        // Activity finishes OK, return the data
        setResult(RESULT_OK, data); // Set result code and bundle data for response
        saveItemsToDatabase();
        finish(); // Close the activity, pass data to parent
    }

    public void onCancel(View v){
        Intent intent = new Intent(EditToDoItemActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            txtTime.setText(hourOfDay + ":" + minute);
                            //Log.i("txtTime is: ",txtTime.getText().toString());
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
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
                    Log.i("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!txtDate is: ",txtDate.getText().toString());
                    String[] dateTime = txtDate.getText().toString().split("-");
                    String year = dateTime[2], month = dateTime[1],day=dateTime[0];
                    String[] hmTime = txtTime.getText().toString().split(":");
                    String hours = hmTime[0],min = hmTime[1];
                    String dueDate = year+"-"+month+"-"+day+" "+hours+":"+min+":00";
                    String name = etItem.getText().toString();
                    int amount = Integer.parseInt(etAmount.getText().toString());
                    try{
                        CartItem cartItem1 = cartItemDao.findByName(name);
                        cartItem1.setAmount(amount);
                        cartItem1.setDueTime(dueDate);
                        cartItemDao.updateItem(cartItem1);
                        Log.i("Update success! item id is: ",cartItem1.getitemID()+" name is : "+cartItem1.getName());
                    }catch(Exception e){
                        CartItem cartItem = new CartItem(name,amount,dueDate,0);
                        cartItemDao.insert(cartItem);
                        Log.i("Insert success! item id is: ",cartItem.getitemID()+" name is : "+cartItem.getName());
                    }
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