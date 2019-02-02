package com.transtan.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.transtan.MainActivity;
import com.transtan.R;
import com.transtan.config.AppConfig;
import com.transtan.utils.CircleTransform;
import com.transtan.utils.FileUtils;
import com.transtan.utils.RetrofitInterface;
import com.transtan.utils.UnsafeOkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;


/**
 * Created by djayakum on 3/22/2018.
 */

public class DonorCardFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener, View.OnTouchListener, TextWatcher {

    ProgressDialog uploading;
    private String TAG = DonorCardFragment.class.getSimpleName();
    private ImageView imageProfile;
    private TextView fullName;
    private TextView address;
    private TextView dob;
    private Spinner gender;
    private Spinner bloodGroup;
    private RecyclerView organs;
    private TextView idNo;
    private TextView email;
    private TextView mobile;
    private Spinner district;
    private TextView emergencyName;
    private TextView emergencyAddress;
    private TextView emergencyMobile;
    private TextView emergencyName2;
    private TextView emergencyAddress2;
    private TextView emergencyMobile2;
    private Button submit;
    private CheckBox[] checkBoxes = new CheckBox[10];
    private TextView warningIdNo;
    private TextView warningEmail;
    private TextView warningMobile;
    private TextView warningEmergencyMobile;
    private TextView warningEmergencyMobile2;
    private String genderParam;
    private String bloodGroupParam;
    private String districtParam;
    private String photoPath;
    private Uri imageUri;
    private HashMap<String, RequestBody> params = new HashMap<>();

    public DonorCardFragment() {

    }

    public static DonorCardFragment newInstance() {
        DonorCardFragment fragment = new DonorCardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private boolean checkStoragePermission() {
        int resultWrite = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int resultRead = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (resultWrite == PackageManager.PERMISSION_GRANTED && resultRead == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, AppConfig.PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, AppConfig.PERMISSION_REQUEST_CODE);
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), "Please provide storage access in App Settings to upload profile photo", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AppConfig.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donorregister, container, false);
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        imageProfile = (ImageView) view.findViewById(R.id.img_profile);
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkStoragePermission() || requestStoragePermission()) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select image"), AppConfig.REQUEST_CODE);
                    } else {
                        Toast.makeText(getView().getContext(),
                                "Please provide storage access to upload Photo", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select image"), AppConfig.REQUEST_CODE);
                }

            }
        });
        fullName = (TextView) view.findViewById(R.id.username);
        fullName.setPadding(30, 20, 20, 20);
        fullName.setOnFocusChangeListener(this);
        address = (TextView) view.findViewById(R.id.useraddress);
        address.setPadding(30, 20, 20, 20);
        address.setOnFocusChangeListener(this);
        dob = (TextView) view.findViewById(R.id.userdob);
        dob.setPadding(30, 20, 20, 20);
        dob.setOnFocusChangeListener(this);
        dob.setOnTouchListener(this);
        gender = (Spinner) view.findViewById(R.id.usergender);
        gender.setOnTouchListener(this);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                genderParam = parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        bloodGroup = (Spinner) view.findViewById(R.id.userBloodGroup);
        bloodGroup.setOnTouchListener(this);
        bloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                bloodGroupParam = parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        checkBoxes[0] = view.findViewById(R.id.organs1);
        checkBoxes[0].setOnClickListener(this);
        checkBoxes[0].setOnTouchListener(this);
        checkBoxes[1] = view.findViewById(R.id.organs2);
        checkBoxes[1].setOnClickListener(this);
        checkBoxes[1].setOnTouchListener(this);
        checkBoxes[2] = view.findViewById(R.id.organs3);
        checkBoxes[2].setOnClickListener(this);
        checkBoxes[2].setOnTouchListener(this);
        checkBoxes[3] = view.findViewById(R.id.organs4);
        checkBoxes[3].setOnClickListener(this);
        checkBoxes[3].setOnTouchListener(this);
        checkBoxes[4] = view.findViewById(R.id.organs5);
        checkBoxes[4].setOnClickListener(this);
        checkBoxes[4].setOnTouchListener(this);
        checkBoxes[5] = view.findViewById(R.id.organs6);
        checkBoxes[5].setOnClickListener(this);
        checkBoxes[5].setOnTouchListener(this);
        checkBoxes[6] = view.findViewById(R.id.organs7);
        checkBoxes[6].setOnClickListener(this);
        checkBoxes[6].setOnTouchListener(this);
        checkBoxes[7] = view.findViewById(R.id.organs8);
        checkBoxes[7].setOnClickListener(this);
        checkBoxes[7].setOnTouchListener(this);
        checkBoxes[8] = view.findViewById(R.id.organs9);
        checkBoxes[8].setOnClickListener(this);
        checkBoxes[8].setOnTouchListener(this);
        checkBoxes[9] = view.findViewById(R.id.organs10);
        checkBoxes[9].setOnClickListener(this);
        checkBoxes[9].setOnTouchListener(this);

        idNo = (TextView) view.findViewById(R.id.userIds);
        idNo.setPadding(30, 20, 20, 20);
        idNo.setOnFocusChangeListener(this);
        email = (TextView) view.findViewById(R.id.userEmail);
        email.setPadding(30, 20, 20, 20);
        email.setOnFocusChangeListener(this);
        mobile = (TextView) view.findViewById(R.id.userMobile);
        mobile.setPadding(30, 20, 20, 20);
        mobile.setOnFocusChangeListener(this);
        district = (Spinner) view.findViewById(R.id.userDistrict);
        district.setOnTouchListener(this);
        district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                districtParam = parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        emergencyName = (TextView) view.findViewById(R.id.emergencyName);
        emergencyName.setPadding(30, 20, 20, 20);
        emergencyName.setOnFocusChangeListener(this);
        emergencyAddress = (TextView) view.findViewById(R.id.emergencyAddress);
        emergencyAddress.setPadding(30, 20, 20, 20);
        emergencyAddress.setOnFocusChangeListener(this);
        emergencyMobile = (TextView) view.findViewById(R.id.emergencyMobile);
        emergencyMobile.setPadding(30, 20, 20, 20);
        emergencyMobile.setOnFocusChangeListener(this);

        emergencyName2 = (TextView) view.findViewById(R.id.emergencyName2);
        emergencyName2.setPadding(30, 20, 20, 20);
        emergencyName2.setOnFocusChangeListener(this);
        emergencyAddress2 = (TextView) view.findViewById(R.id.emergencyAddress2);
        emergencyAddress2.setPadding(30, 20, 20, 20);
        emergencyAddress2.setOnFocusChangeListener(this);
        emergencyMobile2 = (TextView) view.findViewById(R.id.emergencyMobile2);
        emergencyMobile2.setPadding(30, 20, 20, 20);
        emergencyMobile2.setOnFocusChangeListener(this);

        warningIdNo = (TextView) view.findViewById(R.id.warningUserId);
        warningEmail = (TextView) view.findViewById(R.id.warningUserEmail);
        warningMobile = (TextView) view.findViewById(R.id.warningUserMobile);
        warningEmergencyMobile = (TextView) view.findViewById(R.id.warningEmergencyMobile);

        submit = (Button) view.findViewById(R.id.btnSubmit);
        submit.setOnTouchListener(this);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // making json object request
                if (checkMandatoryFields()) {
                    params.put("submit", createPartFromString("submit"));
                    params.put("sname", createPartFromString(fullName.getText().toString()));
                    params.put("address", createPartFromString(address.getText().toString()));
                    params.put("dob", createPartFromString(dob.getText().toString()));
                    params.put("gender", createPartFromString(genderParam));
                    params.put("blood_group", createPartFromString(bloodGroupParam));
                    int organparam = 1;
                    for (CheckBox checkBox : checkBoxes) {
                        if (checkBox.isChecked()) {
                            params.put("organs" + (organparam), createPartFromString(checkBox.getText().toString()));
                        }
                        organparam++;
                    }
                    params.put("id_no", createPartFromString(idNo.getText().toString()));
                    params.put("email", createPartFromString(email.getText().toString()));
                    params.put("phone", createPartFromString(mobile.getText().toString()));
                    params.put("name1", createPartFromString(emergencyName.getText().toString()));
                    params.put("mobile1", createPartFromString(emergencyMobile.getText().toString()));
                    params.put("address1", createPartFromString(emergencyAddress.getText().toString()));
                    params.put("name2", createPartFromString(emergencyName2.getText().toString()));
                    params.put("mobile2", createPartFromString(emergencyMobile2.getText().toString()));
                    params.put("address2", createPartFromString(emergencyAddress2.getText().toString()));
                    params.put("dis", createPartFromString(districtParam));

                    submitDonorDetails();
                } /*else {
                    Toast.makeText(getView().getContext(),
                            "Please check your details and enter all mandatory fields", Toast.LENGTH_LONG).show();
                }*/
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConfig.REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                photoPath = getPath(imageUri);
                Glide.with(this)
                        .load(stream.toByteArray())
                        .transform(new CircleTransform(getContext())) // applying the image transformer
                        .into(imageProfile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //hide Soft Keyboard for spinners
        InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        return false;
    }


    public String getPath(Uri uri) {
        try {

            return FileUtils.getPath(getContext(), uri);

        } catch (CursorIndexOutOfBoundsException e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(getContext(),
                    "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MultipartBody.FORM, descriptionString);
    }

    @Nullable
    private MultipartBody.Part prepareFilePart() {
        if (imageUri != null) {
            String type = getActivity()
                    .getContentResolver().getType(imageUri);
            File photo = null;
            String path = photoPath;
            if (path != null) {
                photo = new File(path);
                RequestBody filePart = RequestBody.create(MediaType.parse(type), photo);
                return MultipartBody.Part.createFormData("photo", photoPath, filePart);
            } else {
                return null;
            }
        }
        return null;

    }

    private void submitDonorDetails() {

        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        MultipartBody.Part photo = prepareFilePart();

        Call<ResponseBody> call = null;

        if (photo != null) {
            call = retrofitInterface.upload(
                    params,
                    prepareFilePart());
        } else {
            call = retrofitInterface.uploadWithoutPhoto(
                    params);
        }
        uploading = ProgressDialog.show(getContext(), "Submitting details", "please wait...", false, false);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onSuccess: " + response.message());
                    new DonorCardGenerator().execute();
                } else {
                    Log.d(TAG, "onSuccess: " + response.message());
                    Toast.makeText(getContext(),
                            "Something went wrong, please try again !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                uploading.dismiss();
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
                Toast.makeText(getContext(),
                        "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.organs1) {
            if (checkBoxes[0].isChecked()) {
                for (CheckBox c : checkBoxes) {
                    c.setChecked(true);
                }
            } else {
                for (CheckBox c : checkBoxes) {
                    c.setChecked(false);
                }
            }
        } else {
            int i = 1;
            for (; i < checkBoxes.length; i++) {
                if (!checkBoxes[i].isChecked()) {
                    checkBoxes[0].setChecked(false);
                    break;
                }
            }
            if (i == checkBoxes.length) {
                checkBoxes[0].setChecked(true);
            }
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        // validation code goes here
        if (!hasFocus) {
            switch (view.getId()) {
                case R.id.userIds:
                    if (validateIdNo(idNo.getText().toString()))
                        warningIdNo.setVisibility(View.INVISIBLE);
                    else
                        warningIdNo.setVisibility(View.VISIBLE);
                    break;
                case R.id.userEmail:
                    if (validateEmail(email.getText().toString()))
                        warningEmail.setVisibility(View.INVISIBLE);
                    else
                        warningEmail.setVisibility(View.VISIBLE);
                    break;
                case R.id.userMobile:
                    if (validateMobile(mobile.getText().toString()))
                        warningMobile.setVisibility(View.INVISIBLE);
                    else
                        warningMobile.setVisibility(View.VISIBLE);
                    break;
                case R.id.emergencyMobile:
                    if (validateMobile(emergencyMobile.getText().toString()))
                        warningEmergencyMobile.setVisibility(View.INVISIBLE);
                    else
                        warningEmergencyMobile.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            TextView textView = (TextView) view.findViewById(view.getId());
            textView.setGravity(Gravity.BOTTOM);
        }
    }

    public boolean checkMandatoryFields() {
        if (fullName.getText().toString().equals("") ||
                address.getText().toString().equals("") ||
                dob.getText().toString().equals("") ||
                genderParam.equals("Gender") ||
                bloodGroupParam.equals("Blood Group") ||
                !validateIdNo(idNo.getText().toString()) ||
                !validateEmail(email.getText().toString()) ||
                !validateMobile(mobile.getText().toString()) ||
                districtParam.equals("District") ||
                emergencyName.getText().toString().equals("") ||
                emergencyAddress.getText().toString().equals("") ||
                !validateMobile(emergencyMobile.getText().toString()) ||
                emergencyName2.getText().toString().equals("") ||
                emergencyAddress2.getText().toString().equals("") ||
                !validateMobile(emergencyMobile2.getText().toString())) {
            Toast.makeText(getView().getContext(),
                    "Please check your details and enter all mandatory fields", Toast.LENGTH_LONG).show();
            return false;
        } else {
            if (mobile.getText().toString().equals(emergencyMobile.getText().toString()) ||
                    mobile.getText().toString().equals(emergencyMobile2.getText().toString())) {
                Toast.makeText(getContext(),
                        "Donor mobile number and emergency mobile number cannot be same, please change to register", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean validateIdNo(String inputIdNo) {
        return (inputIdNo.equals("") || inputIdNo.replaceAll(" ", "").length() == 12);
    }

    public boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile("(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)");
        return (pattern.equals("") || pattern.matcher(email).matches());
    }

    public boolean validateMobile(String mobileNumber) {
        return (mobileNumber.equals("") || mobileNumber.length() == 10);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().equals(mobile)) {

        }

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

            if (photoPath != null) {

                try {
                    Bitmap profilePhoto = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                    canvas.drawBitmap(Bitmap.createScaledBitmap(profilePhoto, 250, 300, true), 32, 300, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            canvas.drawText(fullName.getText().toString(), 400, 530, paint);
            canvas.drawText(dob.getText().toString(), 300, 642, paint);
            canvas.drawText(bloodGroupParam, 300, 740, paint);
            if (checkBoxes[1].isChecked())
                canvas.drawBitmap(checkMark, 245, 1020, null);
            if (checkBoxes[9].isChecked())
                canvas.drawBitmap(checkMark, 423, 1020, null);
            if (checkBoxes[3].isChecked())
                canvas.drawBitmap(checkMark, 610, 1020, null);
            if (checkBoxes[4].isChecked())
                canvas.drawBitmap(checkMark, 780, 1020, null);
            if (checkBoxes[2].isChecked())
                canvas.drawBitmap(checkMark, 970, 1020, null);
            if (checkBoxes[6].isChecked())
                canvas.drawBitmap(checkMark, 1160, 1020, null);
            if (checkBoxes[5].isChecked())
                canvas.drawBitmap(checkMark, 140, 1150, null);
            if (checkBoxes[8].isChecked())
                canvas.drawBitmap(checkMark, 300, 1150, null);
            if (checkBoxes[0].isChecked())
                canvas.drawBitmap(checkMark, 520, 1150, null);
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd.MMM.yyyy");
            String formattedDate = df.format(c);
            canvas.drawText(formattedDate, 1000, 1300, paint);
            canvas.drawText(emergencyName.getText().toString(), 800, 1350, paint);
            canvas.drawText(emergencyAddress.getText().toString(), 150, 1400, paint);
            canvas.drawText(emergencyMobile.getText().toString(), 800, 1450, paint);

            // do final processing of the page
            document.finishPage(page);

            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss");
            String pdfName = "TransTan_DonorCard_"
                    + fullName.getText().toString() + sdf.format(Calendar.getInstance().getTime()) + ".pdf";

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

                TextView dialogMessage = (TextView) dialog.findViewById(R.id.messageDialog);
                dialogMessage.setText("Successfully registered as Donor. Please note down your username & password,\n\n\n" +
                        "\bUsername:" + mobile.getText().toString() + "\n" +
                        "\bPassword:" + dob.getText().toString() + "\n\n\n" +
                        "Donor Card is saved at \b" + filePath);

                Button dialogButtonOk = (Button) dialog.findViewById(R.id.customDialogOk);
                dialogButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
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
