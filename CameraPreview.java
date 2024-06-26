package com.example.camarasample;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // SurfaceHolder.Callback 설치 : Surface(표면)가 생성되거나 삭제될때 알림을 받기 위함
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Surface 생성, 미리보기를 그릴 위치 설정
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (mHolder.getSurface() == null){
            // 미리보기를 위한 surface가 없는 경우
            return;
        }

        // 변경전 미리보기 중지!
        try {
            mCamera.stopPreview();
        } catch (Exception e){}

        // 미리보기 크기 설정, 크기 조정, 회전 또는 형식 변경 코드 위치
        mHolder.setFixedSize(width, height);

        // 새로운 설정으로 미리보기 시작
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.release();
        mCamera = null;
    }

}
