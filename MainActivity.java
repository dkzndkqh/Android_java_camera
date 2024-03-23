package com.example.camarasample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private Button btn_capture, btn_video;
    private Switch flash;
    private ImageView imagePhoto;
    private boolean isRecording = false; // 동영상 촬영 상태 확인 값
    private MediaRecorder mediaRecorder;


    // capture를 위한 상태 값(상수)
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_capture = findViewById(R.id.btn_capture);
        flash = findViewById(R.id.flash);
        btn_video = findViewById(R.id.btn_video);
        imagePhoto = findViewById(R.id.imagePhoto);

        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    // 녹화 중지 및 카메라 해제
                    mediaRecorder.stop();  // 녹화 중지
                    releaseMediaRecorder(); // 미디어 레코드 객체 해제

                    // 녹화 중지 상태 알림
                    btn_video.setText("동영상 촬영");
                    isRecording = false;
                } else {
                    // 비디오 카메라 초기화
                    if (prepareVideoRecorder()) {
                        // 카메라가 사용가능하고 잠금 해제되면 미디어 레코더가 준비되며 녹화 시작이 가능하다.
                        mediaRecorder.start();

                        // 녹화 중 상태 알림
                        btn_video.setText("동영상 촬영중");
                        isRecording = true;
                    } else {
                        // 미디어 레코드 준비가 안되면 카메라 해제
                        releaseMediaRecorder();
                    }
                }
            }
        });

        imagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Gridlist.class);
                startActivity(i);
            }
        });

        flash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setOnFlashFunction(b);

            }
        });

        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if(success) {
                            // 카메라로 부터 이미지 얻기!
                            mCamera.takePicture(null, null, mPicture);

                        }
                    }
                });
            }
        });
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 카메라 인스턴스 얻고 멤버에 설정
        mCamera = getCameraInstance();

        if(checkAutoFocusFunction() && checkFlashFunction()){
            setAutoFocusFunction();
            setAutoFocusPreview();
            setOnFlashFunction(false);
        }
        else {
            Toast.makeText(getApplicationContext(), "기기가 자동 초점 기능 없으면 실행 불가능 ", Toast.LENGTH_SHORT).show();
        }

        if(checkCameraHardware(this)) {
            // 미리보기 뷰 생성 후 액티비티 내의 컨텐츠로 설정한다.
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
            ((ViewGroup)flash.getParent()).removeView(flash);
            preview.addView(flash);
            ((ViewGroup)imagePhoto.getParent()).removeView(imagePhoto);
            preview.addView(imagePhoto);


            hideSystemUI();
        }
        else{
            Toast.makeText(getApplicationContext(), "장치에 카메라를 지원하지 않습니다.\n앱을 종료합니다!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();
        // 카메라 해제 코드 필요하지만 SurfaceView 의 surfaceDestroyed() 메서드에서 처리했으므로 이곳에서는 필요 없음!
    }

    // 카메라 하드웨어 감지
    private boolean checkCameraHardware(Context context){
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
            return true; // 카메라 있음!
        else
            return false; // 카메라 없음
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        }
        catch (Exception e){}
        return c;
    }

    // 콜백 객체
    // 콜백 인터페이스 구현 후 이미지 데이터를 수신하고 파일로 작성한다.
    private Camera.PictureCallback mPicture = new Camera.PictureCallback(){
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8; // 1/8사이즈로 보여주기
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newWidth = 200;
            int newHeight = 200;

            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            Matrix matrix = new Matrix();

            matrix.postScale(scaleWidth, scaleHeight);

           // matrix.postRotate(90);



            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0,0,width,height,matrix,true);
            BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);



            imagePhoto.setImageDrawable(new BitmapDrawable(resizedBitmap));

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

            if(pictureFile == null){
                Log.d("MyCameraApp", "미디어 파일 생성 실패, 저장소 권한을 확인하세요!");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.flush();
                fos.close();
                mCamera.startPreview();
            } catch (FileNotFoundException e) {
                Log.d("MyCameraApp", "해당 파일이 없습니다!");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("CAMERA", "파일 접근 오류!");
                e.printStackTrace();
            }
        }
    };



    // 이미지 또는 비디오 저장 파일 만들기 메서드
    private static File getOutputMediaFile(int type){
        // 안전을 위해 SDCard가 Environment.getExternalStorageState ()를 사용하여 마운트되었는지 확인한다.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "");
        // 이 위치는 생성 된 이미지를 응용 프로그램간에 공유하고 앱을 제거한 후에도 유지하려는 경우에 가장 적합합니다.

        // 저장 디렉토리가 없으면 저장소 디렉토리 생성
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "디렉토리 생성 실패!");
                return null;
            }
        }

        // 미디어 파일 이름 생성
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + "IMG_"+  ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + "VID_"+   ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    // 기능 가용성 확인
    private boolean checkAutoFocusFunction(){
        boolean isOk = false;
        Camera.Parameters params = mCamera.getParameters();

        List<String> focusModes = params.getSupportedFocusModes();

        if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)){
            isOk = true; // 자동 초점 기능 지원함!
        }
        return isOk;
    }

    private void setAutoFocusFunction(){
        // 카메라 파라미터 얻기
        Camera.Parameters params = mCamera.getParameters();
        // 파라미터에 포커스 모드 설정
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        // 카메라 파라미터 설정
        mCamera.setParameters(params);
    }

    private void setAutoFocusPreview(){
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        mCamera.setParameters(parameters);
    }

    private boolean checkFlashFunction(){
        boolean isOk = false;
        Camera.Parameters params = mCamera.getParameters();

        List<String> flashModes = params.getSupportedFlashModes();

        if(flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH) && flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)){
            isOk = true; // 플래쉬 ON/OFF 기능 지원함!
        }
        return isOk;
    }

    private void setOnFlashFunction(boolean isOn){
        // 카메라 파라미터 얻기
        Camera.Parameters params = mCamera.getParameters();
        // 파라미터에 플래쉬 모드 설정
        if(isOn)
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        else
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        // 카메라 파라미터 설정
        mCamera.setParameters(params);
    }

    private boolean prepareVideoRecorder(){
        mediaRecorder = new MediaRecorder();

        // Step 1: 카메라 언락후 미디어 레코더에 카메라 객체 설정
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);
        // Step 2: 오디오 비디오 소스 설정
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // Step 3: 캠코더프로파일을 통해 동영상 출력 형식과 인코딩 설정(API 레벨 8 이상 요구)
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        // Step 4: 동영상 파일 설정
        mediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
        // Step 5: 미이어 레코더에 미리보기 화면을 설정한다.
        mediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
        // Step 6: 미디어 레코드 준비
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("CAMERA", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("CAMERA", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder(){
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // 레코더 설정 삭제
            mediaRecorder.release(); // 레코더 객체 해제
            mediaRecorder = null;
            mCamera.lock();           // 다음 사용을 위해 카메라 접근 잠금 설정
        }
    }





}

