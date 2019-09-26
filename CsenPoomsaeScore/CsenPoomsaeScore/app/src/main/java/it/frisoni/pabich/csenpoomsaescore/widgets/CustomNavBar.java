package it.frisoni.pabich.csenpoomsaescore.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Console;

import it.frisoni.pabich.csenpoomsaescore.R;
import it.frisoni.pabich.csenpoomsaescore.utils.server.WebSocketHelper;

/**
 * Created by marti on 21/04/2017.
 *
 * Questa classe si occupa di gestire una navigation bar costituita da 3 elementi personalizzabili:
 * - un titolo
 * - un pulsante di back
 * - un pulsante di forward
 */

public class CustomNavBar extends RelativeLayout {

    private Button backButton, forwardButton;
    private TextView title, txvTabletConnection;

    public CustomNavBar(Context context) {
        super(context);
        initControl(context, null);
    }

    public CustomNavBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    public CustomNavBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initControl(context, attrs);
    }


    /**
     * Load component XML layout
     */
    private void initControl(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.navigation_bar, this);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        backButton = (Button) findViewById(R.id.btn_back);
        forwardButton = (Button) findViewById(R.id.btn_forward);
        title = (TextView) findViewById(R.id.txv_title);
        txvTabletConnection = (TextView) findViewById(R.id.txv_nav_bar_tablet_connection_status);

        if (attrs != null) {
            TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomNavBar, 0, 0);

            try {
                String back = array.getString(R.styleable.CustomNavBar_backText);
                String forward = array.getString(R.styleable.CustomNavBar_forwardText);
                String title = array.getString(R.styleable.CustomNavBar_titleText);
                boolean backEnabled = array.getBoolean(R.styleable.CustomNavBar_backEnabled, true);
                boolean forwardEnabled = array.getBoolean(R.styleable.CustomNavBar_forwardEnabled, true);

                if (back != null) {
                    setBackText(back);
                }

                if (forward != null) {
                    setForwardText(forward);
                }

                if (title != null) {
                    setTitle(title);
                }

                setBackButtonEnabled(backEnabled);
                setForwardButtonEnabled(forwardEnabled);

            } catch (Exception e) {
                Log.e("CustomNavBar", "Problem during attributes' reading: " + e.getMessage());
            } finally {
                array.recycle();
            }
        }
    }


    public void setBackText(String text) {
        backButton.setText(text);
    }

    public void setForwardText(String text) {
        forwardButton.setText(text);
    }

    public void setTitle(String text) {
        title.setText(text);
    }

    public void setTabletNotConnected() {
        txvTabletConnection.setText(getContext().getText(R.string.tablet_not_connected));
        txvTabletConnection.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_wifi_off), null, null, null);
    }

    public void setTabletConnecting() {
        txvTabletConnection.setText(getContext().getText(R.string.tablet_check_connection));
        txvTabletConnection.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_refresh), null, null, null);
    }

    public void setTabletConnected(final String serverIPAddress, final String deviceID) {
        txvTabletConnection.setText(getContext().getString(R.string.tablet_connected_device_id, serverIPAddress, deviceID));
        txvTabletConnection.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_wifi_on), null, null, null);
    }

    public void setBackButtonEnabled(boolean enabled) {
        backButton.setEnabled(enabled);
        backButton.setVisibility(enabled ? VISIBLE : GONE);
    }

    public void setForwardButtonEnabled(boolean enabled) {
        forwardButton.setEnabled(enabled);
        forwardButton.setVisibility(enabled ? VISIBLE : GONE);
    }


    public Button getBackButton() {
        return backButton;
    }

    public Button getForwardButton() {
        return forwardButton;
    }
}
