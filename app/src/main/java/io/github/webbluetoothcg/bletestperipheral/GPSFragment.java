package io.github.webbluetoothcg.bletestperipheral;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GPSFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GPSFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GPSFragment extends ServiceFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ServiceFragmentDelegate mDelegate;
    private static final UUID GPS_SERVICE_UUID = UUID
            .fromString("0000185F-0000-1000-8000-00805f9b34fb");
    private static final UUID GPS_COORDS_UUID = UUID
            .fromString("00002A20-0000-1000-8000-00805f9b34fb");
    private static final String GPS_COORDS_DESCRIPTION = "Location of device";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public GPSFragment() {
        mGpsCoordsCharacteristic =
                new BluetoothGattCharacteristic(GPS_COORDS_UUID,
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ);

        mGpsCoordsCharacteristic.addDescriptor(
                Peripheral.getClientCharacteristicConfigurationDescriptor());

        mGpsCoordsCharacteristic.addDescriptor(
                Peripheral.getCharacteristicUserDescriptionDescriptor(GPS_COORDS_DESCRIPTION));

        mGpsService = new BluetoothGattService(GPS_SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        mGpsService.addCharacteristic(mGpsCoordsCharacteristic);
        mGpsCoordsCharacteristic.setValue(12,
                BluetoothGattCharacteristic.FORMAT_UINT8, /* offset */ 0);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GPSFragment.
     */
    // TODO: Rename and change types and number of parameters
    private BluetoothGattService mGpsService;
    private BluetoothGattCharacteristic mGpsCoordsCharacteristic;
    public static GPSFragment newInstance(String param1, String param2) {
        GPSFragment fragment = new GPSFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button clickButton = (Button) getView().findViewById(R.id.button_GPSNotify);
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "button press",Toast.LENGTH_SHORT).show();
                Peripherals peripherals = (Peripherals) getActivity();
                peripherals.fetchAddressButtonHander(view);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_gps, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mDelegate = (ServiceFragmentDelegate) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ServiceFragmentDelegate");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDelegate = null;
    }

    @Override
    public BluetoothGattService getBluetoothGattService() {
        return mGpsService;
    }

    @Override
    public ParcelUuid getServiceUUID() {
        return new ParcelUuid(GPS_SERVICE_UUID);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    @Override
    public void notificationsEnabled(BluetoothGattCharacteristic characteristic, boolean indicate) {
        if (characteristic.getUuid() != GPS_COORDS_UUID) {
            return;
        }
        if (indicate) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), R.string.notificationsEnabled, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public void notificationsDisabled(BluetoothGattCharacteristic characteristic) {
        if (characteristic.getUuid() != GPS_COORDS_UUID) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), R.string.notificationsNotEnabled, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}
