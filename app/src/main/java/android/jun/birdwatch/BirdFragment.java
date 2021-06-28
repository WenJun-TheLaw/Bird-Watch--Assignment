package android.jun.birdwatch;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class BirdFragment extends Fragment {
    private static final String ARG_BIRD_ID = "bird_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_GALLERY = 2;
    private Bird mBird;
    private EditText mNameField;
    private EditText mDescriptionField;
    private Button mDateButton;
    private Button mRemoveButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID birdId = (UUID) getArguments().getSerializable(ARG_BIRD_ID);
        mBird = BirdList.get(getActivity()).getBird(birdId);
        mPhotoFile = BirdList.get(requireActivity()).getPhotoFile(mBird);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        BirdList.get(requireActivity()).updateBird(mBird);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            //Getting date
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mBird.setDate(date);
            updateDate();

        } else if (requestCode == REQUEST_PHOTO) {
            //Getting photo from camera and closing up the permissions
            Uri uri = FileProvider.getUriForFile(requireActivity(), "android.jun.birdwatch.fileprovider", mPhotoFile);
            requireActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();

        } else if (requestCode == REQUEST_GALLERY) {
            //Getting photo from gallery
            try {
                OutputStream fOut = new FileOutputStream(mPhotoFile);
                Uri pictureUri = data.getData();
                Bitmap pictureBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), pictureUri);
                pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                fOut.flush();
                fOut.close();
                //Saving the image to the path with name and description
                MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), mPhotoFile.getAbsolutePath(), mPhotoFile.getName(), mPhotoFile.getName());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(requireContext(),"FileNotFoundException: " + e.toString(), Toast.LENGTH_SHORT);
                toast.show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(requireContext(),"IOException: " + e.toString(),Toast.LENGTH_SHORT);
                toast.show();
            }
            updatePhotoView();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bird, container, false);

        //Linking up Name Widgets
        mNameField = (EditText) v.findViewById(R.id.bird_name);
        if (mBird.getName() != null) {
            mNameField.setText(mBird.getName());
        }
        mNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mBird.setName(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Not used
            }
        });

        //Linking up Description Widgets
        mDescriptionField = (EditText) v.findViewById(R.id.bird_description);
        if (mBird.getDescription() != null) {
            mDescriptionField.setText(mBird.getDescription());
        }
        mDescriptionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mBird.setDescription(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Not used
            }
        });

        //Setting up Date Picker button
        mDateButton = (Button) v.findViewById(R.id.bird_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getParentFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mBird.getDate());
                dialog.setTargetFragment(BirdFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        //Setting up remove bird button
        mRemoveButton = (Button) v.findViewById(R.id.bird_remove);
        mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Dialog Interface
                DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case DialogInterface.BUTTON_POSITIVE:
                                BirdList.get(getActivity()).removeBird(mBird.getID());
                                requireActivity().onBackPressed();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you really sure?")
                        .setPositiveButton("Yes", dialogListener)
                        .setNegativeButton("No", dialogListener)
                        .show();
            }
        });

        //Setting up take photo button and photo ImageView
        mPhotoButton = (ImageButton) v.findViewById(R.id.bird_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Determining if device supports camera function
        boolean canTakePhoto = (mPhotoFile != null) && (captureImage.resolveActivity(requireActivity().getPackageManager()) != null);
        mPhotoButton.setOnClickListener(view -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity());
            alertDialog.setTitle(R.string.bird_dialog_title);
            alertDialog.setItems(new CharSequence[]{getString(R.string.bird_dialog_camera), getString(R.string.bird_dialog_gallery)},
                            (dialogInterface, i) -> {
                        switch (i){
                            case 0:
                                //User chose camera
                                //Device supports camera function
                                if (canTakePhoto) {
                                    //Check whether app has permission to use camera
                                    boolean hasPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
                                    if (hasPermission) {
                                        //App has permission
                                        startCameraIntent();
                                    } else {
                                        //App does not have permission, ask for it
                                        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                                    }
                                }
                                //Device does not support camera function
                                else {
                                    Toast toast = Toast.makeText(getContext(), getString(R.string.device_no_support_camera), Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                                break;
                            case 1:
                                //User chose gallery
                                startGalleryIntent();
                                break;
                        }});
            alertDialog.setNegativeButton(R.string.bird_dialog_cancel, (dialogInterface, i) -> dialogInterface.dismiss());
            alertDialog.show();
        });


        //Setting up photo view and update it
        mPhotoView = (ImageView) v.findViewById(R.id.bird_photo);
        updatePhotoView();

        return v;
    }

    // We need to setup the menu bar in the app bar
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.bird_fragment_menu, menu); // get menu from the xml file, bird_fragment_menu.xml
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share_bird_sighting:
                //Sharing the bird sighting
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getBirdSighting());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.bird_sighting_subject));
                i = Intent.createChooser(i, getString(R.string.share_bird_text));
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static BirdFragment newInstance(UUID birdId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_BIRD_ID, birdId);
        BirdFragment fragment = new BirdFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private String getBirdSighting() {
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mBird.getDate()).toString();

        String sighting = getString(R.string.bird_sighting, mBird.getName(), dateString, mBird.getDescription());
        return sighting;
    }

    private void updateDate() {
        mDateButton.setText(mBird.getDate().toString());
    }

    private void updatePhotoView() {
        //If no photo, display nothing
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        }
        //IF got, scale the bitmap and dispay it
        else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), requireActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new RequestPermission(), isGranted -> {
                if (isGranted) {
                    ;
                    startCameraIntent();
                } else {
                    //User denied permission, show sad toast
                    Toast toast = Toast.makeText(getContext(), "Permission denied by user :(", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

    private void cameraGalleryDialog(){
        CharSequence[] options = {"Take picture with camera", "Choose image from gallery", "Cancel"};
    }

    private void startCameraIntent() {
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = FileProvider.getUriForFile(requireActivity(), "android.jun.birdwatch.fileprovider", mPhotoFile);
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        List<ResolveInfo> cameraActivities = requireActivity().getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo activity : cameraActivities) {
            requireActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        startActivityForResult(captureImage, REQUEST_PHOTO);
    }

    private void startGalleryIntent() {
        final Intent selectImage = new Intent();
        selectImage.setType("image/*");
        selectImage.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(selectImage, "Select Picture"),REQUEST_GALLERY);
    }

}


