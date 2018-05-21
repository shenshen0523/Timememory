package com.hfnu.zl.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hfnu.zl.R;
import com.hfnu.zl.database.DaoMaster;
import com.hfnu.zl.database.Diary;
import com.hfnu.zl.database.DiaryDao;
import com.hfnu.zl.tool.Tool;
import com.hfnu.zl.ui.widget.FlowLayout;
import com.hfnu.zl.ui.widget.MySelectView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 添加日记界面和查看日记详情界面的公共界面 继承于AddBaseActivity拥有AddBaseActivity属性和方法 并实现点击监听接口
 */
public class AddDiaryActivity extends AddBaseActivity implements View.OnClickListener {
    private Bundle bundle;
    private DiaryDao diaryDao;
    private MySelectView tv_type_select, tv_mood_select;
    private EditText et_content;
    private FlowLayout fl_record, fl_picture;
    private Button btn_save;
    private int pictureSize = 0,/*照片view的大小 默认为0*/ recordSize = 0;/*语音view的大小 默认为0*/
    private List<String> recordUrl;
    private List<String> pictureUrl;
    private boolean firstload;//是否是第一次加载界面
    private ViewGroup.MarginLayoutParams pictureLp;
    private ViewGroup.MarginLayoutParams recordLp;
    private String recordPath;
    private final static int REQUEST_CODE_PICK_IMAGE = 0;
    private final static int PHOTO_REQUEST_TAKEPHOTO = 1;
    private File tempFile;
    private Diary diary;
    private PopupWindow recordPopupWindow;
    private View recordPopupWindowView;
    private MediaRecorder mRecorder;
    private MediaPlayer mediaPlayer;
    private boolean isTheTape = false;
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_add_diary);
    }

    /**
     * 重写父类的绑定View方法
     */
    @Override
    void bindView() {
        toolbar.setTitle(bundle.getInt("title", R.string.app_name));
        tv_type_select = Tool.findViewById(this, R.id.tv_type_select);
        tv_type_select.setData(Arrays.asList(getResources().getStringArray(R.array.diary_type_array)));
        tv_mood_select = Tool.findViewById(this, R.id.tv_mood_select);
        tv_mood_select.setData(Arrays.asList(getResources().getStringArray(R.array.mood_type_array)));
        tv_mood_select.setLastAloneHandle(false);
        et_content = Tool.findViewById(this, R.id.et_content);
        fl_picture = Tool.findViewById(this, R.id.picture);
        fl_record = Tool.findViewById(this, R.id.record);
        btn_save = Tool.findViewById(this, R.id.btn_save);
        btn_save.setOnClickListener(this);
    }
    /**
     * 重写父类的绑定View之前的方法
     */
    @Override
    void initViewBefore() {
        bundle = getIntent().getExtras();
        mediaPlayer = new MediaPlayer();
        diaryDao = DaoMaster.getDaoSession(this).getDiaryDao();//得到操作日记的数据库操作对象
        pictureUrl = new ArrayList<>();
        recordUrl = new ArrayList<>();
    }
    /**
     * 重写父类的绑定View之后的方法
     */
    @Override
    void initViewLater() {
        diary = (Diary) bundle.getSerializable("data");
        if (diary == null)
            diary = new Diary();
        else {
            isEdit = true;
            tv_type_select.setText(diary.getDiaryType());
            tv_mood_select.setText(diary.getMood());
            et_content.setText(diary.getContent());
            showExamineOrEdit(true);
        }
    }

    /**
     * 用于界面 的控件是显示编辑状态还是查看状态 true为查看状态 false为编辑装状态
     * @param examineOrEdit
     */
    private void showExamineOrEdit(boolean examineOrEdit) {
        if (examineOrEdit) {
            tv_type_select.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            tv_mood_select.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            Drawable rightDrawable = getResources().getDrawable(R.mipmap.ic_down_more);//为MySelectView控件显示更多图标 在右边
            tv_type_select.setCompoundDrawablesWithIntrinsicBounds(null, null, rightDrawable, null);
            tv_mood_select.setCompoundDrawablesWithIntrinsicBounds(null, null, rightDrawable, null);
        }
        tv_type_select.setData(examineOrEdit ? null : Arrays.asList(getResources().getStringArray(R.array.diary_type_array)));
        tv_mood_select.setData(examineOrEdit ? null : Arrays.asList(getResources().getStringArray(R.array.mood_type_array)));
        btn_save.setVisibility(examineOrEdit ? View.GONE : View.VISIBLE);
        et_content.setEnabled(!examineOrEdit);
        et_content.setFocusable(!examineOrEdit);
        et_content.setFocusableInTouchMode(!examineOrEdit);
    }

    /**
     * 将从相册或者拍照得到的图标添加载视图中添加的图片在视图中
     *
     * @param path Uri地址
     */
    private void setPicLayout(String path) {
        setPicLayout(path, true);
    }
    /**
     * 将从相册或者拍照得到的图标添加载视图中添加的图片在视图中
     *
     * @param path Uri地址
     * @param isShowDelete 是否显示删除按钮 当界面为查看日记界面时则隐藏删除按钮
     */
    private void setPicLayout(String path, boolean isShowDelete) {
        pictureUrl.add(path);//将得到的图片url添加到图片集合中
        final View view = LayoutInflater.from(this).inflate(R.layout.image_layout, null);
        final ImageView iv_show = (ImageView) view.findViewById(R.id.iv_show);
        ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
        if (isShowDelete) {
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//点击删除按钮后将该图片的地址从图片地址集合和添加的图片列表中删除中删除
                    pictureUrl.remove(iv_show.getTag().toString());
                    fl_picture.removeView(view);
                }
            });
        } else {
            iv_delete.setVisibility(View.GONE);
        }
        iv_show.setTag(path);//将显示图片的ImageView设置一个标签 内容为图片的地址路径 方便以后操纵该图片时得到其对应的地址路径
        iv_show.setOnClickListener(this);
        try {
            iv_show.setImageDrawable(new BitmapDrawable(ThumbnailUtils.extractThumbnail(MediaStore.Images.Media.
                            getBitmap(getContentResolver(), Uri.parse(path)), Tool.dip2px(AddDiaryActivity.this, pictureSize),
                    Tool.dip2px(AddDiaryActivity.this, pictureSize))));//通过ContentResolver得到图片并且剪切成pictureSize的大小显示在ImageView上
        } catch (IOException e) {
            e.printStackTrace();
        }
        fl_picture.addView(view, pictureLp);//将显示图片后的view添加在图片列表中
    }

    /**
     * 设置添加的录音放在视图中
     *
     * @param path Uri地址
     */
    private void setRecordLayout(String path) {
        setRecordLayout(path, true);
    }
    /**
     * 设置添加的录音放在视图中
     *具体代码意义可参考setPicLayout方法 这里不在赘述
     * @param path Uri地址
     * @param isShowDelete 是否显示删除按钮
     */
    private void setRecordLayout(String path, boolean isShowDelete) {
        recordUrl.add(path);
        final View view = AddDiaryActivity.this.getLayoutInflater().inflate(R.layout.image_layout, null);
        final ImageView iv_show = (ImageView) view.findViewById(R.id.iv_show);
        ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
        if (isShowDelete) {
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recordUrl.remove(iv_show.getTag().toString());
                    fl_record.removeView(view);
                }
            });
        } else
            iv_delete.setVisibility(View.GONE);
        iv_show.setTag(path);
        iv_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRecord(v.getTag().toString());
            }
        });
        iv_show.setImageResource(R.mipmap.voice);
        fl_record.addView(view, recordLp);
    }

    /**
     * 启动相册或相机获取照片后的回调方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && data != null) {
            setPicLayout(data.getData().toString());
        } else if (requestCode == PHOTO_REQUEST_TAKEPHOTO) {//如果是通过相机拍照获取图片的话就通过之前给的路径得到Bitmap
            Bitmap bm = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
            if (bm == null) {
                return;
            }
            setPicLayout(MediaStore.Images.Media.insertImage(getContentResolver(), bm, "", ""));//通知系统媒体库添加该照片
            bm.recycle();//释放Bitmap
        }
    }

    /**
     * 窗口得到焦点改变接听方法
     * 界面加载完成后调用
     * 界面看不见后调用
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !firstload) {//如果是得到焦点并且是启动当前activity后第一加载此界面就执行下面的方法
            firstload = true;
            if (pictureSize == 0) {//得到单个图片的大小
                pictureSize = (fl_picture.getWidth() - Tool.dip2px(this, 12)) / 3;
                pictureLp = new ViewGroup.MarginLayoutParams(pictureSize, pictureSize);
            }
            if (recordSize == 0) {//得到单个录音图片的大小
                recordSize = (fl_record.getWidth() - Tool.dip2px(this, 12)) / 6;
                recordLp = new ViewGroup.MarginLayoutParams(recordSize, recordSize);
            }
            if (isEdit) {//是否是编辑状态
       /*         fl_picture.getChildAt(0).setVisibility(View.GONE);
                fl_record.getChildAt(0).setVisibility(View.GONE);*/
                if (diary.getPictureList().isEmpty()) {
                    fl_picture.setVisibility(View.GONE);
                } else {
                    for (String path : diary.getPictureList()) {
                        Log.i("getPictureList", path);
                        setPicLayout(path, false);
                    }
                }
                if (diary.getRecordList().isEmpty()) {
                    fl_record.setVisibility(View.GONE);
                } else {
                    for (String path : diary.getRecordList()) {
                        setRecordLayout(path, false);
                    }
                }
            } else {
                showAddPicAndRecordView();
            }
        }
    }

    /**
     * 显示添加图片控件和添加录音控件
     */
    private void showAddPicAndRecordView() {
        View addPicView = getLayoutInflater().inflate(R.layout.add_pic_view, null);
        addPicView.setOnClickListener(this);
        addPicView.setLayoutParams(pictureLp);
        fl_picture.addView(addPicView);

        View addRecordView = getLayoutInflater().inflate(R.layout.add_record_view, null);
        addRecordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecordPopupWindow();
            }
        });
        addRecordView.setLayoutParams(recordLp);
        fl_record.addView(addRecordView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_show:
                //点击图片列表的图片后 通过getTag（）方法得到图片的路径并调用系统相册进行展示
                Intent intentImage = new Intent(Intent.ACTION_VIEW);
                intentImage.addCategory(Intent.CATEGORY_DEFAULT);
                intentImage.setDataAndType(Uri.parse(v.getTag().toString()), "image/*");
                startActivity(intentImage);
                break;
            case R.id.add_pic_view:
                showAddPicDialog();//点击添加图片时显示一个对话框
                break;
            case R.id.btn_save:
                if (TextUtils.isEmpty(et_content.getText().toString().trim())) {
                    Toast.makeText(this, "日记内容不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    saveDiaryDialog();
                }
                break;
        }
    }

    /**
     * 检查文件存储路径的上层文件夹是否存在
     */
    private void checkTheFolder() {
        if (recordPath == null) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ".mn");
            if (!file.exists()) {
                file.mkdir();
            }
            recordPath = file.getAbsolutePath();
        }
    }

    /**
     * 点击添加图片时显示的对话框
     */
    private void showAddPicDialog() {
        AlertDialog.Builder addPicDialog = new AlertDialog.Builder(AddDiaryActivity.this);
        addPicDialog.setItems(new String[]{"相册", "拍照"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//添加其item
                switch (which) {
                    case 0:
                        Intent intentPick = new Intent(Intent.ACTION_PICK, null);//选择相册
                        intentPick.setType("image/*");
                        startActivityForResult(intentPick, REQUEST_CODE_PICK_IMAGE);
                        break;
                    case 1://选择拍照
                        checkTheFolder();
                        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // 指定调用相机拍照后照片的储存路径
                        tempFile = new File(recordPath, "/" + System.currentTimeMillis() + ".jpg");
                        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                        startActivityForResult(intentCamera, PHOTO_REQUEST_TAKEPHOTO);
                        break;
                }
            }
        });
        addPicDialog.create().show();
    }
    /**
     *保存日记信息的对话框
     */
    private void saveDiaryDialog() {
        AlertDialog.Builder passDialog = new AlertDialog.Builder(AddDiaryActivity.this);
        passDialog.setTitle(R.string.str_is_set_password);//保存前询问用户是否加密
        View passwordView = getLayoutInflater().inflate(R.layout.manage_password_view, null);
        passwordView.setPadding(Tool.dip2px(AddDiaryActivity.this, 8), Tool.dip2px(AddDiaryActivity.this, 16), Tool.dip2px(AddDiaryActivity.this, 8), 0);
        final EditText passWordEd = Tool.findViewById(passwordView, R.id.et_user_password);
        passDialog.setView(passwordView);
        passDialog.setPositiveButton(R.string.str_ok, null);//设置确认按钮，点击事件设置为空这样对话框就不会关闭
        passDialog.setNeutralButton("不设置密码", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveDiary();
                dialog.cancel();
                dialog.dismiss();
            }
        });
        passDialog.setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {//设置不设置密码按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = passDialog.create();//得到对话框实例
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {//设置确认按钮点击事件
            @Override
            public void onClick(View v) {
                String inputPassWord = passWordEd.getText().toString().trim();
                if (TextUtils.isEmpty(inputPassWord)) {
                    Toast.makeText(AddDiaryActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    diary.setPassWord(inputPassWord);
                    saveDiary();
                    alertDialog.cancel();
                    alertDialog.dismiss();
                }
            }
        });
    }

    /**
     * 保存日记信息
     */
    private void saveDiary() {
        diary.setDiaryType(tv_type_select.getText().toString().trim());
        diary.setMood(tv_mood_select.getText().toString().trim());
        diary.setContent(et_content.getText().toString().trim());
        diary.setWriteTime(new Date());
        diary.setRecord("");
        diary.setPicture("");
        diary.setPictureList(pictureUrl);
        diary.setRecordList(recordUrl);
        if (isEdit) {//如果是编辑状态就更新该日记信息
            diaryDao.update(diary);
        } else//如果是添加状态就保存该日记信息
            diaryDao.insert(diary);
        setResult(MainActivity.HAVE_A_VALUE);//设置返回响应值为HAVE_A_VALUE，这样返回mainactivity后，mainactivity就会判断如果响应值为HAVE_A_VALUE，就更新界面展示刚刚添加的日记
        onBackPressed();
    }

    /**
     * 展示录音PopupWindow
     */
    private void showRecordPopupWindow() {
        checkTheFolder();
        final String currentPath = recordPath + "/" + System.currentTimeMillis() + ".mn";//以当前的时间戳为录音文件名称，以mn为录音文件的后缀名
        recordPopupWindowView = getLayoutInflater().inflate(R.layout.record_popup_view, null);
        final TextView tv_record_state = Tool.findViewById(recordPopupWindowView, R.id.tv_record_state);
        final ImageView iv_record_state = Tool.findViewById(recordPopupWindowView, R.id.iv_record_state);
        iv_record_state.setTag(R.mipmap.record_start);
        final TextView tv_cancel = Tool.findViewById(recordPopupWindowView, R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordPopupWindow.dismiss();
            }
        });
        final TextView tv_ok = Tool.findViewById(recordPopupWindowView, R.id.tv_ok);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((int) iv_record_state.getTag() == R.mipmap.record_start) {
                    Toast.makeText(AddDiaryActivity.this, R.string.str_record_not_started, Toast.LENGTH_SHORT).show();
                } else {
                    recordPopupWindow.dismiss();
                    setRecordLayout(currentPath);
                }
            }
        });
        mRecorder = new MediaRecorder();//实例化媒体录音对象
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置来源为麦克风
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//设置文件格式
        mRecorder.setOutputFile(currentPath);//设置文件路径
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//设置编码
        iv_record_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch ((int) v.getTag()) {//如果是开始录音状态就开始录音并且将控件状态切换为正在录音状态
                    case R.mipmap.record_start:
                        try {
                            mRecorder.prepare();
                            mRecorder.start();
                        } catch (IOException e) {
                        }
                        isTheTape = true;
                        iv_record_state.setImageResource(R.mipmap.record_stop);
                        iv_record_state.setTag(R.mipmap.record_stop);
                        tv_record_state.setText(R.string.str_being_record);
                        break;
                    case R.mipmap.record_stop://如果是正在录音状态就结束录音并且将控件状态切换为点击播放录音
                        mRecorder.stop();
                        mRecorder.release();
                        mRecorder = null;
                        isTheTape = false;
                        iv_record_state.setImageResource(R.mipmap.record_play);
                        iv_record_state.setTag(R.mipmap.record_play);
                        tv_record_state.setText(R.string.str_play_record);
                        break;
                    case R.mipmap.record_play:
                        playRecord(currentPath);//调用播放录音方法
                        break;
                }
            }
        });
        recordPopupWindow = new PopupWindow(recordPopupWindowView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);//实例化PopupWindow对象
        recordPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {//设置PopupWindod关闭监听
                setWindowAlpha(1f);
                if (isTheTape) {
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;
                }
                isTheTape = false;
            }
        });
        recordPopupWindow.setFocusable(true);
        recordPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.windowBackground));
        recordPopupWindow.showAtLocation(getRootView(), Gravity.BOTTOM, 0, 0);
        setWindowAlpha(0.5f);
    }

    /**
     * 播放录音
     * @param path 录音文件路径
     */
    private void playRecord(String path) {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(path);//设置文件路径
            mediaPlayer.prepare();//准备播放
            mediaPlayer.start();//开始播放
        } catch (IOException e) {//可能会有文件被删除而导致找不到抛出IO异常
           // e.printStackTrace();
            Toast.makeText(this,"文件没有找到",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设置窗口透明度
     *
     * @param alpha
     */
    private void setWindowAlpha(float alpha) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = alpha;
        getWindow().setAttributes(params);
    }
    //创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_record_menu, menu);
        if (!isEdit) {//如果是查看状态就显示编辑按钮
            menu.findItem(R.id.edit).setVisible(false);
        }
        return true;
    }

    /**
     *菜单点击选择事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                getSupportActionBar().setTitle(R.string.str_edit_diary);
                showExamineOrEdit(false);
                fl_picture.setVisibility(View.VISIBLE);
                fl_picture.removeAllViews();
                fl_record.setVisibility(View.VISIBLE);
                fl_record.removeAllViews();
                showAddPicAndRecordView();
                if (pictureUrl.size() > 0) {
                    //fl_picture.removeViews(1, pictureUrl.size() - 1);
                    //fl_picture.removeAllViews();
                    pictureUrl.clear();
                    for (String path : diary.getPictureList()) {
                        setPicLayout(path);
                    }
                }
                if (recordUrl.size() > 0) {
                    //fl_record.removeViews(1, recordUrl.size() - 1);
                    //fl_record.removeAllViews();
                    recordUrl.clear();
                    for (String path : diary.getRecordList()) {
                        setRecordLayout(path);
                    }
                }
                item.setVisible(false);
                break;
        }
        return true;
    }
}
