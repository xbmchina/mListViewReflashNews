package com.project.chatwe.android.zero.listviewreflash;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/12.
 */
public class ReflashListView extends ListView implements AbsListView.OnScrollListener{
    View header,footer;
    private int firstVisibleItem;
    private int headerHeight;
    private int scrollState;
    private int lastDataNum;
    private int totalItemCount;
    private boolean isLoading;
    private IReflashListener iReflashListener;

    public ReflashListView(Context context) {
        super(context);
        initView(context);
    }

    public ReflashListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public ReflashListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void initView(Context context){
        LayoutInflater inflater=LayoutInflater.from(context);
        //添加头部下拉刷新
        header=inflater.inflate(R.layout.header_layout,null);
        measureView(header);
        headerHeight=header.getMeasuredHeight();
        Log.i("tag", "headerHeight=" + headerHeight);
        topPadding(-headerHeight);
        //添加尾部分页加载
        footer=inflater.inflate(R.layout.footer_layout,null);
        footer.setVisibility(GONE);

        this.addHeaderView(header);//添加ListView头部布局
        this.addFooterView(footer);//添加ListView尾部布局


        this.setOnScrollListener(this);//监听滑动
    }

    /**
     * 通知父布局，该子布局占用的宽高
     * @param view
     */
    private void measureView(View view){
        ViewGroup.LayoutParams p=view.getLayoutParams();
        if (p==null){
            p=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        }
        int width=ViewGroup.getChildMeasureSpec(0,0,p.width);
        int height;
        int tmpHeight=p.height;
        if (tmpHeight>0){
            height=MeasureSpec.makeMeasureSpec(tmpHeight,MeasureSpec.EXACTLY);
        }else{
            height=MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);

    }

    /**
     * 设置header布局的上边距。
     * @param topPadding
     */
    private void  topPadding(int topPadding){
        header.setPadding(header.getPaddingLeft(),topPadding,header.getPaddingRight(),header.getPaddingBottom());
        header.invalidate();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState=scrollState;
        if (lastDataNum==totalItemCount&&scrollState==SCROLL_STATE_IDLE){
            if (!isLoading){
                isLoading=true;
                footer.setVisibility(VISIBLE);
                //加载数据（不是最新的但是还没看的，即时间在此之前）
                iReflashListener.onLoadData();
                reflashListView();
            }

        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem=firstVisibleItem;
        this.lastDataNum=firstVisibleItem+visibleItemCount;//已经加载的数据=最后一项所在的那页（可见页面）的第一个序号+页面显示的条数
        this.totalItemCount=totalItemCount;
    }

    private int startY;
    private boolean isRemak;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (firstVisibleItem==0){
                    startY= (int) ev.getY();
                    isRemak=true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (state==RELESE){
                    state=REFLASH;
                    //在此加载数据(日期最新)
                    iReflashListener.onReflash();
                    reflashListView();
                }else if (state==PULL){
                    state=NONE;
                    isRemak=false;
                    reflashListView();
                }

                break;
            case MotionEvent.ACTION_MOVE:
                onMove(ev);
                break;
        }

        return super.onTouchEvent(ev);
    }

    private int state;
    final int NONE=0;
    final int PULL=1;
    final int RELESE=2;
    final int REFLASH=3;

    private void onMove(MotionEvent ev){
        if (!isRemak){
            return;
        }
        int moveSpace= (int) (ev.getY()-startY);
        int topPadding= moveSpace-headerHeight;
        switch (state) {
            case NONE:
                if (moveSpace>0){
                    state=PULL;
                    reflashListView();
                }
                break;
            case PULL:
                topPadding(topPadding);
                if (moveSpace>headerHeight+30 && scrollState==SCROLL_STATE_TOUCH_SCROLL){
                    state=RELESE;
                    reflashListView();
                }

                break;
            case RELESE:
                topPadding(topPadding);
                if (moveSpace<=0){
                    state=NONE;
                    isRemak=false;
                    reflashListView();
                }else if (moveSpace<headerHeight+30){
                    state=PULL;
                    reflashListView();
                }
                break;
        }
    }

/*根据当前状态，进行刷新ListView  */
    private void reflashListView(){
        TextView tip= (TextView) findViewById(R.id.tip);
        ImageView arrow= (ImageView) findViewById(R.id.iv_arrow);
        ProgressBar pd= (ProgressBar) findViewById(R.id.progbar);

        RotateAnimation animation=new RotateAnimation(0,180, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(500);
        animation.setFillAfter(true);

        RotateAnimation animation1=new RotateAnimation(180,0, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation1.setDuration(500);
        //动画终止时停留在最后一帧~不然会回到没有执行之前的状态
        animation1.setFillAfter(true);
        switch (state){
            case NONE:
                arrow.clearAnimation();
                topPadding(-headerHeight);
                break;
            case  PULL:
                tip.setText("下拉可以刷新..");

                pd.setVisibility(GONE);
                arrow.setVisibility(VISIBLE);
                arrow.clearAnimation();
                arrow.setAnimation(animation1);
                break;
            case  RELESE:
                tip.setText("松开刷新..");
                pd.setVisibility(GONE);
                arrow.setVisibility(VISIBLE);
                arrow.clearAnimation();
                arrow.setAnimation(animation);
                break;
            case  REFLASH:
                topPadding(50);
                tip.setText("正在刷新..");
                pd.setVisibility(VISIBLE);
                arrow.setVisibility(GONE);
                arrow.clearAnimation();
                break;
        }
    }

    public void reflashComplete(){
        if (state==REFLASH){
            state=NONE;
            isRemak=false;
        }
        isLoading=false;
        footer.setVisibility(GONE);
        TextView lastUpdate= (TextView) findViewById(R.id.lastupdatetime);
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date=new Date(System.currentTimeMillis());
        lastUpdate.setText(format.format(date));

    }
    public void setInterface(IReflashListener iReflashListener){
        this.iReflashListener=iReflashListener;
    }
    public  interface IReflashListener{
        void onReflash();
        void onLoadData();
    }

}
