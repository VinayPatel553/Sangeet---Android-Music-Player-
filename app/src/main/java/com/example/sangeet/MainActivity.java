package com.example.sangeet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lv, lv1;
    TabHost thb;
    Thread cc;
    ImageView previous, play, next,previouso, playo, nexto;
    SeekBar sb,sbo;
    TextView tv,tvo,tva1,tva2,tva1o,tva2o;
    MediaPlayer mp;
    String textContent;
    int position,postiono;
    Thread myupdateseek;
    ConstraintLayout cl;
    SQLiteDatabase db;
    @Override
    protected void onDestroy() {
        myupdateseek.interrupt();
        cc.interrupt();
        mp.release();
        super.onDestroy();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Songs");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thb = findViewById(R.id.tabh);
        lv = findViewById(R.id.listv);
        lv1 = findViewById(R.id.listv1);
        previous = findViewById(R.id.previous);
        play = findViewById(R.id.pause);
        next = findViewById(R.id.next);
        previouso = findViewById(R.id.previouso);
        playo = findViewById(R.id.pauseo);
        nexto = findViewById(R.id.nexto);
        tv = findViewById(R.id.textView);
        sb = findViewById(R.id.seekBar);
        tvo = findViewById(R.id.textViewo);
        sbo = findViewById(R.id.seekBaro);
        cl = findViewById(R.id.cl);
        tva1 = findViewById(R.id.tva1);
        tva2 = findViewById(R.id.tva2);
        tva1o = findViewById(R.id.tva1o);
        tva2o = findViewById(R.id.tva2o);

        thb.setup();
        TabHost.TabSpec tab1 = thb.newTabSpec("Tab1");
        tab1.setIndicator("Local Songs");
        tab1.setContent(R.id.tab1);
        thb.addTab(tab1);

        TabHost.TabSpec tab2 = thb.newTabSpec("Tab2");
        tab2.setIndicator("Now Playing");
        tab2.setContent(R.id.tab2);
        thb.addTab(tab2);

        TabHost.TabSpec tab3 = thb.newTabSpec("Tab3");
        tab3.setIndicator("Online");
        tab3.setContent(R.id.tab3);
        thb.addTab(tab3);

        TabHost.TabSpec tab4 = thb.newTabSpec("Tab4");
        tab4.setContent(R.id.tab2o);






        thb.setCurrentTab(0);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.my)));


        //ONLINE


        String[] items1 = new String[10000];
        for (int i = 0; i < items1.length; i++) {
            items1[i] =i+1+"";

        }
        MediaPlayer m1 = new MediaPlayer();
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(MainActivity.this, R.layout.each, R.id.text_view1, items1);
        lv1.setAdapter(adapter1);

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int positionx, long l) {

                try {
                    m1.reset();
                } catch (Exception e) {

                }
                try {
                    play.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                    mp.release();
                    tab2.setContent(R.id.tab2o);

                } catch (Exception e) {

                }


                try {


                    Toast.makeText(MainActivity.this, "https://paglasongs.com/files/download/id/" + (positionx + 1), Toast.LENGTH_SHORT).show();
                    postiono = positionx;
                    m1.setDataSource("https://paglasongs.com/files/download/id/" + (positionx + 1));
                    m1.prepareAsync();

                    m1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            Toast.makeText(MainActivity.this, "Starting", Toast.LENGTH_SHORT).show();
                            playo.setImageResource(R.drawable.pause);
                            tvo.setText(postiono+1 + "");
                            sbo.setMax(m1.getDuration());
                            tva2o.setText(millisecondsToTime(m1.getDuration()));
                            m1.start();

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                playo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (m1.isPlaying()) {
                            playo.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                            m1.pause();

                        } else {
                            playo.setImageResource(R.drawable.pause);
                            sbo.setMax(m1.getDuration());

                            m1.start();
                        }
                    }
                });

                previouso.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        m1.reset();
                        if (postiono != 0) {
                            postiono = postiono - 1;

                        } else {
                            postiono = items1.length - 1;
                        }
                        try {
                            m1.setDataSource("https://paglasongs.com/files/download/id/" + (postiono + 1));
                            m1.prepareAsync();
                            sbo.setProgress(0);
                            sbo.setMax(m1.getDuration());

                            tvo.setText(postiono + 1 + "");
                            playo.setImageResource(R.drawable.pause);
                            tva2o.setText(millisecondsToTime(m1.getDuration()));


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                nexto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        m1.reset();
                        if (postiono !=items1.length - 1) {
                            postiono = postiono + 1;
                        } else {
                            postiono = 0;
                        }



                        try {
                            m1.setDataSource("https://paglasongs.com/files/download/id/" + (postiono + 1));
                            m1.prepareAsync();
                            sbo.setProgress(0);
                            sbo.setMax(m1.getDuration());

                            tvo.setText(postiono + 1 + "");
                            playo.setImageResource(R.drawable.pause);
                            tva2o.setText(millisecondsToTime(m1.getDuration()));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }



                    }
                });

                m1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if(m1.getCurrentPosition() !=0){
                            m1.reset();
                            if (postiono !=items1.length - 1) {
                                postiono = postiono + 1;
                            } else {
                                postiono = 0;
                            }



                            try {
                                m1.setDataSource("https://paglasongs.com/files/download/id/" + (postiono + 1));
                                m1.prepareAsync();
                                sbo.setProgress(0);
                                sbo.setMax(m1.getDuration());
                                tva2o.setText(millisecondsToTime(m1.getDuration()));

                                tvo.setText(postiono + 1 + "");
                                playo.setImageResource(R.drawable.pause);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }




                    }
                });
                try {
                    sbo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            tva1o.setText(millisecondsToTime(m1.getCurrentPosition()));
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            m1.seekTo(seekBar.getProgress());
                            tva1o.setText(millisecondsToTime(m1.getCurrentPosition()));

                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }








            }
        });


///////////////////////////////////////////////////////////

//bug removed: remind me vinay and internet permission and mp.reset in class
        //bug removed: remind me vinay and internet permission and mp.reset in class
        //bug removed: remind me vinay and internet permission and mp.reset in class
        //bug removed: remind me vinay and internet permission and mp.reset in class

        /////////////////////////////////////////////////////////////////
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        ArrayList<File> mysongs = fetchsongs(Environment.getExternalStorageDirectory());

                        String[] items = new String[mysongs.size()];

                        for (int i = 0; i < mysongs.size(); i++) {
                            items[i] = mysongs.get(i).getName().replace(".mp3", "");
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int positionx, long l) {

                                    try {
                                        play.setImageResource(R.drawable.pause);
                                        mp.stop();
                                        mp.release();

                                    } catch (Exception e) {

                                    }try {
                                        m1.reset();
                                        tab2.setContent(R.id.tab2);

                                    } catch (Exception e) {

                                    }
                                    position = positionx;
                                    String currentSong = lv.getItemAtPosition(positionx).toString();


                                    tv.setText(currentSong);
                                    tv.setSelected(true);

                                    Uri myuri = Uri.parse(mysongs.get(positionx).toString());
                                    mp = MediaPlayer.create(getApplicationContext(), myuri);
                                    mp.start();
                                    thb.setCurrentTab(1);

                                    tva2.setText(millisecondsToTime(mp.getDuration()));
                                    sb.setMax(mp.getDuration());


                                }
                            });
                        }


                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.each, R.id.text_view1, items);
                        lv.setAdapter(adapter);


                        String currentSong = lv.getItemAtPosition(position).toString();


                        tv.setText(currentSong);
                        tv.setSelected(true);
                        Uri myuri = Uri.parse(mysongs.get(position).toString());
                        mp = MediaPlayer.create(getApplicationContext(), myuri);

                        sb.setMax(mp.getDuration());
                        play.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);


                        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                                tva1.setText(millisecondsToTime(mp.getCurrentPosition()));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {


                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                mp.seekTo(seekBar.getProgress());
                                tva1.setText(millisecondsToTime(mp.getCurrentPosition()));

                            }
                        });


                        play.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (mp.isPlaying()) {
                                    play.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                                    mp.pause();
                                } else {
                                    play.setImageResource(R.drawable.pause);
                                    mp.start();
                                }
                            }
                        });

                        previous.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mp.stop();
                                mp.release();
                                if (position != 0) {
                                    position = position - 1;

                                } else {
                                    position = mysongs.size() - 1;
                                }
                                Uri myuri = Uri.parse(mysongs.get(position).toString());
                                mp = MediaPlayer.create(getApplicationContext(), myuri);
                                mp.start();
                                play.setImageResource(R.drawable.pause);
                                sb.setProgress(0);
                                sb.setMax(mp.getDuration());
                                textContent = mysongs.get(position).getName();
                                tv.setText(textContent);
                                tva2.setText(millisecondsToTime(mp.getDuration()));



                            }
                        });

                        next.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                mp.stop();
                                mp.release();
                                if (position != mysongs.size() - 1) {
                                    position = position + 1;
                                } else {
                                    position = 0;
                                }
                                Uri myuri = Uri.parse(mysongs.get(position).toString());
                                mp = MediaPlayer.create(getApplicationContext(), myuri);
                                mp.start();
                                sb.setMax(mp.getDuration());
                                sb.setProgress(0);
                                play.setImageResource(R.drawable.pause);
                                textContent = mysongs.get(position).getName();
                                tv.setText(textContent);
                                tva2.setText(millisecondsToTime(mp.getDuration()));



                            }
                        });
                        myupdateseek = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    while (true) {
                                        try {
                                            if (mp.getCurrentPosition() < mp.getDuration()) {
                                                sb.setProgress(mp.getCurrentPosition());


                                                sleep(1000);
                                            }
                                        } catch (Exception e) {
                                            sleep(1);
                                        }try {
                                            if (m1.getCurrentPosition() < m1.getDuration()) {
                                                sbo.setProgress(m1.getCurrentPosition());
                                                sleep(1000);
                                            }
                                        } catch (Exception e) {
                                            sleep(1);
                                        }
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        myupdateseek.start();


                        //

                        cc = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    while (true) {
                                        Log.d("err","mycc");
                                        sleep(1000);

                                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mediaPlayer) {


                                                mp.stop();
                                                mp.release();
                                                if (position != mysongs.size() - 1) {
                                                    position = position + 1;
                                                } else {
                                                    position = 0;
                                                }
                                                Uri myuri = Uri.parse(mysongs.get(position).toString());
                                                mp = MediaPlayer.create(getApplicationContext(), myuri);
                                                tva2.setText(millisecondsToTime(mp.getDuration()));
                                                mp.start();
                                                sb.setMax(mp.getDuration());
                                                sb.setProgress(0);
                                                play.setImageResource(R.drawable.pause);
                                                textContent = mysongs.get(position).getName();
                                                tv.setText(textContent);




                                            }
                                        });

                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        cc.start();

                        //


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        finish();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.cancelPermissionRequest();
                    }
                })
                .check();


    }


    public ArrayList<File> fetchsongs(File file) {
        ArrayList songList = new ArrayList();
        File[] myallfiels = file.listFiles();
        if (myallfiels != null) {
            for (File myfile : myallfiels) {
                if (!myfile.isHidden() && myfile.isDirectory()) {
                    songList.addAll(fetchsongs(myfile));
                } else {
                    if (myfile.getName().endsWith(".mp3") && !myfile.getName().startsWith(".")) {
                        songList.add(myfile);
                    }


                }
            }

        }
        return songList;
    }
    private String millisecondsToTime(long milliseconds) {
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;
        String secondsStr = Long.toString(seconds);
        String secs;
        if (secondsStr.length() >= 2) {
            secs = secondsStr.substring(0, 2);
        } else {
            secs = "0" + secondsStr;
        }

        return minutes + ":" + secs;
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.logout:
                db=openOrCreateDatabase("Mydata1", Context.MODE_PRIVATE,null);
                db.execSQL("Delete From usert;");
                startActivity(new Intent(MainActivity.this,loginactivity.class));
                finish();
                break;
            case R.id.exit:
                finish();
                Toast.makeText(this, "Exit", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
