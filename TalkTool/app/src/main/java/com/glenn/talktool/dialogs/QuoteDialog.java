package com.glenn.talktool.dialogs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.glenn.talktool.R;

/**
 * Dialog activity that shows the contents of the quote that was selected from the list
 */
public class QuoteDialog extends AppCompatActivity implements View.OnClickListener{

    /**
     * Button for the "OK" action on the dialog
     */
    public Button okButton;
    /**
     * TextView that shows the title
     */
    public TextView textView1;
    /**
     * TextView that shows the body text
     */
    public TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        init();
    }

    private void init(){
        okButton = (Button) findViewById(R.id.okButton);

        okButton.setOnClickListener(this);

        textView1 = (TextView) findViewById(R.id.textTitle);
        textView2 = (TextView) findViewById(R.id.textBody);

        textView1.setText(getIntent().getStringExtra("theTitle"));
        textView2.setText(getIntent().getStringExtra("theBody"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.okButton:
                okButtonClick();
                break;
        }
    }

    private void okButtonClick(){
        finish();
    }
}
