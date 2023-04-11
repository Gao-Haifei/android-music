package com.example.music;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.hrb.library.MiniMusicView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String TAG = "Music";
    ArrayList<String> html, name;
    ListView listView, listView_2, listView_3;
    EditText ed_sou;
    Button btn_sou;
    MyList myList;
    TextView up, down, tt, fanhui;
    TabLayout mytab;
    int ye = 1;
    MyList_3 myList_3;
    ArrayList<String> html_sou, name_sou;

    List<Map<String, Object>> data;
    HashMap<String, Object> map;
    int ii = 0;
    MiniMusicView mini;
    LinearLayout one, two;
    SQLiteDatabase db;
    MySQLite mySQLite;

    LinearLayout lin_be, lin_ge;

    public void get_data() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = db.query("table__", new String[]{"name", "singer", "path"}, null, null, null, null, null);
                data = new ArrayList<>();



                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    map = new HashMap<>();
                    int name = cursor.getColumnIndex("name");
                    int singer = cursor.getColumnIndex("singer");
                    int path = cursor.getColumnIndex("path");

                    String nn = cursor.getString(name);
                    String ss = cursor.getString(singer);
                    String pp = cursor.getString(path);

                    map.put("name", nn);
                    map.put("singer", ss);
                    map.put("path", pp);
                    data.add(map);

                }
                if (myList_3 == null) {
                    myList_3 = new MyList_3(MainActivity.this, data);
                    listView_3.setAdapter(myList_3);
                } else {
                    myList_3.UP_data(data);
                }
            }
        });
    }

    public class MyList_3 extends BaseAdapter {
        List<Map<String, Object>> data;
        Context context;
        LayoutInflater layoutInflater;

        public MyList_3(Context context, List<Map<String, Object>> data) {
            this.context = context;
            this.data = data;
            layoutInflater = LayoutInflater.from(context);
        }

        public void UP_data(List<Map<String, Object>> data) {

            this.data = data;
            this.notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_item, null);
            }

            TextView start = convertView.findViewById(R.id.start);
            TextView name = convertView.findViewById(R.id.text_2);
            TextView text = convertView.findViewById(R.id.text);
            TextView sc = convertView.findViewById(R.id.sc);

            LinearLayout lin_ge_3 = convertView.findViewById(R.id.lin_ge_3);
            LinearLayout lin_be_3 = convertView.findViewById(R.id.lin_be_3);
            lin_ge_3.setVisibility(View.VISIBLE);
            lin_be_3.setVisibility(View.GONE);

            String path, music_name, singer_name;

            singer_name = data.get(position).get("name").toString();
            music_name = data.get(position).get("singer").toString();
            path = data.get(position).get("path").toString();

            text.setText(singer_name);
            if (singer_name.length() < 1) {
                text.setText("未知歌手");
            }
            name.setText(music_name);


            sc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.delete("table__", "name = ?", new String[]{singer_name});
                    data.remove(position);
                    notifyDataSetChanged();
                }
            });


            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mini.setTitleText(music_name);
                    mini.setAuthor(singer_name);
                    mini.startPlayMusic(path);
                    ii = position;

                    mini.setOnNextBtnClickListener(new MiniMusicView.OnNextButtonClickListener() {
                        @Override
                        public void OnClick() {
                            String path, singer_name, music_name;

                            ii++;
                            if (ii <= getCount()) {
                                music_name = data.get(ii).get("name").toString();
                                singer_name = data.get(ii).get("singer").toString();
                                path = data.get(ii).get("path").toString();
                            } else {
                                music_name = data.get(position).get("name").toString();
                                singer_name = data.get(position).get("singer").toString();
                                path = data.get(position).get("path").toString();
                            }
                            mini.setTitleText(music_name);
                            mini.setAuthor(singer_name);
                            mini.startPlayMusic(path);


                        }
                    });

                    mini.setOnMusicStateListener(new MiniMusicView.OnMusicStateListener() {
                        @Override
                        public void onPrepared(int duration) {
                            Log.i(TAG, "start prepare play music");
                        }

                        @Override
                        public void onError(int what, int extra) {
                            Log.i(TAG, "start play music error");
                        }

                        @Override
                        public void onInfo(int what, int extra) {
                            Log.i(TAG, "start play music is stop");
                        }

                        @Override
                        public void onMusicPlayComplete() {
                            Log.i(TAG, "onMusicPlayComplete: 播放完毕");
                            mini.startPlayMusic(html.get(ii++));
                        }

                        //拉进度条，进度条位置加载完成
                        @Override
                        public void onSeekComplete() {
                            Log.i(TAG, "onSeekComplete: 加载完成");
                        }

                        @Override
                        public void onProgressUpdate(int duration, int currentPos) {
                            Log.i(TAG, "onProgressUpdate: " + duration + "~" + currentPos);
                        }

                        @Override
                        public void onHeadsetPullOut() {
                            mini.pausePlayMusic();
                        }
                    });
                }
            });

            return convertView;
        }
    }

    public void SQL() {
        mySQLite = new MySQLite(MainActivity.this, "music.db", null, 1);
        db = mySQLite.getWritableDatabase();
    }

    public class MySQLite extends SQLiteOpenHelper {

        public MySQLite(Context context, String name, Object o, int version) {
            super(context, name, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String sql = "create table table__(name char(50) PRIMARY KEY,singer char(50),path char(50))";
            sqLiteDatabase.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            Toast.makeText(MainActivity.this, "!!!", Toast.LENGTH_SHORT).show();
        }
    }


    public void mytab() {
        mytab.addTab(mytab.newTab().setText("歌曲"));
        mytab.addTab(mytab.newTab().setText("本地"));

        mytab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() == "歌曲") {
                    Toast.makeText(MainActivity.this, "当前选择歌曲", Toast.LENGTH_SHORT).show();

                    lin_ge.setVisibility(View.VISIBLE);
                    lin_be.setVisibility(View.GONE);
//                    tab.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
//                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);//直接用setTextSize(22)也一样
//                    tv.setAlpha(0.9f);//透明度
//                    tv.invalidate();
                } else {
                    Toast.makeText(MainActivity.this, "当前选择本地", Toast.LENGTH_SHORT).show();
                    lin_be.setVisibility(View.VISIBLE);
                    get_data();
                    lin_ge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        html = new ArrayList<>();
        name = new ArrayList<>();

        mini = findViewById(R.id.mini_music);
        mini.setTitleText("");
        mini.setAuthor("");

        listView_3 = findViewById(R.id.list_view_3);
        lin_be = findViewById(R.id.lin_be);
        lin_ge = findViewById(R.id.lin_ge);
        mytab = findViewById(R.id.mytab);
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        listView_2 = findViewById(R.id.list_view_2);
        fanhui = findViewById(R.id.fanhui);
        ed_sou = findViewById(R.id.ed_sou);
        btn_sou = findViewById(R.id.btn_sou);
        up = findViewById(R.id.up);
        down = findViewById(R.id.down);
        tt = findViewById(R.id.tt);
        listView = findViewById(R.id.list_view);
        data(ye);

        mytab();

        SQL();
        btn_sou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = ed_sou.getText().toString();
                if (s.equals("")) {
                    Toast.makeText(MainActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                } else {

                    Sou_data(ed_sou.getText().toString());
                    fanhui.setVisibility(View.VISIBLE);
                    one.setVisibility(View.GONE);
                    two.setVisibility(View.VISIBLE);

                    fanhui.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            two.setVisibility(View.GONE);
                            one.setVisibility(View.VISIBLE);
                            fanhui.setVisibility(View.GONE);
                            ed_sou.setText("");
                        }
                    });
                }

            }
        });


        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ye == 1) {
                    Toast.makeText(MainActivity.this, "已经是第一页", Toast.LENGTH_SHORT).show();
                } else {
                    data(--ye);
                }

            }
        });
        down.setOnClickListener(view -> {
            if (ye == 917) {
                Toast.makeText(MainActivity.this, "已经是最后一页", Toast.LENGTH_SHORT).show();
            } else {
                data(++ye);
            }
        });
    }


    public class MyList_2 extends BaseAdapter {
        ArrayList<String> name_sou, html_sou;
        Context context;
        LayoutInflater layoutInflater;

        public MyList_2(Context context, ArrayList<String> name_sou, ArrayList<String> html_sou) {
            this.context = context;
            this.name_sou = name_sou;
            this.html_sou = html_sou;
            layoutInflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return name_sou.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_item, null);
            }
            TextView start = convertView.findViewById(R.id.start);
            TextView name = convertView.findViewById(R.id.text_2);
            TextView text = convertView.findViewById(R.id.text);
            TextView tj = convertView.findViewById(R.id.tj);
            LinearLayout lin_ge_3 = convertView.findViewById(R.id.lin_ge_3);
            LinearLayout lin_be_3 = convertView.findViewById(R.id.lin_be_3);
            lin_ge_3.setVisibility(View.GONE);
            lin_be_3.setVisibility(View.VISIBLE);

            String path, singer_name, music_name;

            if (name_sou.get(position).contains("_")) {
                path = html_sou.get(position);
                singer_name = name_sou.get(position).split("_")[1];
                music_name = name_sou.get(position).split("_")[0];
            } else if (name_sou.get(position).contains(" ")) {
                path = html_sou.get(position);
                singer_name = name_sou.get(position).split(" ")[1];
                music_name = name_sou.get(position).split(" ")[0];
            } else {
                path = html_sou.get(position);
                singer_name = "未知歌手";
                music_name = name_sou.get(position).split(" ")[0];
            }

            text.setText(music_name);
            if (singer_name.length() < 1) {
                text.setText("未知歌手");
            }
            name.setText(singer_name);


            tj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues values = new ContentValues();
                    values.put("name", music_name);
                    values.put("singer", singer_name);
                    values.put("path", path);
                    db.insertWithOnConflict("table__",null,values,SQLiteDatabase.CONFLICT_REPLACE);
                    Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
                }
            });


            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mini.setTitleText(music_name);
                    mini.setAuthor(singer_name);
                    mini.startPlayMusic(path);
                    ii = position;

                    mini.setOnNextBtnClickListener(new MiniMusicView.OnNextButtonClickListener() {
                        @Override
                        public void OnClick() {
                            String path, singer_name, music_name;

                            ii++;

                            if (name_sou.get(ii).contains("_")) {
                                path = html.get(ii);
                                singer_name = name_sou.get(ii).split("_")[1];
                                music_name = name_sou.get(ii).split("_")[0];

                            } else if (name_sou.get(ii).contains(" ")) {
                                path = html.get(ii);
                                singer_name = name_sou.get(ii).split(" ")[1];
                                music_name = name_sou.get(ii).split(" ")[0];
                            } else {
                                path = html.get(ii);
                                singer_name = "未知歌手";
                                music_name = name_sou.get(ii).split(" ")[0];
                            }


                            mini.setTitleText(music_name);
                            mini.setAuthor(singer_name);
                            mini.startPlayMusic(path);


                        }
                    });

                    mini.setOnMusicStateListener(new MiniMusicView.OnMusicStateListener() {
                        @Override
                        public void onPrepared(int duration) {
                            Log.i(TAG, "start prepare play music");
                        }

                        @Override
                        public void onError(int what, int extra) {
                            Log.i(TAG, "start play music error");
                        }

                        @Override
                        public void onInfo(int what, int extra) {
                            Log.i(TAG, "start play music is stop");
                        }

                        @Override
                        public void onMusicPlayComplete() {
                            Log.i(TAG, "onMusicPlayComplete: 播放完毕");
                            mini.startPlayMusic(html.get(ii++));
                        }

                        //拉进度条，进度条位置加载完成
                        @Override
                        public void onSeekComplete() {
                            Log.i(TAG, "onSeekComplete: 加载完成");
                        }

                        @Override
                        public void onProgressUpdate(int duration, int currentPos) {
                            Log.i(TAG, "onProgressUpdate: " + duration + "~" + currentPos);
                        }

                        @Override
                        public void onHeadsetPullOut() {
                            mini.pausePlayMusic();
                        }
                    });
                }
            });

            return convertView;
        }
    }


    public void Sou_data(String ss) {
        String uri = "https://www.ytmp3.cn/?search=" + ss;

        html_sou = new ArrayList<>();
        name_sou = new ArrayList<>();
        Document document;
        try {
            document = Jsoup.connect(uri).get();
            Elements elements = document.getElementsByClass("ser_3");
            if (elements.size() != 0) {
                Elements all = elements.get(0).getElementsByClass("ser_8");
                int size = all.size();
                for (int i = 0; i < size; i++) {
                    Element e = all.get(i);
                    Elements hh = e.getElementsByTag("a");
                    for (int j = 0; j < hh.size(); j++) {
                        Element element = hh.get(j);
                        String ht = element.attr("href");
                        String data = ht.split("/")[1].replace("html", "mp3");
                        html_sou.add("https://www.ytmp3.cn/down/" + data);
                    }
                    String dd = e.text();
                    name_sou.add(dd);
                }
                MyList_2 myList_2 = new MyList_2(MainActivity.this, name_sou, html_sou);
                listView_2.setAdapter(myList_2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    public void data(int ii) {

        String uri = "https://www.ytmp3.cn/dj/" + ii + ".html";

        tt.setText(ii + "/917");
        Document document;
        try {
            document = Jsoup.connect(uri).get();
            Elements elements = document.getElementsByClass("nwedj_3");// 选择类标签
            if (elements.size() != 0) {
                Elements all = elements.get(0).getElementsByClass("nwedj_8");
                int size = all.size();
                for (int i = 0; i < size; i++) {
                    Element e = all.get(i);

                    Elements hh = e.getElementsByTag("a");
                    for (int j = 0; j < hh.size(); j++) {
                        Element element = hh.get(j);
                        String ht = element.attr("href");
                        String data = ht.split("/")[1].replace("html", "mp3");
                        html.add("https://www.ytmp3.cn/down/" + data);
                    }
                    String dd = e.text();
                    name.add(dd);

                }
            }
            if (myList == null) {
                myList = new MyList(MainActivity.this, name, html);
                listView.setAdapter(myList);
            } else {
                myList.clear();
                myList.Up_data(name, html);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        mini.stopPlayMusic();
        super.onDestroy();
    }

    public class MyList extends BaseAdapter {
        ArrayList<String> name, html;
        Context context;
        LayoutInflater layoutInflater;

        public MyList(Context context, ArrayList<String> name, ArrayList<String> html) {
            this.context = context;
            this.name = name;
            this.html = html;
            layoutInflater = LayoutInflater.from(context);
        }

        public void clear() {
            ArrayList<String> a = new ArrayList<>();
            a.add("");
            this.name = a;
            this.html = a;
        }

        public void Up_data(ArrayList<String> name, ArrayList<String> html) {
            this.name = name;
            this.html = html;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return name.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (position >= name.size() - 1) {
                data(++ye);
            }

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_item, null);
            }
            TextView start = convertView.findViewById(R.id.start);
            TextView text = convertView.findViewById(R.id.text);
            TextView text_2 = convertView.findViewById(R.id.text_2);
            TextView tj = convertView.findViewById(R.id.tj);
            LinearLayout lin_ge_3 = convertView.findViewById(R.id.lin_ge_3);
            LinearLayout lin_be_3 = convertView.findViewById(R.id.lin_be_3);
            lin_ge_3.setVisibility(View.GONE);
            lin_be_3.setVisibility(View.VISIBLE);

//            if (position == name.size()) {
//                text.setText("Loading");
//                text.setTextSize(24);
//                text.setBackgroundColor(Color.RED);
//                data(++ye);
//            }

            ii = 0;
            String path, singer_name, music_name;

            if (name.get(position).contains("_")) {
                path = html.get(position);
                singer_name = name.get(position).split("_")[1];
                music_name = name.get(position).split("_")[0];
            } else if (name.get(position).contains(" ")) {
                path = html.get(position);
                singer_name = name.get(position).split(" ")[1];
                music_name = name.get(position).split(" ")[0];
            } else {
                path = html.get(position);
                singer_name = "未知歌手";
                music_name = name.get(position).split(" ")[0];
            }


            text.setText(music_name);
            if (singer_name.length() < 1) {
                text_2.setText("未知歌手");
            } else {
                text_2.setText(singer_name);
            }


            tj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues values = new ContentValues();
                    values.put("name", music_name);
                    values.put("singer", singer_name);
                    values.put("path", path);
                    db.insertWithOnConflict("table__",null,values,SQLiteDatabase.CONFLICT_REPLACE);
                    Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
                }
            });

            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    mini.setTitleText(music_name);
                    mini.setAuthor(singer_name);
                    mini.startPlayMusic(path);
                    ii = position;

                    mini.setOnNextBtnClickListener(new MiniMusicView.OnNextButtonClickListener() {
                        @Override
                        public void OnClick() {
                            String path, singer_name, music_name;

                            ii++;

                            if (ii < getCount()) {
                                if (name.get(ii).contains("_")) {
                                    path = html.get(ii);
                                    singer_name = name.get(ii).split("_")[1];
                                    music_name = name.get(ii).split("_")[0];

                                } else {
                                    path = html.get(ii);
                                    singer_name = name.get(ii).split(" ")[1];
                                    music_name = name.get(ii).split(" ")[0];
                                }

                            } else {
                                data(++ye);
                                Up_data(name, html);

                                if (name.get(0).contains("_")) {
                                    path = html.get(0);
                                    singer_name = name.get(0).split("_")[1];
                                    music_name = name.get(0).split("_")[0];

                                } else {
                                    path = html.get(0);
                                    singer_name = name.get(0).split(" ")[1];
                                    music_name = name.get(0).split(" ")[0];
                                }


                            }
                            mini.setTitleText(music_name);
                            mini.setAuthor(singer_name);
                            mini.startPlayMusic(path);


                        }
                    });

                    mini.setOnMusicStateListener(new MiniMusicView.OnMusicStateListener() {
                        @Override
                        public void onPrepared(int duration) {
                            Log.i(TAG, "start prepare play music");
                        }

                        @Override
                        public void onError(int what, int extra) {
                            Log.i(TAG, "start play music error");
                        }

                        @Override
                        public void onInfo(int what, int extra) {
                            Log.i(TAG, "start play music is stop");
                        }

                        @Override
                        public void onMusicPlayComplete() {
                            Log.i(TAG, "onMusicPlayComplete: 播放完毕");
                            mini.startPlayMusic(html.get(ii++));
                        }

                        //拉进度条，进度条位置加载完成
                        @Override
                        public void onSeekComplete() {
                            Log.i(TAG, "onSeekComplete: 加载完成");
                        }

                        @Override
                        public void onProgressUpdate(int duration, int currentPos) {
                            Log.i(TAG, "onProgressUpdate: " + duration + "~" + currentPos);
                        }

                        @Override
                        public void onHeadsetPullOut() {
                            mini.pausePlayMusic();
                        }
                    });

//                    if (!a) {
//                        String aa = html.get(position);
//                        mediaPlayer = MediaPlayer.create(MainActivity.this, Uri.parse(aa));
//                        mediaPlayer.start();
//                        a = true;
//                    } else {
//                        mediaPlayer.stop();
//                        mediaPlayer.release();
//                        a = false;
//                    }
                }
            });

            return convertView;
        }
    }
}