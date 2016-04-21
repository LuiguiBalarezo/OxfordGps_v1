package com.tamc.oxfordgps_v1.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;
import com.tamc.oxfordgps_v1.MainActivity;
import com.tamc.oxfordgps_v1.R;
import com.tamc.oxfordgps_v1.utils.ObservableScrollView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * A simple {@link Fragment} subclass.
 */
public class Configuracion extends Fragment {

    private static final String TAG = "Configuracion";
    private Context mContext;
    int id;
    private String correo, dni, celular, nombres, apellidos, ip, port, interval;
    private SharedPreferences sp;

    /*Spinner*/
    private String[] itemsEmails;
    private String[] itemsDnis;
    private String[] itemsCelulares;
    private String[] itemsNombres;
    private String[] itemsApellidos;
    private ArrayAdapter<String> adapter;
    /*---*/

    /*Scrollview scroll*/
    Rect scrollBounds;
    private static final int SCROLL_DIRECTION_CHANGE_THRESHOLD = 5;
    private static final int SCROLL_TO_TOP = -1;
    private static final int SCROLL_TO_BOTTOM = 1;
    private int mScrollDirection = 0;
    int newScrollDirection;
    /*---*/

    /*View Butters*/
    @InjectView(R.id.Scrl_configuracion)
    ObservableScrollView scrl_configuracion;
    @InjectView(R.id.View_indicator_for_toolbar)
    View view_indicator_for_toolbar;
    @InjectView(R.id.Spn_config)
    Spinner spinner1;
    @InjectView(R.id.Edt_correo_config)
    EditText edt_correo_config;
    @InjectView(R.id.Edt_dni_config)
    EditText edt_dni_config;
    @InjectView(R.id.Edt_celular_config)
    EditText edt_celular_config;
    @InjectView(R.id.Edt_nombres_config)
    EditText edt_nombres_config;
    @InjectView(R.id.Edt_apellidos_config)
    EditText edt_apellidos_config;
    @InjectView(R.id.Edt_ip_config)
    EditText edt_ip_config;
    @InjectView(R.id.Edt_port_config)
    EditText edt_port_config;
    @InjectView(R.id.Edt_interval_config)
    EditText edt_interval_config;
    @InjectView(R.id.Rgb_interval_config)
    RangeBar rgb_interval_config;
    @InjectView(R.id.Btn_guardar)
    Button btn_guardar;
    @InjectView(R.id.Btn_modificar)
    Button btn_modificar;
    /*---*/

    public Configuracion() {
    }

    ClicksConfiguracion clicksConfiguracion;
    public interface ClicksConfiguracion {
        public void onClickGuardar(String correo, String dni, String celular, String nombres, String apellidos, String ip, String port, String interval);
        public void onClickModificar(String intervalo);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            clicksConfiguracion = (ClicksConfiguracion) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() ESTAS EN CONFIGURACION");
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_configuracion, container, false);
        if (getArguments() != null) {
            itemsEmails = getArguments().getStringArray("itemsEmails");
            itemsDnis = getArguments().getStringArray("itemsDnis");
            itemsCelulares = getArguments().getStringArray("itemsCelulares");
            itemsNombres = getArguments().getStringArray("itemsNombres");
            itemsApellidos = getArguments().getStringArray("itemsApellidos");
        }
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.setShowToolBar(true, "Configuracion");
        scrl_configuracion.setOnScrollChangedListener(onScrollListener);
        rgb_interval_config.setOnRangeBarChangeListener(onRangeBarChangeListener);

        if (getArguments() != null) {
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, itemsEmails);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinner1.setAdapter(adapter);
        }

        if (TextUtils.isEmpty(sp.getString("INTERVAL",""))){
            rgb_interval_config.setSeekPinByIndex(5);
            edt_interval_config.setText("6");
        }else {
            rgb_interval_config.setSeekPinByIndex(Integer.parseInt(sp.getString("INTERVAL", ""))-1);
        }

        if (!TextUtils.isEmpty(sp.getString("CORREO", ""))) {
            spinner1.setVisibility(View.GONE);
            edt_correo_config.setVisibility(View.VISIBLE);
            btn_guardar.setVisibility(View.GONE);
            btn_modificar.setVisibility(View.VISIBLE);
            edt_correo_config.setText(sp.getString("CORREO", ""));
            edt_dni_config.setText(sp.getString("DNI", ""));
            edt_celular_config.setText(sp.getString("CELULAR", ""));
            edt_nombres_config.setText(sp.getString("NOMBRES", ""));
            edt_apellidos_config.setText(sp.getString("APELLIDOS", ""));
            edt_interval_config.setText(sp.getString("INTERVAL", ""));
        } else {
            spinner1.setVisibility(View.VISIBLE);
            edt_correo_config.setVisibility(View.GONE);
            btn_guardar.setVisibility(View.VISIBLE);
            btn_modificar.setVisibility(View.GONE);
        }

    }

    @OnItemSelected(value = R.id.Spn_config, callback = OnItemSelected.Callback.ITEM_SELECTED)
    public void selectedEmail(){
        id = spinner1.getSelectedItemPosition();
//        Toast.makeText(getActivity(), " " + id, Toast.LENGTH_SHORT).show();
        edt_correo_config.setText(itemsEmails[id]);
        edt_dni_config.setText(itemsDnis[id]);
        edt_celular_config.setText(itemsCelulares[id]);
        edt_nombres_config.setText(itemsNombres[id]);
        edt_apellidos_config.setText(itemsApellidos[id]);
    }

    @OnClick(R.id.Btn_guardar)
    public void click_guardar(){
        correo = edt_correo_config.getText().toString();
        dni = edt_dni_config.getText().toString();
        celular = edt_celular_config.getText().toString();
        nombres = edt_nombres_config.getText().toString();
        apellidos = edt_apellidos_config.getText().toString();
        ip = edt_ip_config.getText().toString();
        port = edt_port_config.getText().toString();
        interval = edt_interval_config.getText().toString();

        initShowErrors();

        if (id > 0 && !TextUtils.isEmpty(correo) && !TextUtils.isEmpty(dni) && !TextUtils.isEmpty(celular) && !TextUtils.isEmpty(nombres) && !TextUtils.isEmpty(apellidos) &&
                !TextUtils.isEmpty(ip) && !TextUtils.isEmpty(port) && !TextUtils.isEmpty(interval)) {
            clicksConfiguracion.onClickGuardar(correo, dni, celular, nombres, apellidos, ip, port, interval);
        } else {
            initShowErrors();
        }
    }

    @OnClick(R.id.Btn_modificar)
    public void click_modificar(){
        Log.d(TAG, "click_modificar()");
        interval = edt_interval_config.getText().toString();
        Log.d(TAG, "click_modificar() Intervalo: ( " + interval +" )minutos.");
        clicksConfiguracion.onClickModificar(interval);
        getActivity().getSupportFragmentManager().popBackStack();
    }

    RangeBar.OnRangeBarChangeListener onRangeBarChangeListener = new RangeBar.OnRangeBarChangeListener() {
        @Override
        public void onRangeChangeListener(RangeBar rangeBar, int i, int i2, String s, String s2) {
            edt_interval_config.setText(s2);
        }
    };

    ObservableScrollView.OnScrollChangedListener onScrollListener = new ObservableScrollView.OnScrollChangedListener() {
        int mScrollPosition;

        @Override
        public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
            if (isViewVisible(view_indicator_for_toolbar)) {
                Log.d(TAG, "VIEW VISIBLE");
            } else {
                Log.d(TAG, "VIEW OCULTO");
                if (Math.abs(y - mScrollPosition) >= SCROLL_DIRECTION_CHANGE_THRESHOLD) {
                    onScrollPositionChanged(mScrollPosition, y);
                }
            }
            mScrollPosition = y;
        }
    };

    private void onScrollPositionChanged(int oldScrollPosition, int newScrollPosition) {
        if (newScrollPosition < oldScrollPosition) {
            newScrollDirection = SCROLL_TO_TOP;
            Log.d(TAG, "SCROLL_TO_TOP");
        } else {
            newScrollDirection = SCROLL_TO_BOTTOM;
            Log.d(TAG, "SCROLL_TO_BOTTOM");
        }

        if (newScrollDirection != mScrollDirection) {
            mScrollDirection = newScrollDirection;
            Log.d(TAG, "TRANSLATEPOSITION");
            translateYToolBar();
        }
    }

    private void translateYToolBar() {
        MainActivity.mToolbar.post(new Runnable() {
            @Override
            public void run() {
                int translationY = 0;
                switch (newScrollDirection) {
                    case SCROLL_TO_BOTTOM:
                        onHide();
                        break;
                    case SCROLL_TO_TOP:
                        onShow();
                        break;
                }
            }
        });
    }

    public void onHide() {
        MainActivity.mToolbar.animate().translationY(-MainActivity.mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    public void onShow() {
        MainActivity.mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    private boolean isViewVisible(View view) {
        scrollBounds = new Rect();
        scrl_configuracion.getDrawingRect(scrollBounds);
        float top = view.getY();
        float bottom = top + view.getHeight();
        if (scrollBounds.top < top && scrollBounds.bottom > bottom) {
            return true;
        } else {
            return false;
        }
    }

    private void initShowErrors() {

        if (TextUtils.isEmpty(dni)) {
            edt_dni_config.setError("complete dato.");
        }

        if (TextUtils.isEmpty(celular)) {
            edt_celular_config.setError("complete dato.");
        }

        if (TextUtils.isEmpty(nombres)) {
            edt_nombres_config.setError("complete dato.");
        }

        if (TextUtils.isEmpty(apellidos)) {
            edt_apellidos_config.setError("complete dato.");
        }

        if (TextUtils.isEmpty(ip)) {
            edt_ip_config.setError("complete dato.");
        }

        if (TextUtils.isEmpty(port)) {
            edt_port_config.setError("complete dato.");
        }

        if (TextUtils.isEmpty(interval)) {
            edt_interval_config.setError("vacio");
        }
    }

}
