package com.example.dream.fareslicer.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CallOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.QueryValue;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.graphics.Bitmap.Config.RGBA_F16;
import static android.graphics.Bitmap.Config.RGB_565;

public class ProfilePage extends AppCompatActivity {

    private static final String TAG = "Profile Page";
    private static Bitmap.Config BITMAP_CONFIG = null;
    public File file;
    public File files;
    Uri imageUri;
    String fileName="";

    private static final int FILE_SELECT_CODE = 0;
    ImageView imageview;

    Bitmap bitmap=null;

    EditText name,phno,email;
    FloatingActionButton save;
    LinearLayout linearLayout;

    ArrayList<String> permissions=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        initView();

        SharedPreferences sp=getSharedPreferences("User",MODE_PRIVATE);
        String user_id=sp.getString("user_id","");
        String phn=sp.getString("user_phno","");

        setPermission();
        if (!phn.equalsIgnoreCase(""))// change phn to user id
        {
            String n=sp.getString("user_name","");
            name.setText(n);
            if (phn.equalsIgnoreCase(""))
            {
                phno.setEnabled(true);
            }
            else
            {
                phno.setText(phn);
                phno.setEnabled(false);

            }
            email.setText(sp.getString("user_email",""));
            fileName=sp.getString("user_photo","");
            String path= Environment.getExternalStorageDirectory() + File.separator + "FareSlicer"+ File.separator +fileName;

            if(fileName.equals("")) {

                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                imageview.setBackgroundColor(color);
//                imageview.setBackgroundColor(0xff00ff00);
            } else {
                Bitmap bitmap = BitmapFactory.decodeFile(path);

//            Toast.makeText(this, ""+bitmap, Toast.LENGTH_SHORT).show();
                imageview.setImageBitmap(bitmap);
            }

        }
        else {

        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            BITMAP_CONFIG = RGBA_F16;
        }
        else
        {
            BITMAP_CONFIG = RGB_565;
        }

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveFunction();
            }
        });
//        filewrite();//should be written on onSave
    }


    private void initView() {

        imageview=findViewById(R.id.profile_image);
        name=findViewById(R.id.profile_userName);
        phno=findViewById(R.id.profile_phno);
        email=findViewById(R.id.profile_email);
        linearLayout=findViewById(R.id.profile_linearLayout);

        save=findViewById(R.id.profile_save);

    }

    public void setPermission()
    {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                for (String p : info.requestedPermissions) {
                    Log.d(TAG, "Permission : " + p);
                    permissions.add(p);
                }

                checkWriteExternalPermission(permissions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkWriteExternalPermission(ArrayList<String> permission)
    {
        ArrayList<String> notgranted =new ArrayList<>();

        for(int i=0;i<permission.size();i++) {

            String per_name=permission.get(i);
            int res = checkCallingOrSelfPermission(permission.get(i));

            if (res == PackageManager.PERMISSION_GRANTED) {

//                Toast.makeText(getApplicationContext(), permission + "permission granted", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Permission granted : " + permission.get(i));

            }
            else {
//                Toast.makeText(getApplicationContext(), permission + "permission not granted", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Permission not granted : " + permission.get(i));

                notgranted.add(permission.get(i));


            }
        }

        String[] p=new String[notgranted.size()];

        for (int i=0;i<notgranted.size();i++) {
            p = notgranted.toArray(new String[i]);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //
//                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        //                            takePictureButton.setEnabled(false);
            requestPermissions(p, 0);

//                    } else {
//                        Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
//                    }

        } else {
            Toast.makeText(this, "You have to give permission externally", Toast.LENGTH_SHORT).show();
        }

    }

    //Image Upload
    //--------------------------------------------------------------------------------------------------------------------------
//    private void showFileChooser() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        try {
//            startActivityForResult(
//                    Intent.createChooser(intent, "Select a File to Upload"),
//                    FILE_SELECT_CODE);
//        } catch (android.content.ActivityNotFoundException ex) {
//            // Potentially direct the user to the Market with a Dialog
//            Toast.makeText(this, "Please install a File Manager.",
//                    Toast.LENGTH_SHORT).show();
//        }
//    }

    private void showFileChooser() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(photoPickerIntent, "Select a File to Upload"),
                FILE_SELECT_CODE);
    }

    //To directly show the bitmap..

//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        if (requestCode == FILE_SELECT_CODE)
//        {
//            if(data!=null)
//            {
//                try
//                {
//                    if (bitmap != null)
//                    {
//                        bitmap.recycle();
//                    }
//
//                    InputStream stream = getContentResolver().openInputStream(data.getData());
//                    bitmap = BitmapFactory.decodeStream(stream);
//                    stream.close();
//                    imageview.setImageBitmap(bitmap);
//                }
//
//                catch (FileNotFoundException e)
//                {
//                    e.printStackTrace();
//                }
//
//                catch (IOException e)
//                {
//                    e.printStackTrace();
//                }
//            }
//
//            else
//            {
//                Drawable drawable = getResources().getDrawable(R.drawable.ic_user);
//                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
//                Canvas canvas = new Canvas(bitmap);
//                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//                drawable.clearColorFilter();
//                drawable.draw(canvas);
//
//                imageview.setImageBitmap(bitmap);
//            }
//
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }


    private void saveFunction() {

        String tb_name=name.getText().toString();
        String tb_phn=phno.getText().toString();
        String tb_email=email.getText().toString();
        String tb_image=fileName;

        Boolean save=false;

        filewrite();
        if(tb_name.equalsIgnoreCase(""))
        {
            Snackbar.make(linearLayout,"Name should not be empty",Snackbar.LENGTH_SHORT).show();
            save=false;
        }
        else if (tb_phn.equalsIgnoreCase(""))
        {
            Snackbar.make(linearLayout,"Phone number should not be empty",Snackbar.LENGTH_SHORT).show();
            save=false;

        }
        else if (tb_email.equalsIgnoreCase(""))
        {
            Snackbar.make(linearLayout,"Email id should not be empty",Snackbar.LENGTH_SHORT).show();
            save=false;

        }
        else
        {
            save=true;
        }

        if(save==true)
        {
            SharedPreferences sharedPreferences=getSharedPreferences("User",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();

            String query="";
            ArrayList<String> value=new ArrayList();
            if (tb_image.equalsIgnoreCase(""))
            {

                query="insert into tb_user (user_name,user_phno,user_email) values(?,?,?)";
                value.add(tb_name);
                value.add(tb_phn);
                value.add(tb_email);

                editor.putString("user_name",tb_name);
                editor.putString("user_phno",tb_phn);
                editor.putString("user_email",tb_email);
                editor.putString("user_photo","");

            }
            else
            {

                query="insert into tb_user (user_name,user_phno,user_email,user_photo) values(?,?,?,?)";
                value.add(tb_name);
                value.add(tb_phn);
                value.add(tb_email);
                value.add(tb_image);

                editor.putString("user_name",tb_name);
                editor.putString("user_phno",tb_phn);
                editor.putString("user_email",tb_email);
                editor.putString("user_photo",tb_image);

            }

            editor.apply();

            insertCall(query,value,tb_phn);
        }

    }

    private void insertCall(String query, ArrayList<String> value, final String tb_phn) {

        QueryValue queryValue=new QueryValue();
        queryValue.setQuery(query);
        queryValue.setValue(value);


        Call<CallResult> call= RetrofitClient.getInstance().getApi().insert(queryValue);

        call.enqueue(new Callback<CallResult>() {
            @Override
            public void onResponse(Call<CallResult> call, Response<CallResult> response) {

                if(response.code()==200)
                {
                    CallResult callResult=response.body();
                    String text= "";
                    if (callResult != null) {
                        text = callResult.getStatus();

                        if(text.equalsIgnoreCase("true"))
                        {
                            Snackbar.make(linearLayout,"Saved successfully",Snackbar.LENGTH_SHORT).show();
                            selectCall(tb_phn);
                        }
                        else
                        {
                            Snackbar.make(linearLayout,"Saved unsuccessful"+text,Snackbar.LENGTH_SHORT).show();

                        }
                    }



                }
                else {
                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                        }
                        else
                        {
                            Log.e("Insertion","Error body is null");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {

                Toast.makeText(ProfilePage.this, "Insert Call failed", Toast.LENGTH_SHORT).show();
                Log.e("Insert", t.getMessage());
            }

        });
    }

    private void selectCall(String tb_phn) {

        String query="select user_id from tb_user where user_phno=?";
        ArrayList<String> value=new ArrayList();
        value.add(tb_phn);

        QueryValue queryValue=new QueryValue();
        queryValue.setQuery(query);
        queryValue.setValue(value);


        Call<CallResult> call= RetrofitClient.getInstance().getApi().select(queryValue);

        call.enqueue(new Callback<CallResult>() {
            @Override
            public void onResponse(Call<CallResult> call, Response<CallResult> response) {

                if(response.code()==200) {

                    CallResult selectResult=response.body();

                    String success= "";
                    if (selectResult != null) {
                        success = selectResult.getStatus();

                        if (success.equalsIgnoreCase("true")) {

                            List<CallOutput> output = selectResult.getOutput();

                            CallOutput item = output.get(0);
                            List<String> list=item.getValue();
                            String id=list.get(0);

                            SharedPreferences sharedPreferences=getSharedPreferences("User",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("user_id",id);
                            editor.apply();

                            startActivity(new Intent(ProfilePage.this,Home.class));

                        } else {
                            Log.e("Selection", "Success is false");
                        }
                    }
                    else
                    {
                        Log.e("Selection", "Success is null ");
                    }

                }
                else {
                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                        }
                        else
                        {
                            Log.e("Selection","Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e("Insertion",e.getMessage());

                    }
                }
            }

            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {

                Toast.makeText(ProfilePage.this, "Selection Call failed", Toast.LENGTH_SHORT).show();
                Log.e("Selction", t.getMessage());
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {

                    // Get the Uri of the selected file
                    Uri uri = data.getData();


                    Log.d(TAG, "File Uri: " + uri.toString());

                    // Get the path
                    String path = null;
                    try {
                        path = ProfilePage.getPath(ProfilePage.this, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    // Log.d(TAG, "File Path: " + path);

                    // Get the file instance
                    if (path != null) {
                        file = new File(path);
                    }
                    else {
                        Log.d(TAG,"Path null");
                    }

                    // Initiate the upload
                    File folder = new File(Environment.getExternalStorageDirectory() +
                            File.separator + "FareSlicer");



                    boolean success = true;
                    if (!folder.exists()) {
                        success = folder.mkdirs();
                    }
                    if (success) {
                        // Do something on success

                        try {

//                            imageUri = data.getData();
                            imageUri = uri;

                            files = new File(folder, file.getName());

                            // make a new bitmap from your file
                            fileName=file.getName();
                            Bitmap bitmap = BitmapFactory.decodeFile(path);
                            imageview.setImageBitmap(bitmap);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Do something else on failure
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void filewrite()
    {
        FileOutputStream outStream = null;
        try
        {


            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
            outStream = new FileOutputStream(files);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
//-----------------------------------------------------------------------------------------------------------------------------

}
