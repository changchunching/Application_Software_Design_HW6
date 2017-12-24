package com.example.user.hw_ch6;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.zip.DataFormatException;


public class MainActivity extends AppCompatActivity{

    private NavigationAdapter navigationAdapter;
    private ListView nav_custom_listview;

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private List<View> viewPager_views;

    private FirstAdapter firstAdapter;
    private ListView firstlist;
    private SecondAdapter secondAdapter;
    private ListView secondlist;
    private ThirdAdapter thirdAdapter;
    private ListView thirdlist;

    private SQLiteDatabase database;
    private SimpleCursorAdapter simpleCursorAdapter;
    Button nav_add_btn,nav_del_btn,first_add,first_del;
    EditText first_edit;
    String CurrentShopName = null;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //DrawerLayout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        MyDBHelper dbHelper = new MyDBHelper(this);
        database = dbHelper.getWritableDatabase();

        initNavigationView();
        initviewPager();

        initfirstlistview();
        initsecondlistview();
        initthirdlistview();
        nav_custom_listview = (ListView)findViewById(R.id.nav_custom_listview);
        nav_custom_listview.setOnItemClickListener(NavListViewClick);
        nav_add_btn = (Button)findViewById(R.id.nav_add_btn);
        nav_add_btn.setOnClickListener(NavAdd);
        nav_del_btn = (Button)findViewById(R.id.nav_del_btn);
        nav_del_btn.setOnClickListener(NavDel);

        View getview = viewPager_views.get(0);
        firstlist = (ListView)getview.findViewById(R.id.firstlist);
        first_add = (Button)getview.findViewById(R.id.first_add);
        first_del = (Button)getview.findViewById(R.id.first_del);
        first_edit = (EditText)getview.findViewById(R.id.first_edit);
        first_add.setOnClickListener(FirstAdd);
        first_del.setOnClickListener(FirstDel);
        View getview2 = viewPager_views.get(1);
        secondlist=(ListView)getview2.findViewById(R.id.secondlist);
        View getview3 = viewPager_views.get(2);
        thirdlist=(ListView)getview3.findViewById(R.id.thirdlist);

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.maplayout);

        PutInNavList();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_detail) {
            if(CurrentShopName!=null){
                String[] colum={"shopname","phone","address"};
                Cursor c = database.query("shopTable",colum,"shopname="+"'"+CurrentShopName+"'",null,null,null,null);
                c.moveToFirst();
                String DisplayText="名稱："+c.getString(c.getColumnIndex("shopname"))+"\n"+
                        "電話："+c.getString(c.getColumnIndex("phone"))+"\n"+
                        "地址："+c.getString(c.getColumnIndex("address"));
                AlertDialog.Builder dialog=new AlertDialog.Builder(this);
                dialog.setTitle("店家詳細資料");
                dialog.setMessage(DisplayText);
                dialog.setPositiveButton("確認",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
            
            else{
                Toast.makeText(getApplicationContext(),"請先選擇店家",Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        if(id == R.id.action_map){
            if(CurrentShopName!=null){
                dialog.show();
                final SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        if(ActivityCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED&&
                                ActivityCompat.checkSelfPermission(MainActivity.this,
                                        Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                            return;
                        }
                        googleMap.clear();
                        MarkerOptions ml = new MarkerOptions();

                        String[] colum={"shopname","phone","address"};
                        Cursor c = database.query("shopTable",colum,"shopname="+"'"+CurrentShopName+"'",null,null,null,null);
                        c.moveToFirst();
                        String addr=c.getString(c.getColumnIndex("address"));

                        Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        double latitude=0.0;
                        double longitude=0.0;
                        try {
                            List<Address> addressLocation = geoCoder.getFromLocationName(addr, 1);
                            latitude = addressLocation.get(0).getLatitude();
                            longitude = addressLocation.get(0).getLongitude();
                        }catch(IOException e){

                        }


                        ml.position(new LatLng(latitude,longitude));
                        ml.title(getTitle().toString());
                        ml.draggable(true);
                        googleMap.addMarker(ml);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),11));
                    }
                });
            }
            else{
                Toast.makeText(getApplicationContext(),"請先選擇店家",Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initNavigationView(){
        //nav_custom_listview = (ListView)findViewById(R.id.nav_custom_listview);
        //navigationAdapter=new NavigationAdapter(this);
        //nav_custom_listview.setAdapter(navigationAdapter);
    }

    private Button.OnClickListener FirstDel = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(CurrentShopName!=null) {
                if (first_edit.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "請於左側輸入要刪除名稱", Toast.LENGTH_SHORT).show();
                } else {
                    database.delete("productTable", "judgename=" + "'" + getTitle() + "' and pos='first' and productname='" + first_edit.getText().toString() + "'", null);
                    Toast.makeText(getApplicationContext(), "刪除成功", Toast.LENGTH_SHORT).show();
                    first_edit.setText("");
                    PutInFirList();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "請先選擇店家", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private Button.OnClickListener FirstAdd = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(CurrentShopName!=null) {
                LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                final View view = layoutInflater.inflate(R.layout.firalert, null);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("新增商品")
                        .setView(view)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText firinput1 = (EditText) (view.findViewById(R.id.firinput1));
                                EditText firinput2 = (EditText) (view.findViewById(R.id.firinput2));
                                EditText firinput3 = (EditText) (view.findViewById(R.id.firinput3));
                                if (firinput1.getText().toString().equals("") ||
                                        firinput2.getText().toString().equals("") ||
                                        firinput3.getText().toString().equals("")) {
                                    Toast.makeText(getApplicationContext(), "輸入資料不完全", Toast.LENGTH_SHORT).show();
                                } else {
                                    ContentValues cv = new ContentValues();
                                    cv.put("judgename", getTitle().toString());
                                    cv.put("pos", "first");
                                    cv.put("productname", firinput1.getText().toString());
                                    cv.put("description", firinput2.getText().toString());
                                    cv.put("price", firinput3.getText().toString());
                                    database.insert("productTable", null, cv);
                                    Toast.makeText(getApplicationContext(), firinput1.getText().toString(), Toast.LENGTH_SHORT).show();
                                    PutInFirList();
                                }
                            }
                        })
                        .show();
            }
            else{
                Toast.makeText(getApplicationContext(), "請先選擇店家", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void LayoutReset(){
        PutInFirList();
        PutInSecList();
        PutInThirList();
    }

    private void PutInThirList(){
        String[] colum={"_ID","judgename","pos","productname","description","price","time"};
        Cursor cursor = database.query("productTable",colum,"judgename='"+getTitle()+"' and pos='third'",null,null,null,null);
        SimpleCursorAdapter simpleCursorAdapter4 = new SimpleCursorAdapter(this,
                R.layout.third_custom_row, cursor, new String[] {"productname","description","time"},
                new int[] {R.id.history_name,R.id.history_des,R.id.history_time}, 0);
        thirdlist.setAdapter(simpleCursorAdapter4);
    }

    private void PutInSecList(){
        String[] colum={"_ID","judgename","pos","productname","description","price","time"};
        Cursor cursor = database.query("productTable",colum,"judgename='"+getTitle()+"' and pos='second'",null,null,null,null);
        SecondListAdapter simpleCursorAdapter3 = new SecondListAdapter(this,
                R.layout.second_custom_row,cursor,
                new String[] {"productname","description","time"},
                new int[] {R.id.order_name,R.id.order_des,R.id.order_time}, 0);
        secondlist.setAdapter(simpleCursorAdapter3);
    }

    private void PutInFirList(){
        String[] colum={"_ID","judgename","pos","productname","description","price"};
        Cursor cursor = database.query("productTable",colum,"judgename='"+getTitle()+"' and pos='first'",null,null,null,null);
        /*SimpleCursorAdapter simpleCursorAdapter2 = new SimpleCursorAdapter(this,
                R.layout.first_custom_row, cursor, new String[] {"productname","description","price"},
                new int[] {R.id.product_name,R.id.product_des,R.id.product_price}, 0);*/
        FirstListAdapter simpleCursorAdapter2 = new FirstListAdapter(this,
                R.layout.first_custom_row,cursor,
                new String[] {"productname","description","price"},
                new int[] {R.id.product_name,R.id.product_des,R.id.product_price}, 0);
        firstlist.setAdapter(simpleCursorAdapter2);
    }

    private ListView.OnItemClickListener NavListViewClick=new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor c = simpleCursorAdapter.getCursor();
            String shopname = c.getString(c.getColumnIndex("shopname"));
            CurrentShopName = shopname;
            setTitle(CurrentShopName);
            LayoutReset();
        }
    };

    private Button.OnClickListener NavDel = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            EditText nav_edit=(EditText)findViewById(R.id.nav_edit);
            if(nav_edit.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(),"請於左側輸入要刪除名稱",Toast.LENGTH_SHORT).show();
            }
            else{
                database.delete("shopTable","shopname="+"'"+nav_edit.getText().toString()+"'",null);
                database.delete("productTable","judgename="+"'"+nav_edit.getText().toString()+"'",null);
                if(nav_edit.getText().toString().equals(getTitle().toString())){
                    setTitle(null);
                    CurrentShopName=null;
                    LayoutReset();
                }
                Toast.makeText(getApplicationContext(),"刪除成功",Toast.LENGTH_SHORT).show();
                nav_edit.setText("");
                PutInNavList();
            }
        }
    };

    private Button.OnClickListener NavAdd = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            LayoutInflater layoutInflater=LayoutInflater.from(MainActivity.this);
            final View view=layoutInflater.inflate(R.layout.navalert, null);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("新增店家")
                    .setView(view)
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText navinput1 = (EditText) (view.findViewById(R.id.navinput1));//shopname
                            EditText navinput2 = (EditText) (view.findViewById(R.id.navinput2));//shoptel
                            EditText navinput3 = (EditText) (view.findViewById(R.id.navinput3));//shopaddr
                            if(navinput1.getText().toString().equals("")||
                                    navinput2.getText().toString().equals("")||
                                    navinput3.getText().toString().equals("")){
                                Toast.makeText(getApplicationContext(),"輸入資料不完全",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                ContentValues cv= new ContentValues();
                                cv.put("shopname",navinput1.getText().toString());
                                cv.put("phone",navinput2.getText().toString());
                                cv.put("address",navinput3.getText().toString());
                                database.insert("shopTable",null,cv);
                                Toast.makeText(getApplicationContext(),"新增成功",Toast.LENGTH_SHORT).show();
                                PutInNavList();
                            }
                        }
                    })
                    .show();
        }
    };

    private void PutInNavList(){
        String[] colum={"_ID","shopname"};
        Cursor cursor = database.query("shopTable",colum,null,null,null,null,null);
        simpleCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.nav_custom_row, cursor, new String[] { "shopname"},
                new int[] { R.id.nav_custom_text}, 0);
        nav_custom_listview.setAdapter(simpleCursorAdapter);
    }

    private void initviewPager(){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        viewPager_views = new ArrayList<View>();
        viewPager_views.add(layoutInflater.inflate(R.layout.layout_first,null));
        viewPager_views.add(layoutInflater.inflate(R.layout.layout_second,null));
        viewPager_views.add(layoutInflater.inflate(R.layout.layout_third,null));

        viewPagerAdapter=new ViewPagerAdapter(viewPager_views,this);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(viewPagerAdapter);
    }

    public class SecondListAdapter extends SimpleCursorAdapter {

        private Context mContext;
        private Context appContext;
        private int layout;
        private Cursor cr;
        private LayoutInflater inflater;

        public SecondListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            this.layout = layout;
            this.mContext = context;
            this.inflater = LayoutInflater.from(context);
            this.cr = c;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return inflater.inflate(layout, null);
        }

        @Override
        public void bindView(final View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);
            final int row_id = cursor.getInt(cursor.getColumnIndex("_id"));
            final TextView order_name = (TextView)view.findViewById(R.id.order_name);
            final TextView order_des = (TextView)view.findViewById(R.id.order_des);
            final TextView order_time = (TextView)view.findViewById(R.id.order_time);
            Button finish = (Button)view.findViewById(R.id.finish);
            finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(),"完成："+order_name.getText(), Toast.LENGTH_SHORT).show();
                    ContentValues cv= new ContentValues();
                    cv.put("pos","third");
                    database.update("productTable",cv,"productname='"+order_name.getText().toString()+"' and pos='second' and _id='"+row_id+"'",null);
                    PutInSecList();
                    PutInThirList();
                }
            });
        }
    }

    public class FirstListAdapter extends SimpleCursorAdapter {

        private Context mContext;
        private Context appContext;
        private int layout;
        private Cursor cr;
        private LayoutInflater inflater;

        public FirstListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            this.layout = layout;
            this.mContext = context;
            this.inflater = LayoutInflater.from(context);
            this.cr = c;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return inflater.inflate(layout, null);
        }

        @Override
        public void bindView(final View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);
            final int row_id = cursor.getInt(cursor.getColumnIndex("_id"));
            final TextView product_name = (TextView)view.findViewById(R.id.product_name);
            final TextView product_des = (TextView)view.findViewById(R.id.product_des);
            final TextView product_price = (TextView)view.findViewById(R.id.product_price);
            Button buy = (Button)view.findViewById(R.id.buy);
            buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(),"購買："+product_name.getText(), Toast.LENGTH_SHORT).show();
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String date = sDateFormat.format(new java.util.Date());
                    ContentValues cv= new ContentValues();
                    cv.put("judgename",getTitle().toString());
                    cv.put("pos","second");
                    cv.put("productname",product_name.getText().toString());
                    cv.put("description",product_des.getText().toString());
                    cv.put("time",date);
                    database.insert("productTable",null,cv);
                    PutInSecList();
                }
            });
        }
    }

    private void initfirstlistview(){

        //firstAdapter=new FirstAdapter(getview.getContext());
        //firstlist.setAdapter(firstAdapter);
    }

    private void initsecondlistview() {

        //secondAdapter=new SecondAdapter(getview.getContext());
        //secondlist.setAdapter(secondAdapter);
    }

    private void initthirdlistview(){

        //thirdAdapter=new ThirdAdapter(getview.getContext());
        //thirdlist.setAdapter(thirdAdapter);
    }
}