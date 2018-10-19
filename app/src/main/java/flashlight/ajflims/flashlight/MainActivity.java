package flashlight.ajflims.flashlight;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import de.hdodenhof.circleimageview.CircleImageView;
import flashlight.ajflims.flashlight.R;

public class MainActivity extends AppCompatActivity {

    CircleImageView mFlashLight;
    Camera camera;
    Camera.Parameters parameters;
    boolean isFlash = false, isTorchOn = false;
    private final int CameraCode = 104;
    private SharedPreferences sp;
    private boolean isFirst;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFlashLight = findViewById(R.id.flashlight);

       checkCameraPermision();

        sp = getSharedPreferences("torch",MODE_PRIVATE);
        isFirst = sp.getBoolean("first",true);

        mFlashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                    if(isFirst){
                        SharedPreferences.Editor editor = sp.edit();
                        camera = Camera.open();
                        parameters = camera.getParameters();
                        isFlash = true;
                        editor.putBoolean("first",false);
                        editor.apply();
                    }

                    if (isFlash) {

                        if (!isTorchOn) {

                            mFlashLight.setImageResource(R.drawable.flashlight_on);
                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            camera.setParameters(parameters);
                            camera.startPreview();
                            isTorchOn = true;

                        } else {

                            mFlashLight.setImageResource(R.drawable.flashlight_off);
                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            camera.setParameters(parameters);
                            camera.stopPreview();
                            isTorchOn = false;

                        }


                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Error.....");
                        builder.setMessage("Flashlight is not Availabe");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }else{

                    Snackbar snackbar = Snackbar.make(findViewById(R.id.snackbar),"Enable Camera Permission",Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Turn On", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",getPackageName(),null);
                            intent.setData(uri);
                            startActivity(intent);

                        }
                    });

                    snackbar.show();
                }
            }
        });
    }

    private void checkCameraPermision() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CAMERA}, CameraCode);

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CameraCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    return;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (camera != null) {

            camera.release();
            camera = null;

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CAMERA}, CameraCode);

            }else{
                if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {

                    camera = Camera.open();
                    parameters = camera.getParameters();
                    isFlash = true;

                }
            }

        }

        if(isTorchOn){
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
            camera.startPreview();
        }


    }

}







