package com.santamaria.manejogastosmensuales.Fragments;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.santamaria.manejogastosmensuales.Adapter.RecyclerViewAdapter;
import com.santamaria.manejogastosmensuales.BuildConfig;
import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.Manifest;

public class MainFragment extends Fragment implements View.OnClickListener {


    private static final int REQUEST_CAMERA = 1;
    private static final int CAMERA_WRITE_PERMISSION = 100;

    private Uri mediaUri;
    private FloatingActionButton fabAdd;
    List<Category> categoryList;
    RecyclerView recyclerViewCategories;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerViewCategories = (RecyclerView) view.findViewById(R.id.recyclerViewCategorias);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());

        categoryList = new LinkedList<>();

        categoryList.add(new Category("Gasolina", R.drawable.under_construct, (float) 0.0));
        categoryList.add(new Category("Servicios", R.drawable.under_construct, (float) 0.0));
        categoryList.add(new Category("Comida", R.drawable.under_construct, (float) 0.0));

        recyclerViewAdapter = new RecyclerViewAdapter(categoryList, R.layout.recycler_cardview_item,
                new RecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Category category, int position) {

                        Toast.makeText(getActivity(), "Prueba 1,2,3, me escuchan?", Toast.LENGTH_SHORT).show();

                    }
                }, getActivity().getMenuInflater());

        recyclerViewCategories.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewCategories.setAdapter(recyclerViewAdapter);

        //fab action
        final FloatingActionButton fabAdd = (FloatingActionButton) view.findViewById(R.id.fabAddCategory);

        fabAdd.setOnClickListener(this);

        //recycler view scroll
        recyclerViewCategories.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 || dy < 0 || dx > 0 || dx < 0) {
                    fabAdd.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fabAdd.show();
                }
            }
        });

        //cuando el layout no va a cambiar, esta propiedad mejora el performace.
        recyclerViewCategories.setHasFixedSize(true);

        //sencilla animacion...
        recyclerViewCategories.setItemAnimator(new DefaultItemAnimator());


        //contextMenu
        //registerForContextMenu(recyclerViewCategories);

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Context Menu");
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.options_card_view_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.editCardView:
                Toast.makeText(getActivity(), "El Item a editar es: " + info.position, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.deleteCardView:
                removeCategory(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void alertCreateCategory(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (!title.isEmpty()) {
            builder.setTitle(title);
        }

        if (!message.isEmpty()) {
            builder.setMessage(message);
        }

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
                    if (mediaUri != null){
                        url = mediaUri.getPath();
                    } else {
                        url = R.drawable.under_construct;
                    }
                    addNewCategory(new Category(categoryName, url, (float) 0));

                } else {
                    Toast.makeText(getActivity(), "The name is required to create a new Category", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //no action required
            }
        });

        builder.create().show();

    }

    private void addNewCategory(Category category) {

        int position = categoryList.size();
        categoryList.add(category);
        recyclerViewAdapter.notifyItemInserted(position);
        recyclerViewLayoutManager.scrollToPosition(position);

    }

    private void removeCategory(int position) {

        categoryList.remove(position);
        recyclerViewAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.fabAddCategory) {
            alertCreateCategory("Create new Category", "");
        } else if (view.getId() == R.id.imageViewCamera) {
            takePicture();
        } else if (view.getId() == R.id.imageViewGallery) {
            Toast.makeText(getContext(), "Abrir Galeria", Toast.LENGTH_SHORT).show();
        }

    }

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

        if (requestCode == CAMERA_WRITE_PERMISSION){

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createMedia();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            //in case I need to use the picture to something.

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
