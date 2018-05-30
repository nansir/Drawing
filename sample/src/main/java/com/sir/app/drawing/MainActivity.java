package com.sir.app.drawing;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.sir.library.drawing.DrawableView;
import com.sir.library.drawing.DrawableViewConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 电子签名
 * Created by zhuyinan on 2017/12/22.
 * Contact by 445181052@qq.com
 */
public class MainActivity extends AppCompatActivity {

    DrawableView drawableView;
    ImageView show;

    private String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Drawing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        show = (ImageView) findViewById(R.id.show);
        drawableView = (DrawableView) findViewById(R.id.drawable_view);

        DrawableViewConfig config = new DrawableViewConfig();
        config.setStrokeColor(getResources().getColor(R.color.colorPrimary));
        config.setShowCanvasBounds(true);
        config.setStrokeWidth(10.0f);
        config.setMinZoom(1.0f);
        config.setMaxZoom(3.0f);
        config.setCanvasHeight(500);
        config.setCanvasWidth(700);
        drawableView.setConfig(config);
    }

    public void btnOnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_retreat:
                drawableView.undo();
                break;
            case R.id.btn_clear:
                drawableView.clear();
                break;
            case R.id.btn_save:
                show.setImageBitmap(drawableView.obtainBitmap());
                //saveBitmap(drawableView.obtainBitmap());
                break;
        }
    }

    //保存位图
    public void saveBitmap(Bitmap bitmap) {
        FileOutputStream fos;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
            fos = new FileOutputStream(new File(filePath, System.currentTimeMillis() + ".png"));
            if (null != fos) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, fos);
                fos.flush();
                fos.close();
            }
            Log.i("TAG", "save bitmap succeed");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
