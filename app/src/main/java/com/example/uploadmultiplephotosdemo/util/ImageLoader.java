package com.example.uploadmultiplephotosdemo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 图片加载类
 */
public class ImageLoader {

    private static ImageLoader imageLoader;

    /**
     * /图片加载核心对象
     */
    private LruCache<String, Bitmap> lruCache;


    /*
    线程池
     */
    private ExecutorService executorService;

    private static final  int DEFULT_THREAD_COUNT = 1;

    /**
     * 队列的调度方式
     */

    private static Type type = Type.lIFO;
    /**
     * 任务队列
     */

    private LinkedList<Runnable> mTaskQueue;
    /**
     * 后台轮询线程
     */

    private Thread thread;
    private android.os.Handler mPoolThreadHandler;

    /**
     * UI线程handler
     */

    private Handler uiHandler;

    private Semaphore semaphore = new Semaphore(0);//信号量a'd'd
    public enum Type{
        FIFI,lIFO;
    }

    public ImageLoader(int mThreadCount,Type type){

        init(mThreadCount,type);
    }

    public static ImageLoader getInstance(){
        if(imageLoader == null){
            synchronized (ImageLoader.class){
                if(imageLoader == null){
                    imageLoader = new ImageLoader(DEFULT_THREAD_COUNT,type);
                }
            }
        }

        return imageLoader;
    }
    private void init(int threadcount, Type type){
        thread = new Thread(){
            @Override
            public void run() {
                Looper.prepare();

                mPoolThreadHandler = new android.os.Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);

                        //线程池去取出一个任务执行

                        executorService.execute(getTasks());
                    }
                };

                //释放一个信号量
                semaphore.release();
                Looper.loop();
            }
        };

        thread.start();

        //最大内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1) {
            lruCache = new LruCache<String,Bitmap>(cacheMemory){
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes() * value.getHeight();
                }
            };
        }

        //创建线程池
        executorService = Executors.newFixedThreadPool(threadcount);
        mTaskQueue = new LinkedList<Runnable>();
        this.type = type;
    }


    /**
     * 根据imageview设置图片
     * @param path
     * @param imageView
     */
    public void loadImage(final String path, final ImageView imageView) throws InterruptedException {
        imageView.setTag(path);

        if(uiHandler == null){
            uiHandler = new android.os.Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    //获取得到图片，为imageview回调设置图片

                    ImageBeanHolder imageBeanHolder = (ImageBeanHolder) msg.obj;
                    Bitmap bm = imageBeanHolder.bitmap;
                    ImageView imageView1 = imageBeanHolder.imageView;
                    String path = imageBeanHolder.path;

                    if(imageView1.getTag().equals(path)){
                        imageView1.setImageBitmap(bm);
                    }

                }
            };
        }else{
            addTasks(new Runnable(){
                @Override
                public void run() {


                    //加载图片,图片压缩
                    //1.获得图片大小
                    getImageViewSize(imageView);
                    ImageSize imageSize = new ImageSize();
                    //2.压缩图片
                    Bitmap bm = decodeSampleBitmapFromPath(path,imageSize.width,imageSize.height);
                    //3.把图片加入缓存
                    addBitmapToLruCache(path,bm);


                    refreshBitmap(bm, imageView, path);

                }
            });
        }

        Bitmap bm = getBitmapFromLruCache(path);

        if(bm != null){
            refreshBitmap(bm, imageView, path);
        }
    }

    public void refreshBitmap(Bitmap bm, ImageView imageView, String path) {
        Message message = Message.obtain();
        ImageBeanHolder holder = new ImageBeanHolder();
        holder.bitmap = bm;
        holder.imageView = imageView;
        holder.path = path;
        message.obj = holder;
        uiHandler.sendMessage(message);
    }

    /**
     * 将图片加入Lrucache
     * @param path
     * @param bm
     */
    private void addBitmapToLruCache(String path, Bitmap bm) {
        if(getBitmapFromLruCache(path) == null){
            if(bm!= null){
                    lruCache.put(path,bm);
            }
        }
    }

    /**
     * 根据长宽高压缩图片
     * @param path
     * @param width
     * @param height
     * @return
     */
    private Bitmap decodeSampleBitmapFromPath(String path, int width, int height) {
        //获取图片的宽高，并不把图片加载在内存中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);

        options.inSampleSize = caculateInSampleSize(options,width,height);

        //使用获取到的insamplesize再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path,options);
        return bitmap;
    }


    /***
     * 根据需求的宽和高以及图片实际的宽和高计算samplesize
     * @param options
     * @param
     * @param
     * @return
     */
    private int caculateInSampleSize(BitmapFactory.Options options, int rewidth, int reheight) {
        int width = options.outWidth;
        int height = options.outHeight;

        int inSampleSize = 1;

        if(width > rewidth || height > reheight){

            int widthRadio = Math.round(width*1.0f/rewidth);
            int heightRadio = Math.round(height*1.0f/reheight);

            inSampleSize = Math.max(widthRadio,heightRadio);
        }

        return inSampleSize;
    }

    /**
     * 获取图片宽和高
     * @param imageView
     * @return
     */
    private ImageSize getImageViewSize(ImageView imageView) {
        ImageSize imageSize = new ImageSize();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        int width = imageView.getWidth();
        int height = imageView.getHeight();

        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();

        if(width <= 0){
            width = layoutParams.width;
        }
        if(width <= 0){
            width = getImageViewFieldValue(imageView,"mMaxWidth"); //检查最大值
        }
        if(width <= 0){
            width = displayMetrics.widthPixels;
        }


        if(height <= 0){
            height = layoutParams.height;
        }
        if(height <= 0){
            height = getImageViewFieldValue(imageView,"mMaxHeight");
        }
        if(height <= 0){
            height = displayMetrics.heightPixels;
        }
        imageSize.height = height;
        imageSize.width = width;
        return imageSize;
    }

    /**
     * 通过反射获取imageview某个值属性
     * @param object
     * @param fieldName
     * @return
     */

    private static int getImageViewFieldValue(Object object,String fieldName)  {

        int value = 0;


        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if(fieldValue > 0&& fieldValue <Integer.MAX_VALUE){
                value = fieldValue;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return value;
    }

    private Runnable getTasks() {
        if(type == Type.FIFI){
            return mTaskQueue.removeFirst();
        }else if(type == Type.lIFO){
            return null;
        }

        return null;
    }

    private synchronized void addTasks(Runnable runnable) throws InterruptedException {
        mTaskQueue.add(runnable);

        try {
            semaphore.acquire();
        }catch (InterruptedException e){

        }
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    private Bitmap getBitmapFromLruCache(String path) {
            return lruCache.get(path);
    }

    private class ImageBeanHolder{
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }


    private class ImageSize{
        int width;
        int height;
    }
}
