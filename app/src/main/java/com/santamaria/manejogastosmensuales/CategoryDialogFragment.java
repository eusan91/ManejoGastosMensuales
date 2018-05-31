package com.santamaria.manejogastosmensuales;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.santamaria.manejogastosmensuales.Domain.Category;

/**
 * Created by Santamaria on 12/04/2017.
 */

public class CategoryDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TITLE_EXTRA = "title";
    public static final String CATEGORY_EXTRA = "category";
    public static final String TYPE_EXTRA = "type";
    public static final String CATEGORY_DIALOG_FRAGMENT_EXTRA = "categoryDialogFragment";
    public static final String CATEGORY_NEW_EXTRA = "CategoryNew";
    public static final String CATEGORY_OLD_EXTRA = "CategoryOld";

    public static final int CREATION_TYPE = 99;
    public static final int EDITION_TYPE = 98;

    private String title;
    private Category category;
    private int dialogType;
    private int selectedColorR;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {

            if (bundle.getString(TITLE_EXTRA) != null && !bundle.getString(TITLE_EXTRA).isEmpty()) {
                title = bundle.getString(TITLE_EXTRA);
            }

            if (bundle.getParcelable(CATEGORY_EXTRA) != null) {
                category = bundle.getParcelable(CATEGORY_EXTRA);
            }

            dialogType = bundle.getInt(TYPE_EXTRA, 0);

        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title);

        View viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_category, null);
        builder.setView(viewInflated);

        final EditText categoryNameInput = viewInflated.findViewById(R.id.categoryNameInput);
        final ImageView imageViewColors = viewInflated.findViewById(R.id.imageViewColors);

        imageViewColors.setOnClickListener(this);

        builder.setPositiveButton(dialogType == CREATION_TYPE ?
                getString(R.string.Category_dialog_frag_positive_button_text_create) : getString(R.string.Category_dialog_frag_positive_button_text_update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String categoryName = categoryNameInput.getText().toString().trim();

                if (!categoryName.isEmpty()) {

                    Category categoryTemp;

                    categoryTemp = new Category(categoryName, selectedColorR);

                    //return bundle
                    Bundle bundle = new Bundle();
                    if (dialogType == CREATION_TYPE){
                        bundle.putParcelable(CATEGORY_EXTRA, categoryTemp);
                    }
                    else if (dialogType == EDITION_TYPE) {
                        bundle.putParcelable(CATEGORY_NEW_EXTRA, categoryTemp);
                        bundle.putParcelable(CATEGORY_OLD_EXTRA, category);
                    }

                    Intent intent = new Intent().putExtras(bundle);

                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);

                    dismiss();

                } else {
                    Toast.makeText(getActivity(), R.string.Category_dialog_frag_error_category_name_empty, Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //no action required
                dismiss();
            }
        });

        if (dialogType == EDITION_TYPE) {
            categoryNameInput.setText(category.getNombre());
        }

        return builder.create();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.imageViewColors) {
            createColorPickerDialog();
        }

    }

    private void createColorPickerDialog(){

        ColorPickerDialogBuilder
                .with(getContext())
                .setTitle(getString(R.string.dialog_color_picker_title))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        //Toast.makeText(getContext(), "onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton(getString(R.string.dialog_color_picker_ok), new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        selectedColorR = selectedColor;
                    }
                })
                .setNegativeButton(getString(R.string.dialog_color_picker_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

}
