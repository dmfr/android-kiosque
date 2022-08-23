package za.dams.kiosque;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;

import android.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestCameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestCameraFragment extends DialogFragment implements View.OnClickListener {

    public interface CameraListener {
        void onCameraResult(boolean photoTaken);
    }
    private CameraListener mListener ;

    private static final class MainThreadExecutor implements Executor {
        static final Executor INSTANCE = new MainThreadExecutor();

        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable r) {
            handler.post(r);
        }
    }

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture ;
    private PreviewView previewView ;
    private View btnTakePicture ;
    private ProgressDialog mProgressDialog ;

    public TestCameraFragment() {
        // Required empty public constructor
    }
    public static TestCameraFragment newInstance() {
        TestCameraFragment fragment = new TestCameraFragment();
        return fragment;
    }

    public void setListener( CameraListener cl ) {
        mListener = cl ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Photo preview");

        View rootView = inflater.inflate(R.layout.fragment_test_camera, container, false);

        previewView = rootView.findViewById(R.id.preview_view) ;
        btnTakePicture = rootView.findViewById(R.id.btn_takepicture) ;

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        cameraProviderFuture = ProcessCameraProvider.getInstance(getActivity());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, MainThreadExecutor.INSTANCE);

        Executor cameraExecutor = Executors.newSingleThreadExecutor() ;

        btnTakePicture.setOnClickListener(this);
    }

    void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture =
                new ImageCapture.Builder()
                        .setTargetRotation(getView().getDisplay().getRotation())
                        .build();

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageCapture, preview);
    }

    @Override
    public void onResume() {
        // Set the width of the dialog proportional to 90% of the screen width
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int minSize = (int)(Math.min(size.x,size.y) * 1) ;


        window.setLayout(minSize, (int)(minSize * 4 / 3) );
        window.setGravity(Gravity.CENTER);


        super.onResume();
        //barcodeView.resume();
    }


    @Override
    public void onClick(View v) {
        if( v==btnTakePicture ) {
            takePicture();
        }
    }
    public void takePicture() {
        mProgressDialog = ProgressDialog.show(getActivity(), "",
                "Loading. Please wait...", true);

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(new File(getContext().getFilesDir(), "testFilename.jpg")).build();
        imageCapture.takePicture(outputFileOptions, Executors.newSingleThreadExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                        // insert your code here.
                        try {
                            Thread.sleep(2000);
                        } catch( Exception e ) {

                        }
                        boolean isCheck = (Looper.myLooper() == Looper.getMainLooper()) ;
                        if( isCheck ) {
                            Log.w("DAMS", "Main thread");
                        } else {
                            Log.w("DAMS", "Not main thread");
                        }
                        try {
                            Thread.sleep(2000);
                        } catch( Exception e ) {

                        }

                        // Get a handler that can be used to post to the main thread
                        Handler mainHandler = new Handler(TestCameraFragment.this.getActivity().getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (TestCameraFragment.this.mProgressDialog != null) {
                                    mProgressDialog.cancel();
                                }
                                TestCameraFragment.this.dismiss();
                            }
                        };
                        mainHandler.post(myRunnable);
                    }

                    @Override
                    public void onError(ImageCaptureException exception) {

                    }

                }
        );
    }


}