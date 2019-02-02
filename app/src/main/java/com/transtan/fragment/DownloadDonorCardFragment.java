package com.transtan.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.transtan.MainActivity;
import com.transtan.R;
import com.transtan.config.AppConfig;
import com.transtan.model.DonorData;
import com.transtan.utils.RetrofitInterface;
import com.transtan.utils.UnsafeOkHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

/**
 * Created by djayakum on 3/22/2018.
 */

public class DownloadDonorCardFragment extends Fragment implements View.OnFocusChangeListener, View.OnTouchListener {

    ProgressDialog uploading;
    private TextView username;
    private TextView password;
    private Button btnDownload;
    private TextView inputMobile;
    private TextView inputDob;
    private DonorData donorData;

    public DownloadDonorCardFragment() {

    }

    public static DownloadDonorCardFragment newInstance() {
        DownloadDonorCardFragment fragment = new DownloadDonorCardFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_downloadcard, container, false);
        username = (TextView) view.findViewById(R.id.downloadUsername);
        password = (TextView) view.findViewById(R.id.downloadPassword);
        password.setOnTouchListener(this);

        inputMobile = (TextView) view.findViewById(R.id.warningUserMobile);
        inputDob = (TextView) view.findViewById(R.id.warningUserDob);

        btnDownload = (Button) view.findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkMandatoryFields()) {
                    downloadDonorCard();

                } else {
                    Toast.makeText(getView().getContext(),
                            "Please enter all mandatory fields", Toast.LENGTH_LONG).show();
                }

            }
        });

        return view;
    }

    public boolean validateMobile(String mobileNumber) {
        if (mobileNumber.length() == 10) {
            inputDob.setVisibility(View.INVISIBLE);
            return true;
        } else {
            inputMobile.setVisibility(View.VISIBLE);
            return false;
        }
    }

    public boolean validateDob(String dob) {
        Pattern pattern = Pattern.compile("^([0-2][0-9]||3[0-1]).(0[0-9]||1[0-2]).([0-9][0-9])?[0-9][0-9]$");
        if (pattern.matcher(dob).matches()) {
            inputDob.setVisibility(View.INVISIBLE);
            return true;
        } else {
            inputDob.setVisibility(View.VISIBLE);
            return false;
        }
    }

    public boolean checkMandatoryFields() {
        if (!validateMobile(username.getText().toString()) ||
                !validateDob(password.getText().toString())) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        // validation code goes here
        if (!hasFocus) {
            switch (view.getId()) {
                case R.id.downloadUsername:
                    if (validateMobile(username.getText().toString()))
                        inputMobile.setVisibility(View.INVISIBLE);
                    else
                        inputMobile.setVisibility(View.VISIBLE);
                    break;
                case R.id.downloadPassword:
                    if (validateDob(password.getText().toString()))
                        inputDob.setVisibility(View.INVISIBLE);
                    else
                        inputDob.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void downloadDonorCard() {

        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        Call<List<DonorData>> call = retrofitInterface.getDonorData(
                username.getText().toString(),
                password.getText().toString());

        uploading = ProgressDialog.show(getContext(), "Downloading your card", "please wait...", false, false);

        call.enqueue(new Callback<List<DonorData>>() {
            @Override
            public void onResponse(Call<List<DonorData>> call, Response<List<DonorData>> response) {
                if (response.isSuccessful()) {
                    donorData = response.body().get(0);
                    if (donorData.name != null || donorData.name.equals("")) {
                        new DonorCardGenerator().execute();
                    } else {
                        // Error in login. Get the error message
                        uploading.dismiss();
                        Toast.makeText(getContext(),
                                "Donor details not found, please try again", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Error in login. Get the error message
                    uploading.dismiss();
                    Toast.makeText(getContext(),
                            "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DonorData>> call, Throwable t) {
                uploading.dismiss();
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
                Toast.makeText(getContext(),
                        "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //hide Soft Keyboard for spinners
        InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        return false;
    }

    private class DonorCardGenerator extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... param) {

            PdfDocument document = new PdfDocument();

            // repaint the user's text into the page
            View content = getView().findViewById(R.id.username);

            // crate a page description
            int pageNumber = 1;
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(AppConfig.A4_WIDTH,
                    AppConfig.A4_WIDTH, pageNumber).create();

            // create a new page from the PageInfo
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setTextSize(40);
            Bitmap cardBackground = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_donarcard);
            Bitmap checkMark = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_checkmark_18p);
            canvas.drawBitmap(cardBackground, 10, 10, null);


            if (!donorData.photo.equals("0")) {
                Bitmap profilePhoto = null;
                try {
                    profilePhoto = Glide.
                            with(getContext()).
                            load(AppConfig.BASE_URL + AppConfig.PHOTO_LOCATION_PATH + donorData.photo).
                            asBitmap().
                            into(250, 300). // Width and height
                            get();
                    canvas.drawBitmap(Bitmap.createScaledBitmap(profilePhoto, 250, 300, true), 32, 300, null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            canvas.drawText(donorData.name, 400, 530, paint);
            canvas.drawText(donorData.dob, 300, 642, paint);
            canvas.drawText(donorData.blood_group, 300, 740, paint);
            if (!donorData.organs2.equals(""))
                canvas.drawBitmap(checkMark, 245, 1020, null);
            if (!donorData.organs10.equals(""))
                canvas.drawBitmap(checkMark, 423, 1020, null);
            if (!donorData.organs4.equals(""))
                canvas.drawBitmap(checkMark, 610, 1020, null);
            if (!donorData.organs5.equals(""))
                canvas.drawBitmap(checkMark, 780, 1020, null);
            if (!donorData.organs3.equals(""))
                canvas.drawBitmap(checkMark, 970, 1020, null);
            if (!donorData.organs7.equals(""))
                canvas.drawBitmap(checkMark, 1160, 1020, null);
            if (!donorData.organs6.equals(""))
                canvas.drawBitmap(checkMark, 140, 1150, null);
            if (!donorData.organs9.equals(""))
                canvas.drawBitmap(checkMark, 300, 1150, null);
            if (!donorData.organs.equals(""))
                canvas.drawBitmap(checkMark, 520, 1150, null);

            canvas.drawText(donorData.upload_date, 1000, 1300, paint);
            canvas.drawText(donorData.emg_name1, 800, 1350, paint);
            if(donorData.emg_address1.length()>45) {
                paint.setTextSize(30);
                canvas.drawText(donorData.emg_address1, 80, 1400, paint);
                paint.setTextSize(40);
            } else {
                canvas.drawText(donorData.emg_address1, 80, 1400, paint);
            }
            canvas.drawText(donorData.emg_mobile1, 800, 1450, paint);

            // do final processing of the page
            document.finishPage(page);

            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss");
            String pdfName = "TransTan_DonorCard_"
                    + donorData.name + sdf.format(Calendar.getInstance().getTime()) + ".pdf";

            File dir = new File("//sdcard//Download//");

            File outputFile = new File(dir, pdfName);

            try {
                outputFile.createNewFile();
                OutputStream out = new FileOutputStream(outputFile);
                document.writeTo(out);
                document.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return outputFile.getPath();
        }

        @Override
        protected void onPostExecute(String filePath) {
            uploading.dismiss();
            if (filePath != null) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_custom);
                // Custom Android Allert Dialog Title
                dialog.setTitle("Transtan");

                TextView dialogMessage = (TextView) dialog.findViewById(R.id.messageDialog);
                dialogMessage.setText("\nYour donor Card is saved at " + filePath + "\n");

                Button dialogButtonOk = (Button) dialog.findViewById(R.id.customDialogOk);
                dialogButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();/*
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();*/
                    }
                });

                dialog.show();

            } else {
                Toast.makeText(getContext(),
                        "Something went wrong, please try again" + filePath, Toast.LENGTH_SHORT).show();
            }
        }
    }


}
