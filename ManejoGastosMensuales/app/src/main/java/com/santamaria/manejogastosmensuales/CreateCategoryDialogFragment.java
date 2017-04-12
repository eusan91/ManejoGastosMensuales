package com.santamaria.manejogastosmensuales;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.santamaria.manejogastosmensuales.Domain.Category;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Santamaria on 12/04/2017.
 */

public class CreateCategoryDialogFragment extends DialogFragment implements View.OnClickListener {


    private static final int REQUEST_CAMERA = 1;

    private static final int CAMERA_WRITE_PERMISSION = 100;

    private Uri mediaUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mediaUri = (Uri) savedInstanceState.getParcelable("mediaUri");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Create new Category");

        View viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_category, null);
        builder.setView(viewInflated);

        final EditText categoryNameInput = (EditText) viewInflated.findViewById(R.id.categoryNameInput);
        final ImageView imageViewCamera = (ImageView) viewInflated.findViewById(R.id.imageViewCamera);
        final ImageView imageViewGallery = (ImageView) viewInflated.findViewById(R.id.imageViewGallery);

        imageViewCamera.setOnClickListener(this);
        imageViewGallery.setOnClickListener(this);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String categoryName = categoryNameInput.getText().toString().trim();

                if (!categoryName.isEmpty()) {

                    Object url;
                    if (mediaUri != null) {
                        url = mediaUri.getPath();
                    } else {
                        url = R.drawable.under_construct;
                    }

                    //return bundle
                    Category category = new Category(categoryName, mediaUri, (float) 0);

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("Category", category);

                    Intent intent = new Intent().putExtras(bundle);

                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);

                    dismiss();

                } else {
                    Toast.makeText(getActivity(), "The name is required to create a new Category", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //no action required
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("mediaUri", mediaUri);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {

            mediaUri = (Uri) savedInstanceState.getParcelable("mediaUri");
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.imageViewCamera) {
            takePicture();
        } else if (view.getId() == R.id.imageViewGallery) {
            Toast.makeText(getActivity(), "Abrir Galeria", Toast.LENGTH_SHORT).show();
        }

    }

    //------------------------------- Logic to take pictures ----------------------------------

    private void takePicture() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //ask for permission
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    showExplanationOfRequestPermission();

                } else {
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_WRITE_PERMISSION);
                }
            } else {
                //no need for permission
                createMedia();
            }
        } else {
            //No permissions required
            createMedia();
        }
    }

    private void showExplanationOfRequestPermission() {

        new AlertDialog.Builder(getActivity())
                .setTitle("Request of permissions")
                .setMessage("This application needs permissions to save pictures")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //request permission
                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_WRITE_PERMISSION);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "Permission not grated, we can not open the camera", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void createMedia() {

        try {
            mediaUri = createMediaFile();

            if (mediaUri == null) {
                Toast.makeText(getContext(), "There was an error", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_WRITE_PERMISSION) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createMedia();
            }
        }
    }

    private Uri createMediaFile() throws IOException {

        if (!availableInternalStorage()) {
            return null;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String fileName = "IMG_" + timeStamp + "_";
        File file;

        File pathToStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        file = File.createTempFile(fileName, ".jpg", pathToStorage);

        MediaScannerConnection.scanFile(getContext(), new String[]{file.getPath()}, new String[]{"image/jpeg"}, null);

        return Uri.fromFile(file);
    }

    private boolean availableInternalStorage() {

        String state = Environment.getExternalStorageState();

        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
