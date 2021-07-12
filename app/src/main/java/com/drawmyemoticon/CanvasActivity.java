package com.drawmyemoticon;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drawmyemoticon.Adapters.GifFrameAdapter;
import com.drawmyemoticon.CanvasHandle.AnimatedGIFWriter;
import com.drawmyemoticon.CanvasHandle.CanvasView;
import com.github.dhaval2404.colorpicker.ColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CanvasActivity extends AppCompatActivity {
    private InterstitialAd mInterstitialAd;

    RecyclerView gifFrameView;
    CanvasView canvasView;
    SeekBar thicknessSeekBar;
    TextView thicknessTextView, addFrameTextView, currentFrameTextView, saveTextView;
    ImageView addGifFrameButton;
    ProgressBar progressBar;

    boolean isThicknessSeekBarOpened = false;

    ArrayList<Bitmap> imageList = new ArrayList<>();
    String title = "My Emoticon";
    int size;
    boolean isGif = false;
    boolean saveAsOriginalScale = true;
    Bitmap selectedImage = null;
    boolean isSaveSucceed = false;

    // Temporal path to show save location to user
    String savePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        setAds();
        setValues();
        setViews();
    }

    private void setAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mInterstitialAd = new InterstitialAd(this);
        // Test ID : ca-app-pub-3940256099942544/1033173712
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                showSaveResult();
            }
        });
    }

    private void showSaveInterstitialAd() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            showSaveResult();
        }
    }

    private void setValues() {
        Intent intent = getIntent();
        title = intent.getStringExtra("canvasTitle");
        size = intent.getIntExtra("canvasSize", 512);
        isGif = intent.getBooleanExtra("isGif", false);
        saveAsOriginalScale = intent.getBooleanExtra("saveAsOriginalScale", false);

        if (intent.getBooleanExtra("isSelectedImage", false)) {
            byte[] arr = getIntent().getByteArrayExtra("image");
            selectedImage = BitmapFactory.decodeByteArray(arr, 0, arr.length);
        }
    }

    private void setViews() {
        setGifFrameView();

        canvasView = findViewById(R.id.canvasView);
        if (selectedImage != null) {
            canvasView.drawBitmap(selectedImage);
        }

        thicknessTextView = findViewById(R.id.thicknessTextView);
        thicknessTextView.setVisibility(View.GONE);

        thicknessSeekBar = findViewById(R.id.thicknessSeekBar);
        thicknessSeekBar.setVisibility(View.GONE);
        thicknessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                canvasView.setThickness(i);
                thicknessTextView.setText("" + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        saveTextView = findViewById(R.id.saveTextView);
        progressBar = findViewById(R.id.canvasProgressBar);
        saveTextView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceType")
    public void buttonEvent(View view) {
        switch (view.getId()) {
            case R.id.addGifFrameButton:
                imageList.add(canvasToBitmap(canvasView));
                canvasView.clearCanvas();
//                canvasView.drawShadowPath();
                updateGifFrameView();
                break;

            case R.id.thicknessChangeButton:
                if (isThicknessSeekBarOpened) {
                    thicknessSeekBar.setVisibility(View.GONE);
                    thicknessTextView.setVisibility(View.GONE);
                    isThicknessSeekBarOpened = false;
                } else {
                    thicknessSeekBar.setVisibility(View.VISIBLE);
                    thicknessTextView.setVisibility(View.VISIBLE);
                    isThicknessSeekBarOpened = true;
                }
                break;

            case R.id.colorChangeButton:
                new ColorPickerDialog
                        .Builder(this)
                        .setTitle("Pick Theme")
                        .setColorShape(ColorShape.SQAURE)
                        .setDefaultColor(getColor(R.color.black))
                        .setColorListener(new ColorListener() {
                            @Override
                            public void onColorSelected(int color, @NotNull String colorHex) {
                                // Handle Color Selection
                                canvasView.setColor(color);
                            }
                        })
                        .show();

                break;

            case R.id.eraserButton:
                canvasView.setColor(Color.WHITE);
                break;

            case R.id.removeLastButton:
                canvasView.removeLastPath();
                break;

            case R.id.clearCanvasButton:
                canvasView.clearCanvas();
                break;

            case R.id.saveCanvasButton:
                saveTextView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                if (isGif) {
                    try {
                        saveGif();
                    } catch (Exception e) {
                        e.printStackTrace();
                        isSaveSucceed = false;
                    }
                } else {
                    saveImage(canvasToBitmap(canvasView));
                }
                showSaveInterstitialAd();
                break;

            case R.id.backToHomeButton:
                finish();
        }
    }

    private Bitmap canvasToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        if (saveAsOriginalScale || isGif || size == 0) {
            return bitmap;
        }
        return Bitmap.createScaledBitmap(bitmap, size, size, false);
    }

    private void saveImage(Bitmap bitmap) {
        String imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + getString(R.string.directory) + "/" + title + ".png";
        savePath = imagePath;
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(imagePath));
            isSaveSucceed = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            isSaveSucceed = false;
        }
    }

    private void saveGif() throws Exception {
        String gifPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + getString(R.string.directory_gif) + "/" + title + ".gif";
        savePath = gifPath;
        OutputStream os = new FileOutputStream(gifPath);

        // True for dither. Will need more memory and CPU
        AnimatedGIFWriter writer = new AnimatedGIFWriter(true);
        // Grab the Bitmap whatever way you can
        // Use -1 for both logical screen width and height to use the first frame dimension
        writer.prepareForWrite(os, -1, -1);
        for (Bitmap bitmap : imageList) {
            if (size != 0) {
                bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
            }
            writer.writeFrame(os, bitmap);
        }

        // Keep adding frame here
        writer.finishWrite(os);
        // And you are done!!!
        isSaveSucceed = true;
    }

    private void scanGallery(String path){
        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(this,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    private void showSaveResult() {
        if (isSaveSucceed) {
            Toast.makeText(getApplicationContext(), getString(R.string.save_location) + savePath, Toast.LENGTH_LONG).show();
            scanGallery(savePath);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.save_failed), Toast.LENGTH_LONG).show();
        }
        saveTextView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void updateGifFrameView() {
        RecyclerView.Adapter adapter;
        // specify an adapter (see also next example)
        adapter = new GifFrameAdapter(imageList);
        ((GifFrameAdapter) adapter).setOnItemClickListener(new GifFrameAdapter.OnItemClickListener() {
            @Override
            public void onFrameClick(Bitmap image) {
                canvasView.clearCanvas();
                canvasView.drawBitmap(image);
            }

            @Override
            public void onRemoveFrameButtonClick(int position) {
                imageList.remove(position);
                updateGifFrameView();
            }
        });
        gifFrameView.setAdapter(adapter);
        currentFrameTextView.setText(getString(R.string.current_frames) + imageList.size());
    }

    private void setGifFrameView() {
        RecyclerView.LayoutManager layoutManager;
        addGifFrameButton = findViewById(R.id.addGifFrameButton);
        addFrameTextView = findViewById(R.id.addFrameTextView);
        currentFrameTextView = findViewById(R.id.currentFrameTextView);
        gifFrameView = findViewById(R.id.gifListRecyclerView);
        if (!isGif) {
            gifFrameView.setVisibility(View.GONE);
            addGifFrameButton.setVisibility(View.GONE);
            addFrameTextView.setVisibility(View.GONE);
            currentFrameTextView.setVisibility(View.GONE);
        }

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        gifFrameView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        gifFrameView.setLayoutManager(layoutManager);
    }
}