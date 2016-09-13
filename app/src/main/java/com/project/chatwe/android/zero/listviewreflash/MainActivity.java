package com.project.chatwe.android.zero.listviewreflash;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity implements ReflashListView.IReflashListener{
    ArrayList<ItemEntity> itemEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setData();
        showList(itemEntities);

    }

    ApkAdapater adapater;
    ReflashListView listView;
    private  void showList(ArrayList<ItemEntity> itemEntities){
        if (adapater==null){
            listView= (ReflashListView) findViewById(R.id.lv);
            adapater=new ApkAdapater(this,itemEntities);
            listView.setAdapter(adapater);
            listView.setInterface(this);
        }else {
            adapater.onDateChange(itemEntities);
        }

    }

    private  void   setData(){
        itemEntities=new ArrayList<>();
        for (int i=0;i<10;i++){
            ItemEntity entity=new ItemEntity();
            entity.setTitle("Hello");
            entity.setContent("This is amagzing !");
            itemEntities.add(entity);
        }
    }

    private  void   setLastData(){
        for (int i=0;i<2;i++){
            ItemEntity entity=new ItemEntity();
            entity.setTitle("baby");
            entity.setContent("You are beautiful!!!");
            itemEntities.add(0,entity);//插入到顶部
        }
    }


    private  void   setMoreData(){
        for (int i=0;i<2;i++){
            ItemEntity entity=new ItemEntity();
            entity.setTitle("More");
            entity.setContent("I am  working!!!");
            itemEntities.add(entity);//插入到底部
        }
    }


    @Override
    public void onReflash() {

        setLastData();
        showList(itemEntities);
        listView.reflashComplete();

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                /**
//                 *要执行的操作
//                 */
//
//
//
//            }
//        }, 2000);//3秒后执行Runnable中的run方法


    }

    @Override
    public void onLoadData() {
        setMoreData();
        showList(itemEntities);
        listView.reflashComplete();
    }


}
