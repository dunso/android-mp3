package activity;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import Data.Mp3;
import Utils.DateUtil;
import Utils.AccessUtil;
import Data.Adapter;
import so.dun.mp3.R;

@SuppressLint("DefaultLocale")
public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    //View element
    private TextView textViewSongName;
    private SeekBar seekBar;
    private TextView textViewTimeLength;
    private TextView textViewTimePosition;
    private ImageButton buttonPause;
    private ImageButton buttonStop;
    private ListView listView;

    //Listener
    private SeekBarListener seekBarListener;
    private ButtonClickListener buttonClickListener;

    //Handler
    private Handler handler;

    //Adapter
    private ArrayAdapter<String> adapter;

    private Mp3 mp3;
    private MediaPlayer mediaPlayer;

    /**
     * Called when the activity is first created.
     * Load layout file，show data on the view
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout file
        setContentView(R.layout.activity_main);

        // Fetch the references of view
        textViewSongName = findViewById(R.id.textView_song_name);
        seekBar = findViewById(R.id.SeekBar);
        textViewTimeLength = findViewById(R.id.textView_time_length);
        textViewTimePosition = findViewById(R.id.textView_time_position);
        buttonPause = findViewById(R.id.imageButton_play);
        buttonStop = findViewById(R.id.imageButton_stop);
        listView = findViewById(R.id.listView);

        //Register seek bar listener
        seekBarListener = new SeekBarListener();
        seekBar.setOnSeekBarChangeListener(seekBarListener);

        //Register button click listener
        buttonClickListener = new ButtonClickListener();
        buttonPause.setOnClickListener(buttonClickListener);
        buttonStop.setOnClickListener(buttonClickListener);

        // Register item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
                prepareMediaPlayer(index);
                mediaPlayer.start();
                handler.post(updateSeekBar);
                buttonPause.setImageResource(R.mipmap.pause);
            }
        });

        handler = new Handler();

        mediaPlayer = new MediaPlayer();
        mp3 = new Mp3();

        //Check the access permission
        if (AccessUtil.verifyStoragePermissions(this)) {
            if (mp3.initSongs()) {
                adapter = Adapter.songListAdapter(this, android.R.layout.simple_list_item_1, mp3.getSongList());
                listView.setAdapter(adapter);
            } else {
                Toast.makeText(this, "SD卡不可用或者SD卡上没有歌曲", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mp3.initSongs()) {
                        adapter = Adapter.songListAdapter(this, android.R.layout.simple_list_item_1, mp3.getSongList());
                        listView.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "SD卡不可用或者SD卡上没有歌曲", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "你拒绝了访问SD卡的权限！", Toast.LENGTH_SHORT).show();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                return;
            }
        }
    }

    @Override
    protected void onStart() {
        //Start sequence：onCreate()->onStart()->onResume();
        super.onStart();
        if (!mediaPlayer.isPlaying() && mp3.getSongList() != null && mp3.getSongList().size() > 0) {
            prepareMediaPlayer(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(updateSeekBar);
    }

    @Override
    protected void onPause() {
        mp3.initSongs();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //Destroy sequence：onPause()->onStop()->onDestory()
        super.onStop();
        super.onDestroy();
        mediaPlayer.reset();
    }

    private void prepareMediaPlayer(int index) {
        mediaPlayer.reset();
        try {
            //Set the data source
            mediaPlayer.setDataSource(mp3.getSongPath().get(index));
            //Prepare the media
            mediaPlayer.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Change the title to the current music
        String songName = mp3.getSongList().get(index);
        textViewSongName.setText(songName);

        //Change the time length of current music
        int length = mediaPlayer.getDuration();
        textViewTimeLength.setText(DateUtil.formatTime(length));
        seekBar.setMax(length);
    }

    //Update process, callback task
    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            //Update the view
            int position = mediaPlayer.getCurrentPosition();
            textViewTimePosition.setText(DateUtil.formatTime(position));
            seekBar.setProgress(position);
            if (seekBar.getProgress() < seekBar.getMax()) {
                handler.postDelayed(this, 100);
            }
        }
    };

    class ButtonClickListener implements View.OnClickListener {

        /**
         * Listening the button click event
         *
         * @param view: On which UI
         */
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.imageButton_play:
                    doPause();
                    break;
                case R.id.imageButton_stop:
                    doStop();
                    break;
            }
        }

        private void doPause() {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                buttonPause.setImageResource(R.mipmap.play);
            } else {
                mediaPlayer.start();
                buttonPause.setImageResource(R.mipmap.pause);
            }

        }

        private void doStop() {
            // TODO Auto-generated method stub
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            buttonPause.setImageResource(R.mipmap.play);
            prepareMediaPlayer(0);
        }
    }

    class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * Change the process of seek bar
         *
         * @see android.widget.SeekBar.OnSeekBarChangeListener#onProgressChanged(android.widget.SeekBar, int, boolean)
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mediaPlayer.seekTo(progress);
                textViewTimePosition.setText(DateUtil.formatTime(progress));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            //remove the updateSeekBar thread from queue
            handler.removeCallbacks(updateSeekBar);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayer.start();
            handler.post(updateSeekBar);
            buttonPause.setImageResource(R.mipmap.pause);
        }
    }
}

