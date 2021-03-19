package com.example.direct_order.order_sheet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;

public class ImageCropActivity extends AppCompatActivity {
    private static final int REQUEST_SELECT_PICTURE = 1001;
    private OnOptionUriSetupListener optionUriSetupListener;
    private OnUploadCompleteListener uploadCompleteListener;
    private Uri resultUri;
    private ImageView imageView;
    private int count;

    public void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] mimeTypes = {"image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }
        startActivityForResult(intent, REQUEST_SELECT_PICTURE);
    }

    protected void startCropActivity(@NonNull Uri uri) {
        resultUri = Uri.fromFile(new File(getCacheDir(), "SampleCropImage"+(count++)+".png"));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        UCrop.of(uri, resultUri)
                .withOptions(options)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_PICTURE && data !=null) {
                Uri imgUri = data.getData();
                if (imgUri != null) {
                    startCropActivity(imgUri);
                }
            }
            else if (requestCode == UCrop.REQUEST_CROP) {
                resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    imageView.setImageURI(resultUri);
                    if (OrderSheetActivity.type == OptionType.CHECKBOX_IMAGE || OrderSheetActivity.type == OptionType.RADIOBUTTON_IMAGE) {
                        optionUriSetupListener.onOptionUriSetup(CompoundImageOptionActivity.uris);
                    }
                }
            }
        }
        else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
        }
        else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Log.d("IMAGE_CROP", cropError.getMessage());
        }
    }

    protected String uploadImage(Uri uri, String folder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String filename = sdf.format(System.currentTimeMillis());
        String filePath = folder + filename + getFileExtension(uri.toString());
        StorageReference ref = FirebaseStorage.getInstance().getReference(filePath);
        UploadTask uploadTask = ref.putFile(uri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if (uploadCompleteListener != null)
                            uploadCompleteListener.onUploadComplete();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        return filePath;
    }

    //파일 확장자
    protected static String getFileExtension(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf("."));
        return extension;
    }

    public Uri getResultUri() {
        return resultUri;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setOptionUriSetupListener(OnOptionUriSetupListener optionUriSetupListener) {
        this.optionUriSetupListener = optionUriSetupListener;
    }

    public void setUploadCompleteListener(OnUploadCompleteListener uploadCompleteListener) {
        this.uploadCompleteListener = uploadCompleteListener;
    }
}
